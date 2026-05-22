package com.main.CoreWorks.simulators;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.entities.Player;
import com.main.CoreWorks.moveset.DamageMove;
import com.main.CoreWorks.moveset.HealMove;
import com.main.CoreWorks.moveset.Move;

public class CombatSim {
    private Player player;
    private Array<Enemy> enemies;
    private Queue<Move> queuedFactoryMoves = new Queue<>();
    private boolean win = false;
    private boolean lost = false;
    private Array<String> combatLog = new Array<>(2);

    public CombatSim(Player player, Array<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
    }

    public void enqueueMoves(Queue<Move> queue) {
        while (queue.size > 0) {
            queuedFactoryMoves.addLast(queue.removeFirst());
        }
    }

    private void addLog(String log) {
        if (combatLog.size >= 2) {
            combatLog.removeIndex(0);
        }
        combatLog.add(log);
    }

    /*
    How does advanceTick resolve combat? (First iteration for milestone 1)
        1. Resolves all factory moves
        2. Checks and removes any dead enemies (prevents dead enemies from taking moves otherwise)
        3. Checks if player has won / lost
        4. If not, enemies now resolve their attacks (if any)
        5. Checks and removes any dead enemies (status effects deaths)
        6. Checks if player has won / lost once more
     */

    public void advanceTick() {
        if (win || lost) {
            return;
        }
        resolveFactoryMoves();
        removeDead();
        winLoss();
        if (win || lost) {
            return;
        }
        updateEnemies();
        removeDead();
        winLoss();
    }

    public void resolveFactoryMoves() {
        while (queuedFactoryMoves.size > 0) {
            Move move = queuedFactoryMoves.removeFirst();
            if (move == null) {
                continue;
            } else {
                // Always kills enemies from left to right + add to combat log for display
                // TBD if we want to enable target selection
                if (move instanceof DamageMove) {
                    move.execute(enemies.first());
                    addLog(String.format("%s dealt %s damage to %s", player.displayName(), move.getValue(), enemies.first().displayName()));
                }
                // Can add more via if statements in the future
                if (move instanceof HealMove) {
                    move.execute(player);
                    addLog(String.format("%s healed for %s", player.displayName(), move.getValue()));
                }
            }
        }
    }

    public void updateEnemies() {
        for (Enemy enemy : enemies) {
            // Hardcoding the move target for now, eventually should make wrapper classes for different moves that target different things
            Move currMove = enemy.getMove();
            int playerHP = player.displayCurrentHp();
            int enemyHP = enemy.displayCurrentHp();

            // Also hardcoding all possible moves and how to add to combat log for now
            // Definitely should change in the future
            switch (currMove) {
                // Healing for now only heals oneself
                // Healing other targets for future consideration
                case HealMove healMove:
                    enemy.tick(enemy);
                    int newEnemyHP = enemy.displayCurrentHp();
                    if (enemyHP != newEnemyHP) {
                        addLog(String.format("%s healed itself for %s", enemy.displayName(), currMove.getValue()));
                    }
                    break;

                case DamageMove damageMove:
                    enemy.tick(player);
                    int newPlayerHP = player.displayCurrentHp();
                    if (newPlayerHP != playerHP) {
                        addLog(String.format("%s dealt %s damage to %s", enemy.displayName(), currMove.getValue(), player.displayName()));
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public void removeDead() {
        // Reverse order to patch potential bugs with 2 dead enemies side by side
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) {
                enemies.removeIndex(i);
            }
        }
    }

    public void winLoss() {
        if (player.isDead()) {
            lost = true;
        }

        if (enemies.size == 0) {
            win = true;
        }
    }

    public boolean isWin() {
        return win;
    }

    public boolean isLost() {
        return lost;
    }

    public Player getPlayer() {
        return player;
    }

    public Array<Enemy> getEnemies() {
        return enemies;
    }

    public Array<String> getCombatLog() {
        return combatLog;
    }
}
