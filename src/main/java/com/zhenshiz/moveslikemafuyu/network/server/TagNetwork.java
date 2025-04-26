package com.zhenshiz.moveslikemafuyu.network.server;

import com.zhenshiz.moveslikemafuyu.MovesLikeMafuyu;
import com.zhenshiz.moveslikemafuyu.payload.c2s.TagPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MovesLikeMafuyu.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class TagNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MovesLikeMafuyu.MOD_ID);
        registrar.playBidirectional(TagPayload.TYPE, TagPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        (payload, context) -> {
                        },
                        (payload, context) -> {
                            ServerPlayer player = (ServerPlayer) context.player();
                            String tag = payload.tag();
                            if (payload.state()) {
                                if (!player.getTags().contains(tag)) {
                                    player.addTag(tag);
                                }
                            } else {
                                player.removeTag(tag);
                                if (tag.equals("craw")) {
                                    player.setForcedPose(null);
                                }
                            }
                        }
                )
        );
    }
}
