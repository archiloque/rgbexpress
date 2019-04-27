package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class FullLevelState extends AbstractLevelState {

    /**
     * The current level
     */
    @NotNull
    private final Level level;

    /**
     * Starting bump map
     */
    @NotNull
    final boolean[] initialBumpMap;

    /**
     * Number of unprocessed cargos
     */
    private final int previousNumberOfUnprocessedCargos;

    /**
     * Maps of the road as {@link RoadElement}
     */
    @NotNull
    private final byte[] previousRoadMaps;

    /**
     * Map of the packages to pick, designed by their package as {@link MapElement}
     */
    @NotNull
    private final byte[] previousPickMap;

    /**
     * Map of the warehouse drop points, designed by their package as {@link MapElement}
     */
    @NotNull
    private final byte[] previousUnloadMap;

    /**
     * Map of the bumps
     */
    @NotNull
    private final boolean[] previousBumpMap;

    /**
     * Current trucks
     */
    @NotNull
    private final Truck[] currentTrucks;

    /**
     * Map of the enabled switches, containing the id of the switch or -1 if not switch
     */
    @NotNull
    private final byte[] previousSwitchMap;

    /**
     * The switch status per if of the switch (are they enabled or disabled)
     */
    @NotNull
    private final boolean[] previousSwitchState;

    FullLevelState(
            final @NotNull Level level,
            final @NotNull boolean[] initialBumpMap,
            final int previousNumberOfUnprocessedCargos,
            final @NotNull byte[] previousRoadMaps,
            final @NotNull byte[] previousPickMap,
            final @NotNull byte[] previousUnloadMap,
            final @NotNull boolean[] previousBumpMap,
            final @NotNull Truck[] currentTrucks,
            final @NotNull byte[] previousSwitchMap,
            final @NotNull boolean[] previousSwitchState
    ) {
        this.initialBumpMap = initialBumpMap;
        this.level = level;
        this.previousNumberOfUnprocessedCargos = previousNumberOfUnprocessedCargos;
        this.previousRoadMaps = previousRoadMaps;
        this.previousUnloadMap = previousUnloadMap;
        this.previousPickMap = previousPickMap;
        this.previousBumpMap = previousBumpMap;
        this.currentTrucks = currentTrucks;
        this.previousSwitchMap = previousSwitchMap;
        this.previousSwitchState = previousSwitchState;
    }

    /**
     * Process the current state
     *
     * @return a Truck[] if we find a winning solution
     */
    @Override
    @Nullable
    public Truck[] processState() throws IOException {
        if (LOG) {
            log(0, "Processing new state");
        }
        return processTruck(
                0,
                new Truck[level.numberOfTrucks],
                previousNumberOfUnprocessedCargos,
                previousRoadMaps,
                previousPickMap,
                previousUnloadMap,
                previousBumpMap,
                previousSwitchMap,
                previousSwitchState,
                null,
                false
        );
    }

    /**
     * Function to be called recursively on each {@link Truck}
     *
     * @return a Truck[] if we find a winning solution
     */
    private @Nullable Truck[] processTruck(
            final int truckIndex,
            final @NotNull Truck[] nextTrucks,
            final int nextNumberOfUnprocessedCargos,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @NotNull boolean[] nextBumpMap,
            final @NotNull byte[] nextSwitchMap,
            final @NotNull boolean[] nextSwitchState,
            final @Nullable IntegerListElement forbiddenLocations,
            final boolean anyTruckDriving
    ) throws IOException {
        Truck currentTruck = currentTrucks[truckIndex];
        if (currentTruck.stopped) {
            return processStoppedTruck(
                    truckIndex,
                    currentTruck,
                    nextTrucks,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap,
                    nextBumpMap,
                    nextSwitchMap,
                    nextSwitchState,
                    forbiddenLocations,
                    nextNumberOfUnprocessedCargos,
                    anyTruckDriving);
        } else {
            return processNotSoppedTruck(
                    truckIndex,
                    currentTruck,
                    nextTrucks,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap,
                    nextBumpMap,
                    nextSwitchMap,
                    nextSwitchState,
                    forbiddenLocations,
                    nextNumberOfUnprocessedCargos,
                    anyTruckDriving);
        }
    }

    private @Nullable Truck[] processNotSoppedTruck(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @NotNull boolean[] nextBumpMap,
            final @NotNull byte[] nextSwitchMap,
            final @NotNull boolean[] nextSwitchState,
            final @Nullable IntegerListElement forbiddenLocations,
            final int nextNumberOfUnprocessedCargos,
            final boolean anyTruckDriving) throws IOException {
        // try to go up
        Truck[] result = processNotSoppedTruckTryStopping(
                truckIndex,
                currentTruck,
                nextNumberOfUnprocessedCargos,
                nextTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                nextBumpMap,
                nextSwitchMap,
                nextSwitchState,
                forbiddenLocations,
                anyTruckDriving
        );
        if (result != null) {
            return result;
        }
        // we can stop right here
        result = processNotSoppedTruckTryGoing(
                truckIndex,
                currentTruck,
                nextTrucks,
                nextNumberOfUnprocessedCargos,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                nextBumpMap,
                nextSwitchMap,
                nextSwitchState,
                forbiddenLocations,
                Direction.DOWN,
                level.width
        );
        if (result != null) {
            return result;
        }
        // try to go left
        result = processNotSoppedTruckTryGoing(
                truckIndex,
                currentTruck,
                nextTrucks,
                nextNumberOfUnprocessedCargos,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                nextBumpMap,
                nextSwitchMap,
                nextSwitchState,
                forbiddenLocations,
                Direction.LEFT,
                (short) -1
        );
        if (result != null) {
            return result;
        }
        // try to go right
        result = processNotSoppedTruckTryGoing(
                truckIndex,
                currentTruck,
                nextTrucks,
                nextNumberOfUnprocessedCargos,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                nextBumpMap,
                nextSwitchMap,
                nextSwitchState,
                forbiddenLocations,
                Direction.RIGHT,
                (short) +1
        );
        if (result != null) {
            return result;
        }
        // try to go down
        result = processNotSoppedTruckTryGoing(
                truckIndex,
                currentTruck,
                nextTrucks,
                nextNumberOfUnprocessedCargos,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                nextBumpMap,
                nextSwitchMap,
                nextSwitchState,
                forbiddenLocations,
                Direction.UP,
                (short) -level.width
        );
        return result;
    }

    private @Nullable Truck[] processNotSoppedTruckTryStopping(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final int nextNumberOfUnprocessedCargos,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @NotNull boolean[] nextBumpMap,
            final @NotNull byte[] nextSwitchMap,
            final @NotNull boolean[] nextSwitchState,
            final @Nullable IntegerListElement forbiddenLocations,
            final boolean anyTruckDriving) throws IOException {
        short currentPosition = currentTruck.currentPosition;
        if (LOG) {
            log(truckIndex, "Truck " + truckIndex + " " + MapElement.TRUCK_TO_NAME.get(level.trucksTypes[truckIndex]) + " at (" + (currentPosition / level.width) + ", " + (currentPosition % level.width) + ") and want to stop");
        }

        if (
                (!anyTruckHere(nextTrucks, currentPosition)) &&
                        (currentTruck.cargo == 0)) {
            Truck newTruck = new Truck(
                    currentPosition,
                    true,
                    currentTruck.cargo,
                    currentTruck.previousPositions
            );
            return endProcessTruck(
                    truckIndex,
                    nextNumberOfUnprocessedCargos,
                    newTruck,
                    nextTrucks,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap,
                    nextBumpMap,
                    nextSwitchMap,
                    nextSwitchState,
                    forbiddenLocations,
                    anyTruckDriving
            );
        } else {
            return null;
        }
    }

    private @Nullable Truck[] processNotSoppedTruckTryGoing(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final int nextNumberOfUnprocessedCargos,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @NotNull boolean[] nextBumpMap,
            final @NotNull byte[] nextSwitchMap,
            final @NotNull boolean[] nextSwitchState,
            final @Nullable IntegerListElement nextForbiddenLocations,
            final int direction,
            final short deltaPosition) throws IOException {
        short currentPosition = currentTruck.currentPosition;
        byte currentRoad = getElementInMap(currentPosition, nextRoadMap, level.roadsSmallMapIndexes);
        byte currenTruckType = level.trucksTypes[truckIndex];
        if (LOG) {
            log(truckIndex, "Truck " + truckIndex + " " + MapElement.TRUCK_TO_NAME.get(currenTruckType) + " at (" + (currentPosition / level.width) + ", " + (currentPosition % level.width) + ") and want to go " + Direction.AS_CHAR[direction] + " with road being [" + RoadElement.BYTE_TO_CHAR.get(currentRoad) + "]");
        }

        if (!RoadElement.CAN_GO[direction][currentRoad]) {
            if (LOG) {
                log(truckIndex, "Can't go there because of road");
            }
            return null;
        }
        short targetPosition = (short) (currentPosition + deltaPosition);

        if (anyTruckHere(nextTrucks, targetPosition)) {
            if (LOG) {
                log(truckIndex, "Can't go there because of another truck");
            }
            return null;
        }

        @NotNull byte[] previousPositions = currentTruck.previousPositions;
        int previousPathLength = previousPositions.length;
        byte[] newPositions = new byte[previousPathLength + 1];
        System.arraycopy(previousPositions, 0, newPositions, 0, previousPathLength);
        newPositions[previousPathLength] = level.roadsSmallMapIndexes[currentPosition];

        int newCargo = currentTruck.cargo;

        int nextNextNumberOfUnprocessedCargos = nextNumberOfUnprocessedCargos;
        @NotNull byte[] nextNextPickMap = nextPickMap;
        @NotNull byte[] nextNextUnloadMap = nextUnloadMap;
        @NotNull boolean[] nextNextBumpMap = nextBumpMap;

        byte elementToUnload = getElementInMap(targetPosition, nextNextUnloadMap, level.unloadSmallMapIndexes);
        if (elementToUnload != MapElement.EMPTY) {
            if (newCargo == 0) {
                // no cargo on a space we should unload one => stop
                if (LOG) {
                    log(truckIndex, "Can't go there because we should unload a cargo and we don't have any");
                }
                return null;
            }

            // get the last package
            byte cargo = getLastPackage(newCargo);
            if (cargo != elementToUnload) {
                // package of the wrong type => stop
                if (LOG) {
                    log(truckIndex, "Can't go there because the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " package doesn't match the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " warehouse");
                }
                return null;
            }

            if (!MapElement.CAN_UNLOAD[currenTruckType][elementToUnload]) {
                if (LOG) {
                    log(truckIndex, "Can't go there because the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " package can't be unload by a " + MapElement.TRUCK_TO_NAME.get(currenTruckType) + " truck");
                }
                return null;
            }
            // unload the package
            if (LOG) {
                log(truckIndex, "Unload a " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " package");
            }

            nextNextUnloadMap = nextNextUnloadMap.clone();
            nextNextUnloadMap[level.unloadSmallMapIndexes[targetPosition]] = MapElement.EMPTY;

            newCargo = unloadPackage(newCargo);
            nextNextNumberOfUnprocessedCargos--;

            if (nextNextNumberOfUnprocessedCargos == 0) {
                // we found a solution !
                Truck[] nextNextTrucks = nextTrucks.clone();

                @NotNull Truck newTruck = new Truck(
                        targetPosition,
                        false,
                        newCargo,
                        newPositions
                );

                nextNextTrucks[truckIndex] = newTruck;
                if (!checkForbiddenLocations(nextNextTrucks, nextForbiddenLocations)) {
                    if (LOG) {
                        log(truckIndex, "A truck is on a forbidden location");
                    }
                    return null;
                }
                if (currentTrucks.length != truckIndex)
                    System.arraycopy(
                            currentTrucks,
                            truckIndex + 1,
                            nextNextTrucks,
                            truckIndex + 1,
                            currentTrucks.length - truckIndex - 1
                    );

                return nextNextTrucks;
            }
        } else {
            byte elementToPick = getElementInMap(targetPosition, nextNextPickMap, level.pickSmallMapIndexes);
            if (elementToPick != MapElement.EMPTY) {
                if (cargoFull(newCargo)) {
                    // enough cargo already => stop
                    if (LOG) {
                        log(truckIndex, "Can't go there because we already have enough cargo");
                    }
                    return null;
                } else {
                    // pick the content since it OK
                    nextNextPickMap = nextPickMap.clone();
                    nextNextPickMap[level.pickSmallMapIndexes[targetPosition]] = MapElement.EMPTY;
                    newCargo = loadPackage(newCargo, elementToPick);
                    if (LOG) {
                        log(truckIndex, "Load a package");
                    }
                }
            } else {
                if (nextNextBumpMap[targetPosition]) {
                    if (LOG) {
                        log(truckIndex, "On a bump");
                    }

                    byte dumpedCargo = (newCargo == 0) ? -1 : ((byte) (newCargo & 0xff));
                    if (dumpedCargo == -1) {
                        // no cargo on a space we should unload one => stop
                        if (LOG) {
                            log(truckIndex, "Can't go there because we should unload a cargo and we don't have any");
                        }
                        return null;
                    }
                    if (LOG) {
                        log(truckIndex, "Unload a " + MapElement.PACKAGE_TO_NAME.get(dumpedCargo) + " cargo because of a bump");
                    }
                    newCargo = newCargo >> 8;

                    // remove the bump
                    nextNextBumpMap = nextNextBumpMap.clone();
                    nextNextBumpMap[targetPosition] = false;

                    // set the cargo on the map
                    nextNextPickMap = nextNextPickMap.clone();
                    nextNextPickMap[level.pickSmallMapIndexes[targetPosition]] = dumpedCargo;
                }
            }
        }

        byte targetRoad = getElementInMap(targetPosition, nextRoadMap, level.roadsSmallMapIndexes);
        if (RoadElement.BLOCKED_ROAD[targetRoad]) {
            if (LOG) {
                log(truckIndex, "Road ahead is blocked");
            }
            return null;
        }

        if (LOG) {
            log(truckIndex, "We can go there");
        }

        @NotNull byte[] nextNextRoads = nextRoadMap.clone();
        byte removedDirection = RoadElement.REMOVE_DIRECTION[direction][currentRoad];
        if (removedDirection == RoadElement.ERROR) {
            woops(level, currentPosition);
        }
        nextNextRoads[level.roadsSmallMapIndexes[currentPosition]] = removedDirection;
        int oppositeDirection = Direction.OPPOSITE[direction];
        removedDirection = RoadElement.REMOVE_DIRECTION[oppositeDirection][targetRoad];
        if (removedDirection == RoadElement.ERROR) {
            woops(level, currentPosition);
        }
        nextNextRoads[level.roadsSmallMapIndexes[targetPosition]] = removedDirection;

        byte[] nextNextSwitchMap = nextSwitchMap;
        boolean[] nextNextSwitchState = nextSwitchState;
        IntegerListElement nextNextForbiddenLocations = nextForbiddenLocations;

        byte switchId = nextNextSwitchMap[currentPosition];
        if (switchId != -1) {
            if (LOG) {
                log(truckIndex, "On a switch");
            }
            Level.SwitchGroup switchGroup = level.switchGroups[switchId];

            for (short roadToSwitchPosition : switchGroup.roads) {
                nextNextForbiddenLocations = new IntegerListElement(roadToSwitchPosition, nextNextForbiddenLocations);
                int currentRoadAtPosition = getElementInMap(roadToSwitchPosition, nextNextRoads, level.roadsSmallMapIndexes);
                byte switchedRoad = RoadElement.SWITCHED_ELEMENT[currentRoadAtPosition];
                if (switchedRoad == RoadElement.ERROR) {
                    woops(level, roadToSwitchPosition);
                }
                nextNextRoads[level.roadsSmallMapIndexes[roadToSwitchPosition]] = switchedRoad;
            }

            for (short disabledSwitch : switchGroup.disabledSwitches) {
                if (disabledSwitch != currentPosition) {
                    nextNextForbiddenLocations = new IntegerListElement(disabledSwitch, nextNextForbiddenLocations);
                }
            }
            for (short enabledSwitch : switchGroup.enabledSwitches) {
                if (enabledSwitch != currentPosition) {
                    nextNextForbiddenLocations = new IntegerListElement(enabledSwitch, nextNextForbiddenLocations);
                }
            }
            boolean currentSwitchStatus = nextNextSwitchState[switchId];

            nextNextSwitchMap = nextNextSwitchMap.clone();
            short[] switchesToEnable = currentSwitchStatus ? switchGroup.disabledSwitches : switchGroup.enabledSwitches;
            short[] switchesToDisable = currentSwitchStatus ? switchGroup.enabledSwitches : switchGroup.disabledSwitches;

            for (short switchPosition : switchesToEnable) {
                nextNextSwitchMap[switchPosition] = switchId;
            }
            for (short switchPosition : switchesToDisable) {
                nextNextSwitchMap[switchPosition] = -1;
            }

            nextNextSwitchState = nextNextSwitchState.clone();
            nextNextSwitchState[switchId] = !currentSwitchStatus;
        }

        @NotNull Truck newTruck = new Truck(
                targetPosition,
                false,
                newCargo,
                newPositions.clone()
        );

        return endProcessTruck(
                truckIndex,
                nextNextNumberOfUnprocessedCargos,
                newTruck,
                nextTrucks,
                nextNextRoads,
                nextNextPickMap,
                nextNextUnloadMap,
                nextNextBumpMap,
                nextNextSwitchMap,
                nextSwitchState,
                nextNextForbiddenLocations,
                true
        );
    }

    private @Nullable Truck[] processStoppedTruck(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @NotNull boolean[] nextBumpMap,
            final @NotNull byte[] nextSwitchMap,
            final @NotNull boolean[] nextSwitchState,
            final @Nullable IntegerListElement forbiddenLocations,
            final int nextNumberOfUnprocessedCargos,
            final boolean anyTruckDriving) throws IOException {
        // already stopped, going on

        // check if not other truck on the same place
        if (anyTruckHere(nextTrucks, currentTruck.currentPosition)) {
            if (LOG) {
                log(truckIndex, "Truck " + truckIndex + " is stopped and there's another truck here");
            }
            return null;
        }
        if (LOG) {
            log(truckIndex, "Truck " + truckIndex + " is already stopped");
        }
        return endProcessTruck(
                truckIndex,
                nextNumberOfUnprocessedCargos,
                currentTruck,
                nextTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                nextBumpMap,
                nextSwitchMap,
                nextSwitchState,
                forbiddenLocations,
                anyTruckDriving
        );
    }

    private @Nullable Truck[] endProcessTruck(
            final int truckIndex,
            final int nextNumberOfUnprocessedCargos,
            final Truck newTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @NotNull boolean[] nextBumpMap,
            final @NotNull byte[] nextSwitchMap,
            final @NotNull boolean[] nextSwitchState,
            final @Nullable IntegerListElement forbiddenLocations,
            final boolean anyTruckDriving) throws IOException {
        // Add truck to list
        Truck[] nextNextTrucks = nextTrucks.clone();
        nextNextTrucks[truckIndex] = newTruck;

        if (truckIndex == (level.numberOfTrucks - 1)) {
            // last truck
            if (!anyTruckDriving) {
                if (LOG) {
                    log(truckIndex, "No truck is driving");
                }
                return null;
            } else {
                if (checkForbiddenLocations(nextTrucks, forbiddenLocations)) {
                    if (LOG) {
                        log(truckIndex, "Adding new level state to list");
                    }
                    level.states.add(new FullLevelState(
                            level,
                            initialBumpMap,
                            nextNumberOfUnprocessedCargos,
                            nextRoadMap,
                            nextPickMap,
                            nextUnloadMap,
                            nextBumpMap,
                            nextNextTrucks,
                            nextSwitchMap,
                            nextSwitchState
                    ));
                } else {
                    if (LOG) {
                        log(truckIndex, "Truck is on a place it shouldn't be");
                    }
                }
            }
            return null;
        } else {
            // not last truck
            if (LOG) {
                log(truckIndex + 1, "Processing truck number " + (truckIndex + 1));
            }

            return processTruck(
                    truckIndex + 1,
                    nextNextTrucks,
                    nextNumberOfUnprocessedCargos,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap,
                    nextBumpMap,
                    nextSwitchMap,
                    nextSwitchState,
                    forbiddenLocations,
                    anyTruckDriving);
        }
    }

    private boolean checkForbiddenLocations(
            @NotNull final Truck[] trucks,
            @Nullable final IntegerListElement forbiddenLocations) {
        if (forbiddenLocations == null) {
            return true;
        } else {
            Map<Integer, Integer> forbiddenLocationsMap = new HashMap<>();
            IntegerListElement currentLocation = forbiddenLocations;
            while (currentLocation != null) {
                forbiddenLocationsMap.put(currentLocation.element, currentLocation.element);
                currentLocation = currentLocation.previous;
            }
            for (Truck truck : trucks) {
                if ((truck != null) && (forbiddenLocationsMap.containsKey(truck.currentPosition))) {
                    return false;
                }
            }
            return true;
        }
    }

}
