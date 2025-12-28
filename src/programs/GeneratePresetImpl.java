package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    static final Random rng = new Random();
    static final int MAX_UNITS_OF_TYPE = 11;

    /**
     * Генерирует армию компьютера с использованием жадного алгоритма.
     * 
     * @param unitList список типов юнитов для выбора
     * @param maxPoints максимальный бюджет очков для армии
     * @return армия компьютера с оптимальным составом юнитов
     */
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        int totalCost = 0;
        List<Unit> armyUnits = new ArrayList<>();
        PositionGenerator positionGenerator = new PositionGenerator();
        List<Unit> rangedUnitTypes = getRangedUnitTypes(unitList);
        
        // Добавляем юниты каждого типа в порядке ранжирования
        for (Unit unitType : rangedUnitTypes) {
            for (int addedForType = 0; totalCost + unitType.getCost() <= maxPoints && addedForType < MAX_UNITS_OF_TYPE; addedForType++) {
                int[] coordinates = positionGenerator.getNextPosition();
                int posX = coordinates[0];
                int posY = coordinates[1];
                
                Unit unit = new Unit(
                    unitType.getUnitType() + " " + (addedForType + 1),
                    unitType.getUnitType(),
                    unitType.getHealth(),
                    unitType.getBaseAttack(),
                    unitType.getCost(),
                    unitType.getAttackType(),
                    unitType.getAttackBonuses(),
                    unitType.getDefenceBonuses(),
                    posX,
                    posY
                );
                
                armyUnits.add(unit);
                totalCost += unit.getCost();
            }
        }
        
        Army result = new Army(armyUnits);
        result.setPoints(totalCost);
        return result;
    }

    // Сортирует юниты по урону на очко, затем по выживаемости и возвращает ранжированный список юнитов
    private List<Unit> getRangedUnitTypes(List<Unit> unitList) {
        List<Unit> rangedUnitTypes = new ArrayList<>(unitList);
        rangedUnitTypes.sort((u1, u2) -> {
            double efficiency1 = (double) u1.getBaseAttack() / u1.getCost();
            double efficiency2 = (double) u2.getBaseAttack() / u2.getCost();
            if (Math.abs(efficiency1 - efficiency2) > 1e-9) {
                return Double.compare(efficiency2, efficiency1);
            }
            double survivability1 = (double) u1.getHealth() / u1.getCost();
            double survivability2 = (double) u2.getHealth() / u2.getCost();
            return Double.compare(survivability2, survivability1);
        });
        return rangedUnitTypes;
    }

    /**
     * Внутренний класс для генерации уникальных координат.
     * Хранит список доступных позиций и случайно выбирает из них, удаляя выбранную.
     */
    private static class PositionGenerator {
        private final List<int[]> availablePositions;
        private static final Random random = new Random();

        public PositionGenerator() {
            this.availablePositions = new ArrayList<>();
            // Инициализация всех доступных координат, для зоны компьютера это x: 0-2, y: 0-20
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 21; y++) {
                    availablePositions.add(new int[]{x, y});
                }
            }
        }

        /**
         * Возвращает случайную доступную позицию и удаляет её из списка доступных.
         * @return массив из двух элементов [x, y]
         */
        public int[] getNextPosition() {
            if (availablePositions.isEmpty()) {
                throw new IllegalStateException("Нет доступных позиций");
            }
            int randomIndex = random.nextInt(availablePositions.size());
            return availablePositions.remove(randomIndex);
        }
    }
}
