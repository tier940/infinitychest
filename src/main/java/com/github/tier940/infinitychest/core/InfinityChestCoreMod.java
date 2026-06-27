package com.github.tier940.infinitychest.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import zone.rong.mixinbooter.ILateMixinLoader;

@IFMLLoadingPlugin.Name("InfinityChestCore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class InfinityChestCoreMod implements IFMLLoadingPlugin, ILateMixinLoader {

    @Override
    public String[] getASMTransformerClass() { return new String[0]; }

    @Override
    public String getModContainerClass() { return null; }

    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() { return null; }

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList("mixins.infinitychest.json");
    }
}
