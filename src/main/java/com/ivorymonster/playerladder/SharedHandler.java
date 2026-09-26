package com.ivorymonster.playerladder;

import com.google.common.collect.Sets;
import com.ivorymonster.playerladder.networking.packet.ClientExistsPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.IdentifierException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.*;

import static com.ivorymonster.playerladder.PlayerLadder.config;

public class SharedHandler {
    private static final Set<EntityType<?>> entityTypesRideList = Sets.newHashSet();
    private static final Set<TagKey<EntityType<?>>> entityTagsRideList = Sets.newHashSet();
    private static final Set<EntityType<?>> entityTypesPickUpList = Sets.newHashSet();
    private static final Set<TagKey<EntityType<?>>> entityTagsPickUpList = Sets.newHashSet();
    private static final Map<UUID, Vec3[]> motionData = new HashMap<>();
    private static final Map<UUID, Integer> shiftData = new HashMap<>();

    public static InteractionResult rideEntity(Player player, Entity newVehicle, Level level, InteractionHand hand) {
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND && canRideLiving(newVehicle) && player.getItemInHand(hand).isEmpty()) {
            Entity vehicle = getHighestOrSelf(newVehicle, player, config().server.stepUpLimit);

            if(vehicle == null) return InteractionResult.FAIL;
            player.startRiding(vehicle);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult pickUpEntity(Player player, Entity newPassenger, Level level, InteractionHand hand) {
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND && canPickUpLiving(newPassenger) && player.getItemInHand(hand).isEmpty()) {
            Entity vehicle = getHighestOrSelf(player, newPassenger, config().server.pickUpLimit);

            if(vehicle == null) return InteractionResult.FAIL;
            newPassenger.startRiding(vehicle);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult putDownEntity(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        Entity passenger = player.getFirstPassenger();
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND && player.isVehicle() && player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty() && canForceDismount(passenger)) {
            var blockFace = hitResult.getDirection();
            var blockPos = hitResult.getBlockPos();
            var entityWidth = passenger.getBbWidth() / 2.0f;
            var entityHeight = passenger.getBbHeight();
            var offsetX = switch (blockFace) {
                case EAST -> entityWidth + 1;
                case WEST -> -entityWidth;
                default -> 0.5f;
            };
            var offsetY = switch (blockFace) {
                case UP -> 1;
                case DOWN -> -entityHeight;
                default -> 0;
            };
            var offsetZ = switch (blockFace) {
                case SOUTH -> entityWidth + 1;
                case NORTH -> -entityWidth;
                default -> 0.5f;
            };
            passenger.stopRiding();
            passenger.dismountTo(blockPos.getX() + offsetX, blockPos.getY() + offsetY, blockPos.getZ() + offsetZ);
            return InteractionResult.SUCCESS;
        }
        return  InteractionResult.PASS;
    }

    public static InteractionResult throwEntity(Player player, Level level) {
        Entity passenger = player.getFirstPassenger();
        double BlockInteractRange = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
        HitResult blockHitResult = player.pick(BlockInteractRange, 0.0f, false);
        EntityHitResult entityHitResult = getEntityHitResult(player, BlockInteractRange);
        if(!level.isClientSide() && blockHitResult.getType() == HitResult.Type.MISS && (entityHitResult == null || entityHitResult.getEntity().getName().getString().equals(passenger.getName().getString())) && canForceDismount(passenger)) {
            var lookVec = player.getLookAngle();
            var throwStrength = config().server.throwStrength;
            passenger.stopRiding();
            Vec3 PlayerVelocity = motionData.get(player.getUUID())[1];
            passenger.setDeltaMovement(lookVec.x * throwStrength + PlayerVelocity.x, lookVec.y * throwStrength + PlayerVelocity.y, lookVec.z * throwStrength + PlayerVelocity.z);
            if (passenger instanceof ServerPlayer) ((ServerPlayer) passenger).connection.send(new ClientboundSetEntityMotionPacket(passenger));
            return InteractionResult.SUCCESS;
        }
        return  InteractionResult.PASS;
    }

    private static Entity getHighestOrSelf(Entity vehicle, Entity newPassenger, int limit) {
        int count = -1;
        while (vehicle.isVehicle()) {
            count++;
            vehicle = vehicle.getFirstPassenger();
            if(vehicle == newPassenger || count >= limit) return null;
        }
        return vehicle;
    }

    private static boolean canRideLiving(Entity entity) {
        if(entity instanceof Player) {
            if (config().server.requireCrouchToRide && !entity.isCrouching()) return false;
            return config().server.allowRidingPlayers;
        }
        if (config().server.invertRideEntityBlacklist && config().server.allowRidingEntities) {
            return entityTypesRideList.contains(entity.getType()) ||
                    entityTagsRideList.stream().anyMatch(entity::is);
        }
        return config().server.allowRidingEntities &&
                !entityTypesRideList.contains(entity.getType()) &&
                entityTagsRideList.stream().noneMatch(entity::is);
    }

    private static boolean canPickUpLiving(Entity entity) {
        if(entity instanceof Player) {
            return config().server.allowPickingUpPlayers;
        }
        if(config().server.invertPickUpEntityBlacklist && config().server.allowPickingUpEntities) {
            return entityTypesPickUpList.contains(entity.getType()) ||
                    entityTagsPickUpList.stream().anyMatch(entity::is);
        }

        return config().server.allowPickingUpEntities &&
                !entityTypesPickUpList.contains(entity.getType()) &&
                entityTagsPickUpList.stream().noneMatch(entity::is);
    }

    private static boolean canForceDismount(Entity entity) {
        if(entity instanceof Player) {
            return config().server.allowForceDismount;
        }
        return true;
    }

    public static void onPlayerTick(Player player) {
        if(!player.level().isClientSide()) {
            UUID playerUUID = player.getUUID();
            Vec3 currentPosition = player.position();
            Vec3[] playerMotionData = motionData.getOrDefault(playerUUID, new Vec3[]{currentPosition, Vec3.ZERO});
            Vec3 previousPosition = playerMotionData[0];
            Vec3 PlayerVelocity = currentPosition.subtract(previousPosition);
            motionData.put(playerUUID, new Vec3[]{currentPosition, PlayerVelocity});
            if (player.isVehicle() && !ServerPlayNetworking.canSend((ServerPlayer) player, ClientExistsPacket.TYPE)) {
                if (player.isShiftKeyDown()) {
                    int shiftTime = shiftData.getOrDefault(playerUUID, 0);
                    if (shiftTime >= 40) {
                        player.getFirstPassenger().stopRiding();
                        shiftData.put(playerUUID, 0);
                    } else {
                        shiftTime++;
                        shiftData.put(playerUUID, shiftTime);
                    }
                } else {
                    shiftData.put(playerUUID, 0);
                }
            }
        }
    }

    public static void onMount(Entity vehicle, Entity passenger) {
        if(!vehicle.level().isClientSide() && vehicle instanceof Player) {
            ((ServerPlayer)vehicle).connection.send(new ClientboundSetPassengersPacket(vehicle));
        }
    }

    public static void onDismount(Entity vehicle) {
        if(!vehicle.level().isClientSide() && vehicle instanceof Player)
            ((ServerPlayer) vehicle).connection.send(new ClientboundSetPassengersPacket(vehicle));
    }

    public static void onLogOut(Player player) {
        if(player.isPassenger() && player.getVehicle() instanceof Player)
            player.stopRiding();
    }

    public static void onGameModeChange(Player player, GameType gameMode) {
        if(player.isVehicle() && (config().server.dismountOnGameModeChange || gameMode == GameType.SPECTATOR))
            player.getFirstPassenger().stopRiding();
    }

    private static void addExcludedEntityType(String entity, Set<EntityType<?>> typeSet) {
        Identifier id = Identifier.tryParse(entity);
        if(id != null) {
            Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
            entityType.ifPresent(typeSet::add);
        }
    }

    private static void addExcludedEntityTag(String tag, Set<TagKey<EntityType<?>>> tagSet) {
        try {
            TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, Identifier.parse(tag.substring(1)));
            tagSet.add(tagKey);
        } catch (IdentifierException ignored) {}
    }

    public static void setExcludedRideEntries(List<String> entries) {
        entityTagsRideList.clear();
        entityTypesRideList.clear();
        parseBlacklistEntries(entries, entityTypesRideList, entityTagsRideList);
    }

    public static void setExcludedPickUpEntries(List<String> entries) {
        entityTagsPickUpList.clear();
        entityTypesPickUpList.clear();
        parseBlacklistEntries(entries, entityTypesPickUpList, entityTagsPickUpList);
    }

    public static void parseBlacklistEntries(List<String> entries, Set<EntityType<?>> typeSet, Set<TagKey<EntityType<?>>> tagSet) {
        for(String entry : entries) {
            if(entry.isEmpty()) continue;

            if(entry.startsWith("#")) {
                SharedHandler.addExcludedEntityTag(entry, tagSet);
            }else{
                SharedHandler.addExcludedEntityType(entry, typeSet);
            }
        }
    }

    public static EntityHitResult getEntityHitResult(Player player, double range) {
        Vec3 cameraPos = player.getEyePosition(1.0F);
        Vec3 viewVec = player.getViewVector(1.0F);
        Vec3 endPos = cameraPos.add(viewVec.scale(range));
        AABB boundingBox = player.getBoundingBox().expandTowards(viewVec.scale(range)).inflate(1.0D);

        return ProjectileUtil.getEntityHitResult(player, cameraPos, endPos, boundingBox, (entity) -> !entity.isSpectator() && entity.isPickable(), range);
    }
}
