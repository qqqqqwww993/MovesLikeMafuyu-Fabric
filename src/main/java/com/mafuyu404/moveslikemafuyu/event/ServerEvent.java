package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.ModConfig;
import com.mafuyu404.moveslikemafuyu.util.PlayerForcedPoseAccess;
import com.mafuyu404.moveslikemafuyu.network.ConfigMessage;
import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

public class ServerEvent {
    private static CompoundTag config = new CompoundTag();

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            onConfigLoad(handler.getPlayer());
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                serverSwim(player);
            }
        });
    }

    public static void onConfigLoad(ServerPlayer player) {
        if (config.isEmpty()) config = ModConfig.getAllConfig();
        // 将服务端配置同步给客户端
        NetworkHandler.sendToClient(player, NetworkHandler.CONFIG_MESSAGE_ID, new ConfigMessage(config));
        player.removeTag("slide");
        player.removeTag("craw");
    }

    public static void serverSwim(Player player) {
        // 服务端同步才能改玩家碰撞箱
        if (player.isSpectator()) return;
        PlayerForcedPoseAccess poseAccess = (PlayerForcedPoseAccess) player;
        
        if (ModConfig.enable("ShallowSwimming") && player.isInWater() && player.isSprinting()) {
            poseAccess.moveslikemafuyu$setForcedPose(Pose.SWIMMING);
            return;
        }
        if (player.getTags().contains("craw")) {
            poseAccess.moveslikemafuyu$setForcedPose(Pose.SWIMMING);
            return;
        }
        // 添加滑铲姿势处理
        if (player.getTags().contains("slide")) {
            poseAccess.moveslikemafuyu$setForcedPose(Pose.SWIMMING);
            return;
        }
        if (poseAccess.moveslikemafuyu$getForcedPose() == Pose.SWIMMING) {
            poseAccess.moveslikemafuyu$setForcedPose(null);
        }
    }
}