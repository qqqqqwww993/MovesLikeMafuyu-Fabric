package com.zhenshiz.moveslikemafuyu.payload.c2s;

import com.zhenshiz.moveslikemafuyu.MovesLikeMafuyu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record TagPayload(String tag, boolean state) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TagPayload> TYPE = new CustomPacketPayload.Type<>(MovesLikeMafuyu.ResourceLocationMod("tag"));
    public static final StreamCodec<FriendlyByteBuf, TagPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            TagPayload::tag,
            ByteBufCodecs.BOOL,
            TagPayload::state,
            TagPayload::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
