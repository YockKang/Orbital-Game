package com.main.CoreWorks.Generators;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.Upgrade.*;
import com.main.CoreWorks.Rewards.*;
import com.main.CoreWorks.RunPersistence.*;
import com.main.CoreWorks.database.*;

import java.util.Random;

public class RewardGenerator {
    public static Array<Reward> generateCombatReward(RunState runState) {
        Array<Reward> rewards = new Array<>();

        // Add the Rewards in the code below
        rewards.add(randomBuildingReward(runState));
        rewards.add(randomBuildingReward(runState));
        rewards.add(randomBuildingReward(runState));
        rewards.add(randomUpgradeReward(runState));
        rewards.add(randomUpgradeReward(runState));
        rewards.add(randomUpgradeReward(runState));

        // Return statement below
        return rewards;
    }

    public static Array<Reward> generateRestNodeReward(RunState runState) {
        Array<Reward> rewards = new Array<>();

        // Add the Rewards in the code below
        rewards.add(randomUpgradeReward(runState));
        rewards.add(randomUpgradeReward(runState));

        // Return statement below
        return rewards;
    }

    // Handles the building reward generation with the following odds:
    // Tier 0: 70%, Tier 1: 20%, Tier 2: 9%, Tier 3: 1%
    private static Reward randomBuildingReward(RunState runState) {
        int num = runState.getRandom().nextInt(100);

        if (num <= 70) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 0));
        } else if (num <= 90) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 1));
        } else if (num <= 98) {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 2));
        } else {
            return new AddBuildingReward(BuildingTierDatabase.getRandomBuilding(runState.getRandom(), 3));
        }
    }

    private static Reward randomUpgradeReward(RunState runState) {
        Random random = runState.getRandom();
        MapNode node = runState.getCurrNode();
        return new AddUpgradeReward(
            UpgradeFactory.randomUpgrade(random, node.getMultiplier()));
    }
}
