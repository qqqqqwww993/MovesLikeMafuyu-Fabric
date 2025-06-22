package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.ModConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Timer;
import java.util.TimerTask;

@Environment(EnvType.CLIENT)
public class SwimEvent {
    private static int COOLDOWN;
    private static int AIR_COST;
    private static int cooldown = COOLDOWN;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                swim(client.player);
            }
        });
    }

    public static void swim(Player player) {
        if (player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;
        if (cooldown > 0 && cooldown <= COOLDOWN) {
            cooldown--;
        }
        if (ModConfig.enable("ShallowSwimming") && player.isInWater() && options.keySprint.isDown()) {
            player.setSprinting(true);
            player.setSwimming(true);
        }
        if (ModConfig.enable("Freestyle") && !player.isUnderWater() && player.isSwimming()) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0, motion.z);
        }
    }

    public static void handleKeyInput(int key, int action) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player == null || player.isSpectator()) return;

        if (ModConfig.enable("SwimmingBoost") && key == options.keySprint.key.getValue()) {
            if (cooldown <= 0 && player.isSwimming() && action == InputConstants.PRESS) {
                cooldown = COOLDOWN;
                Vec3 lookDirection = player.getLookAngle();
                double boost = 0.4;
                player.setDeltaMovement(
                        player.getDeltaMovement().add(lookDirection.x * boost, lookDirection.y * boost, lookDirection.z * boost)
                );
                player.setAirSupply(player.getAirSupply() - AIR_COST);
                // 播放水声
                player.playSound(
                        SoundEvents.AMBIENT_UNDERWATER_ENTER,
                        0.9f,  // 音量
                        0.8f   // 音调
                );
            }
        }

        if (key == options.keyJump.key.getValue()) {
            if (!player.isUnderWater() && player.isInWater() && player.isSwimming()) {
                if (SlideEvent.cooldown > 0 || ! ModConfig.enable("SwimmingPush") || action != InputConstants.PRESS || player.getTags().contains("slide")) return;
                player.setSwimming(false);
                if (!player.getTags().contains("slide")) {
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            SlideEvent.startSlide(player);
                        }
                    },110);
                }
            }
        }
    }

    public static void onConfigLoad() {
        COOLDOWN = ModConfig.ConfigCache.getInt("SwimmingBoostCooldown");
        AIR_COST = ModConfig.ConfigCache.getInt("SwimmingBoostAirCost");
    }
}