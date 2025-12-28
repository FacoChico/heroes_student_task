package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.stream.Collectors;
import java.util.List;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    /**
     * Выполняет пошаговую симуляцию боя между армиями.
     * 
     * @param playerArmy армия игрока
     * @param computerArmy армия компьютера
     * @throws InterruptedException если возникла ошибка атаки во время симуляции
     */
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> leftArmyUnits = playerArmy.getUnits();
        List<Unit> rightArmyUnits = computerArmy.getUnits();

        while (bothArmiesContainsLivingUnits(leftArmyUnits, rightArmyUnits)) {
            List<Unit> allLivingUnits = getOrderedLivingUnits(leftArmyUnits);
            List<Unit> rightLivingUnits = getOrderedLivingUnits(rightArmyUnits);
            
            // Объединяем и сортируем всех юнитов по убыванию атаки
            allLivingUnits.addAll(rightLivingUnits);
            allLivingUnits.sort((u1, u2) -> {
                int attackDiff = Integer.compare(u2.getBaseAttack(), u1.getBaseAttack());
                return attackDiff != 0 ? attackDiff : u1.getName().compareTo(u2.getName());
            });

            for (Unit unit : allLivingUnits) {
                if (!bothArmiesContainsLivingUnits(leftArmyUnits, rightArmyUnits)) {
                    break;
                }
                
                if (!unit.isAlive()) {
                    continue;
                }
                
                performAttack(unit);
            }
        }
    }

    private List<Unit> getOrderedLivingUnits(List<Unit> units) {
        List<Unit> livingUnits = units.stream().filter(Unit::isAlive).collect(Collectors.toList());
        
        livingUnits.sort((u1, u2) -> {
            int attackDiff = Integer.compare(u2.getBaseAttack(), u1.getBaseAttack());
            return attackDiff != 0 ? attackDiff : u1.getName().compareTo(u2.getName());
        });
        return livingUnits;
    }

    private boolean performAttack(Unit attacker) throws InterruptedException {
        Unit target = attacker.getProgram().attack();
        if (target != null) {
            printBattleLog.printBattleLog(attacker, target);
            return true;
        }
        return false;
    }

    private boolean bothArmiesContainsLivingUnits(List<Unit> leftArmyUnits, List<Unit> rightArmyUnits) {
        return leftArmyUnits.stream().anyMatch(Unit::isAlive) && rightArmyUnits.stream().anyMatch(Unit::isAlive);
    }
}
