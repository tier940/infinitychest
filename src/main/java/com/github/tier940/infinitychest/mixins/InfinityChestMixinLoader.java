package com.github.tier940.infinitychest.mixins;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.minecraftforge.fml.common.Loader;

import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.api.util.ModLog;
import com.github.tier940.infinitychest.api.util.Mods;
import com.google.common.collect.ImmutableMap;

import zone.rong.mixinbooter.ILateMixinLoader;

@SuppressWarnings("unused")
public class InfinityChestMixinLoader implements ILateMixinLoader {

    public static final Map<String, Boolean> modMixinsConfig = new ImmutableMap.Builder<String, Boolean>()
            .put(Mods.Names.MODULRAUI, true)
            .build();

    @SuppressWarnings("SimplifyStreamApiCallChains")
    @Override
    public List<String> getMixinConfigs() {
        return modMixinsConfig.keySet().stream().map(mod -> "mixins." + Tags.MODID + "." + mod + ".json")
                .collect(Collectors.toList());
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        String[] parts = mixinConfig.split("\\.");

        if (parts.length != 4 && parts.length != 5) {
            ModLog.logger.fatal("Mixin Config Check Failed! Invalid Length.");
            ModLog.logger.fatal("Mixin Config: {}", mixinConfig);
            return true;
        }

        if (!Objects.equals(parts[1], Tags.MODID)) {
            ModLog.logger.error(
                    "Non InfinityChest Mixin Found in Mixin Queue. This is probably an error. Skipping...");
            ModLog.logger.error("Mixin Config: {}", mixinConfig);
            return true;
        }

        if (!Loader.isModLoaded(parts[2])) {
            ModLog.logger.error(
                    "Mod '{}' is not loaded. If this is a normal InfinityChest instance, this is probably an error.",
                    parts[2]);
            ModLog.logger.error("Not Loading Mixin Config {}", mixinConfig);
            return false;
        }

        if (!modMixinsConfig.containsKey(parts[2]) || !modMixinsConfig.get(parts[2])) {
            ModLog.logger.info("Integration for Mod '{}' is not enabled, or does not exist.", parts[2]);
            ModLog.logger.info("Not Loading Mixin Config {}", mixinConfig);
            return false;
        }

        return true;
    }
}
