package com.mafuyu404.moveslikemafuyu.network;

import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHandler {
    public static final ResourceLocation TAG_MESSAGE_ID = new ResourceLocation(MovesLikeMafuyu.MODID, "tag_message");
    public static final ResourceLocation KNOCK_MESSAGE_ID = new ResourceLocation(MovesLikeMafuyu.MODID, "knock_message");
    public static final ResourceLocation CONFIG_MESSAGE_ID = new ResourceLocation(MovesLikeMafuyu.MODID, "config_message");

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(TAG_MESSAGE_ID, TagMessage::handleServer);
        ServerPlayNetworking.registerGlobalReceiver(KNOCK_MESSAGE_ID, KnockMessage::handleServer);
    }

    public static void sendToClient(ServerPlayer player, ResourceLocation id, Object packet) {
        if (packet instanceof ConfigMessage configMessage) {
            ServerPlayNetworking.send(player, id, configMessage.encode());
        }
    }

    public static void sendToServer(ResourceLocation id, Object packet) {
        if (packet instanceof TagMessage tagMessage) {
            ClientPlayNetworking.send(id, tagMessage.encode());
        } else if (packet instanceof KnockMessage knockMessage) {
            ClientPlayNetworking.send(id, knockMessage.encode());
        }
    }
}