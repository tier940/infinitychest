package com.github.tier940.infinitychest.core;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.github.tier940.infinitychest.common.InfinityChestConfigHolder;

public class ItemBlockInfinityChest extends ItemBlock {

    public ItemBlockInfinityChest(BlockInfinityChest block) {
        super(block);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        NBTTagCompound teTag = tag != null && tag.hasKey("BlockEntityTag") ? tag.getCompoundTag("BlockEntityTag") : null;
        if (teTag == null || !teTag.hasKey("lc:template") || teTag.getLong("lc:count") <= 0) {
            return super.getItemStackDisplayName(stack);
        }
        ItemStack template = new ItemStack(teTag.getCompoundTag("lc:template"));
        return super.getItemStackDisplayName(stack) + " (" + template.getDisplayName() + ")";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null || !tag.hasKey("BlockEntityTag")) return false;
        NBTTagCompound teTag = tag.getCompoundTag("BlockEntityTag");
        return teTag.hasKey("lc:template") && teTag.getLong("lc:count") > 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound tag = stack.getTagCompound();
        NBTTagCompound teTag = tag != null && tag.hasKey("BlockEntityTag") ? tag.getCompoundTag("BlockEntityTag") : null;
        boolean hasContents = teTag != null && teTag.hasKey("lc:template") && teTag.getLong("lc:count") > 0;

        if (!hasContents) {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.infinitychest.infinitychest.empty"));
        } else {
            ItemStack template = new ItemStack(teTag.getCompoundTag("lc:template"));
            long count = teTag.getLong("lc:count");
            tooltip.add(TextFormatting.WHITE + template.getDisplayName());
            tooltip.add(TextFormatting.YELLOW.toString() + count + TextFormatting.GRAY + " / " +
                    InfinityChestConfigHolder.capacity);
            tooltip.add(TextFormatting.YELLOW + I18n.format("tooltip.infinitychest.infinitychest.lc", count / LcUnit.LC_SIZE));
        }

        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("tooltip.infinitychest.infinitychest.usage.right_click"));
            tooltip.add(TextFormatting.DARK_GRAY +
                    I18n.format("tooltip.infinitychest.infinitychest.usage.right_click_holding"));
            tooltip.add(TextFormatting.DARK_GRAY +
                    I18n.format("tooltip.infinitychest.infinitychest.usage.sneak_right_click"));
        } else {
            tooltip.add(TextFormatting.DARK_GRAY + I18n.format("tooltip.infinitychest.infinitychest.shift_hint"));
        }
    }
}
