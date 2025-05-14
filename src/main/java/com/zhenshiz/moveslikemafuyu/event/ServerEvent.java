package com.zhenshiz.moveslikemafuyu.event;

import com.zhenshiz.moveslikemafuyu.Config;
import com.zhenshiz.moveslikemafuyu.MovesLikeMafuyu;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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
    public static void serverSwim(PlayerTickEvent.Post event) {
        // 服务端同步才能改玩家碰撞箱
        Player player = event.getEntity();
        if (player.isLocalPlayer() || player.isSpectator()) return;
        if (Config.ENABLE_SHALLOW_SWIMMING.get() && player.isInWater() && player.isSprinting()) {
            player.setForcedPose(Pose.SWIMMING);
            return;
        }
        if (player.getTags().contains("craw")) {
            player.setForcedPose(Pose.SWIMMING);
            return;
        }
        if (player.getForcedPose() == Pose.SWIMMING) player.setForcedPose(null);
    }
}
