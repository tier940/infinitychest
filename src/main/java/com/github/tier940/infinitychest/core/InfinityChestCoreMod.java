package com.github.tier940.infinitychest.core;

import java.io.*;
import java.util.Map;
import java.util.Properties;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.jetbrains.annotations.Nullable;

import com.github.tier940.infinitychest.Tags;
import com.github.tier940.infinitychest.api.util.ModLog;

public class InfinityChestCoreMod implements IFMLLoadingPlugin {

    static Properties coremodConfig = new Properties();

    @Override
    public String[] getASMTransformerClass() {
        ModLog.logger.info("InfinityChestCoreMod: getASMTransformerClass() called");
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        ModLog.logger.info("InfinityChestCoreMod: getSetupClass() called");
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        ModLog.logger.info("InfinityChestCoreMod: injectData() called");
        File mcLocation = (File) data.get("mcLocation");
        File configDir = new File(mcLocation, "config");
        // noinspection ResultOfMethodCallIgnored
        configDir.mkdir();
        File config = new File(configDir, Tags.MODID + "/InfinityChestCoreMod.properties");
        try (Reader r = new FileReader(config)) {
            coremodConfig.load(r);
        } catch (FileNotFoundException ignored) {
            // not a problem
        } catch (IOException e) {
            ModLog.logger.warn("Can't read coremod config. Proceeding with defaults!", e);
        }
        try (Writer w = new FileWriter(config)) {
            coremodConfig.store(w, "Config file for InfinityChest CoreMod");
        } catch (IOException e) {
            ModLog.logger.warn("Can't write coremod config. Changes may not have been saved!", e);
        }
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
