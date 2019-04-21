package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

final class LevelState {

    private final @NotNull Level level;
    private final int previousNumberOfUnprocessedElements;
    private final @NotNull byte[] previousRoads;
    private final @NotNull byte[] previousPickMap;
    private final @NotNull byte[] previousUnloadMap;
    private final @NotNull Truck[] currentTrucks;

    private static final boolean LOG = "true".equals(System.getenv("LOG"));

    LevelState(
            final @NotNull Level level,
            final int previousNumberOfUnprocessedElements,
            final @NotNull byte[] previousRoads,
            final @NotNull byte[] previousPickMap,
            final @NotNull byte[] previousUnloadMap,
            final @NotNull Truck[] currentTrucks) {
        this.level = level;
        this.previousNumberOfUnprocessedElements = previousNumberOfUnprocessedElements;
        this.previousRoads = previousRoads;
        this.previousUnloadMap = previousUnloadMap;
        this.previousPickMap = previousPickMap;
        this.currentTrucks = currentTrucks;
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
                previousNumberOfUnprocessedElements,
                previousRoads,
                previousPickMap,
                previousUnloadMap,
                null,
                false
        );
    }

    private @Nullable Truck[] processTruck(
            final int truckIndex,
            final @NotNull Truck[] nextTrucks,
            final int nextNumberOfUnprocessedElements,
            final @NotNull byte[] nextRoads,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @Nullable IntegerListElement forbiddenLocations,
            final boolean anyTruckDriving
    ) {
        Truck currentTruck = currentTrucks[truckIndex];
        if (currentTruck.status == Truck.STATUS_STOPPED) {
            return processStoppedTruck(
                    truckIndex,
                    currentTruck,
                    nextTrucks,
                    nextRoads,
                    nextPickMap,
                    nextUnloadMap,
                    forbiddenLocations,
                    nextNumberOfUnprocessedElements,
                    anyTruckDriving);
        } else {
            return processNotSoppedTruck(
                    truckIndex,
                    currentTruck,
                    nextTrucks,
                    nextRoads,
                    nextPickMap,
                    nextUnloadMap,
                    forbiddenLocations,
                    nextNumberOfUnprocessedElements,
                    anyTruckDriving);
        }
    }

    private @Nullable Truck[] processNotSoppedTruck(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoads,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @Nullable IntegerListElement forbiddenLocations,
            final int nextNumberOfUnprocessedElements,
            final boolean anyTruckDriving) {
        // try to go up
        Truck[] result = processNotSoppedTruckTryGoing(
                truckIndex,
                currentTruck,
                nextTrucks,
                nextNumberOfUnprocessedElements,
                nextRoads,
                nextPickMap,
                nextUnloadMap,
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
                nextNumberOfUnprocessedElements,
                nextRoads,
                nextPickMap,
                nextUnloadMap,
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
                nextNumberOfUnprocessedElements,
                nextRoads,
                nextPickMap,
                nextUnloadMap,
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
                nextNumberOfUnprocessedElements,
                nextRoads,
                nextPickMap,
                nextUnloadMap,
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
                nextNumberOfUnprocessedElements,
                nextTrucks,
                nextRoads,
                nextPickMap,
                nextUnloadMap,
                forbiddenLocations,
                anyTruckDriving
        );
        return result;
    }

    private @Nullable Truck[] processNotSoppedTruckTryStopping(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final int nextNumberOfUnprocessedElements,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoads,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
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
                    nextNumberOfUnprocessedElements,
                    newTruck,
                    nextTrucks,
                    nextRoads,
                    nextPickMap,
                    nextUnloadMap,
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
            final int nextNumberOfUnprocessedElements,
            final @NotNull byte[] nextRoads,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @Nullable IntegerListElement forbiddenLocations,
            final int direction,
            final int deltaPosition) {
        int currentPosition = currentTruck.currentPosition;
        byte currentRoad = nextRoads[currentPosition];
        if (LOG) {
            String message = "Truck " + truckIndex + " at (" + (currentPosition / level.width) + ", " + (currentPosition % level.width) + ") and want to go " + Direction.AS_CHAR[direction] + " with road being [" + RoadElement.BYTE_TO_CHAR.get(currentRoad) + "]";
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

        int nextNextNumberOfUnprocessedElements = nextNumberOfUnprocessedElements;
        @NotNull byte[] nextNextPickMap = nextPickMap;
        @NotNull byte[] nextNextUnloadMap = nextUnloadMap;

        byte canUnload = nextNextUnloadMap[targetPosition];
        if (canUnload != MapElement.EMPTY) {
            if (newTruck.cargo == null) {
                // no cargo on a space we should unload one => stop
                if (LOG) {
                    System.out.println("Can't go there because we should unload a cargo and we don't have any");
                }
                return null;
            }

            // get the last cargo
            byte cargo = newTruck.cargo.element;
            if (cargo != canUnload) {
                // cargo of the wrong type => stop
                if (LOG) {
                    System.out.println("Can't go there because we should unload a cargo of another type");
                }
                return null;
            }
            // unload the cargo
            if (LOG) {
                System.out.println("Unload a cargo");
            }
            nextNextUnloadMap = Arrays.copyOf(nextNextUnloadMap, nextNextUnloadMap.length);
            nextNextUnloadMap[targetPosition] = MapElement.EMPTY;
            newTruck.cargo = newTruck.cargo.previous;
            nextNextNumberOfUnprocessedElements--;

            if (nextNextNumberOfUnprocessedElements == 0) {
                // we found a solution !
                Truck[] nextNextTrucks = Arrays.copyOf(nextTrucks, currentTrucks.length);
                nextNextTrucks[truckIndex] = newTruck;
                if (!checkForbiddenLocations(nextNextTrucks, forbiddenLocations)) {
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
            byte canPick = nextNextPickMap[targetPosition];
            if (canPick != MapElement.EMPTY) {
                if (!MapElement.CAN_PICK[newTruck.type][canPick]) {
                    // cargo of the wrong type => stop
                    if (LOG) {
                        System.out.println("Can't go there because we can't pick a cargo");
                    }
                    return null;
                } else if ((newTruck.cargo != null) && (newTruck.cargo.size == 3)) {
                    // enough cargo already => stop
                    if (LOG) {
                        System.out.println("Can't go there because we already have enough cargo");
                    }
                    return null;
                } else {
                    // pick the content since it OK
                    nextNextPickMap = Arrays.copyOf(nextNextPickMap, nextNextPickMap.length);
                    nextNextPickMap[targetPosition] = MapElement.EMPTY;
                    newTruck.cargo = new ByteListElement(canPick, newTruck.cargo);
                    if (LOG) {
                        System.out.println("Load a cargo");
                    }
                }
            }
        }

        byte targetRoad = nextRoads[targetPosition];
        if (RoadElement.BLOCKED_ROAD[targetRoad]) {
            if (LOG) {
                System.out.println("Road ahead is blocked");
            }
            return null;
        }

        if (LOG) {
            System.out.println("We can go there");
        }

        @NotNull byte[] nextNextRoads = Arrays.copyOf(nextRoads, nextRoads.length);
        byte removedDirection = RoadElement.REMOVE_DIRECTION[direction][currentRoad];
        if (removedDirection == RoadElement.ERROR) {
            throw new IllegalArgumentException("Woops !");
        }
        nextNextRoads[currentPosition] = removedDirection;
        int oppositeDirection = Direction.OPPOSITE[direction];
        removedDirection = RoadElement.REMOVE_DIRECTION[oppositeDirection][targetRoad];
        if (removedDirection == RoadElement.ERROR) {
            throw new IllegalArgumentException("Woops !");
        }
        nextNextRoads[targetPosition] = removedDirection;

        IntegerListElement nexForbiddenLocations = forbiddenLocations;

        @Nullable int[] roadsToSwitchPositions = level.switchMap[targetPosition];
        if (roadsToSwitchPositions != null) {
            if (LOG) {
                System.out.println("On a switch");
            }
            for (int roadToSwitchPosition : roadsToSwitchPositions) {
                nexForbiddenLocations = new IntegerListElement(roadToSwitchPosition, nexForbiddenLocations);
                int currentRoadAtPosition = nextNextRoads[roadToSwitchPosition];
                byte switchedRoad = RoadElement.SWITCHED_ELEMENT[currentRoadAtPosition];
                nextNextRoads[roadToSwitchPosition] = switchedRoad;
            }
        }

        return endProcessTruck(
                truckIndex,
                nextNextNumberOfUnprocessedElements,
                newTruck,
                nextTrucks,
                nextNextRoads,
                nextNextPickMap,
                nextNextUnloadMap,
                nexForbiddenLocations,
                true
        );
    }

    private @Nullable Truck[] processStoppedTruck(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoads,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final @Nullable IntegerListElement forbiddenLocations,
            final int nextNumberOfUnprocessedElements,
            final boolean anyTruckDriving) {
        // already stopped, going on

        // check if not other truck on the same place
        if (anyTruckHere(nextTrucks, currentTruck.currentPosition)) {
            return null;
        }
        return endProcessTruck(
                truckIndex,
                nextNumberOfUnprocessedElements,
                currentTruck,
                nextTrucks,
                nextRoads,
                nextPickMap,
                nextUnloadMap,
                forbiddenLocations,
                anyTruckDriving
        );
    }

    private @Nullable Truck[] endProcessTruck(
            final int truckIndex,
            final int nextNumberOfUnprocessedElements,
            final Truck newTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoads,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
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
                            nextNumberOfUnprocessedElements,
                            nextRoads,
                            nextPickMap,
                            nextUnloadMap,
                            nextNextTrucks
                    ));
                }
            }
            return null;
        } else {
            // not last truck
            return processTruck(
                    truckIndex + 1,
                    nextNextTrucks,
                    nextNumberOfUnprocessedElements,
                    nextRoads,
                    nextPickMap,
                    nextUnloadMap,
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


}
