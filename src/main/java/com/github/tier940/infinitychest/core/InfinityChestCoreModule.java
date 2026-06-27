package com.github.tier940.infinitychest.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;

import com.github.tier940.infinitychest.BlockInfinityChest;
import com.github.tier940.infinitychest.ItemBlockInfinityChest;
import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.TileInfinityChest;
import com.github.tier940.infinitychest.api.modules.IModule;
import com.github.tier940.infinitychest.api.modules.TModule;
import com.github.tier940.infinitychest.modules.Modules;

@TModule(moduleID = Modules.MODULE_CORE, containerID = Tags.MODID, name = "Infinity Chest Core", coreModule = true)
public class InfinityChestCoreModule implements IModule {

    public static BlockInfinityChest blockInfinityChest;
    public static ItemBlockInfinityChest itemBlockInfinityChest;

    private Logger logger;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        GameRegistry.registerTileEntity(TileInfinityChest.class,
                new ResourceLocation(Tags.MODID, "infinitychest"));
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

    @Override
    public Logger getLogger() {
        return logger;
    }
}
