/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.RenderTarget
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.Screenshot
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.texture.DynamicTexture
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import dev.shadowsovereign.ShadowSovereign;
import dev.shadowsovereign.client.ClientVigil;
import dev.shadowsovereign.client.ModLanguage;
import dev.shadowsovereign.client.RoyalTheme;
import dev.shadowsovereign.odyssey.OdysseyText;
import dev.shadowsovereign.vigil.VigilText;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class VigilAlbumScreen
extends Screen {
    private final List<Path> photos = new ArrayList<Path>();
    private int selected;
    private ResourceLocation texture;
    private int imageW;
    private int imageH;
    private boolean confirmDelete;
    private static int captureTicks;
    private static boolean previousHud;

    public VigilAlbumScreen() {
        super((Component)VigilText.c("album", new Object[0]));
    }

    private static Path directory() {
        Minecraft m = Minecraft.m_91087_();
        return m.f_91069_.toPath().resolve("screenshots/soulbound-army").resolve(m.f_91074_ == null ? "local" : m.f_91074_.m_20148_().toString());
    }

    private void release() {
        if (this.texture != null && this.f_96541_ != null) {
            this.f_96541_.m_91097_().m_118513_(this.texture);
            this.texture = null;
        }
    }

    public static void captureAfter(int ticks) {
        Minecraft m;
        block10: {
            m = Minecraft.m_91087_();
            if (m.f_91074_ == null || captureTicks > 0) {
                return;
            }
            try {
                if (!Files.isDirectory(VigilAlbumScreen.directory(), new LinkOption[0])) break block10;
                try (Stream<Path> stream = Files.list(VigilAlbumScreen.directory());){
                    if (stream.filter(file -> file.getFileName().toString().matches("vigil_[0-9]+\\.png")).limit(20L).count() >= 20L) {
                        m.f_91074_.m_5661_((Component)OdysseyText.c("album_full", new Object[0]), true);
                        return;
                    }
                }
            }
            catch (IOException failure) {
                ShadowSovereign.LOGGER.warn("Cannot inspect local photograph capacity", (Throwable)failure);
                return;
            }
        }
        previousHud = m.f_91066_.f_92062_;
        m.f_91066_.f_92062_ = true;
        captureTicks = Math.max(3, Math.min(200, ticks));
        m.m_91152_(null);
    }

    protected void m_7856_() {
        this.release();
        this.photos.clear();
        try {
            Files.createDirectories(VigilAlbumScreen.directory(), new FileAttribute[0]);
            try (Stream<Path> stream = Files.list(VigilAlbumScreen.directory());){
                this.photos.addAll(stream.filter(p -> p.getFileName().toString().matches("vigil_[0-9]+\\.png")).sorted(Comparator.reverseOrder()).limit(20L).toList());
            }
            this.selected = Math.max(0, Math.min(this.selected, this.photos.size() - 1));
            this.load();
        }
        catch (IOException e) {
            ShadowSovereign.LOGGER.warn("Cannot open local Soulbound photographs", (Throwable)e);
        }
    }

    private void load() {
        this.release();
        if (this.photos.isEmpty()) {
            return;
        }
        try {
            Path file = this.photos.get(this.selected);
            if (Files.size(file) > 0xC00000L) {
                return;
            }
            try (InputStream in = new DataInputStream(Files.newInputStream(file, new OpenOption[0]));){
                byte[] header = in.readNBytes(24);
                if (header.length < 24 || header[0] != -119 || header[1] != 80) {
                    return;
                }
                ByteBuffer dims = ByteBuffer.wrap(header);
                this.imageW = dims.getInt(16);
                this.imageH = dims.getInt(20);
                if (this.imageW < 1 || this.imageH < 1 || this.imageW > 4096 || this.imageH > 4096) {
                    return;
                }
            }
            in = Files.newInputStream(file, new OpenOption[0]);
            try {
                NativeImage image = NativeImage.m_85058_((InputStream)in);
                this.texture = this.f_96541_.m_91097_().m_118490_("vigil_photo", new DynamicTexture(image));
            }
            finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        catch (IOException | RuntimeException e) {
            ShadowSovereign.LOGGER.warn("Cannot read a local Soulbound photograph", (Throwable)e);
        }
    }

    public boolean m_7043_() {
        return false;
    }

    public void m_88315_(GuiGraphics g, int x, int y, float partial) {
        this.m_280273_(g);
        RoyalTheme.book(g, this.f_96543_, this.f_96544_, 7);
        ModLanguage.draw(g, this.f_96547_, ClientVigil.t("album", new Object[0]) + " \u00b7 " + this.photos.size() + " / 20", 16, 18, -1981295, false);
        if (this.texture != null) {
            double scale = Math.min(((double)this.f_96543_ - 40.0) / (double)this.imageW, ((double)this.f_96544_ - 100.0) / (double)this.imageH);
            int w = (int)((double)this.imageW * scale);
            int h = (int)((double)this.imageH * scale);
            g.m_280411_(this.texture, (this.f_96543_ - w) / 2, 46, w, h, 0.0f, 0.0f, this.imageW, this.imageH, this.imageW, this.imageH);
        }
        int bw = (this.f_96543_ - 40) / 4;
        String[] labels = new String[]{"<", ClientVigil.t("capture", new Object[0]), ClientVigil.t(this.confirmDelete ? "confirm_delete" : "delete", new Object[0]), ">"};
        for (int i = 0; i < 4; ++i) {
            int xx = 16 + i * bw;
            RoyalTheme.frame(g, xx, this.f_96544_ - 40, bw - 3, 27, -282517454);
            ModLanguage.draw(g, this.f_96547_, ModLanguage.fit(this.f_96547_, labels[i], bw - 10), xx + 5, this.f_96544_ - 30, -988710, false);
        }
        super.m_88315_(g, x, y, partial);
    }

    public boolean m_6375_(double x, double y, int b) {
        if (b != 0 || y < (double)(this.f_96544_ - 40) || y >= (double)(this.f_96544_ - 13)) {
            return false;
        }
        int at = (int)((x - 16.0) / (double)((this.f_96543_ - 40) / 4));
        if (at == 1) {
            if (this.photos.size() >= 20) {
                return true;
            }
            previousHud = this.f_96541_.f_91066_.f_92062_;
            this.f_96541_.f_91066_.f_92062_ = true;
            captureTicks = 3;
            this.f_96541_.m_91152_(null);
        } else if (at == 0 || at == 3) {
            if (!this.photos.isEmpty()) {
                this.selected = Math.floorMod(this.selected + (at == 0 ? -1 : 1), this.photos.size());
                this.load();
            }
            this.confirmDelete = false;
        } else if (at == 2 && !this.photos.isEmpty()) {
            if (!this.confirmDelete) {
                this.confirmDelete = true;
            } else {
                this.release();
                try {
                    Files.deleteIfExists(this.photos.get(this.selected));
                }
                catch (IOException e) {
                    ShadowSovereign.LOGGER.warn("Cannot delete selected Soulbound photograph", (Throwable)e);
                }
                this.confirmDelete = false;
                this.m_7856_();
            }
        }
        return true;
    }

    public void m_7861_() {
        this.release();
        super.m_7861_();
    }

    @Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT})
    public static final class Capture {
        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @SubscribeEvent
        public static void tick(TickEvent.ClientTickEvent e) {
            block13: {
                if (e.phase != TickEvent.Phase.END || captureTicks <= 0 || --captureTicks > 0) {
                    return;
                }
                Minecraft m = Minecraft.m_91087_();
                try {
                    if (m.f_91074_ == null) break block13;
                    Files.createDirectories(VigilAlbumScreen.directory(), new FileAttribute[0]);
                    try (NativeImage image = Screenshot.m_92279_((RenderTarget)m.m_91385_());){
                        if (image.m_84982_() <= 4096 && image.m_85084_() <= 4096) {
                            image.m_85066_(VigilAlbumScreen.directory().resolve("vigil_" + System.currentTimeMillis() + ".png"));
                        }
                    }
                    m.f_91074_.m_5661_((Component)VigilText.c("photo_saved", new Object[0]), true);
                }
                catch (IOException | RuntimeException failure) {
                    ShadowSovereign.LOGGER.warn("Cannot save Soulbound photograph", (Throwable)failure);
                }
                finally {
                    m.f_91066_.f_92062_ = previousHud;
                }
            }
        }
    }
}

