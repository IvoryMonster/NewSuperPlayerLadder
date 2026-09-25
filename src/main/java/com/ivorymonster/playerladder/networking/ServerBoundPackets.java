package com.ivorymonster.playerladder.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.ivorymonster.playerladder.networking.packet.ThrowPacket;
import com.ivorymonster.playerladder.SharedHandler;

public class ServerBoundPackets {
    public static void ThrowPayload(ThrowPacket throwPacket, ServerPlayNetworking.Context context) {
        SharedHandler.throwEntity(context.player(), context.player().level());
    }
}
