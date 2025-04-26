package com.zhenshiz.moveslikemafuyu;

import com.zhenshiz.moveslikemafuyu.event.ClimbEvent;
import com.zhenshiz.moveslikemafuyu.event.SlideEvent;
import com.zhenshiz.moveslikemafuyu.event.SwimEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = MovesLikeMafuyu.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ModConfigSpec CONFIG_SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_SLIDE;
    public static final ModConfigSpec.BooleanValue ENABLE_DAP;
    public static final ModConfigSpec.BooleanValue ENABLE_SLIDE_REPEAT;
    public static final ModConfigSpec.BooleanValue ENABLE_SLIDE_KNOCK;
    public static final ModConfigSpec.BooleanValue ENABLE_CLIMB;
    public static final ModConfigSpec.BooleanValue ENABLE_CLIMB_JUMP;
    public static final ModConfigSpec.BooleanValue ENABLE_FALLING_RESCUE;
    public static final ModConfigSpec.BooleanValue ENABLE_SHALLOW_SWIMMING;
    public static final ModConfigSpec.BooleanValue ENABLE_SWIMMING_BOOST;
    public static final ModConfigSpec.BooleanValue ENABLE_FREESTYLE;
    public static final ModConfigSpec.BooleanValue ENABLE_SWIMMING_PUSH;
    public static final ModConfigSpec.BooleanValue ENABLE_CRAW;
    public static final ModConfigSpec.BooleanValue ENABLE_CRAW_SLIDE;
    public static final ModConfigSpec.BooleanValue ENABLE_LEAP;
    public static final ModConfigSpec.BooleanValue ENABLE_JUMP_CANCEL_CRAW;
    public static final ModConfigSpec.IntValue SLIDE_DURATION;
    public static final ModConfigSpec.IntValue SLIDE_AIR_DURATION;
    public static final ModConfigSpec.IntValue DAP_TIMES;
    public static final ModConfigSpec.IntValue SLIDE_COOLDOWN;
    public static final ModConfigSpec.IntValue CLIMB_JUMP_COOLDOWN;
    public static final ModConfigSpec.IntValue SWIMMING_BOOST_COOLDOWN;
    public static final ModConfigSpec.IntValue SWIMMING_BOOST_AIR_COST;

    static {
        ModConfigSpec.Builder CONFIG_BUILDER = new ModConfigSpec.Builder();
        CONFIG_BUILDER.push("slide_setting");
        ENABLE_SLIDE = CONFIG_BUILDER.define("enable_slide", true);
        ENABLE_DAP = CONFIG_BUILDER.define("enable_dap", true);
        ENABLE_SLIDE_REPEAT = CONFIG_BUILDER.define("enable_slide_repeat", true);
        ENABLE_SLIDE_KNOCK = CONFIG_BUILDER.define("enable_slide_knock", true);
        CONFIG_BUILDER.pop();
        CONFIG_BUILDER.push("climb_setting");
        ENABLE_CLIMB = CONFIG_BUILDER.define("enable_climb", true);
        ENABLE_CLIMB_JUMP = CONFIG_BUILDER.define("enable_climb_jump", true);
        ENABLE_FALLING_RESCUE = CONFIG_BUILDER.define("enable_falling_rescue", true);
        CONFIG_BUILDER.pop();
        CONFIG_BUILDER.push("swimming_setting");
        ENABLE_SHALLOW_SWIMMING = CONFIG_BUILDER.define("enable_shallow_swimming", true);
        ENABLE_SWIMMING_BOOST = CONFIG_BUILDER.define("enable_swimming_boost", true);
        ENABLE_FREESTYLE = CONFIG_BUILDER.define("enable_free_style", true);
        ENABLE_SWIMMING_PUSH = CONFIG_BUILDER.define("enable_swimming_push", true);
        CONFIG_BUILDER.pop();
        CONFIG_BUILDER.push("craw_setting");
        ENABLE_CRAW = CONFIG_BUILDER.define("enable_craw", true);
        ENABLE_CRAW_SLIDE = CONFIG_BUILDER.define("enable_craw_slide", true);
        ENABLE_LEAP = CONFIG_BUILDER.define("enable_leap", true);
        ENABLE_JUMP_CANCEL_CRAW = CONFIG_BUILDER.define("enable_jump_cancel_crawl", false);
        CONFIG_BUILDER.pop();
        CONFIG_BUILDER.push("attribute_setting");
        SLIDE_DURATION = CONFIG_BUILDER.defineInRange("slide_duration", 25, 0, Integer.MAX_VALUE);
        SLIDE_AIR_DURATION = CONFIG_BUILDER.defineInRange("slide_air_duration", 2, 0, Integer.MAX_VALUE);
        DAP_TIMES = CONFIG_BUILDER.defineInRange("dap_times", 2, 0, Integer.MAX_VALUE);
        SLIDE_COOLDOWN = CONFIG_BUILDER.defineInRange("slide_cooldown", 60, 0, Integer.MAX_VALUE);
        CLIMB_JUMP_COOLDOWN = CONFIG_BUILDER.defineInRange("climb_jump_cooldown", 60, 0, Integer.MAX_VALUE);
        SWIMMING_BOOST_COOLDOWN = CONFIG_BUILDER.defineInRange("swimming_boost_cooldown", 60, 0, Integer.MAX_VALUE);
        SWIMMING_BOOST_AIR_COST = CONFIG_BUILDER.defineInRange("swimming_boost_air_cost", 30, 0, 300);
        CONFIG_BUILDER.pop();
        CONFIG_SPEC = CONFIG_BUILDER.build();
    }

    @SubscribeEvent
    public static void resetConfig(ModConfigEvent.Reloading event) {
        ClimbEvent.COOLDOWN = Config.CLIMB_JUMP_COOLDOWN.get();
        SlideEvent.TIMER = Config.SLIDE_DURATION.get();
        SlideEvent.AIR_TIMER = Config.SLIDE_AIR_DURATION.get();
        SlideEvent.COOLDOWN = Config.SLIDE_COOLDOWN.get();
        SlideEvent.DAP_TIMES = Config.DAP_TIMES.get();
        SwimEvent.COOLDOWN = Config.SWIMMING_BOOST_COOLDOWN.get();
        SwimEvent.AIR_COST = Config.SWIMMING_BOOST_AIR_COST.get();
    }
}
