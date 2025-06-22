package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.ModConfig;
import com.mafuyu404.moveslikemafuyu.network.KnockMessage;
import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import com.mafuyu404.moveslikemafuyu.network.TagMessage;
import com.mafuyu404.moveslikemafuyu.util.PlayerForcedPoseAccess;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SlideEvent {
    private static int TIMER;
    private static int AIR_TIMER;
    private static int COOLDOWN;
    private static int DAP_TIMES;
    private static double timer = TIMER;
    private static int air_timer = AIR_TIMER;
    public static int cooldown = COOLDOWN;
    public static int dap_times = DAP_TIMES;
    public static double dap_motion = 1;
    private static boolean canDap = false;
    private static boolean dap_refreshed = false;
    private static final long Knock_Delay = 500;
    private static long lastKnockTime = 0;
    private static CameraType storedCameraType;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                slideAction(client.player);
                onCollision(client.player);
            }
        });
    }

    public static void onCollision(Player player) {
        if (! ModConfig.enable("SlideKnock"))
            return;
        if (player.isSpectator()) return;
        if (!player.getTags().contains("slide")) return;
        if (System.currentTimeMillis() - lastKnockTime < Knock_Delay)
            return;
        
        // 扩大检测范围，提高击飞检测精度
        List<Entity> AllEntities = player.level().getEntities(player, player.getBoundingBox().inflate(1.0));
        if (AllEntities.isEmpty()) return;
        
        ArrayList<Entity> entities = new ArrayList<>();
        Vec3 lookDirection = player.getLookAngle();
        Vec3 playerPos = player.position();
        
        AllEntities.forEach(entity -> {
            if (!(entity instanceof LivingEntity)) return;
            if (entity == player) return; // 排除自己
            
            Vec3 entityPos = entity.position();
            Vec3 toEntity = entityPos.subtract(playerPos).normalize();
            
            // 改进方向检测逻辑
            double dotProduct = lookDirection.dot(toEntity);
            if (dotProduct > 0.3) { // 在前方大致方向的实体
                entities.add(entity);
            }
        });
        
        if (entities.isEmpty()) return;
        ArrayList<Integer> entityId = new ArrayList<>();
        entities.forEach(entity -> entityId.add(entity.getId()));
        NetworkHandler.sendToServer(NetworkHandler.KNOCK_MESSAGE_ID, new KnockMessage(entityId));
        lastKnockTime = System.currentTimeMillis();
    }

    public static void slideAction(Player player) {
        if (player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;

        if (cooldown > 0 && cooldown <= COOLDOWN) {
            cooldown--;
            double speed = player.getDeltaMovement().length();
            if (!player.isSprinting()) cooldown--;
            if (player.getSpeed() > 0 && player.getSpeed() < 0.15) cooldown--;
            if (speed < 0.05) cooldown--;
            return;
        }

        if (player.getTags().contains("slide")) {
            // 确保姿势持续设置
            PlayerForcedPoseAccess poseAccess = (PlayerForcedPoseAccess) player;
            if (poseAccess.moveslikemafuyu$getForcedPose() != Pose.SWIMMING) {
                poseAccess.moveslikemafuyu$setForcedPose(Pose.SWIMMING);
            }
            
            if (storedCameraType != null) options.setCameraType(storedCameraType);
            if (player.getDeltaMovement().length() < 0.1) {
                cancel(player);
                return;
            }
            options.keyShift.setDown(true);
            Vec3 motion = player.getDeltaMovement();
            Vec3 lookDirection = player.getLookAngle();
            
            if (dap_times == DAP_TIMES && player.isInWater() && !canDap) {
                dap_times--;
                player.setDeltaMovement(
                        motion.add(0, 0.5, 0)
                );
            }
            else if (player.isInWater() && canDap && ModConfig.enable("Dap")) {
                canDap = false;
                dap_times--;
                player.setDeltaMovement(
                        motion.add(0, 0.7 * dap_motion, 0)
                );
                dap_motion *= 0.92;
            }
            if (!player.onGround() && !player.isInWater()) {
                if (dap_times > 0 && dap_times != DAP_TIMES && !canDap) {
                    canDap = true;
                    dap_refreshed = false;
                    double boost = 0.1;
                    player.setDeltaMovement(
                            motion.add(lookDirection.x*boost, 0, lookDirection.z*boost)
                    );
                }
                // 仅增加下坠
                player.setDeltaMovement(
                        player.getDeltaMovement().add(0, -0.025, 0)
                );
                air_timer--;
                if (ModConfig.enable("SlideRepeat")) timer = TIMER;
            } else {
                // 在地上滑行
                air_timer = AIR_TIMER; // 落地重置滞空时间
                if (player.level().getBlockState(player.blockPosition().below()).is(BlockTags.ICE)) timer += 0.5;
                timer--;
                if (player.getDeltaMovement().y > 0) {
                    timer -= 2;
                }
            }
            if (timer <= 0 || air_timer <= 0) {
                cancel(player);
            }
        }
    }

    public static void handleKeyInput(int key, int action) {
        if (Minecraft.getInstance().screen != null) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player == null || player.isSpectator() || action != InputConstants.PRESS) return;

        if (key == options.keyJump.key.getValue()) {
            if (player.getTags().contains("slide")) {
                if (ModConfig.enable("Dap") && canDap && !dap_refreshed) {
                    dap_refreshed = true;
                    dap_times++;
                }
                else cancel(player);
            }
        }

        if (key == options.keyShift.key.getValue()) {
            if (player.isSprinting() && player.onGround() && !player.isInWater() && !player.isFallFlying() && !options.keyJump.isDown()) {
                if (!player.getTags().contains("craw")) startSlide(player);
            }
        }

        if (key == options.keyDown.key.getValue()) {
            if (player.getTags().contains("slide")) {
                cancel(player);
            }
        }
    }

    public static void onConfigLoad() {
        TIMER = ModConfig.ConfigCache.getInt("SlideDuration");
        AIR_TIMER = ModConfig.ConfigCache.getInt("SlideAirDuration");
        COOLDOWN = ModConfig.ConfigCache.getInt("SlideCooldown");
        DAP_TIMES = ModConfig.ConfigCache.getInt("DapTimes");
    }

    public static void startSlide(Player player) {
        Options options = Minecraft.getInstance().options;
        if (! ModConfig.enable("Slide") || cooldown > 0) return;
        timer = TIMER;
        air_timer = AIR_TIMER;
        dap_times = DAP_TIMES;
        canDap = false;
        dap_motion = 1;
        storedCameraType = options.getCameraType();
        
        // 设置滑铲姿势
        PlayerForcedPoseAccess poseAccess = (PlayerForcedPoseAccess) player;
        poseAccess.moveslikemafuyu$setForcedPose(Pose.SWIMMING);
        
        NetworkHandler.sendToServer(NetworkHandler.TAG_MESSAGE_ID, new TagMessage("slide", true));
        player.setSprinting(true);
        player.addTag("slide");
        Vec3 lookDirection = player.getLookAngle();
        double boost = 0.5;
        player.startFallFlying();
        player.setDeltaMovement(
                player.getDeltaMovement().add(lookDirection.x * boost, 0, lookDirection.z * boost)
        );
        options.keyShift.setDown(true);
        player.playSound(
                SoundEvents.GENERIC_SMALL_FALL,
                0.5f,  // 音量
                0.8f   // 音调
        );
    }

    private static void cancel(Player player) {
        // 取消滑铲姿势
        PlayerForcedPoseAccess poseAccess = (PlayerForcedPoseAccess) player;
        poseAccess.moveslikemafuyu$setForcedPose(null);
        
        NetworkHandler.sendToServer(NetworkHandler.TAG_MESSAGE_ID, new TagMessage("slide", false));
        Minecraft.getInstance().options.keyShift.setDown(false);
        player.setShiftKeyDown(false);
        player.stopFallFlying();
        if (!player.getTags().contains("craw")) player.setSprinting(true);
        if (player.isInWater()) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0, motion.z);
        }
        player.removeTag("slide");
        cooldown = COOLDOWN;
    }

    // 这个方法将通过mixin调用
    public static boolean shouldCancelFlyIntoDamage(LivingEntity entity) {
        return entity.getTags().contains("slide");
    }
}