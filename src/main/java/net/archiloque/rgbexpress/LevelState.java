package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class LevelState {

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
     * Map of the packages to pick, designed by their package as {@link MapElement
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

    private static final boolean LOG = "true".equals(System.getenv("LOG"));

    LevelState(
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
    @Nullable Truck[] processState() {
        return processTruck(
                0,
                new Truck[0],
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
    ) {
        Truck currentTruck = currentTrucks[truckIndex];
        if (currentTruck.status == Truck.STATUS_STOPPED) {
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
            final boolean anyTruckDriving) {
        // try to go up
        Truck[] result = processNotSoppedTruckTryGoing(
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
                -level.width
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
                -1
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
                +1
        );
        if (result != null) {
            return result;
        }
        // we can stop right here
        result = processNotSoppedTruckTryStopping(
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
            final boolean anyTruckDriving) {
        if (
                (!anyTruckHere(nextTrucks, currentTruck.currentPosition)) &&
                        (currentTruck.cargo == null)) {
            Truck newTruck = new Truck(
                    currentTruck.currentPosition,
                    currentTruck.type,
                    Truck.STATUS_STOPPED,
                    null,
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
            final int deltaPosition) {
        int currentPosition = currentTruck.currentPosition;
        byte currentRoad = nextRoadMap[currentPosition];
        if (LOG) {
            String message = "Truck " + truckIndex + " " + MapElement.TRUCK_TO_NAME.get(currentTruck.type) + " at (" + (currentPosition / level.width) + ", " + (currentPosition % level.width) + ") and want to go " + Direction.AS_CHAR[direction] + " with road being [" + RoadElement.BYTE_TO_CHAR.get(currentRoad) + "]";
            System.out.println(message);
        }

        if (!RoadElement.CAN_GO[direction][currentRoad]) {
            if (LOG) {
                System.out.println("Can't go there because of road");
            }
            return null;
        }
        int targetPosition = currentPosition + deltaPosition;

        if (anyTruckHere(nextTrucks, targetPosition)) {
            if (LOG) {
                System.out.println("Can't go there because of another truck");
            }
            return null;
        }

        @NotNull Truck newTruck = new Truck(
                targetPosition,
                currentTruck.type,
                Truck.STATUS_DRIVING,
                currentTruck.cargo,
                new IntegerListElement(currentPosition, currentTruck.previousPositions)
        );

        int nextNextNumberOfUnprocessedCargos = nextNumberOfUnprocessedCargos;
        @NotNull byte[] nextNextPickMap = nextPickMap;
        @NotNull byte[] nextNextUnloadMap = nextUnloadMap;
        @NotNull boolean[] nextNextBumpMap = nextBumpMap;

        byte elementToUnload = nextNextUnloadMap[targetPosition];
        if (elementToUnload != MapElement.EMPTY) {
            if (newTruck.cargo == null) {
                // no cargo on a space we should unload one => stop
                if (LOG) {
                    System.out.println("Can't go there because we should unload a cargo and we don't have any");
                }
                return null;
            }

            // get the last cargo
            byte cargo = newTruck.cargo.element;
            if (cargo != elementToUnload) {
                // cargo of the wrong type => stop
                if (LOG) {
                    System.out.println("Can't go there because the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " cargo doesn't match the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + "warehouse");
                }
                return null;
            }

            if (!MapElement.CAN_UNLOAD[newTruck.type][elementToUnload]) {
                if (LOG) {
                    System.out.println("Can't go there because the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " cargo can't be unload by a " + MapElement.TRUCK_TO_NAME.get(newTruck.type) + " truck");
                }
                return null;
            }
            // unload the cargo
            if (LOG) {
                System.out.println("Unload a " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " cargo");
            }

            nextNextUnloadMap = Arrays.copyOf(nextNextUnloadMap, nextNextUnloadMap.length);
            nextNextUnloadMap[targetPosition] = MapElement.EMPTY;
            newTruck.cargo = newTruck.cargo.previous;
            nextNextNumberOfUnprocessedCargos--;

            if (nextNextNumberOfUnprocessedCargos == 0) {
                // we found a solution !
                Truck[] nextNextTrucks = Arrays.copyOf(nextTrucks, currentTrucks.length);
                nextNextTrucks[truckIndex] = newTruck;
                if (!checkForbiddenLocations(nextNextTrucks, nextForbiddenLocations)) {
                    if (LOG) {
                        System.out.println("A truck is on a forbidden location");
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
            byte elementToPick = nextNextPickMap[targetPosition];
            if (elementToPick != MapElement.EMPTY) {
                if ((newTruck.cargo != null) && (newTruck.cargo.size == 3)) {
                    // enough cargo already => stop
                    if (LOG) {
                        System.out.println("Can't go there because we already have enough cargo");
                    }
                    return null;
                } else {
                    // pick the content since it OK
                    nextNextPickMap = Arrays.copyOf(nextNextPickMap, nextNextPickMap.length);
                    nextNextPickMap[targetPosition] = MapElement.EMPTY;
                    newTruck.cargo = new ByteListElement(elementToPick, newTruck.cargo);
                    if (LOG) {
                        System.out.println("Load a cargo");
                    }
                }
            } else {
                if (nextNextBumpMap[targetPosition]) {
                    if (LOG) {
                        System.out.println("On a bump");
                    }
                    if (newTruck.cargo == null) {
                        // no cargo on a space we should unload one => stop
                        if (LOG) {
                            System.out.println("Can't go there because we should unload a cargo and we don't have any");
                        }
                        return null;
                    }
                    // update the truck
                    byte cargo = newTruck.cargo.element;
                    newTruck.cargo = newTruck.cargo.previous;

                    if (LOG) {
                        System.out.println("Unload a " + MapElement.PACKAGE_TO_NAME.get(cargo) + " cargo because of a bump");
                    }

                    // remove the bump
                    nextNextBumpMap = Arrays.copyOf(nextNextBumpMap, nextNextBumpMap.length);
                    nextNextBumpMap[targetPosition] = false;

                    // set the cargo on the map
                    nextNextPickMap = Arrays.copyOf(nextNextPickMap, nextNextPickMap.length);
                    nextNextPickMap[targetPosition] = cargo;
                }
            }
        }

        byte targetRoad = nextRoadMap[targetPosition];
        if (RoadElement.BLOCKED_ROAD[targetRoad]) {
            if (LOG) {
                System.out.println("Road ahead is blocked");
            }
            return null;
        }

        if (LOG) {
            System.out.println("We can go there");
        }

        @NotNull byte[] nextNextRoads = Arrays.copyOf(nextRoadMap, nextRoadMap.length);
        byte removedDirection = RoadElement.REMOVE_DIRECTION[direction][currentRoad];
        if (removedDirection == RoadElement.ERROR) {
            woops(currentPosition);
        }
        nextNextRoads[currentPosition] = removedDirection;
        int oppositeDirection = Direction.OPPOSITE[direction];
        removedDirection = RoadElement.REMOVE_DIRECTION[oppositeDirection][targetRoad];
        if (removedDirection == RoadElement.ERROR) {
            woops(currentPosition);
        }
        nextNextRoads[targetPosition] = removedDirection;

        byte[] nextNextSwitchMap = nextSwitchMap;
        boolean[] nextNextSwitchState = nextSwitchState;
        IntegerListElement nextNextForbiddenLocations = nextForbiddenLocations;

        byte switchId = nextNextSwitchMap[currentPosition];
        if (switchId != -1) {
            if (LOG) {
                System.out.println("On a switch");
            }
            Level.SwitchGroup switchGroup = level.switchGroups[switchId];

            for (int roadToSwitchPosition : switchGroup.roads) {
                nextNextForbiddenLocations = new IntegerListElement(roadToSwitchPosition, nextNextForbiddenLocations);
                int currentRoadAtPosition = nextNextRoads[roadToSwitchPosition];
                byte switchedRoad = RoadElement.SWITCHED_ELEMENT[currentRoadAtPosition];
                if (switchedRoad == RoadElement.ERROR) {
                    woops(roadToSwitchPosition);
                }
                nextNextRoads[roadToSwitchPosition] = switchedRoad;
            }

            for (int disabledSwitch : switchGroup.disabledSwitches) {
                if (disabledSwitch != currentPosition) {
                    nextNextForbiddenLocations = new IntegerListElement(disabledSwitch, nextNextForbiddenLocations);
                }
            }
            for (int enabledSwitch : switchGroup.enabledSwitches) {
                if (enabledSwitch != currentPosition) {
                    nextNextForbiddenLocations = new IntegerListElement(enabledSwitch, nextNextForbiddenLocations);
                }
            }
            boolean currentStatus = nextNextSwitchState[switchId];

            nextNextSwitchMap = Arrays.copyOf(nextNextSwitchMap, nextNextSwitchMap.length);
            int[] switchesToEnable = currentStatus ? switchGroup.disabledSwitches : switchGroup.enabledSwitches;
            int[] switchesToDisable = currentStatus ? switchGroup.enabledSwitches : switchGroup.disabledSwitches;

            for (int switchPosition : switchesToEnable) {
                nextNextSwitchMap[switchPosition] = switchId;
            }
            for (int switchPosition : switchesToDisable) {
                nextNextSwitchMap[switchPosition] = -1;
            }

            nextNextSwitchState = Arrays.copyOf(nextNextSwitchState, MapElement.NUMBER_OF_SWITCH_TYPES);
            nextNextSwitchState[switchId] = !currentStatus;
        }

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
            final boolean anyTruckDriving) {
        // already stopped, going on

        // check if not other truck on the same place
        if (anyTruckHere(nextTrucks, currentTruck.currentPosition)) {
            return null;
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
            final boolean anyTruckDriving) {
        // Add truck to list
        Truck[] nextNextTrucks = Arrays.copyOf(nextTrucks, truckIndex + 1);
        nextNextTrucks[truckIndex] = newTruck;

        if (truckIndex == currentTrucks.length - 1) {
            // last truck
            if (!anyTruckDriving) {
                return null;
            } else {
                if (checkForbiddenLocations(nextTrucks, forbiddenLocations)) {
                    level.states.add(new LevelState(
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
                }
            }
            return null;
        } else {
            // not last truck
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

    private boolean anyTruckHere(final @NotNull Truck[] trucks, final int position) {
        for (Truck truck : trucks) {
            if (truck.currentPosition == position) {
                return true;
            }
        }
        return false;
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
                if (forbiddenLocationsMap.containsKey(truck.currentPosition)) {
                    return false;
                }
            }
            return true;
        }
    }

    private void woops(int position) {
        Level.Coordinate coordinate = level.getCoordinate(position);
        throw new IllegalArgumentException("Woops for (" + coordinate.line + ", " + coordinate.column + ")");
    }


}
