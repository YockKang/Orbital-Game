package com.main.CoreWorks.simulators;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.*;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.*;
import com.main.CoreWorks.entities.Character;
import com.main.CoreWorks.moveset.*;

public class CombatSim {
    private Player player;
    private Array<Enemy> enemies;
    private Queue<Move> queuedFactoryMoves = new Queue<>();
    private boolean win = false;
    private boolean lost = false;
    private Array<String> combatLog = new Array<>(10);
    private FactoryGrid grid;


    public CombatSim(Player player, Array<Enemy> enemies) {
        this.player = player;
        this.enemies = enemies;
    }

    public void enqueueMoves(Queue<Move> queue) {
        while (queue.size > 0) {
            queuedFactoryMoves.addLast(queue.removeFirst());
        }
    }

    public void setGrid(FactoryGrid grid) {
        this.grid = grid;
    }

    private void addLog(int tick, String log) {
        if (combatLog.size >= 50) {
            combatLog.removeIndex(0);
        }
        combatLog.add("Tick " + tick + ": " + log);
    }

    /*
    How does advanceTick resolve combat? (First iteration for milestone 1)
        1. Resolves all factory moves
        2. Checks and removes any dead enemies (prevents dead enemies from taking moves otherwise)
        3. Checks if player has won / lost
        4. If not, enemies now resolve their attacks (if any)
        5. Checks and removes any dead enemies (status effects deaths)
        6. Checks if player has won / lost once more
        7. If not, player now resolve their status effects (if any)
        8. Kills the player if dead (status effects death)
        9. Checks if player has won / lost once more
     */

    public void advanceTick(RunState runState, int tick) {
        if (win || lost) {
            return;
        }
        resolveFactoryMoves(runState, tick);
        removeDead();
        winLoss();
        if (win || lost) {
            return;
        }
        updateEnemiesSE(runState, tick);
        removeDead();
        winLoss();
        if (win || lost) {
            return;
        }
        updateEnemies(runState, tick);
        removeDead();
        winLoss();
        if (win || lost) {
            return;
        }
        updatePlayerSE(runState, tick);
        removeDead();
        winLoss();
    }

    private void updateEnemiesSE(RunState runState, int tick) {
        for (Enemy enemy : enemies) {
            updateStatusEffects(runState, tick, enemy);
        }
    }

    private void updatePlayerSE(RunState runState, int tick) {
        updateStatusEffects(runState, tick, player);
    }

    public void resolveFactoryMoves(RunState runState, int tick) {
        while (queuedFactoryMoves.size > 0) {
            Move move = queuedFactoryMoves.removeFirst();
            if (move == null) {
                continue;
            } else {
                executeMove(runState, move, player, tick);
            }
        }
    }

    public void updateEnemies(RunState runState, int tick) {
        for (Enemy enemy : enemies) {

            Move currMove = enemy.tick();

            if (currMove != null) {
                executeMove(runState, currMove, enemy, tick);
            }
        }
    }

    private void executeMove(RunState runState, Move move, Character attacker, int tick) {
        Character target = null;
        if (attacker == player) {
            if (move.getRandomTarget()) {
                if (enemies.size > 1) {
                    target = enemies.get(runState.getRandom().nextInt(enemies.size - 1));
                } else {
                    target = enemies.get(0);
                }
            } else {
                int tgtnum = move.getTarget();
                if (Integer.signum(tgtnum) >= 0) {
                    if (tgtnum < enemies.size) {
                        target = enemies.get(tgtnum);
                    }
                } else {
                    if (tgtnum > -enemies.size) {
                        target = enemies.get(enemies.size - tgtnum);
                    }
                }
            }
        } else {
            target = player;
        }
        if (target == null) {
            return;
        }
        switch (move) {
            // Healing for now only heals oneself
            // Healing other targets for future consideration
            case HealMove healMove -> {
                int oldHP = attacker.displayCurrentHp();
                move.execute(attacker);
                int newHP = attacker.displayCurrentHp();
                if (oldHP != newHP) {
                    addLog(tick, String.format("%s healed itself for %s", attacker.displayName(), move.getValue()));
                }
            }

            case ShieldMove shieldMove -> {
                int oldShield = attacker.displayShield();
                move.execute(attacker);
                int newShield = attacker.displayShield();
                if (oldShield != newShield) {
                    addLog(tick, String.format("%s shielded itself for %s", attacker.displayName(), move.getValue()));
                }
            }

            case DamageMove damageMove -> {
                int oldHP = target.displayCurrentHp();
                move.execute(target);
                int newHP = target.displayCurrentHp();
                if (oldHP != newHP) {
                    addLog(tick, String.format("%s dealt %s damage to %s", attacker.displayName(), move.getValue(), target.displayName()));
                }
            }

            case DisableBuildingMove disableBuildingMove -> {

                Building building = null;

                // Generates a random grid coordinate and checks if any building based moves can be cast (i.e there exists a building in the randomly chosen grid)
                // Eventually we will outsource this to the RunState class so that each RUN has a fixed random seed, not each COMBAT.
                int randomX = runState.getRandom().nextInt(grid.getMaxWidth());
                int randomY = runState.getRandom().nextInt(grid.getMaxHeight());
                building = this.grid.getBuildingAt(randomX, randomY);

                move.execute(building);

                if (building != null) {
                    addLog(tick, String.format("%s disabled %s for %s ticks", attacker.displayName(), building.displayName(), move.getValue()));
                }
            }
            case StatusEffectMove statusEffectMove -> {
                if (statusEffectMove.getEffect().isOnSelf()) {
                    move.execute(attacker);
                    addLog(tick, String.format("%s applied %s %s to self", attacker.displayName(), move.getValue(), statusEffectMove.getEffect().getType()));
                } else {
                    move.execute(target);
                    addLog(tick, String.format("%s applied %s %s to %s", attacker.displayName(), move.getValue(), statusEffectMove.getEffect().getType(), target.displayName()));
                }
            }

            case TrueDamageMove trueDamageMove -> {
                int oldHP = target.displayCurrentHp();
                move.execute(target);
                int newHP = target.displayCurrentHp();
                if (oldHP != newHP) {
                    addLog(tick, String.format("%s dealt %s piercing damage to %s", attacker.displayName(), move.getValue(), target.displayName()));
                }
            }

            default -> {
                // do nothing
            }
        }
    }

    private void updateStatusEffects(RunState runState, int tick, Character character) {
        ObjectMap<String, StatusEffect> statusEffects = character.getStatusEffects();
        Array<String> completedEffects = new Array<>();
        for (ObjectMap.Entry<String, StatusEffect> entry : statusEffects) {
            String type = entry.key;
            StatusEffect effect = entry.value;
            int value = effect.tick();
            if (value != 0) {
                switch (type) {
                    // Status effect execution here
                    case "Poison" -> {
                        Move mv = new TrueDamageMove(value, 0);
                        mv.execute(character);
                        addLog(tick, String.format("%s received %s Poison damage", character.displayName(), value));
                    }

                    case "Fortitude" -> {
                        Move mv = new ShieldMove(value, 0);
                        mv.execute(character);
                        addLog(tick, String.format("%s gained %s shield", character.displayName(), value));
                    }

                    default -> {
                        System.out.println("Unregistered Status Effect: " + type);
                    }
                }
            }
            if (effect.getValue() == 0) {
                completedEffects.add(type);
            }
        }
        for (String type : completedEffects) {
            statusEffects.remove(type);
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
            player.clearStatusEffects();
            player.setShield(0);
        }

        if (enemies.size == 0) {
            win = true;
            player.clearStatusEffects();
            player.setShield(0);
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
