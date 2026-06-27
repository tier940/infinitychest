package com.github.tier940.infinitychest;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.github.tier940.infinitychest.core.InfinityChestCoreModule;

@Mod(modid = Tags.MODID, name = Tags.MODNAME, version = Tags.VERSION, useMetadata = true,
        acceptedMinecraftVersions = "[1.12,1.13)")
@Mod.EventBusSubscriber(modid = Tags.MODID)
public class InfinityChestMod {

    private static final InfinityChestCoreModule CORE = new InfinityChestCoreModule();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        CORE.preInit(event);
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void clientInit(FMLInitializationEvent event) {
        net.minecraftforge.fml.client.registry.ClientRegistry.bindTileEntitySpecialRenderer(
                TileInfinityChest.class,
                new com.github.tier940.infinitychest.client.RenderTileInfinityChest());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        CORE.registerBlocks(event);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        CORE.registerItems(event);
    }
}
