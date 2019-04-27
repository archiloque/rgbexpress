package net.archiloque.rgbexpress;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

final class LevelState extends AbstractLevelState {

    /**
     * The current level
     */
    @NotNull
    private final Level level;

    /**
     * Number of unprocessed packages per package type
     */
    private final int previousNumberOfUnprocessedPackages;

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
     * Current trucks
     */
    @NotNull
    private final Truck[] currentTrucks;

    LevelState(
            final @NotNull Level level,
            final int previousNumberOfUnprocessedPackages,
            final @NotNull byte[] previousRoadMaps,
            final @NotNull byte[] previousPickMap,
            final @NotNull byte[] previousUnloadMap,
            final @NotNull Truck[] currentTrucks
    ) {
        this.level = level;
        this.previousNumberOfUnprocessedPackages = previousNumberOfUnprocessedPackages;
        this.previousRoadMaps = previousRoadMaps;
        this.previousUnloadMap = previousUnloadMap;
        this.previousPickMap = previousPickMap;
        this.currentTrucks = currentTrucks;
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
                previousNumberOfUnprocessedPackages,
                0,
                previousRoadMaps,
                previousPickMap,
                previousUnloadMap
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
            final int nextNumberOfUnprocessedPackages,
            final int nextNumberOfNotStoppedTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap) throws IOException {
        Truck currentTruck = currentTrucks[truckIndex];
        if (currentTruck.stopped) {
            return processStoppedTruck(
                    truckIndex,
                    currentTruck,
                    nextTrucks,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap,
                    nextNumberOfUnprocessedPackages,
                    nextNumberOfNotStoppedTrucks);
        } else {
            return processNotSoppedTruck(
                    truckIndex,
                    currentTruck,
                    nextTrucks,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap,
                    nextNumberOfUnprocessedPackages,
                    nextNumberOfNotStoppedTrucks);
        }
    }

    private @Nullable Truck[] processNotSoppedTruck(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final int nextNumberOfUnprocessedPackages,
            final int nextNumberOfNotStoppedTrucks) throws IOException {
        // try to stop
        Truck[] result = processNotSoppedTruckTryStopping(
                truckIndex,
                currentTruck,
                nextNumberOfUnprocessedPackages,
                nextNumberOfNotStoppedTrucks,
                nextTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap);
        if (result != null) {
            return result;
        }
        // try to go down
        result = processNotSoppedTruckTryGoing(
                truckIndex,
                currentTruck,
                nextTrucks,
                nextNumberOfUnprocessedPackages,
                nextNumberOfNotStoppedTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
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
                nextNumberOfUnprocessedPackages,
                nextNumberOfNotStoppedTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
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
                nextNumberOfUnprocessedPackages,
                nextNumberOfNotStoppedTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                Direction.RIGHT,
                (short) +1
        );
        if (result != null) {
            return result;
        }
        // try to go up
        result = processNotSoppedTruckTryGoing(
                truckIndex,
                currentTruck,
                nextTrucks,
                nextNumberOfUnprocessedPackages,
                nextNumberOfNotStoppedTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap,
                Direction.UP,
                (short) -level.width
        );
        return result;
    }

    private @Nullable Truck[] processNotSoppedTruckTryStopping(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final int nextNumberOfUnprocessedPackages,
            final int nextNumberOfNotStoppedTrucks,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap) throws IOException {
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
                    nextNumberOfUnprocessedPackages,
                    nextNumberOfNotStoppedTrucks,
                    newTruck,
                    nextTrucks,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap
            );
        } else {
            return null;
        }
    }

    private @Nullable Truck[] processNotSoppedTruckTryGoing(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final int nextNumberOfUnprocessedPackages,
            final int nextNumberOfNotStoppedTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final int direction,
            final short deltaPosition) throws IOException {
        short currentPosition = currentTruck.currentPosition;
        byte currentRoad = getElementInMap(currentPosition, nextRoadMap, level.roadsSmallMapIndexes);
        byte currentTruckType = level.trucksTypes[truckIndex];
        if (LOG) {
            log(truckIndex, "Truck " + truckIndex + " " + MapElement.TRUCK_TO_NAME.get(currentTruckType) + " at (" + (currentPosition / level.width) + ", " + (currentPosition % level.width) + ") and want to go " + Direction.AS_CHAR[direction] + " with road being [" + RoadElement.BYTE_TO_CHAR.get(currentRoad) + "]");
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

        int nextNextNumberOfUnprocessedPackages = nextNumberOfUnprocessedPackages;
        @NotNull byte[] nextNextPickMap = nextPickMap;
        @NotNull byte[] nextNextUnloadMap = nextUnloadMap;

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
            byte lastPackage = getLastPackage(newCargo);
            if (lastPackage != elementToUnload) {
                // package of the wrong type => stop
                if (LOG) {
                    log(truckIndex, "Can't go there because the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " package doesn't match the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " warehouse");
                }
                return null;
            }

            if (!MapElement.CAN_UNLOAD[currentTruckType][elementToUnload]) {
                if (LOG) {
                    log(truckIndex, "Can't go there because the " + MapElement.PACKAGE_TO_NAME.get(elementToUnload) + " package can't be unload by a " + MapElement.TRUCK_TO_NAME.get(currentTruckType) + " truck");
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
            nextNextNumberOfUnprocessedPackages = removeOnePackage(nextNextNumberOfUnprocessedPackages, lastPackage);

            if (nextNextNumberOfUnprocessedPackages == 0) {
                // we found a solution !
                Truck[] nextNextTrucks = nextTrucks.clone();

                @NotNull Truck newTruck = new Truck(
                        targetPosition,
                        false,
                        newCargo,
                        newPositions
                );

                nextNextTrucks[truckIndex] = newTruck;

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

        @NotNull Truck newTruck = new Truck(
                targetPosition,
                false,
                newCargo,
                newPositions.clone()
        );

        int nextNextNumberOfNotStoppedTrucks = addOneTruck(nextNumberOfNotStoppedTrucks, currentTruckType);

        return endProcessTruck(
                truckIndex,
                nextNextNumberOfUnprocessedPackages,
                nextNextNumberOfNotStoppedTrucks,
                newTruck,
                nextTrucks,
                nextNextRoads,
                nextNextPickMap,
                nextNextUnloadMap
        );
    }

    private @Nullable Truck[] processStoppedTruck(
            final int truckIndex,
            final @NotNull Truck currentTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap,
            final int nextNumberOfUnprocessedPackages,
            final int nextNumberOfNotStoppedTrucks) throws IOException {
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
                nextNumberOfUnprocessedPackages,
                nextNumberOfNotStoppedTrucks,
                currentTruck,
                nextTrucks,
                nextRoadMap,
                nextPickMap,
                nextUnloadMap);
    }

    private @Nullable Truck[] endProcessTruck(
            final int truckIndex,
            final int nextNumberOfUnprocessedPackages,
            final int nextNumberOfNotStoppedTrucks,
            final Truck newTruck,
            final @NotNull Truck[] nextTrucks,
            final @NotNull byte[] nextRoadMap,
            final @NotNull byte[] nextPickMap,
            final @NotNull byte[] nextUnloadMap) throws IOException {
        // Add truck to list
        Truck[] nextNextTrucks = nextTrucks.clone();
        nextNextTrucks[truckIndex] = newTruck;

        if (truckIndex == (level.numberOfTrucks - 1)) {
            // last truck
            if (!enoughTrucksForPackages(nextNumberOfNotStoppedTrucks, nextNumberOfUnprocessedPackages)) {
                if (LOG) {
                    log(truckIndex, "Not enough truck is driving");
                }
                return null;
            } else {
                if (LOG) {
                    log(truckIndex, "Adding new level state to list");
                }
                level.states.add(new LevelState(
                        level,
                        nextNumberOfUnprocessedPackages,
                        nextRoadMap,
                        nextPickMap,
                        nextUnloadMap,
                        nextNextTrucks
                ));
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
                    nextNumberOfUnprocessedPackages,
                    nextNumberOfNotStoppedTrucks,
                    nextRoadMap,
                    nextPickMap,
                    nextUnloadMap);
        }
    }


}
