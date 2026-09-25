package com.ivorymonster.playerladder.networking;

import com.ivorymonster.playerladder.networking.packet.ClientExistsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import com.ivorymonster.playerladder.networking.packet.ThrowPacket;

public class ModPackets {
    private static void registerServerBound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(ThrowPacket.TYPE, ThrowPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ThrowPacket.TYPE, ServerBoundPackets::ThrowPayload);
    }

    private static void registerClientBound(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        registry.register(ClientExistsPacket.TYPE, ClientExistsPacket.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ClientExistsPacket.TYPE, ClientBoundPackets::ClientExistsPayload);
    }

    public static void registerServerPackets() {
        registerServerBound(PayloadTypeRegistry.serverboundPlay());
    }

    public static void registerClientPackets() {
        registerClientBound(PayloadTypeRegistry.clientboundPlay());
    }

}
