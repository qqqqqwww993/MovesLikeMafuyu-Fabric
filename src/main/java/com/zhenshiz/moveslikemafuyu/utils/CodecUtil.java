package com.zhenshiz.moveslikemafuyu.utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CodecUtil {
    /**
     * 实现了 StreamCodec 接口，用于编码和解码类型为 {@code Vector4f} 的数据.
     */
    public static final StreamCodec<FriendlyByteBuf, List<Integer>> INT_LIST = new StreamCodec<>() {
        public @NotNull List<Integer> decode(FriendlyByteBuf buf) {
            List<Integer> result = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                result.add(buf.readInt());
            }
            return result;
        }

        public void encode(FriendlyByteBuf buf, List<Integer> list) {
            buf.writeInt(list.size());
            list.forEach(buf::writeInt);
        }
    };
}
