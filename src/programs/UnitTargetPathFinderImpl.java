package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    
    private static final int FIELD_HEIGHT = 21;
    private static final int FIELD_WIDTH = 27;
    
    private static final int[][] MOVEMENT_OFFSETS = {
        {-1, 0}, {1, 0}, {0, -1}, {0, 1},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };

    /**
     * Находит кратчайший путь от атакующего юнита до цели используя BFS.
     * 
     * @param attackUnit атакующий юнит
     * @param targetUnit цель атаки
     * @param existingUnitList список всех юнитов на поле
     * @return список координат пути от атакующего до цели, пустой список если путь не найден
     */
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        boolean[][] obstacleMap = createObstacleMap(existingUnitList, attackUnit, targetUnit);
        Edge[][] parentMap = new Edge[FIELD_WIDTH][FIELD_HEIGHT];
        
        List<Edge> route = findRoute(attackUnit.getxCoordinate(), attackUnit.getyCoordinate(), targetUnit.getxCoordinate(), targetUnit.getyCoordinate(), 
                                            obstacleMap, parentMap);
        
        return route;
    }

    private boolean[][] createObstacleMap(List<Unit> units, Unit attackUnit, Unit targetUnit) {
        boolean[][] obstacleMap = new boolean[FIELD_WIDTH][FIELD_HEIGHT];
        
        for (Unit unit : units) {
            if (unit.isAlive()) {
                obstacleMap[unit.getxCoordinate()][unit.getyCoordinate()] = true;
            }
        }
        
        // Исключаем стартовую и целевую позиции из препятствий
        obstacleMap[attackUnit.getxCoordinate()][attackUnit.getyCoordinate()] = false;
        obstacleMap[targetUnit.getxCoordinate()][targetUnit.getyCoordinate()] = false;
        
        return obstacleMap;
    }

    private List<Edge> findRoute(int sourceX, int sourceY, int destinationX, int destinationY,
                                        boolean[][] obstacleMap, Edge[][] parentMap) {
        Queue<Edge> queue = new LinkedList<>();
        boolean[][] visited = new boolean[FIELD_WIDTH][FIELD_HEIGHT];
        
        Edge startPosition = new Edge(sourceX, sourceY);
        queue.add(startPosition);
        visited[sourceX][sourceY] = true;
        
        while (!queue.isEmpty()) {
            Edge currentPosition = queue.poll();
            
            if (currentPosition.getX() == destinationX && currentPosition.getY() == destinationY) {
                break;
            }
            
            exploreNeighbors(currentPosition, obstacleMap, visited, parentMap, queue);
        }
        
        if (visited[destinationX][destinationY]) {
            return reconstructPath(destinationX, destinationY, parentMap);
        }
        
        return new ArrayList<>();
    }

    private void exploreNeighbors(Edge current, boolean[][] obstacleMap, boolean[][] visited,
                                 Edge[][] parentMap, Queue<Edge> queue) {
        for (int[] offset : MOVEMENT_OFFSETS) {
            int nextX = current.getX() + offset[0];
            int nextY = current.getY() + offset[1];
            
            if (isValidPosition(nextX, nextY) && !visited[nextX][nextY] && !obstacleMap[nextX][nextY]) {
                visited[nextX][nextY] = true;
                Edge nextPosition = new Edge(nextX, nextY);
                parentMap[nextX][nextY] = current;
                queue.add(nextPosition);
            }
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < FIELD_WIDTH && y >= 0 && y < FIELD_HEIGHT;
    }

    private List<Edge> reconstructPath(int destinationX, int destinationY, Edge[][] parentMap) {
        List<Edge> path = new ArrayList<>();
        Edge current = new Edge(destinationX, destinationY);
        
        while (current != null) {
            path.add(current);
            current = parentMap[current.getX()][current.getY()];
        }
        
        Collections.reverse(path);
        return path;
    }
}
