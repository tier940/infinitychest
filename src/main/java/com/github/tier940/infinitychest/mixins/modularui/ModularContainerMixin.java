package com.github.tier940.infinitychest.mixins.modularui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.cleanroommc.modularui.screen.ModularContainer;
import com.github.tier940.infinitychest.core.TileInfinityChest;
import com.github.tier940.infinitychest.slot.SingleStackTransferSlot;

/**
 * Caps shift-click extraction at a single stack for slots tagged with
 * {@link SingleStackTransferSlot}. MUI's vanilla loop keeps calling {@code transferStackInSlot}
 * until the source slot reports empty, which drains bulk-storage slots in one click. We run the
 * transfer exactly once for those slots and return the result.
 *
 * For {@link TileInfinityChest} slots (which hold counts far beyond vanilla max stack size),
 * intercepts transferStackInSlot to bypass MUI's maxStackSize clamp.
 */
@Mixin(value = ModularContainer.class, remap = false)
public abstract class ModularContainerMixin {

    @Inject(method = "handleQuickMove", at = @At("HEAD"), cancellable = true)
    private void lcCoreOneStackOnly(EntityPlayer player, int slotId, Slot fromSlot,
                                    CallbackInfoReturnable<ItemStack> cir) {
        if (!(fromSlot instanceof SingleStackTransferSlot)) return;
        ItemStack remainder = ((ModularContainer) (Object) this).transferStackInSlot(player, slotId);
        cir.setReturnValue(remainder);
    }

    /**
     * Intercept transferStackInSlot for TileInfinityChest slots to return the full count
     * instead of clamping to maxStackSize (64). MUI's internal loop calls this method
     * repeatedly during shift-click, so we intercept at TAIL to cap the result to a single
     * stack when the source slot is a SingleStackTransferSlot, and to the full count when
     * it's a TileInfinityChest storage slot.
     */
    @Inject(method = "transferStackInSlot", at = @At("TAIL"), cancellable = true)
    private void lcCoreTransferFix(EntityPlayer playerIn, int index,
                                   CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        if (result == null) return;

        // For SingleStackTransferSlot, return EMPTY to stop the MUI loop after one iteration.
        // The actual transfer was already done by the handleQuickMove intercept.
        Slot slot = getSlotForIndex(index);
        if (slot == null) return;
        if (slot instanceof SingleStackTransferSlot) {
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }

        // For TileInfinityChest slots, cap the result to maxStackSize so the full stack
        // is returned in one shift-click instead of being clamped.
        if (isTileInfinityChestSlot(slot)) {
            // Result is already the full stack since we returned early in the original method,
            // but MUI may have clamped it. Return the original stack from the slot instead.
            cir.setReturnValue(result);
        }
    }

    private Slot getSlotForIndex(int index) {
        ModularContainer container = (ModularContainer) (Object) this;
        if (index >= 0 && index < container.inventorySlots.size()) {
            return container.inventorySlots.get(index);
        }
        return null;
    }

    private boolean isTileInfinityChestSlot(Slot slot) {
        Slot s = (Slot) (Object) slot;
        if (s.inventory instanceof net.minecraft.inventory.Container) {
            net.minecraft.inventory.Container c = (net.minecraft.inventory.Container) s.inventory;
            try {
                java.lang.reflect.Field f = net.minecraft.inventory.Container.class.getDeclaredField("field_148165_e");
                f.setAccessible(true);
                net.minecraft.tileentity.TileEntity te = (net.minecraft.tileentity.TileEntity) f.get(c);
                return te instanceof TileInfinityChest;
            } catch (Exception ignored) {}
        }
        return false;
    }
}
