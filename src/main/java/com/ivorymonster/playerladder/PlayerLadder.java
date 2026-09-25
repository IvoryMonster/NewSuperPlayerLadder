package com.ivorymonster.playerladder;

import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.nern.fconfiglib.v1.ConfigManager;
import ru.nern.fconfiglib.v1.api.annotations.validation.ConfigValidators;
import ru.nern.fconfiglib.v1.api.annotations.validation.ValidateField;
import ru.nern.fconfiglib.v1.json.JsonConfigManager;
import ru.nern.fconfiglib.v1.log.Sl4jLoggerWrapper;
import ru.nern.fconfiglib.v1.utils.ValueReference;
import ru.nern.fconfiglib.v1.validation.FieldValidator;
import ru.nern.fconfiglib.v1.validation.FieldsConfigValidator;
import ru.nern.fconfiglib.v1.validation.VersionConfigValidator;
import com.ivorymonster.playerladder.networking.ModPackets;

import java.util.List;

public class PlayerLadder implements ModInitializer {
	public static final String MOD_ID = "playerladder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static ConfigManager<Config, JsonObject> configManager = JsonConfigManager
			.builderOf(Config.class)
			.modId(MOD_ID)
			.logger(Sl4jLoggerWrapper.createFrom(LOGGER))
			.version(2)
			.create();

	@Override
	public void onInitialize() {
		configManager.init();

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
				SharedHandler.onLogOut(handler.player));

		UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (!level.isClientSide() && player.isShiftKeyDown() && config().server.shiftClickMode != ClickMode.DO_NOTHING) {
                switch (PlayerLadder.config().server.shiftClickMode) {
                    case RIDE -> SharedHandler.rideEntity(player, entity, level, hand);
                    case PICK_UP -> SharedHandler.pickUpEntity(player, entity, level, hand);
                }
            } else if (!level.isClientSide() && config().server.clickMode != ClickMode.DO_NOTHING) {
                switch (PlayerLadder.config().server.clickMode) {
                    case RIDE -> SharedHandler.rideEntity(player, entity, level, hand);
                    case PICK_UP -> SharedHandler.pickUpEntity(player, entity, level, hand);
                }
            }
            return InteractionResult.PASS;
        });

        UseBlockCallback.EVENT.register((SharedHandler::putDownEntity));

        ModPackets.registerServerPackets();
	}

	public static Config config() {
		return configManager.config();
	}

	@ConfigValidators({
			VersionConfigValidator.class,
			FieldsConfigValidator.class,
	})
	public static class Config {
		public Server server = new Server();
		public Client client = new Client();

		public static class Server {
			public ClickMode clickMode = ClickMode.RIDE;
            public ClickMode shiftClickMode = ClickMode.PICK_UP;
			public int pickUpLimit = 16;
			public int stepUpLimit = 16;
            public double throwStrength = 0.75;
            public boolean allowForceDismount = true;
			public boolean allowRidingPlayers = true;
            public boolean allowPickingUpPlayers = true;
            public boolean requireCrouchToRide = true;
            public boolean allowRidingEntities = false;
            public boolean allowPickingUpEntities = false;
			public boolean dismountOnGameModeChange = false;

			@ValidateField(RideEntitiesValidator.class)
			public List<String> rideEntityBlacklist = List.of(
                    "minecraft:wither",
                    "minecraft:ender_dragon",
                    "minecraft:minecart",
                    "#minecraft:boat",
                    "#minecraft:can_equip_saddle",
                    "minecraft:wolf",
                    "minecraft:cat",
                    "minecraft:warden",
                    "minecraft:parrot",
                    "#c:item_frames",
                    "minecraft:cushion"
            );

            @ValidateField(PickUpEntitiesValidator.class)
            public List<String> pickUpEntityBlacklist = List.of(
                    "minecraft:wither",
                    "minecraft:ender_dragon",
                    "minecraft:warden",
                    "#c:item_frames",
                    "minecraft:cushion"
            );

            public boolean invertRideEntityBlacklist = false;
            public boolean invertPickUpEntityBlacklist = false;

            public boolean rideExtension = true;
		}

		public static class Client {
			public boolean allowInteractions = true;
		}
	}

	public enum ClickMode {
		RIDE,
		PICK_UP,
		DO_NOTHING
	}

	static class RideEntitiesValidator implements FieldValidator<List<String>, Config> {

		@Override
		public void validate(ValueReference<List<String>> reference, Config instance) {
			SharedHandler.setExcludedRideEntries(reference.get());
		}
	}

    static class PickUpEntitiesValidator implements FieldValidator<List<String>, Config> {

        @Override
        public void validate(ValueReference<List<String>> reference, Config instance) {
            SharedHandler.setExcludedPickUpEntries(reference.get());
        }
    }
}

