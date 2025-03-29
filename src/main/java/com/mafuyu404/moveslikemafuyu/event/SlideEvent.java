package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID, value = Dist.CLIENT)
public class SlideEvent {
    private static final int TIMER = 30;
    private static final int AIR_TIMER = 30;
    private static final int COOLDOWN = 90;
    private static int timer = TIMER;
    private static int air_timer = AIR_TIMER;
    private static int cooldown = 0;
    public static boolean enable = false;
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (cooldown > 0 && cooldown <= COOLDOWN) {
            cooldown--;
        }
        if (enable) {
            player.setShiftKeyDown(false);
            if (!player.onGround() && !player.isInWater()) {
                air_timer--;
                timer = TIMER;
            }
            else {
                air_timer = AIR_TIMER; // 落地重置滞空时间
                timer--;
            }
            if (timer <= 0 || air_timer < 0) {
                timer = TIMER;
                air_timer = AIR_TIMER;
                player.stopFallFlying();
                player.setSprinting(true);
                enable = false;
                cooldown = COOLDOWN;
            }
            return;
        }

        if (player.isSprinting() && player.isShiftKeyDown() && player.onGround() && !player.isInWater() && cooldown == 0) {
            enable = true;
            Vec3 lookDirection = player.getLookAngle();
            double boost = 0.45;
            player.startFallFlying();
            player.setDeltaMovement(
                player.getDeltaMovement().add(lookDirection.x * boost, 0, lookDirection.z * boost)
            );
            player.setShiftKeyDown(false);
        }
    }

    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (enable) {
                timer = TIMER;
                air_timer = AIR_TIMER;
                player.stopFallFlying();
                player.setSprinting(true);
                enable = false;
                cooldown = COOLDOWN;
            }
        }
    }
}
