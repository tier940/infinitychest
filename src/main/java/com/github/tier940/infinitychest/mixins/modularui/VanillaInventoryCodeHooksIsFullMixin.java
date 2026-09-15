package com.github.tier940.infinitychest.mixins.modularui;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.tier940.infinitychest.core.TileInfinityChest;

/**
 * Mixin into {@link net.minecraftforge.items.VanillaInventoryCodeHooks} to make
 * {@code isFull} return false for TileInfinityChest handlers.
 *
 * We inject at RETURN and cancel the original method, returning false for infinity chests.
 */
@Mixin(net.minecraftforge.items.VanillaInventoryCodeHooks.class)
public class VanillaInventoryCodeHooksIsFullMixin {

    /**
     * Inject at the head of isFull. If the handler is a TileInfinityChest,
     * return false immediately using Cancellable.
     *
     * We target the method by signature: it takes a single IItemHandler parameter
     * and returns boolean. Using method = "*" to work around missing MCP mapping
     * for this external Forge jar.
     */
    @Inject(method = "isFull", at = @At("HEAD"), cancellable = true, remap = false)
    private static void injectIsFull(IItemHandler itemHandler, CallbackInfoReturnable<Boolean> cir) {
        if (isInfinityChestHandler(itemHandler)) {
            cir.setReturnValue(false);
        }
    }

    private static boolean isInfinityChestHandler(IItemHandler handler) {
        if (handler == null) return false;

        // Check if this is TileInfinityChest's inner ItemHandler class
        Class<?> clazz = handler.getClass();
        if (clazz.getEnclosingClass() != null &&
                clazz.getEnclosingClass().equals(TileInfinityChest.class)) {
            return true;
        }

        // Check wrapped handlers
        if (handler instanceof CombinedInvWrapper) {
            try {
                CombinedInvWrapper wrapper = (CombinedInvWrapper) handler;
                java.lang.reflect.Field f = CombinedInvWrapper.class.getDeclaredField("itemHandler");
                f.setAccessible(true);
                net.minecraftforge.items.IItemHandlerModifiable[] handlers = (net.minecraftforge.items.IItemHandlerModifiable[]) f
                        .get(wrapper);
                for (net.minecraftforge.items.IItemHandlerModifiable h : handlers) {
                    if (isInfinityChestHandler(h)) return true;
                }
            } catch (Exception ignored) {
                // ignore
            }
        }

        return false;
    }
}
