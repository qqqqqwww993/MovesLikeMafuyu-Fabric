package com.mafuyu404.moveslikemafuyu.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class KnockMessage {
    private final int size;
    private final ArrayList<Integer> entityId;

    public KnockMessage(ArrayList<Integer> entityId) {
        this.size = entityId.size();
        this.entityId = entityId;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(this.size);
        this.entityId.forEach(buffer::writeInt);
        return buffer;
    }

    public static KnockMessage decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        ArrayList<Integer> entityId = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            entityId.add(buffer.readInt());
        }
        return new KnockMessage(entityId);
    }

    public static void handleServer(MinecraftServer server, ServerPlayer player,
                                    ServerGamePacketListenerImpl handler,
                                    FriendlyByteBuf buffer,
                                    PacketSender responseSender) {
        KnockMessage message = decode(buffer);
        server.execute(() -> {
            if (player == null) return;
            Level level = player.level();
            
            // 使用玩家的实际运动方向而不是视线方向
            Vec3 playerVelocity = player.getDeltaMovement().normalize();
            if (playerVelocity.length() < 0.1) {
                playerVelocity = player.getLookAngle(); // 备用方案
            }
            
            message.entityId.forEach(id -> {
                Entity entity = level.getEntity(id);
                if (entity == null) return;
                
                Vec3 motion = entity.getDeltaMovement();
                double boost = 2.0; // 增加击飞力度
                
                // 计算击飞方向（从玩家到实体的方向）
                Vec3 knockDirection = entity.position().subtract(player.position()).normalize();
                
                // 应用击飞效果
                entity.setDeltaMovement(motion.add(
                    knockDirection.x * boost, 
                    0.8, // 增加垂直击飞
                    knockDirection.z * boost
                ));
                entity.hurtMarked = true;
                
                // 添加击飞音效
                entity.playSound(net.minecraft.sounds.SoundEvents.PLAYER_ATTACK_KNOCKBACK, 1.0f, 1.0f);
            });
            System.out.println("收到击飞包，实体数量: " + message.entityId.size());
            message.entityId.forEach(id -> {
                Entity entity = level.getEntity(id);
                System.out.println("处理实体ID: " + id + ", 实体: " + (entity != null ? entity.getClass().getSimpleName() : "null"));
            });
            
            // 修正玩家反冲
            Vec3 currentVelocity = player.getDeltaMovement();
            player.setDeltaMovement(currentVelocity.add(
                -playerVelocity.x * 0.2, 
                0, 
                -playerVelocity.z * 0.2
            ));
            player.hurtMarked = true;
        });

    }
}