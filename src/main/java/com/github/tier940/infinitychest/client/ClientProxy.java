package com.github.tier940.infinitychest.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.common.CommonProxy;
import com.github.tier940.infinitychest.core.InfinityChestCoreModule;
import com.github.tier940.infinitychest.core.TileInfinityChest;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileInfinityChest.class, new RenderTileInfinityChest());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                InfinityChestCoreModule.itemBlockInfinityChest, 0,
                new ModelResourceLocation(new ResourceLocation(Tags.MODID, "infinitychest"), "inventory"));
    }
}
