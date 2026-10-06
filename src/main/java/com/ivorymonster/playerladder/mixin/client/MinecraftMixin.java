package com.ivorymonster.playerladder.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.ivorymonster.playerladder.networking.packet.ThrowPacket;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "startUseItem", at = @At("HEAD"))
	private void init(CallbackInfo info) {
        Player player = Minecraft.getInstance().player;
        HitResult hit = Minecraft.getInstance().hitResult;
        var hand = player.getMainHandItem();
        if (hand.isEmpty() && player.isShiftKeyDown() && player.isVehicle() && (hit.getType() == HitResult.Type.MISS || (hit.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hit).getEntity().isPassengerOfSameVehicle(player)))) {
            ClientPlayNetworking.send(new ThrowPacket());
        }
	}
}