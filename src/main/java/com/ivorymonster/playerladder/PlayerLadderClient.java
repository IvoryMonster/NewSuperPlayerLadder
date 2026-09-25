package com.ivorymonster.playerladder;

import com.ivorymonster.playerladder.networking.ModPackets;
import net.fabricmc.api.ClientModInitializer;

public class PlayerLadderClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModPackets.registerClientPackets();
    }
}
