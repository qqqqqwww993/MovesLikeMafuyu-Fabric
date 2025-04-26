package com.zhenshiz.moveslikemafuyu.event;

import com.zhenshiz.moveslikemafuyu.MovesLikeMafuyu;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = MovesLikeMafuyu.MOD_ID)
public class ServerEvent {

    @SubscribeEvent
    public static void onConfigLoad(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.isLocalPlayer()) return;
        player.removeTag("slide");
        player.removeTag("craw");
    }

    @SubscribeEvent
    public static void playerTagHandler(ServerTickEvent.Pre event) {
        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player.getTags().contains("craw") && !player.isSpectator()) {
                player.setForcedPose(Pose.SWIMMING);
            }
        });
    }
}
