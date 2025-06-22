package com.mafuyu404.moveslikemafuyu.network;

import com.mafuyu404.moveslikemafuyu.ModConfig;
import com.mafuyu404.moveslikemafuyu.event.ClimbEvent;
import com.mafuyu404.moveslikemafuyu.event.CrawEvent;
import com.mafuyu404.moveslikemafuyu.event.SlideEvent;
import com.mafuyu404.moveslikemafuyu.event.SwimEvent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigMessage {
    private final CompoundTag config;

    public ConfigMessage(CompoundTag config) {
        this.config = config;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        buffer.writeNbt(this.config);
        return buffer;
    }

    public static ConfigMessage decode(FriendlyByteBuf buffer) {
        return new ConfigMessage(buffer.readNbt());
    }

    public static void handleClient(FriendlyByteBuf buffer) {
        ConfigMessage message = decode(buffer);
        Minecraft.getInstance().execute(() -> {
            System.out.print("MovesLikeMafuyu Config: " + message.config.toString());
            ModConfig.updateConfig(message.config);
            SlideEvent.onConfigLoad();
            SwimEvent.onConfigLoad();
            CrawEvent.onConfigLoad();
            ClimbEvent.onConfigLoad();
        });
    }
}