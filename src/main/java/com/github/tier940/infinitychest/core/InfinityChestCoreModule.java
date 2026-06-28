package com.github.tier940.infinitychest.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.api.ModValues;
import com.github.tier940.infinitychest.api.modules.IModule;
import com.github.tier940.infinitychest.api.modules.TModule;
import com.github.tier940.infinitychest.common.CommonProxy;
import com.github.tier940.infinitychest.modules.Modules;

@TModule(
         moduleID = Modules.MODULE_CORE,
         containerID = ModValues.MODID,
         name = "Infinity Chest Core",
         description = "Core of InfinityChest",
         coreModule = true)
public class InfinityChestCoreModule implements IModule {

    public static final Logger logger = LogManager.getLogger(Tags.MODNAME + " Core");

    public static BlockInfinityChest blockInfinityChest;
    public static ItemBlockInfinityChest itemBlockInfinityChest;

    @SidedProxy(modId = ModValues.MODID,
                clientSide = "com.github.tier940.infinitychest.client.ClientProxy",
                serverSide = "com.github.tier940.infinitychest.common.CommonProxy")
    public static CommonProxy proxy;

    @Override
    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public void construction(FMLConstructionEvent event) {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        logger.info("Hello World!");
        GameRegistry.registerTileEntity(TileInfinityChest.class,
                new ResourceLocation(Tags.MODID, "infinitychest"));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Override
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        blockInfinityChest = new BlockInfinityChest();
        blockInfinityChest.setRegistryName(Tags.MODID, "infinitychest");
        blockInfinityChest.setTranslationKey(Tags.MODID + ".infinitychest");
        blockInfinityChest.setCreativeTab(CreativeTabs.DECORATIONS);
        event.getRegistry().register(blockInfinityChest);
    }

    @Override
    public void registerItems(RegistryEvent.Register<Item> event) {
        itemBlockInfinityChest = new ItemBlockInfinityChest(blockInfinityChest);
        itemBlockInfinityChest.setRegistryName(blockInfinityChest.getRegistryName());
        event.getRegistry().register(itemBlockInfinityChest);
    }
}
