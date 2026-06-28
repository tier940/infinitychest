package com.github.tier940.infinitychest.modules;

import com.github.tier940.infinitychest.api.ModValues;
import com.github.tier940.infinitychest.api.modules.IModuleContainer;
import com.github.tier940.infinitychest.api.modules.ModuleContainer;

@ModuleContainer
public class Modules implements IModuleContainer {

    public static final String MODULE_CORE = "core";
    public static final String MODULE_TOOLS = "tools";
    public static final String MODULE_INTEGRATION = "integration";

    @Override
    public String getID() {
        return ModValues.MODID;
    }
}
