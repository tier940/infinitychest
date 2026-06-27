package com.github.tier940.infinitychest.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.core.InfinityChestCoreModule;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Tags.MODID)
public class InfinityChestClientEvents {

    private static final ModelResourceLocation CHEST_MRL = new ModelResourceLocation(
            new ResourceLocation(Tags.MODID, "infinitychest"), "inventory");

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(InfinityChestCoreModule.itemBlockInfinityChest, 0, CHEST_MRL);
    }
}
