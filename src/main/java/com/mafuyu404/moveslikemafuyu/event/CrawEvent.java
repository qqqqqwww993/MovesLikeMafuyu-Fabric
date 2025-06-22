package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.ModConfig;
import com.mafuyu404.moveslikemafuyu.util.PlayerForcedPoseAccess;
import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import com.mafuyu404.moveslikemafuyu.network.TagMessage;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class CrawEvent {
    private static final int DOUBLE_PRESS_DELAY = 250; // 毫秒
    private static final int JUMP_TIMER = 500;
    private static long lastShiftPressTime;
    private static long lastJumpPressTime;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                onClientTick(client.player);
            }
        });
    }

    public static void onClientTick(Player player) {
        if (player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;
        PlayerForcedPoseAccess poseAccess = (PlayerForcedPoseAccess) player;

        if (ModConfig.enable("Craw") && player.getTags().contains("craw") && !player.isSpectator() && !player.getTags().contains("slide")) {
            options.keyShift.setDown(false);
            poseAccess.moveslikemafuyu$setForcedPose(Pose.SWIMMING);
        }
    }

    public static void handleKeyInput(int key, int action) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player == null || player.isSpectator()) return;

        if (key == options.keyShift.key.getValue() && action == InputConstants.PRESS) {
            long currentTime = System.currentTimeMillis();
            if (player.getTags().contains("craw")) {
                // 爬行状态下按潜行键就是退出爬行
                cancelCraw(player);
            }
            else if (ModConfig.enable("Craw") && currentTime - lastShiftPressTime < DOUBLE_PRESS_DELAY && player.onGround()) {
                // 不在爬行状态且双击潜行键那就进入爬行状态
                startCraw(player);
            }
            else if (ModConfig.enable("Leap") && player.isSprinting() && currentTime - lastJumpPressTime < JUMP_TIMER && player.getDeltaMovement().y > 0 && !player.onGround() && !player.isInWater()) {
                // 满足条件就触发飞扑
                // 按下潜行键的时间距离跳跃不能过长lastJumpPressTime
                Vec3 lookDirection = player.getLookAngle();
                double boost = 0.25;
                player.setDeltaMovement(
                        player.getDeltaMovement().add(lookDirection.x * boost, 0.15, lookDirection.z * boost)
                );
                startCraw(player);
                lastJumpPressTime *= 10;
            }
            lastShiftPressTime = currentTime;
        }

        if (key == options.keyJump.key.getValue() && action == InputConstants.PRESS) {
            if (player.onGround()) {
                lastJumpPressTime = System.currentTimeMillis();
            }
            if (ModConfig.enable("JumpCancelCraw") && player.getTags().contains("craw")) {
                cancelCraw(player);
                options.keyJump.setDown(false);
            }
        }

        if (key == options.keySprint.key.getValue() && action == InputConstants.PRESS) {
            if (ModConfig.enable("CrawSlide") && player.getPose() == Pose.SWIMMING && player.onGround()) {
                if (SlideEvent.cooldown <= 0 && !player.getTags().contains("slide")) SlideEvent.startSlide(player);
            }
        }
    }

    public static void startCraw(Player player) {
        if (player.isSpectator()) return;
        NetworkHandler.sendToServer(NetworkHandler.TAG_MESSAGE_ID, new TagMessage("craw", true));
        player.addTag("craw");
        player.setSprinting(false);
    }

    public static void cancelCraw(Player player) {
        NetworkHandler.sendToServer(NetworkHandler.TAG_MESSAGE_ID, new TagMessage("craw", false));
        player.removeTag("craw");
        PlayerForcedPoseAccess poseAccess = (PlayerForcedPoseAccess) player;
        poseAccess.moveslikemafuyu$setForcedPose(null);
    }

    public static void onConfigLoad() {
    }
}