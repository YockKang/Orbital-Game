package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Resources.Modifier;
import com.main.CoreWorks.Resources.ModifierRegistry;
import com.main.CoreWorks.util.Pair;

import java.util.Random;

public class UpgradeFactory {

    public static Upgrade randomUpgrade(Random random, float strength) {
        double buildingType = random.nextDouble();
        String buildingName = "";

        if (buildingType < 0.25) {
            buildingName = "Shooter";
        } else if (buildingType < 0.45) {
            buildingName = "Defender";
        } else if (buildingType < 0.55) {
            buildingName = "Refiner";
        } else if (buildingType < 0.75) {
            buildingName = "Miner";
        } else if (buildingType < 0.95) {
            buildingName = "Generic";
        } else {
            buildingName = "Random";
        }

        return randomTypedUpgrade(random, strength, buildingName);

    }

    private static float roundDP(float input, int n) {
        return (float) (Math.round((double) input * Math.pow(10, n)) / Math.pow(10, n));
    }

    public static Upgrade randomTypedUpgrade(Random random, float strength, String type) {

        Array<String> upgradesGroup = new Array<>(UpgradeTypeRegistry.get(type));

        int numUpgrades = 1;

        /*
        distribution is being worked on
        currently by cutoff
        0-1
        1-1.2
        1.2+
         */
        if (strength <= 1) {
            /*
            est. distribution
            1: 90%
            2: 8%
            3: 1.5%
            4: .5%
             */
            numUpgrades = Math.min(Math.max((int) Math.expm1(random.nextGaussian(-1.135, .886)) + 1, 0), 3) + 1;
        } else if (strength < 1.2) {
            /*
            est. distribution
            1: 50%
            2: 35%
            3: 10%
            4: 5%
             */
            numUpgrades = Math.min(Math.max((int) Math.expm1(random.nextGaussian(0, .668)) + 1, 0), 3) + 1;
        } else {
            /*
            est. distribution
            1: 20%
            2: 50%
            3: 20%
            4: 10%
             */
            numUpgrades = Math.min(Math.max((int) Math.expm1(random.nextGaussian(.43, .512)) + 1, 0), 3) + 1;
        }

        if (upgradesGroup.contains("Speed", true)) {
            upgradesGroup.removeValue("Speed", false);
            upgradesGroup.add("FlatSpeed");
            upgradesGroup.add("SpeedMult");
        }

        if (upgradesGroup.contains("Damage", true)) {
            upgradesGroup.removeValue("Damage", false);
            upgradesGroup.add("BaseDamage");
            upgradesGroup.add("DamageMult");
        }

        Array<UpgradeAspect> upgrades = new Array<>();

        while (numUpgrades > 0 && upgradesGroup.size > 0) {
            numUpgrades--;
            UpgradeAspect thisUpgrade = null;
            String category = "";
            if (upgradesGroup.size > 1) {
                category = upgradesGroup.get(random.nextInt(upgradesGroup.size));
                upgradesGroup.removeValue(category, true);
            } else {
                category = upgradesGroup.get(0);
                upgradesGroup.removeValue(category, true);
            }
            double m = strength + .3;
            double var = Math.pow(1 / strength, 2) / 2;
            double s2 = Math.log(1 + var / m*m);
            double mu  = Math.log(m) - s2/2;
            double power = Math.expm1(random.nextGaussian(mu, Math.sqrt(s2)))+ 1;
            float adjPower = 0;
            switch (category) {
                case "FlatSpeed" -> {
                    adjPower = (float) (power * 0.2);
                    if (adjPower > 0.05) {
                        adjPower = roundDP(adjPower, 2);
                    } else {
                        adjPower = 0.05f;
                    }
                    thisUpgrade = new FlatSpeedUpgrade(adjPower);
                }
                case "SpeedMult" -> {
                    adjPower = (float) (power * 0.1);
                    if (adjPower > 0.05) {
                        adjPower = roundDP(adjPower, 2);
                    } else {
                        adjPower = 0.05f;
                    }
                    thisUpgrade = new SpeedMultUpgrade(adjPower);
                }
                case "Buffer" -> {
                    adjPower = (float) (power * 2);
                    if (!((int) adjPower > 0)) {
                        adjPower = 1;
                    }
                    thisUpgrade = new BufferSizeUpgrade((int) adjPower);
                }
                case "MineMult" -> {
                    adjPower = (float) (power / 2);
                    if (!((int) adjPower > 0)) {
                        adjPower = 0;
                    }
                    thisUpgrade = new MineMultUpgrade((int) adjPower);
                }
                case "BaseDamage" -> {
                    adjPower = (float) power;
                    if (!((int) adjPower > 0)) {
                        adjPower = 1;
                    }
                    thisUpgrade = new FlatDamageUpgrade(Math.round(adjPower));
                }
                case "DamageMult" -> {
                    adjPower = (float) (power * 0.1);
                    if (adjPower > 0.05) {
                        adjPower = roundDP(adjPower, 2);
                    } else {
                        adjPower = 0.05f;
                    }
                    thisUpgrade = new BaseDamageUpgrade(adjPower);
                }
                case "ResourceMod" -> {
                    Pair<String, Pair<Float, Array<String>>> mod = ModifierRegistry.getRandom(random);
                    String modType = mod.first;
                    float modVal = 0f;
                    String modStr = null;
                    if (mod.second.second == null) {
                        modVal = (float) (power * mod.second.first);
                    } else {
                        modStr = mod.second.second.get(random.nextInt(mod.second.second.size));
                    }
                    Modifier newMod = new Modifier(modType, modVal, modStr);
                    thisUpgrade = new ResourceModifierUpgrade(newMod);
                }
            }
            if (thisUpgrade != null) {
                upgrades.add(thisUpgrade);
            }
        }

        return new Upgrade(upgrades);

    }
}
