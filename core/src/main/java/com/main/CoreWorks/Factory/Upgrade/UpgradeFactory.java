package com.main.CoreWorks.Factory.Upgrade;

import com.badlogic.gdx.utils.*;
import java.util.Random;

public class UpgradeFactory {

    public static Upgrade randomUpgrade(Random random, float strength) {
        float buildingType = random.nextFloat();
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
            numUpgrades = Math.min(Math.max((int) (Math.expm1(random.nextGaussian(-1.135, .886)) + 1), 0), 3) + 1;
        } else if (strength < 1.2) {
            /*
            est. distribution
            1: 50%
            2: 35%
            3: 10%
            4: 5%
             */
            numUpgrades = Math.min(Math.max((int) (Math.expm1(random.nextGaussian(0, .668)) + 1), 0), 3) + 1;
        } else {
            /*
            est. distribution
            1: 20%
            2: 50%
            3: 20%
            4: 10%
             */
            numUpgrades = Math.min(Math.max((int) (Math.expm1(random.nextGaussian(.43, .512)) + 1), 0), 3) + 1;
        }

        if (upgradesGroup.contains("Speed", true)) {
            upgradesGroup.removeValue("Speed", true);
            upgradesGroup.add("FlatSpeed");
            upgradesGroup.add("SpeedMult");
        }

        if (upgradesGroup.contains("Damage", true)) {
            upgradesGroup.removeValue("Damage", true);
            upgradesGroup.add("BaseDamage");
            upgradesGroup.add("DamageMult");
        }

        Array<UpgradeAspect> upgrades = new Array<>();

        while (numUpgrades > 0 && upgradesGroup.size > 0) {
            numUpgrades--;
            UpgradeAspect thisUpgrade = null;
            String category = "";
            if (upgradesGroup.size > 1) {
                category = upgradesGroup.get(random.nextInt(upgradesGroup.size - 1));
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
                case "FlatSpeed":
                    adjPower = (float) (power * 0.2);
                    if (adjPower > 0.05) {
                        adjPower = roundDP(adjPower, 2);
                    } else {
                        adjPower = 0.05f;
                    }
                    thisUpgrade = new FlatSpeedUpgrade(adjPower);
                    break;
                case "SpeedMult":
                    adjPower = (float) (power * 0.1);
                    if (adjPower > 0.05) {
                        adjPower = roundDP(adjPower, 2);
                    } else {
                        adjPower = 0.05f;
                    }
                    thisUpgrade = new SpeedMultUpgrade(adjPower);
                    break;
                case "Buffer":
                    adjPower = (float) (power * 2);
                    if (!((int) adjPower > 0)) {
                        adjPower = 1;
                    }
                    thisUpgrade = new BufferSizeUpgrade((int) adjPower);
                    break;
                case "MineMult":
                    adjPower = (float) (power / 2);
                    if (!((int) adjPower > 0)) {
                        adjPower = 0;
                    }
                    thisUpgrade = new MineMultUpgrade((int) adjPower);
                    break;
                case "BaseDamage":
                    adjPower = (float) power;
                    if (!((int) adjPower > 0)) {
                        adjPower = 1;
                    }
                    thisUpgrade = new FlatDamageUpgrade(Math.round(adjPower));
                    break;
                case "DamageMult":
                    adjPower = (float) (power * 0.1);
                    if (adjPower > 0.05) {
                        adjPower = roundDP(adjPower, 2);
                    } else {
                        adjPower = 0.05f;
                    }
                    thisUpgrade = new BaseDamageUpgrade(adjPower);
                    break;
            }
            if (thisUpgrade != null) {
                upgrades.add(thisUpgrade);
            }
        }

        return new Upgrade(upgrades);

    }
}
