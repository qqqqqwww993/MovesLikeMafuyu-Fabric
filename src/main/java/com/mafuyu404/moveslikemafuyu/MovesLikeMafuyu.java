package com.mafuyu404.moveslikemafuyu;

import com.mafuyu404.moveslikemafuyu.network.NetworkHandler;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MovesLikeMafuyu.MODID)
public class MovesLikeMafuyu {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "moveslikemafuyu";
    public MovesLikeMafuyu() {
        NetworkHandler.register();
    }
}
