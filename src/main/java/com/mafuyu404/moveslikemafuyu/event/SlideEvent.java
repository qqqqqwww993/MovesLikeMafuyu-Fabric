package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.Config;
import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import com.mafuyu404.moveslikemafuyu.network.KnockMessage;
import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import com.mafuyu404.moveslikemafuyu.network.TagMessage;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID, value = Dist.CLIENT)
public class SlideEvent {
    private static int TIMER;
    private static int AIR_TIMER;
    private static int COOLDOWN;
    private static int DAP_TIMES;
    private static double timer = TIMER;
    private static int air_timer = AIR_TIMER;
    public static int cooldown = 0;
    public static int dap_times = DAP_TIMES;
    private static boolean canDap = false;
    private static final long Knock_Delay = 500;
    private static long lastKnockTime = 0;
    @SubscribeEvent
    public static void slideAction(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.isLocalPlayer() || player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;

        if (cooldown > 0 && cooldown <= COOLDOWN) {
            cooldown--;
            return;
        }

        if (player.getTags().contains("slide")) {
            if (options.keyDown.isDown()) {
                cancel(player);
                return;
            }
            options.keyShift.setDown(true);
            if (player.isInWater() && dap_times > 0 && Config.enable("Dap")) {
                canDap = true;
                player.setDeltaMovement(
                    player.getDeltaMovement().add(0, 0.1, 0)
                );
            }
            if (!player.onGround() && !player.isInWater()) {
                if (canDap) {
                    canDap = false;
                    dap_times--;
                }
                player.setDeltaMovement(
                    player.getDeltaMovement().add(0, -0.025, 0)
                );
                air_timer--;
                if (Config.enable("SlideRepeat")) timer = TIMER;
            } else {
                air_timer = AIR_TIMER; // 落地重置滞空时间
                if (player.level().getBlockState(player.blockPosition().below()).is(BlockTags.ICE)) timer += 0.5;
                timer--;
                if (player.getDeltaMovement().y > 0) {
                    timer -= 2;
                    Vec3 lookDirection = player.getLookAngle();
                    double boost = 0.04;
                    player.setDeltaMovement(
                        player.getDeltaMovement().add(lookDirection.x * boost, 0, lookDirection.z * boost)
                    );
                }
            }
            if (timer <= 0 || air_timer <= 0) {
                cancel(player);
            }
        }
    }
    @SubscribeEvent
    public static void onAction(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player == null || player.isSpectator() || event.getAction() != InputConstants.PRESS) return;
        if (event.getKey() == options.keyJump.getKey().getValue()) {
            if (player.getTags().contains("slide")) {
                cancel(player);
            }
        }
        if (event.getKey() == options.keyShift.getKey().getValue()) {
            if (player.isSprinting() && player.onGround() && !player.isInWater() && !player.isFallFlying() && player.isLocalPlayer()) {
                if (!player.getTags().contains("craw")) startSlide(player);
            }
        }
    }
    @SubscribeEvent
    public static void onCollision(TickEvent.PlayerTickEvent event) {
        if (!Config.enable("SlideKnock")) return;
        Player player = event.player;
        if (!player.isLocalPlayer() || player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;
        if (!player.getTags().contains("slide")) return;
        if (System.currentTimeMillis() - lastKnockTime < Knock_Delay) return;
        List<Entity> AllEntities = player.level().getEntities(player, player.getBoundingBox().inflate(0.1));
        if (AllEntities.isEmpty()) return;
        ArrayList<Entity> entities = new ArrayList<>();
        Vec3 lookDirection = player.getLookAngle();
        AllEntities.forEach(entity -> {
            boolean xCheck = (entity.position().x - player.position().x) / lookDirection.x > 0;
            if (!xCheck) return;
            boolean zCheck = (entity.position().z - player.position().z) / lookDirection.z > 0;
            if (!zCheck) return;
            entities.add(entity);
        });
        if (entities.isEmpty()) return;
        ArrayList<Integer> entityId = new ArrayList<>();
        entities.forEach(entity -> entityId.add(entity.getId()));
        NetworkHandler.CHANNEL.sendToServer(new KnockMessage(entityId));
        lastKnockTime = System.currentTimeMillis();
    }
    @SubscribeEvent
    public static void onConfigLoad(PlayerEvent.PlayerLoggedInEvent event) {
        TIMER = Config.ConfigCache.getInt("SlideDuration");
        AIR_TIMER = Config.ConfigCache.getInt("SlideAirDuration");
        COOLDOWN = Config.ConfigCache.getInt("SlideCooldown");
        DAP_TIMES = Config.ConfigCache.getInt("DapTimes");
    }
    public static void startSlide(Player player) {
        if (!Config.enable("Slide") || cooldown > 0) return;
        Options options = Minecraft.getInstance().options;
        NetworkHandler.CHANNEL.sendToServer(new TagMessage("slide", true));
        player.setSprinting(true);
        player.addTag("slide");
        Vec3 lookDirection = player.getLookAngle();
        double boost = 0.5;
        player.startFallFlying();
        player.setDeltaMovement(
            player.getDeltaMovement().add(lookDirection.x * boost, 0, lookDirection.z * boost)
        );
        options.keyShift.setDown(true);
    }
    private static void cancel(Player player) {
        NetworkHandler.CHANNEL.sendToServer(new TagMessage("slide", false));
        Minecraft.getInstance().options.keyShift.setDown(false);
        player.setShiftKeyDown(false);
        player.stopFallFlying();
        if (!player.getTags().contains("craw")) player.setSprinting(true);
        if (player.isInWater()) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0, motion.z);
        }
        player.removeTag("slide");
        timer = TIMER;
        air_timer = AIR_TIMER;
        cooldown = COOLDOWN;
        dap_times = DAP_TIMES;
    }
}
