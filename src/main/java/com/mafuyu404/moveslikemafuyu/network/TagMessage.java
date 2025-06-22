package com.mafuyu404.moveslikemafuyu.network;

import com.mafuyu404.moveslikemafuyu.util.PlayerForcedPoseAccess;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class TagMessage {
    private final String tag;
    private final boolean state;

    public TagMessage(String tag, boolean state) {
        this.tag = tag;
        this.state = state;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeUtf(this.tag);
        buffer.writeBoolean(this.state);
        return buffer;
    }

    public static TagMessage decode(FriendlyByteBuf buffer) {
        return new TagMessage(buffer.readUtf(), buffer.readBoolean());
    }

    public static void handleServer(MinecraftServer server, ServerPlayer player,
                                    ServerGamePacketListenerImpl handler,
                                    FriendlyByteBuf buffer,
                                    PacketSender responseSender) {
        TagMessage message = decode(buffer);
        server.execute(() -> {
            if (message.state) {
                if (!player.getTags().contains(message.tag)) {
                    player.addTag(message.tag);
                }
            } else {
                player.removeTag(message.tag);
                if (message.tag.equals("craw")) {
                    PlayerForcedPoseAccess poseAccess = (PlayerForcedPoseAccess) player;
                    poseAccess.moveslikemafuyu$setForcedPose(null);
                }
            }
        });
    }
}