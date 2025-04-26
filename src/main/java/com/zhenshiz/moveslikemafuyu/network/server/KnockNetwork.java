package com.zhenshiz.moveslikemafuyu.network.server;

import com.zhenshiz.moveslikemafuyu.MovesLikeMafuyu;
import com.zhenshiz.moveslikemafuyu.payload.c2s.KnockPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MovesLikeMafuyu.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class KnockNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MovesLikeMafuyu.MOD_ID);
        registrar.playBidirectional(KnockPayload.TYPE, KnockPayload.CODEC,
                new DirectionalPayloadHandler<>(
                        (payload, context) -> {
                        },
                        (payload, context) -> {
                            ServerPlayer player = (ServerPlayer) context.player();
                            Level level = player.level();
                            Vec3 playerMotion = player.getLookAngle();
                            payload.entityId().forEach(id -> {
                                Entity entity = level.getEntity(id);
                                if (entity == null) return;
                                Vec3 motion = entity.getDeltaMovement();
                                double boost = 1;
                                entity.setDeltaMovement(motion.add(playerMotion.x * boost, 0.7, playerMotion.z * boost));
                                entity.hurtMarked = true;
                            });
                            player.setDeltaMovement(playerMotion.add(-playerMotion.x, 0, -playerMotion.z));
                            player.hurtMarked = true;
                        }
                )
        );
    }
}
