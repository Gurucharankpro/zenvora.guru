/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.level.block.AmethystClusterBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.BushBlock
 *  net.minecraft.world.level.block.GlassBlock
 *  net.minecraft.world.level.block.LeavesBlock
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.registries.RegistryObject
 */
package dev.shadowsovereign.client;

import dev.shadowsovereign.monolith.MonolithContent;
import java.util.Map;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid="shadowsovereign", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class MonolithClient {
    @SubscribeEvent
    public static void setup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            for (Map.Entry<String, RegistryObject<Block>> entry : MonolithContent.blocks.entrySet()) {
                Block b = (Block)entry.getValue().get();
                if (b instanceof BushBlock || b instanceof LeavesBlock || b instanceof AmethystClusterBlock || entry.getKey().equals("weeping_silver_vines")) {
                    ItemBlockRenderTypes.setRenderLayer((Block)b, (RenderType)RenderType.m_110463_());
                }
                if (!(b instanceof GlassBlock)) continue;
                ItemBlockRenderTypes.setRenderLayer((Block)b, (RenderType)RenderType.m_110466_());
            }
            ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)MonolithContent.SILVER.get()), (RenderType)RenderType.m_110466_());
            ItemBlockRenderTypes.setRenderLayer((Fluid)((Fluid)MonolithContent.SILVER_FLOW.get()), (RenderType)RenderType.m_110466_());
        });
    }
}

