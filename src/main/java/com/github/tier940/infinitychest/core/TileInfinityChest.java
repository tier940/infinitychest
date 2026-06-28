package com.github.tier940.infinitychest.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.cleanroommc.modularui.widgets.slot.SlotGroup;
import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.common.InfinityChestConfigHolder;
import com.github.tier940.infinitychest.slot.SingleSlotInputView;
import com.github.tier940.infinitychest.slot.SingleSlotOutputView;
import com.github.tier940.infinitychest.slot.SingleStackModularSlot;

/**
 * Holds one item template and a long-typed count (vanilla {@link ItemStack#getCount()} is byte-sized
 * so the count is tracked alongside the template). Capacity is sourced from
 * {@link InfinityChestConfigHolder#capacity}.
 */
public class TileInfinityChest extends TileEntity implements IGuiHolder<PosGuiData> {

    private static final String NBT_TEMPLATE = "lc:template";
    private static final String NBT_COUNT = "lc:count";

    private ItemStack template = ItemStack.EMPTY;
    private long count = 0L;
    private final ItemHandler handler = new ItemHandler();

    public ItemStack getTemplate() {
        return template;
    }

    public long getCount() {
        return count;
    }

    public boolean isEmpty() {
        return template.isEmpty() || count <= 0L;
    }

    /** True iff {@code candidate} matches the stored template (or the chest is empty). */
    public boolean accepts(ItemStack candidate) {
        if (candidate.isEmpty()) return false;
        if (template.isEmpty()) return true;
        return template.getItem() == candidate.getItem() && template.getMetadata() == candidate.getMetadata() &&
                ItemStack.areItemStackTagsEqual(template, candidate);
    }

    /** Inserts as much as possible. Returns the leftover ItemStack. */
    public ItemStack insert(ItemStack stack) {
        if (stack.isEmpty()) return stack;
        if (!accepts(stack)) return stack;
        long room = InfinityChestConfigHolder.capacity - count;
        if (room <= 0L) return stack;
        int accepted = (int) Math.min(stack.getCount(), room);
        if (template.isEmpty()) {
            template = stack.copy();
            template.setCount(1);
        }
        count += accepted;
        markDirty();
        ItemStack leftover = stack.copy();
        leftover.setCount(stack.getCount() - accepted);
        return leftover.getCount() <= 0 ? ItemStack.EMPTY : leftover;
    }

    /** Extracts up to {@code maxAmount} items. May return EMPTY. */
    public ItemStack extract(int maxAmount) {
        if (isEmpty() || maxAmount <= 0) return ItemStack.EMPTY;
        int taken = (int) Math.min(maxAmount, Math.min(count, (long) template.getMaxStackSize()));
        ItemStack out = template.copy();
        out.setCount(taken);
        count -= taken;
        if (count <= 0L) {
            count = 0L;
            template = ItemStack.EMPTY;
        }
        markDirty();
        return out;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (!template.isEmpty()) {
            NBTTagCompound t = new NBTTagCompound();
            template.writeToNBT(t);
            nbt.setTag(NBT_TEMPLATE, t);
            nbt.setLong(NBT_COUNT, count);
        }
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey(NBT_TEMPLATE)) {
            template = new ItemStack(nbt.getCompoundTag(NBT_TEMPLATE));
            template.setCount(1);
            count = nbt.getLong(NBT_COUNT);
        } else {
            template = ItemStack.EMPTY;
            count = 0L;
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) handler;
        return super.getCapability(capability, facing);
    }

    /** IItemHandler view: one virtual slot whose count is clamped to a byte-sized ItemStack. */
    private class ItemHandler implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot != 0 || isEmpty()) return ItemStack.EMPTY;
            // Real count (clamped to int) so MUI's transferStackInSlot and Forge's isItemValid
            // round-trip preserve the chest contents.
            int visible = (int) Math.min(count, (long) Integer.MAX_VALUE);
            ItemStack copy = template.copy();
            copy.setCount(visible);
            return copy;
        }

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot != 0 || stack.isEmpty() || !accepts(stack)) return stack;
            long room = InfinityChestConfigHolder.capacity - count;
            if (room <= 0L) return stack;
            int accepted = (int) Math.min(stack.getCount(), room);
            if (!simulate) {
                if (template.isEmpty()) {
                    template = stack.copy();
                    template.setCount(1);
                }
                count += accepted;
                markDirty();
            }
            ItemStack leftover = stack.copy();
            leftover.setCount(stack.getCount() - accepted);
            return leftover.getCount() <= 0 ? ItemStack.EMPTY : leftover;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 || isEmpty() || amount <= 0) return ItemStack.EMPTY;
            int taken = (int) Math.min(amount, Math.min(count, (long) template.getMaxStackSize()));
            ItemStack out = template.copy();
            out.setCount(taken);
            if (!simulate) {
                count -= taken;
                if (count <= 0L) {
                    count = 0L;
                    template = ItemStack.EMPTY;
                }
                markDirty();
            }
            return out;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            if (slot != 0) return;
            // Vanilla-faithful complete replacement so Forge's isItemValid side-effect
            // (setStackInSlot(EMPTY) followed by setStackInSlot(currentStack)) round-trips
            // without corrupting the chest contents.
            if (stack.isEmpty()) {
                template = ItemStack.EMPTY;
                count = 0L;
            } else {
                template = stack.copy();
                template.setCount(1);
                count = Math.min((long) stack.getCount(), InfinityChestConfigHolder.capacity);
            }
            markDirty();
        }
    }

    /** Used by the block on destruction to spawn the held contents as stacks. */
    public IItemHandler getItemHandler() {
        return handler;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        ModularPanel panel = ModularPanel.defaultPanel("infinitychest").size(176, 173);
        // Not singleton: we register three ItemSlot widgets (icon / IN / OUT) that all share
        // handler index 0, and singleton groups reject more than one slot at runtime.
        SlotGroup group = new SlotGroup("infinitychest", 1, SlotGroup.STORAGE_SLOT_PRIO, true);
        syncManager.registerSlotGroup(group);

        // Read-only icon at the very top-left mirroring the stored template.
        ModularSlot iconSlot = new ModularSlot(handler, 0) {

            @Override
            public int getSlotStackLimit() {
                return Integer.MAX_VALUE;
            }
        }.ignoreMaxStackSize(true).slotGroup(group).accessibility(false, false);
        panel.child(new ItemSlot().slot(iconSlot).pos(9, 9));

        // Two info lines to the right of the icon: template name and item count.
        panel.child(new TextWidget<>(IKey.dynamic(this::renderTemplateName)).pos(31, 9).size(140, 10));
        panel.child(new TextWidget<>(IKey.dynamic(this::renderItemCount)).pos(31, 19).size(140, 10));
        panel.child(new TextWidget<>(IKey.dynamic(this::renderLcCount)).pos(31, 29).size(30, 10));

        // IN slot — insert-only, always shows empty.
        ModularSlot inSlot = new SingleStackModularSlot(new SingleSlotInputView(handler), 0) {

            @Override
            public int getSlotStackLimit() {
                return Integer.MAX_VALUE;
            }
        }.ignoreMaxStackSize(true).slotGroup(group).canTake(false);
        panel.child(new TextWidget<>(IKey.lang("gui.infinitychest.slot_in")).pos(116, 45).size(16, 8));
        panel.child(new ItemSlot().slot(inSlot).pos(116, 55));

        // OUT slot — take-only, capped at one max-stack-size chunk.
        ModularSlot outSlot = new SingleStackModularSlot(new SingleSlotOutputView(handler), 0)
                .ignoreMaxStackSize(true).slotGroup(group).canPut(false);
        panel.child(new TextWidget<>(IKey.lang("gui.infinitychest.slot_out")).pos(149, 45).size(20, 8));
        panel.child(new ItemSlot().slot(outSlot).pos(149, 55));

        // Inventory label sits just above the player inventory grid.
        panel.child(new TextWidget<>(IKey.lang("container.inventory")).pos(8, 76).size(80, 10));

        panel.bindPlayerInventory(9);
        panel.onCloseAction(() -> {
            if (world != null && !world.isRemote) {
                world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS,
                        0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
        });
        return panel;
    }

    @Override
    public ModularScreen createScreen(PosGuiData data, ModularPanel mainPanel) {
        return new ModularScreen(Tags.MODID, mainPanel);
    }

    @SideOnly(Side.CLIENT)
    private String renderTemplateName() {
        return isEmpty() ? I18n.format("gui.infinitychest.empty") : template.getDisplayName();
    }

    @SideOnly(Side.CLIENT)
    private String renderItemCount() {
        return I18n.format("gui.infinitychest.item_count", isEmpty() ? "0" : String.format("%,d", count));
    }

    @SideOnly(Side.CLIENT)
    private String renderLcCount() {
        return I18n.format("gui.infinitychest.lc_count", isEmpty() ? 0L : count / 3456L);
    }
}
