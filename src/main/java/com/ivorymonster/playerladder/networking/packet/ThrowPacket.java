package com.ivorymonster.playerladder.networking.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.ivorymonster.playerladder.PlayerLadder;

public record ThrowPacket() implements CustomPacketPayload {
    public static final Type<ThrowPacket> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(PlayerLadder.MOD_ID, "throw_packet")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ThrowPacket> STREAM_CODEC = StreamCodec.of((value, buf) -> {}, buf -> new ThrowPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}