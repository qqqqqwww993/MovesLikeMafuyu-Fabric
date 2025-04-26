package com.zhenshiz.moveslikemafuyu;

import com.mojang.logging.LogUtils;
import com.zhenshiz.moveslikemafuyu.utils.StrUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(MovesLikeMafuyu.MOD_ID)
public class MovesLikeMafuyu {
    public static final String MOD_ID = "moveslikemafuyu";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MovesLikeMafuyu(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC, StrUtil.format("{}_config.toml", MOD_ID));
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public static ResourceLocation ResourceLocationMod(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
