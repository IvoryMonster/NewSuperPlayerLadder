package com.ivorymonster.playerladder.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.ivorymonster.playerladder.PlayerLadder;
import com.ivorymonster.playerladder.SharedHandler;

import static com.ivorymonster.playerladder.PlayerLadder.config;


public class ClothConfigIntegration {
    public static Screen generateConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.playerladder.config"));

        builder.setSavingRunnable(() -> PlayerLadder.configManager.save(PlayerLadder.configManager.getConfigFile()));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory serverCategory = builder.getOrCreateCategory(Component.translatable("server.playerladder.config"));

        serverCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("rightClickMode.playerladder.config"), PlayerLadder.ClickMode.class, config().server.clickMode)
                .setTooltip(Component.translatable("rightClickMode.playerladder.description"))
                .setSaveConsumer(clickMode -> config().server.clickMode = clickMode).build());

        serverCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("shiftClickMode.playerladder.config"), PlayerLadder.ClickMode.class, config().server.shiftClickMode)
                .setTooltip(Component.translatable("shiftClickMode.playerladder.description"))
                .setSaveConsumer(clickMode -> config().server.shiftClickMode = clickMode).build());

        serverCategory.addEntry(entryBuilder.startIntField(Component.translatable("pickUpLimit.playerladder.config"), config().server.pickUpLimit)
                .setMin(1)
                .setTooltip(Component.translatable("pickUpLimit.playerladder.description"))
                .setSaveConsumer(value -> config().server.pickUpLimit = value).build());

        serverCategory.addEntry(entryBuilder.startIntField(Component.translatable("stepUpLimit.playerladder.config"), config().server.stepUpLimit)
                .setMin(1)
                .setTooltip(Component.translatable("stepUpLimit.playerladder.description"))
                .setSaveConsumer(value -> config().server.stepUpLimit = value).build());

        serverCategory.addEntry(entryBuilder.startDoubleField(Component.translatable("throwStrength.playerladder.config"), config().server.throwStrength)
                .setMin(0)
                .setMax(100.0)
                .setTooltip(Component.translatable("throwStrength.playerladder.description"))
                .setSaveConsumer(value -> config().server.throwStrength = value).build());

        serverCategory.addEntry((entryBuilder.startBooleanToggle(Component.translatable("allowForceDismount.playerladder.config"), config().server.allowForceDismount)
                .setTooltip(Component.translatable("allowForceDismount.playerladder.description"))
                .setSaveConsumer(value -> config().server.allowForceDismount = value).build()));

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowRidingPlayers.playerladder.config"), config().server.allowRidingPlayers)
                .setTooltip(Component.translatable("allowRidingPlayers.playerladder.description"))
                .setSaveConsumer(value -> config().server.allowRidingPlayers = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowPickingUpPlayers.playerladder.config"), config().server.allowPickingUpPlayers)
                .setTooltip(Component.translatable("allowPickingUpPlayers.playerladder.description"))
                .setSaveConsumer(value -> config().server.allowPickingUpPlayers = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("requireCrouchToRide.playerladder.config"), config().server.requireCrouchToRide)
                .setTooltip(Component.translatable("requireCrouchToRide.playerladder.description"))
                .setSaveConsumer(value -> config().server.requireCrouchToRide = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowRidingEntities.playerladder.config"), config().server.allowRidingEntities)
                .setTooltip(Component.translatable("allowRidingEntities.playerladder.description"))
                .setSaveConsumer(value -> config().server.allowRidingEntities = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowPickingUpEntities.playerladder.config"), config().server.allowRidingEntities)
                .setTooltip(Component.translatable("allowPickingUpEntities.playerladder.description"))
                .setSaveConsumer(value -> config().server.allowPickingUpEntities = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("dismountOnGameModeChange.playerladder.config"), config().server.dismountOnGameModeChange)
                .setTooltip(Component.translatable("dismountOnGameModeChange.playerladder.description"))
                .setSaveConsumer(value -> config().server.dismountOnGameModeChange = value).build());

        serverCategory.addEntry(entryBuilder.startStrList(Component.translatable("rideEntityBacklist.playerladder.config"), config().server.rideEntityBlacklist)
                .setTooltip(Component.translatable("rideEntityBacklist.playerladder.description"))
                .setDisplayRequirement(Requirement.isTrue(() -> config().server.allowRidingEntities))
                .setSaveConsumer(entries -> {
                    SharedHandler.setExcludedRideEntries(entries);
                    config().server.rideEntityBlacklist = entries;
                }).build());

        serverCategory.addEntry(entryBuilder.startStrList(Component.translatable("pickUpEntityBlacklist.playerladder.config"), config().server.pickUpEntityBlacklist)
                .setTooltip(Component.translatable("pickUpEntityBlacklist.playerladder.description"))
                .setDisplayRequirement(Requirement.isTrue(() -> config().server.allowPickingUpEntities))
                .setSaveConsumer(entries -> {
                    SharedHandler.setExcludedPickUpEntries(entries);
                    config().server.pickUpEntityBlacklist = entries;
                }).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("invertRideEntityList.playerladder.config"), config().server.invertRideEntityBlacklist)
                .setTooltip(Component.translatable("invertRideEntityList.playerladder.config"))
                .setDisplayRequirement(Requirement.isTrue(() -> config().server.allowRidingEntities))
                .setSaveConsumer(value -> config().server.invertRideEntityBlacklist = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("invertPickUpEntityList.playerladder.config"), config().server.invertPickUpEntityBlacklist)
                .setTooltip(Component.translatable("invertPickUpEntityList.playerladder.config"))
                .setDisplayRequirement(Requirement.isTrue(() -> config().server.allowPickingUpEntities))
                .setSaveConsumer(value -> config().server.invertPickUpEntityBlacklist = value).build());

        serverCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("rideExtension.playerladder.config"), config().server.rideExtension)
                .setTooltip(Component.translatable("rideExtension.playerladder.description"))
                .setSaveConsumer(value -> config().server.rideExtension = value).build());


        ConfigCategory clientCategory = builder.getOrCreateCategory(Component.translatable("client.playerladder.config"));

        clientCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("allowInteractions.playerladder.config"), config().client.allowInteractions)
                .setTooltip(Component.translatable("allowInteractions.playerladder.description"))
                .setSaveConsumer(value -> config().client.allowInteractions = value).build());

        return builder.build();
    }
}
