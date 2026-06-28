package com.github.tier940.infinitychest.core;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.github.tier940.infinitychest.InfinityChestConfigHolder;

public class ItemBlockInfinityChest extends ItemBlock {

    public ItemBlockInfinityChest(BlockInfinityChest block) {
        super(block);
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
        tooltip.add(I18n.format("tooltip.infinitychest.infinitychest.capacity",
                String.format("%,d", InfinityChestConfigHolder.capacity)));
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("BlockEntityTag")) {
            NBTTagCompound teTag = tag.getCompoundTag("BlockEntityTag");
            if (teTag.hasKey("lc:template") && teTag.getLong("lc:count") > 0) {
                ItemStack template = new ItemStack(teTag.getCompoundTag("lc:template"));
                long count = teTag.getLong("lc:count");
                tooltip.add(I18n.format("tooltip.infinitychest.infinitychest.stored",
                        template.getDisplayName(), String.format("%,d", count)));
            }
        }
    }
}
