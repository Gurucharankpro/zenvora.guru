/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 */
package dev.shadowsovereign.sentinel;

import dev.shadowsovereign.discovery.DiscoveryContent;
import dev.shadowsovereign.mastery.MasteryCatalog;
import dev.shadowsovereign.mastery.MasteryService;
import dev.shadowsovereign.odyssey.GuideService;
import dev.shadowsovereign.odyssey.OdysseyState;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public final class MasteryRewards {
    public static List<ItemStack> reward(MasteryCatalog.Stage stage) {
        if (stage.chapter().equals("awakening") && stage.id().endsWith(".8")) {
            return List.of(new ItemStack((ItemLike)DiscoveryContent.SIGIL.get()));
        }
        if (stage.chapter().startsWith("hunt.")) {
            if (stage.id().endsWith(".4")) {
                return List.of(new ItemStack((ItemLike)DiscoveryContent.SIGIL.get()));
            }
            if (stage.id().endsWith(".8")) {
                return List.of(new ItemStack((ItemLike)Items.f_42415_, 2), new ItemStack((ItemLike)Items.f_42677_, 4));
            }
            return List.of();
        }
        if (stage.chapter().equals("graduation")) {
            return List.of(new ItemStack((ItemLike)DiscoveryContent.SIGIL.get(), stage.id().equals("master.oath") ? 3 : 1), new ItemStack((ItemLike)Items.f_42415_, stage.id().equals("master.oath") ? 8 : 1));
        }
        List<MasteryCatalog.Stage> chapter = MasteryCatalog.chapter(stage.chapter());
        if (!chapter.isEmpty() && chapter.get(chapter.size() - 1).id().equals(stage.id())) {
            return List.of(new ItemStack((ItemLike)DiscoveryContent.SIGIL.get()), new ItemStack((ItemLike)Items.f_42416_, 4));
        }
        return List.of();
    }

    private static CompoundTag mailbox(ServerPlayer p) {
        CompoundTag c = GuideService.state((ServerPlayer)p).camp;
        if (!c.m_128425_("MasteryMail", 10)) {
            c.m_128365_("MasteryMail", (Tag)new CompoundTag());
        }
        return c.m_128469_("MasteryMail");
    }

    public static void enqueue(ServerPlayer p, MasteryCatalog.Stage stage) {
        OdysseyState state = GuideService.state(p);
        if (!MasteryService.claimed(p, stage.id()) || state.grants.contains("itemreward:" + stage.id())) {
            return;
        }
        List<ItemStack> items = MasteryRewards.reward(stage);
        if (items.isEmpty() || !state.grant("itemreward:" + stage.id())) {
            return;
        }
        ListTag list = new ListTag();
        for (ItemStack item : items) {
            list.add((Object)item.m_41739_(new CompoundTag()));
        }
        MasteryRewards.mailbox(p).m_128365_(stage.id(), (Tag)list);
        GuideService.dirty(p);
    }

    public static void reconcile(ServerPlayer p) {
        for (MasteryCatalog.Stage stage : MasteryCatalog.STAGES) {
            MasteryRewards.enqueue(p, stage);
        }
    }

    public static int collect(ServerPlayer p) {
        MasteryRewards.reconcile(p);
        CompoundTag mail = MasteryRewards.mailbox(p);
        int count = 0;
        for (String key : List.copyOf(mail.m_128431_())) {
            ListTag remaining = new ListTag();
            for (Tag value : mail.m_128437_(key, 10)) {
                ItemStack stack = ItemStack.m_41712_((CompoundTag)((CompoundTag)value));
                int before = stack.m_41613_();
                p.m_150109_().m_36054_(stack);
                count += before - stack.m_41613_();
                if (stack.m_41619_()) continue;
                remaining.add((Object)stack.m_41739_(new CompoundTag()));
            }
            if (remaining.isEmpty()) {
                mail.m_128473_(key);
                continue;
            }
            mail.m_128365_(key, (Tag)remaining);
        }
        p.m_150109_().m_6596_();
        GuideService.dirty(p);
        return count;
    }

    public static int pending(ServerPlayer p) {
        int count = 0;
        CompoundTag mail = MasteryRewards.mailbox(p);
        for (String key : mail.m_128431_()) {
            for (Tag n : mail.m_128437_(key, 10)) {
                count += ItemStack.m_41712_((CompoundTag)((CompoundTag)n)).m_41613_();
            }
        }
        return count;
    }
}

