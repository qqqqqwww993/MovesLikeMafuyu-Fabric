package com.mafuyu404.moveslikemafuyu;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.nbt.CompoundTag;

@Config(name = "moveslikemafuyu")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("slide")
    @ConfigEntry.Gui.TransitiveObject
    public SlideConfig slide = new SlideConfig();

    @ConfigEntry.Category("climb")
    @ConfigEntry.Gui.TransitiveObject
    public ClimbConfig climb = new ClimbConfig();

    @ConfigEntry.Category("swimming")
    @ConfigEntry.Gui.TransitiveObject
    public SwimmingConfig swimming = new SwimmingConfig();

    @ConfigEntry.Category("craw")
    @ConfigEntry.Gui.TransitiveObject
    public CrawConfig craw = new CrawConfig();

    @ConfigEntry.Category("attribute")
    @ConfigEntry.Gui.TransitiveObject
    public AttributeConfig attribute = new AttributeConfig();

    public static class SlideConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean enableSlide = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableDap = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableSlideRepeat = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableSlideKnock = true;
    }

    public static class ClimbConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean enableClimb = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableClimbJump = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableFallingRescue = true;
    }

    public static class SwimmingConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean enableShallowSwimming = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableSwimmingBoost = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableFreestyle = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableSwimmingPush = true;
    }

    public static class CrawConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean enableCraw = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableCrawSlide = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableLeap = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableJumpCancelCraw = false;
    }

    public static class AttributeConfig {
        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int slideDuration = 25;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int slideAirDuration = 30;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
        public int dapTimes = 2;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 200)
        public int slideCooldown = 60;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 200)
        public int climbJumpCooldown = 60;

        @ConfigEntry.BoundedDiscrete(min = 0, max = 200)
        public int swimmingBoostCooldown = 60;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
        public int swimmingBoostAirCost = 30;
    }

    private static ModConfig instance;

    public static void register() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        instance = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static ModConfig getInstance() {
        if (instance == null) {
            instance = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        }
        return instance;
    }

    // 保持与原有代码的兼容性
    public static CompoundTag ConfigCache = new CompoundTag();

    public static void updateConfig(CompoundTag config) {
        ConfigCache = config;
    }

    public static CompoundTag getAllConfig() {
        ModConfig config = getInstance();
        CompoundTag tag = new CompoundTag();

        // Slide settings
        tag.putBoolean("enableSlide", config.slide.enableSlide);
        tag.putBoolean("enableDap", config.slide.enableDap);
        tag.putBoolean("enableSlideRepeat", config.slide.enableSlideRepeat);
        tag.putBoolean("enableSlideKnock", config.slide.enableSlideKnock);

        // Climb settings
        tag.putBoolean("enableClimb", config.climb.enableClimb);
        tag.putBoolean("enableClimbJump", config.climb.enableClimbJump);
        tag.putBoolean("enableFallingRescue", config.climb.enableFallingRescue);

        // Swimming settings
        tag.putBoolean("enableShallowSwimming", config.swimming.enableShallowSwimming);
        tag.putBoolean("enableSwimmingBoost", config.swimming.enableSwimmingBoost);
        tag.putBoolean("enableFreestyle", config.swimming.enableFreestyle);
        tag.putBoolean("enableSwimmingPush", config.swimming.enableSwimmingPush);

        // Craw settings
        tag.putBoolean("enableCraw", config.craw.enableCraw);
        tag.putBoolean("enableCrawSlide", config.craw.enableCrawSlide);
        tag.putBoolean("enableLeap", config.craw.enableLeap);
        tag.putBoolean("enableJumpCancelCraw", config.craw.enableJumpCancelCraw);

        // Attribute settings
        tag.putInt("SlideDuration", config.attribute.slideDuration);
        tag.putInt("SlideAirDuration", config.attribute.slideAirDuration);
        tag.putInt("DapTimes", config.attribute.dapTimes);
        tag.putInt("SlideCooldown", config.attribute.slideCooldown);
        tag.putInt("ClimbJumpCooldown", config.attribute.climbJumpCooldown);
        tag.putInt("SwimmingBoostCooldown", config.attribute.swimmingBoostCooldown);
        tag.putInt("SwimmingBoostAirCost", config.attribute.swimmingBoostAirCost);

        return tag;
    }

    public static boolean enable(String key) {
        ModConfig config = getInstance();
        return switch (key) {
            case "Slide" -> config.slide.enableSlide;
            case "Dap" -> config.slide.enableDap;
            case "SlideRepeat" -> config.slide.enableSlideRepeat;
            case "SlideKnock" -> config.slide.enableSlideKnock;
            case "Climb" -> config.climb.enableClimb;
            case "ClimbJump" -> config.climb.enableClimbJump;
            case "FallingRescue" -> config.climb.enableFallingRescue;
            case "ShallowSwimming" -> config.swimming.enableShallowSwimming;
            case "SwimmingBoost" -> config.swimming.enableSwimmingBoost;
            case "Freestyle" -> config.swimming.enableFreestyle;
            case "SwimmingPush" -> config.swimming.enableSwimmingPush;
            case "Craw" -> config.craw.enableCraw;
            case "CrawSlide" -> config.craw.enableCrawSlide;
            case "Leap" -> config.craw.enableLeap;
            case "JumpCancelCraw" -> config.craw.enableJumpCancelCraw;
            default -> false;
        };
    }
}