package com.ivorymonster.playerladder.networking.packet;

import com.ivorymonster.playerladder.PlayerLadder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientExistsPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientExistsPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(PlayerLadder.MOD_ID, "client_exists_packet")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientExistsPacket> STREAM_CODEC = StreamCodec.of((value, buf) -> {}, buf -> new ClientExistsPacket());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
