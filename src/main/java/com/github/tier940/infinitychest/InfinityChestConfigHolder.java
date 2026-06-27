package com.github.tier940.infinitychest;

import net.minecraftforge.common.config.Config;

@Config.LangKey(Tags.MODID + ".config.infinitychest")
@Config(modid = Tags.MODID, name = Tags.MODID + "/infinitychest", category = "InfinityChest")
public class InfinityChestConfigHolder {

    @Config.Comment({ "Maximum number of items a single InfinityChest can hold.",
            "Default: 2000000000" })
    @Config.RangeInt(min = 64, max = Integer.MAX_VALUE)
    @Config.RequiresMcRestart
    public static int capacity = 2_000_000_000;
}
