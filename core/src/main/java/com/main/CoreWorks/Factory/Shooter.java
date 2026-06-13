package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.ResourceRequest.*;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.*;

public class Shooter extends Building {

    protected Queue<Resource> magazine;
    protected int magSize;
    protected float baseDmg = 1f;
    protected int flatDmg = 0;
    protected int attackCount = 1;

    public Shooter(int coolDown, int magSize, boolean[][] shape) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "shooter");
        this.magazine = new Queue<>(magSize);
        this.magSize = magSize;
    }

    public Shooter(JsonValue data) {
        super(data);
        this.magSize = data.getInt("MagSize");
        this.magazine = new Queue<>(magSize);
        this.baseDmg = data.getFloat("BaseDmg");
        if (data.get("AttackCount") != null) {
            attackCount = data.getInt("AttackCount");
        }
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name).append(" #").append(idNum)
            .append("\nSpeed ")
            .append(getSpeed())
            .append("\nDamage:\n")
            .append(attackCount).append(" * (x * ").append(baseDmg).append(" + ").append(flatDmg).append(")")
            .append('\n')
            .append("Magazine\n")
            .append("<-First   Last->\n")
            .append(magazine)
            .toString();
    }

    @Override
    public Array<Move> updateEnabled() {
        currCooldown += getSpeed();
        Array<Move> moves = new Array<>();
        while (currCooldown >= cooldownTimer) {
            if (magazine.notEmpty()) {
                currCooldown -= cooldownTimer;
                moves.addAll(shoot());
            } else {
                currCooldown = cooldownTimer;
                break;
            }
        }
        return moves;
    }

    public void addToAnythingQueue(Resource x) {
        addToMag(x);
    }

    public void addToMag(Resource x) {
        magazine.addLast(x);
    }

    private int calculateDmg(Resource r) {
        return (int) (baseDmg * r.getDmgMult() + flatDmg);
    }

    public Array<Move> shoot() {
        Array<Move> result = new Array<>();
        Move dmg = new DamageMove(calculateDmg(magazine.removeFirst()), 0);
        for (int i = 0; i < attackCount; i++) {
            result.add(dmg);
        }
        return result;
    }

    @Override
    public Array<ResourceRequest> generateDemandRequests() {
        Array<ResourceRequest> requests = new Array<>();
        int magMissing = magSize - magazine.size;
        if (magMissing > 0) {
            if (whitelist != null) {
                requests.add(new WhitelistRequest(this, magMissing, whitelist));
            } else {
                requests.add(new AnythingRequest(this, magMissing));
            }
        }
        return requests;
    }

    @Override
    public void clear() {
        super.clear();
        this.magazine.clear();
    }

    @Override
    public int getCapacityMult() {
        return magSize;
    }

    @Override
    public void setCapacityMult(int newCap) {
        magSize = newCap;
    }

    @Override
    public void changeCapacityMult(int delta) { magSize += delta; }

    public void changeBaseDamage(float delta) {
        baseDmg += delta;
    }

    public void changeFlatDamage(float delta) {
        flatDmg += delta;
    }
}
