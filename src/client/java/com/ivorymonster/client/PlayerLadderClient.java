package com.ivorymonster.client;

import com.ivorymonster.client.networking.ClientModPackets;
import net.fabricmc.api.ClientModInitializer;

public class PlayerLadderClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
        ClientModPackets.registerClientPackets();
	}
}