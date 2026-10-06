package com.ivorymonster.playerladder.networking;

import com.ivorymonster.playerladder.networking.packet.ClientExistsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientBoundPackets {
    public static  void ClientExistsPayload(ClientExistsPacket clientExistsPacket, ClientPlayNetworking.Context context) {
        //Literally does nothing
    }
}
