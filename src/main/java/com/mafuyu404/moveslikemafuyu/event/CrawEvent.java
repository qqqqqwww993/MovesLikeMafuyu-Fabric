package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.Config;
import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import com.mafuyu404.moveslikemafuyu.network.TagMessage;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID, value = Dist.CLIENT)
public class CrawEvent {
    private static final int DOUBLE_PRESS_DELAY = 250; // 毫秒
    private static final int JUMP_TIMER = 250;
    private static long lastShiftPressTime;
    private static long lastJumpPressTime;
    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.isLocalPlayer() || player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;

        if (Config.enable("Craw") && player.getTags().contains("craw") && !player.isSpectator() && !player.getTags().contains("slide")) {
            options.keyShift.setDown(false);
            player.setForcedPose(Pose.SWIMMING);
        }
    }
    @SubscribeEvent
    public static void onAction(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player == null || player.isSpectator()) return;
        if (event.getKey() == options.keyShift.getKey().getValue() && event.getAction() == InputConstants.PRESS) {
            long currentTime = System.currentTimeMillis();
            if (player.getTags().contains("craw")) {
                // 爬行状态下按潜行键就是退出爬行
                cancel(player);
            }
            else if (Config.enable("Craw") && currentTime - lastShiftPressTime < DOUBLE_PRESS_DELAY && player.onGround()) {
                // 不在爬行状态且双击潜行键那就进入爬行状态
                startCraw(player);
            }
            else if (Config.enable("Leap") && player.isSprinting() && currentTime - lastJumpPressTime < JUMP_TIMER && !player.onGround() && !player.isInWater()) {
                // 满足条件就触发飞扑
                Vec3 lookDirection = player.getLookAngle();
                double boost = 0.25;
                player.setDeltaMovement(
                    player.getDeltaMovement().add(lookDirection.x * boost, 0.1, lookDirection.z * boost)
                );
                startCraw(player);
            }
            lastShiftPressTime = currentTime;
        }
        if (event.getKey() == options.keyJump.getKey().getValue() && event.getAction() == InputConstants.PRESS) {
            lastJumpPressTime = System.currentTimeMillis();
            if (Config.enable("JumpCancelCraw")) {
                cancel(player);
                options.keyJump.setDown(false);
            }
        }
        if (event.getKey() == options.keySprint.getKey().getValue() && event.getAction() == InputConstants.PRESS) {
            if (Config.enable("CrawSlide") && player.getTags().contains("craw") && player.onGround()) {
                if (SlideEvent.cooldown <= 0) SlideEvent.startSlide(player);
            }
        }
    }
    public static void startCraw(Player player) {
        if (player.isSpectator()) return;
        NetworkHandler.CHANNEL.sendToServer(new TagMessage("craw", true));
        player.addTag("craw");
        player.setSprinting(false);
    }
    public static void cancel(Player player) {
        NetworkHandler.CHANNEL.sendToServer(new TagMessage("craw", false));
        player.removeTag("craw");
        player.setForcedPose(null);
    }
    @SubscribeEvent
    public static void onConfigLoad(PlayerEvent.PlayerLoggedInEvent event) {

    }
}
