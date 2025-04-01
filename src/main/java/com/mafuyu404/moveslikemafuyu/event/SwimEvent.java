package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID, value = Dist.CLIENT)
public class SwimEvent {
    private static final int COOLDOWN = 70;
    private static int cooldown = COOLDOWN;
    @SubscribeEvent
    public static void swim(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.isLocalPlayer()) return;
        Options options = Minecraft.getInstance().options;
        if (cooldown > 0 && cooldown <= COOLDOWN) {
            cooldown--;
        }
        if (player.isInWater() && options.keySprint.isDown()) {
            player.setSprinting(true);
            player.setSwimming(true);
        }
        if (!player.isUnderWater() && player.isSwimming()) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.x, 0, motion.z);
        }
    }
    @SubscribeEvent
    public static void swimRush(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player != null && event.getKey() == options.keySprint.getKey().getValue()) {
            if (cooldown <= 0 && player.isSwimming() && event.getAction() == InputConstants.PRESS) {
                cooldown = COOLDOWN;
                Vec3 lookDirection = player.getLookAngle();
                double boost = 0.45;
                player.setDeltaMovement(
                    player.getDeltaMovement().add(lookDirection.x * boost, 0, lookDirection.z * boost)
                );
            }
        }
        if (player != null && event.getKey() == options.keyJump.getKey().getValue()) {
            if (!player.isUnderWater() && player.isSwimming()) {
                if (SlideEvent.cooldown > 0) return;
                player.setSwimming(false);
                player.setSprinting(true);
                Vec3 lookDirection = player.getLookAngle();
                double boost = 0.01;
                player.setDeltaMovement(
                    player.getDeltaMovement().add(lookDirection.x * boost, lookDirection.y * boost, lookDirection.z * boost)
                );
                if (!player.getTags().contains("slide")) SlideEvent.startSlide(player);
            }
        }
    }
}
