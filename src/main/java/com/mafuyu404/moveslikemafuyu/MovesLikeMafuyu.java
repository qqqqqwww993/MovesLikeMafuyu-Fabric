package com.mafuyu404.moveslikemafuyu;


import com.mafuyu404.moveslikemafuyu.event.ServerEvent;
import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;

public class MovesLikeMafuyu implements ModInitializer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "moveslikemafuyu";
    @Override
    public void onInitialize () {
        ModConfig.register();
        NetworkHandler.registerServerReceivers();
        ServerEvent.init();
    }
}
