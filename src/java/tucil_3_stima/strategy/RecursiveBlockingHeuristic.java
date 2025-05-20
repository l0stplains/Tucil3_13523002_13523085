package tucil_3_stima.strategy;

import tucil_3_stima.model.Board;
import tucil_3_stima.model.State;
import tucil_3_stima.model.Vehicle;

import java.util.*;

public class RecursiveBlockingHeuristic implements Heuristic {
    private Board board;
    private Set<Integer> seenVehicles;

    @Override
    public int evaluate(Board board, State state) {
        this.board = board;
        this.seenVehicles = new HashSet<>();

        if (isAtExit(state)) {
            return 0;
        }

        return calcMinMoves(state);
    }

    private boolean isAtExit(State state) {
        Vehicle primary = board.getVehicle(0);
        int pos = state.getPositions()[0];
        int cols = board.getCols();
        int row = pos / cols;
        int col = pos % cols;

        boolean horizontal = primary.isHorizontal();
        int exitPos = horizontal ? board.getExitCol() : board.getExitRow();

        if (horizontal) {
            return col + primary.length() - 1 == exitPos;
        } else {
            return row + primary.length() - 1 == exitPos;
        }
    }

    private int calcMinMoves(State state) {
        seenVehicles.add(0);

        int moveCount = 1;

        for (Integer blockingVehicle : findBlockers(state)) {
            int frontSpace = calcSpaceNeeded(0, blockingVehicle, 0, true, state);
            int backSpace = calcSpaceNeeded(0, blockingVehicle, 0, false, state);

            moveCount += calcObstructionValue(blockingVehicle, frontSpace, backSpace, state);
        }

        return moveCount;
    }

    private List<Integer> findBlockers(State state) {
        List<Integer> blockers = new ArrayList<>();

        Vehicle primary = board.getVehicle(0);
        boolean isRedHorizontal = primary.isHorizontal();
        int[] vehiclePositions = state.getPositions();
        int redPos = vehiclePositions[0];
        int cols = board.getCols();
        int redRow = redPos / cols;
        int redCol = redPos % cols;

        int exitPos = isRedHorizontal ? board.getExitCol() : board.getExitRow();
        int fixedAxis = isRedHorizontal ? redRow : redCol;
        int redLength = primary.length();

        for (int i = 1; i < vehiclePositions.length; i++) {
            Vehicle vehicle = board.getVehicle(i);

            if (isRedHorizontal == vehicle.isHorizontal()) {
                continue;
            }

            int vehPos = vehiclePositions[i];
            int vehRow = vehPos / cols;
            int vehCol = vehPos % cols;

            int crossAxis = isRedHorizontal ? vehCol : vehRow;
            int vehAxisVal = isRedHorizontal ? vehRow : vehCol;

            // skip if vehicle is behind red car's front
            if (isRedHorizontal) {
                if (crossAxis < redCol + redLength) {
                    continue;
                }
            } else {
                if (crossAxis < redRow + redLength) {
                    continue;
                }
            }

            // check if vehicle blocks red car's path
            int vehLength = vehicle.length();
            if (fixedAxis >= vehAxisVal && fixedAxis < vehAxisVal + vehLength) {
                blockers.add(i);
            }
        }

        return blockers;
    }

    private int calcObstructionValue(int vehicleIdx, int frontSpaceNeeded, int backSpaceNeeded, State state) {
        seenVehicles.add(vehicleIdx);

        int moveVal = 1;
        int[] vehiclePositions = state.getPositions();

        for (int nextVehicle = 0; nextVehicle < vehiclePositions.length; nextVehicle++) {
            if (nextVehicle == vehicleIdx || seenVehicles.contains(nextVehicle)) {
                continue;
            }

            if (!doVehiclesOverlap(vehicleIdx, nextVehicle, state)) {
                continue;
            }

            int forwardVal = 0, backwardVal = 0;

            boolean canMoveForward = checkMovement(vehicleIdx, nextVehicle, frontSpaceNeeded, true, state);
            boolean canMoveBackward = checkMovement(vehicleIdx, nextVehicle, backSpaceNeeded, false, state);

            int forwardSpaceNeeded = calcSpaceNeeded(vehicleIdx, nextVehicle, frontSpaceNeeded, true, state);
            int backwardSpaceNeeded = calcSpaceNeeded(vehicleIdx, nextVehicle, backSpaceNeeded, false, state);

            if (!canMoveForward) {
                forwardVal = calcObstructionValue(nextVehicle, forwardSpaceNeeded, backwardSpaceNeeded, state);
            } else if (isEdgeBlocking(vehicleIdx, frontSpaceNeeded, true, state)) {
                forwardVal = Integer.MAX_VALUE;
            }

            if (!canMoveBackward) {
                backwardVal = calcObstructionValue(nextVehicle, forwardSpaceNeeded, backwardSpaceNeeded, state);
            } else if (isEdgeBlocking(vehicleIdx, backSpaceNeeded, false, state)) {
                backwardVal = Integer.MAX_VALUE;
            }

            moveVal += Math.min(forwardVal, backwardVal);
        }

        return moveVal;
    }

    private boolean checkMovement(int vehicleIdx, int nextVehicle, int spaceNeeded, boolean isForward, State state) {
        boolean isBlockerBehind = isPositionedBehind(vehicleIdx, nextVehicle, state);

        if ((isBlockerBehind && isForward) || (!isBlockerBehind && !isForward)) {
            return true;
        }

        int availableSpace = getAvailableSpace(vehicleIdx, nextVehicle, isForward, state);
        return availableSpace >= spaceNeeded;
    }

    private int calcSpaceNeeded(int vehicleIdx, int nextVehicle, int baseSpaceNeeded, boolean isForward, State state) {
        Vehicle vehicle = board.getVehicle(vehicleIdx);
        Vehicle nextVeh = board.getVehicle(nextVehicle);

        // if same orientation, use diff calculation
        if (vehicle.isHorizontal() == nextVeh.isHorizontal()) {
            int availSpace = getAvailableSpace(vehicleIdx, nextVehicle, isForward, state);
            return baseSpaceNeeded - availSpace;
        }

        int[] positions = state.getPositions();
        int cols = board.getCols();

        int vehPos = positions[vehicleIdx];
        int nextPos = positions[nextVehicle];

        int vehRow = vehPos / cols;
        int vehCol = vehPos % cols;
        int nextRow = nextPos / cols;
        int nextCol = nextPos % cols;

        int vehFixed = vehicle.isHorizontal() ? vehRow : vehCol;
        int nextVar = nextVeh.isHorizontal() ? nextCol : nextRow;

        if (isForward) {
            return Math.abs(vehFixed - nextVar) + 1;
        }

        int nextLength = nextVeh.length();
        return Math.abs(vehFixed - (nextVar + nextLength));
    }

    private int getAvailableSpace(int vehicleIdx, int nextVehicle, boolean isForward, State state) {
        Vehicle vehicle = board.getVehicle(vehicleIdx);
        Vehicle nextVeh = board.getVehicle(nextVehicle);
        int[] positions = state.getPositions();
        int cols = board.getCols();

        int vehPos = positions[vehicleIdx];
        int nextPos = positions[nextVehicle];

        int vehRow = vehPos / cols;
        int vehCol = vehPos % cols;
        int nextRow = nextPos / cols;
        int nextCol = nextPos % cols;

        int vehVar = vehicle.isHorizontal() ? vehCol : vehRow;
        int vehEnd = vehVar + vehicle.length();

        // different orientation logic
        if (vehicle.isHorizontal() != nextVeh.isHorizontal()) {
            int nextFixed = nextVeh.isHorizontal() ? nextRow : nextCol;

            if (isForward) {
                return Math.abs(vehEnd - nextFixed);
            }

            return Math.abs(vehVar - nextFixed) + 1;
        }

        // same orientation logic
        int nextVar = nextVeh.isHorizontal() ? nextCol : nextRow;

        if (isForward) {
            return Math.abs(vehEnd - nextVar);
        }

        int nextEnd = nextVar + nextVeh.length();
        return Math.abs(vehVar - nextEnd);
    }

    private boolean isEdgeBlocking(int vehicleIdx, int spaceNeeded, boolean isForward, State state) {
        Vehicle vehicle = board.getVehicle(vehicleIdx);
        int[] positions = state.getPositions();
        int cols = board.getCols();
        int rows = board.getRows();

        int vehPos = positions[vehicleIdx];
        int vehRow = vehPos / cols;
        int vehCol = vehPos % cols;

        int vehVar = vehicle.isHorizontal() ? vehCol : vehRow;
        int vehEnd = vehVar + vehicle.length();
        int boardSize = vehicle.isHorizontal() ? cols : rows;

        if (isForward && (vehEnd + spaceNeeded > boardSize)) {
            return true;
        }

        if (!isForward && (vehVar - spaceNeeded < 0)) {
            return true;
        }

        return false;
    }

    private boolean doVehiclesOverlap(int vehicleIdx, int nextVehicle, State state) {
        Vehicle vehicle = board.getVehicle(vehicleIdx);
        Vehicle nextVeh = board.getVehicle(nextVehicle);
        int[] positions = state.getPositions();
        int cols = board.getCols();

        int vehPos = positions[vehicleIdx];
        int nextPos = positions[nextVehicle];

        int vehRow = vehPos / cols;
        int vehCol = vehPos % cols;
        int nextRow = nextPos / cols;
        int nextCol = nextPos % cols;

        int vehFixed = vehicle.isHorizontal() ? vehRow : vehCol;
        int nextFixed = nextVeh.isHorizontal() ? nextRow : nextCol;

        // same orientation check - need same fixed coordinate
        if (vehicle.isHorizontal() == nextVeh.isHorizontal()) {
            return vehFixed == nextFixed;
        }

        // different orientation - check overlap
        int nextVar = nextVeh.isHorizontal() ? nextCol : nextRow;
        int nextEnd = nextVar + nextVeh.length();

        return vehFixed >= nextVar && vehFixed < nextEnd;
    }

    private boolean isPositionedBehind(int vehicleIdx, int nextVehicle, State state) {
        Vehicle vehicle = board.getVehicle(vehicleIdx);
        Vehicle nextVeh = board.getVehicle(nextVehicle);
        int[] positions = state.getPositions();
        int cols = board.getCols();

        int vehPos = positions[vehicleIdx];
        int nextPos = positions[nextVehicle];

        int vehRow = vehPos / cols;
        int vehCol = vehPos % cols;
        int nextRow = nextPos / cols;
        int nextCol = nextPos % cols;

        int vehVar = vehicle.isHorizontal() ? vehCol : vehRow;
        int nextVar = nextVeh.isHorizontal() ? nextCol : nextRow;

        // same orientation check
        if (vehicle.isHorizontal() == nextVeh.isHorizontal()) {
            int nextEnd = nextVar + nextVeh.length();
            return nextEnd <= vehVar;
        }

        // different orientation check
        int vehEnd = vehVar + vehicle.length();
        int nextFixed = nextVeh.isHorizontal() ? nextRow : nextCol;

        return nextFixed < vehEnd;
    }
}