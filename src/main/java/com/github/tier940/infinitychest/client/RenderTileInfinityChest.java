package com.github.tier940.infinitychest.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.github.tier940.infinitychest.core.TileInfinityChest;

@SideOnly(Side.CLIENT)
public class RenderTileInfinityChest extends TileEntitySpecialRenderer<TileInfinityChest> {

    @Override
    public void render(TileInfinityChest te, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (te.isEmpty()) return;
        ItemStack template = te.getTemplate();

        float angle = (te.getWorld().getTotalWorldTime() + partialTicks) * 2.0f;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.375, z + 0.5);
        GlStateManager.rotate(angle, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(1.15f, 1.15f, 1.15f);

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItem(template, ItemCameraTransforms.TransformType.FIXED);
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }
}
