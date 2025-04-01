package com.mafuyu404.moveslikemafuyu.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SlideMessage {
    private final boolean slide;

    public SlideMessage(boolean slide) {
        this.slide = slide;
    }

    public static void encode(SlideMessage msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.slide);
    }

    public static SlideMessage decode(FriendlyByteBuf buffer) {
        return new SlideMessage(buffer.readBoolean());
    }

    public static void handle(SlideMessage msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (msg.slide) {
                if (!player.getTags().contains("slide")) player.addTag("slide");
            }
            else player.removeTag("slide");
        });
        ctx.get().setPacketHandled(true);
    }
}
