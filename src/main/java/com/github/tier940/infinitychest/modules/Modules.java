package com.github.tier940.infinitychest.modules;

import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.api.modules.IModuleContainer;

public class Modules implements IModuleContainer {

    public static final String MODULE_CORE = "core";

    @Override
    public String getID() {
        return Tags.MODID;
    }
}
