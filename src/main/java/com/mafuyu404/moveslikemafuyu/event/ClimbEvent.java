package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.Config;
import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID, value = Dist.CLIENT)
public class ClimbEvent {
    private static int COOLDOWN;
    private static long cooldown = COOLDOWN;
    public static boolean Falling = true;
    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!player.isLocalPlayer() || player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;
        if (cooldown > 0 && cooldown <= COOLDOWN) {
            cooldown--;
        }
        if (!Config.enable("FallingRescue")) {
            Falling = false;
            return;
        }
        double verticalSpeed = player.getDeltaMovement().y;
        Falling = verticalSpeed < 0 && verticalSpeed > -1 && !player.onGround() && !player.isInWater() && !player.isPassenger();
        if (Falling && player.onClimbable() && options.keyShift.isDown()) {
            player.setDeltaMovement(0, 0, 0);
        }
    }
    @SubscribeEvent
    public static void jumpOnClimbable(InputEvent.Key event) {
        if (Minecraft.getInstance().screen != null) return;
        if (!Config.enable("ClimbJump")) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player == null || player.isSpectator()) return;
        if (cooldown <= 0 && event.getKey() == options.keyJump.getKey().getValue()) {
            if (player.isShiftKeyDown() && player.onClimbable()) {
                player.jumpFromGround();
                cooldown = COOLDOWN;
            }
        }
    }
    @SubscribeEvent
    public static void onConfigLoad(PlayerEvent.PlayerLoggedInEvent event) {
        COOLDOWN = Config.ConfigCache.getInt("ClimbJumpCooldown");
    }
}
