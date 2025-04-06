package com.mafuyu404.moveslikemafuyu.network;

import com.mafuyu404.moveslikemafuyu.Config;
import com.mafuyu404.moveslikemafuyu.event.ClimbEvent;
import com.mafuyu404.moveslikemafuyu.event.CrawEvent;
import com.mafuyu404.moveslikemafuyu.event.SlideEvent;
import com.mafuyu404.moveslikemafuyu.event.SwimEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfigMessage {
    private final CompoundTag config;

    public ConfigMessage(CompoundTag config) {
        this.config = config;
    }

    public static void encode(ConfigMessage msg, FriendlyByteBuf buffer) {
        buffer.writeNbt(msg.config);
    }

    public static ConfigMessage decode(FriendlyByteBuf buffer) {
        return new ConfigMessage(buffer.readNbt());
    }

    public static void handle(ConfigMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            System.out.print("MovesLikeMafuyu Config: " + msg.config.toString());
            Config.updateConfig(msg.config);
            PlayerEvent.PlayerLoggedInEvent event = new PlayerEvent.PlayerLoggedInEvent(ctx.get().getSender());
            SlideEvent.onConfigLoad(event);
            SwimEvent.onConfigLoad(event);
            CrawEvent.onConfigLoad(event);
            ClimbEvent.onConfigLoad(event);
        });
        ctx.get().setPacketHandled(true);
    }
}
