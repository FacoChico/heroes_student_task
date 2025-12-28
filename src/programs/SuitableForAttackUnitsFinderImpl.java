package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;


public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    private static final int FIELD_WIDTH = 27;
    private static final int FIELD_HEIGHT = 21;

    /**
     * Определяет список юнитов, доступных для атаки (не заблокированных другими).
     * 
     * @param unitsByRow список юнитов, сгруппированных по рядам
     * @param isLeftArmyTarget true если цель - левая армия, false - правая
     * @return список доступных для атаки юнитов
     */
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        boolean[][] occupiedPositions = buildOccupiedPositionsMap(unitsByRow);
        List<Unit> activeUnits = extractActiveUnits(unitsByRow);
        
        return filterAccessibleUnits(activeUnits, occupiedPositions, isLeftArmyTarget);
    }

    private boolean[][] buildOccupiedPositionsMap(List<List<Unit>> unitsByRow) {
        boolean[][] occupiedPositions = new boolean[FIELD_WIDTH][FIELD_HEIGHT];
        
        for (List<Unit> row : unitsByRow) {
            if (row == null) {
                continue;
            }
            for (Unit unit : row) {
                if (unit != null && unit.isAlive()) {
                    int x = unit.getxCoordinate();
                    int y = unit.getyCoordinate();
                    occupiedPositions[x][y] = true;
                }
            }
        }
        
        return occupiedPositions;
    }

    private List<Unit> extractActiveUnits(List<List<Unit>> unitsByRow) {
        List<Unit> activeUnits = new ArrayList<>();
        
        for (List<Unit> row : unitsByRow) {
            if (row == null) {
                continue;
            }
            for (Unit unit : row) {
                if (unit != null && unit.isAlive()) {
                    activeUnits.add(unit);
                }
            }
        }
        
        return activeUnits;
    }

    private List<Unit> filterAccessibleUnits(List<Unit> units, boolean[][] occupiedPositions, 
                                            boolean isLeftArmyTarget) {
        List<Unit> accessibleUnits = new ArrayList<>();
        
        for (Unit unit : units) {
            if (canBeAttacked(unit, occupiedPositions, isLeftArmyTarget)) {
                accessibleUnits.add(unit);
            }
        }
        
        return accessibleUnits;
    }

    private boolean canBeAttacked(Unit unit, boolean[][] occupiedPositions, boolean isLeftArmyTarget) {
        int unitX = unit.getxCoordinate();
        int unitY = unit.getyCoordinate();

        if (isLeftArmyTarget) {
            for (int x = unitX - 1; x >= 0; x--) {
                if (occupiedPositions[x][unitY]) {
                    return false;
                }
            }
        } else {
            for (int x = unitX + 1; x < FIELD_WIDTH; x++) {
                if (occupiedPositions[x][unitY]) {
                    return false;
                }
            }
        }

        return true;
    }
}
