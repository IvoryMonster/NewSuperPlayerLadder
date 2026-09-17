package ru.nern.playerladder.mixin.shared;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.commands.RideCommand;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.nern.playerladder.PlayerLadder;

@Mixin(RideCommand.class)
public class RideCommandMixin {

    @WrapOperation(method = "mount",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;is(Ljava/lang/Object;)Z"))
    private static boolean playerladder$rideExtension(Entity entity, Object EntityType, Operation<Boolean> original) {
        if (PlayerLadder.config().server.rideExtension) {
            return false;
        } else {
            return original.call(entity, EntityType);
        }
    }
}