package com.zhenshiz.moveslikemafuyu.payload.c2s;

import com.zhenshiz.moveslikemafuyu.MovesLikeMafuyu;
import com.zhenshiz.moveslikemafuyu.utils.CodecUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record KnockPayload(int size, List<Integer> entityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<KnockPayload> TYPE = new CustomPacketPayload.Type<>(MovesLikeMafuyu.ResourceLocationMod("knock"));
    public static final StreamCodec<FriendlyByteBuf, KnockPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            KnockPayload::size,
            CodecUtil.INT_LIST,
            KnockPayload::entityId,
            KnockPayload::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
