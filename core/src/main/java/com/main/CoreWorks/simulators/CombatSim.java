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

    public CombatSim(Player player, Array<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
    }

    public void enqueueMoves(Queue<Move> queue) {
        while (queue.size > 0) {
            queuedFactoryMoves.addLast(queue.removeFirst());
        }
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
                // Always kills enemies from left to right
                // TBD if we want to enable target selection
                move.execute(enemies.first());
            }
        }
    }

    public void updateEnemies() {
        for (Enemy enemy : enemies) {
            // Hardcoding the move target for now, eventually should make wrapper classes for different moves that target different things
            Move currMove = enemy.getMove();
            switch (currMove) {
                case HealMove healMove:
                    enemy.tick(enemy);
                    break;

                case DamageMove damageMove:
                    enemy.tick(player);
                    break;

                default:
                    break;
            }
        }
    }

    public void removeDead() {
        for (int i = 0; i < enemies.size; i++) {
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
}
