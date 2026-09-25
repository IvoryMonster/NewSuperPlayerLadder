package com.ivorymonster.playerladder.networking;

import com.ivorymonster.playerladder.networking.packet.ClientExistsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ClientBoundPackets {
    public static  void ClientExistsPayload(ClientExistsPacket clientExistsPacket, ClientPlayNetworking.Context context) {
        //Literally does nothing
    }
}
