/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.ThrownItemRenderer
 *  net.minecraft.client.renderer.entity.layers.EyesLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.client.renderer.item.CompassItemPropertyFunction
 *  net.minecraft.client.renderer.item.ItemProperties
 *  net.minecraft.client.renderer.item.ItemPropertyFunction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.GlobalPos
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.GlassBlock
 *  net.minecraft.world.level.block.LanternBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.block.SugarCaneBlock
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.EntityRenderersEvent$RegisterRenderers
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsovereign.client.ChimeMiteRenderer;
import dev.shadowsovereign.client.DevourerModel;
import dev.shadowsovereign.expanse.ExpanseContent;
import dev.shadowsovereign.gloom.GloomContent;
import dev.shadowsovereign.mythic.MythicBeast;
import dev.shadowsovereign.mythic.MythicContent;
import dev.shadowsovereign.mythic.MythicExpedition;
import dev.shadowsovereign.odyssey.OdysseyRealm;
import java.util.stream.Stream;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class MythicBeastRenderer
extends MobRenderer<MythicBeast, DevourerModel<MythicBeast>> {
    private final ResourceLocation texture;
    private final int kind;

    public MythicBeastRenderer(EntityRendererProvider.Context context, final int kind) {
        super(context, new DevourerModel(MythicExpedition.IDS[kind]), kind == 0 ? 0.65f : (kind >= 13 ? 0.6f : 1.4f));
        this.kind = kind;
        this.texture = new ResourceLocation("shadowsovereign", "textures/entity/" + MythicExpedition.IDS[kind] + ".png");
        this.m_115326_((RenderLayer)new EyesLayer<MythicBeast, DevourerModel<MythicBeast>>((RenderLayerParent)this){

            public RenderType m_5708_() {
                return RenderType.m_110488_((ResourceLocation)new ResourceLocation("shadowsovereign", "textures/entity/" + MythicExpedition.IDS[kind] + "_glow.png"));
            }
        });
    }

    public ResourceLocation getTextureLocation(MythicBeast m) {
        return this.kind == Integer.MIN_VALUE && (m.companionFlags() & 1) != 0 ? new ResourceLocation("shadowsovereign:textures/entity/removed_companion_tamed.png") : this.texture;
    }

    protected void scale(MythicBeast m, PoseStack stack, float partial) {
        float size = MythicBeast.RENDER_SCALE[this.kind];
        stack.m_85841_(size, size, size);
    }

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer((EntityType)MythicContent.MITE.get(), ChimeMiteRenderer::new);
        e.registerEntityRenderer((EntityType)MythicContent.BOLT.get(), c -> new ThrownItemRenderer(c, 0.6f, true));
        for (int k = 0; k < MythicContent.BEASTS.size(); ++k) {
            int kind = k;
            e.registerEntityRenderer((EntityType)MythicContent.BEASTS.get(k).get(), c -> new MythicBeastRenderer(c, kind));
        }
    }

    @SubscribeEvent
    public static void cutout(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            ItemProperties.register((Item)((Item)ExpanseContent.COMPASS.get()), (ResourceLocation)new ResourceLocation("angle"), (ItemPropertyFunction)new CompassItemPropertyFunction((w, stack, entity) -> stack.m_41782_() && stack.m_41783_().m_128425_("LodestonePos", 10) ? GlobalPos.m_122643_((ResourceKey)ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)(ResourceLocation.m_135820_((String)stack.m_41783_().m_128461_("LodestoneDimension")) == null ? OdysseyRealm.KEY.m_135782_() : new ResourceLocation(stack.m_41783_().m_128461_("LodestoneDimension")))), (BlockPos)NbtUtils.m_129239_((CompoundTag)stack.m_41783_().m_128469_("LodestonePos"))) : null));
            for (RegistryObject<Block> b : Stream.concat(GloomContent.blocks.values().stream(), ExpanseContent.blocks.values().stream()).toList()) {
                Block block = (Block)b.get();
                if (block instanceof BushBlock || block instanceof SugarCaneBlock || block instanceof LeavesBlock || block instanceof LanternBlock) {
                    ItemBlockRenderTypes.setRenderLayer((Block)block, (RenderType)RenderType.m_110463_());
                }
                if (!(block instanceof GlassBlock)) continue;
                ItemBlockRenderTypes.setRenderLayer((Block)block, (RenderType)RenderType.m_110466_());
            }
        });
    }
}

