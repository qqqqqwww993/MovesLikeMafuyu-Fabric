package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID, value = Dist.DEDICATED_SERVER)
public class ServerSlide {
    @SubscribeEvent
    public static void slide(TickEvent.PlayerTickEvent event) {
    }
}
