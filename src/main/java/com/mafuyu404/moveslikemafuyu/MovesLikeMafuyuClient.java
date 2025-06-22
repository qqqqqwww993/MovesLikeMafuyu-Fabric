package com.mafuyu404.moveslikemafuyu;

import com.mafuyu404.moveslikemafuyu.event.*;
import net.fabricmc.api.ClientModInitializer;

public class MovesLikeMafuyuClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClimbEvent.init();
        CrawEvent.init();
        SwimEvent.init();
        SlideEvent.init();
    }
}