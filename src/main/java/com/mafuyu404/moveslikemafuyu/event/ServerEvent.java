package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.Config;
import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import com.mafuyu404.moveslikemafuyu.network.ConfigMessage;
import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID)
public class ServerEvent {
    private static CompoundTag config = new CompoundTag();
    @SubscribeEvent
    public static void onConfigLoad(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.isLocalPlayer()) return;
        if (config.isEmpty()) config = Config.getAllConfig();
        NetworkHandler.sendToClient((ServerPlayer) event.getEntity(), new ConfigMessage(config));
        player.removeTag("slide");
        player.removeTag("craw");
    }
    @SubscribeEvent
    public static void playerTagHandler(TickEvent.ServerTickEvent event) {
        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if (player.getTags().contains("craw") && !player.isSpectator()) {
                player.setForcedPose(Pose.SWIMMING);
            }
        });
    }
}
