package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.ResourceRequest.*;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.*;

import java.util.Objects;

public class Defender extends Building{

    private String action;
    protected Queue<Resource> magazine;
    protected int magSize;
    protected float baseDef = 1;;
    protected int flatDef = 0;
    protected int defCount = 1;

    public Defender(int coolDown, int magSize, boolean[][] shape) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "defender");
        this.magazine = new Queue<>(magSize);
        this.magSize = magSize;
        this.action = "Heal";
    }

    public Defender(JsonValue data) {
        super(data);
        this.magSize = data.getInt("MagSize");
        this.magazine = new Queue<>(magSize);
        this.action = data.getString("Action");
        this.baseDef = data.getFloat("BaseDef");
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name).append(" #").append(idNum)
            .append("\nSpeed ")
            .append(getSpeed())
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
                moves.addAll(defend());
            } else {
                currCooldown = cooldownTimer - getSpeed();
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

    private int calculateVal(Resource r) {
        return (int) (baseDef * r.getDmgMult() + flatDef);
    }


    public Array<Move> defend() {
        Array<Move> result = new Array<>();
        int value = calculateVal(magazine.removeFirst());
        Move move = null;
        switch (action) {
            case ("Heal") -> {
                move = new HealMove(value, 0);
            }
            case ("Shield") -> {
                move = new ShieldMove(value, 0);
            }
            case ("ShieldOT") -> {
                move = new StatusEffectMove("Fortitude", value, 4, .5f, true, true, 0);
            }
        }
        if (move!= null) {
            for (int i = 0; i < defCount; i++) {
                result.add(move);
            }
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

}
