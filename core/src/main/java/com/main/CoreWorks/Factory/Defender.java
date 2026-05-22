package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.moveset.DamageMove;
import com.main.CoreWorks.moveset.HealMove;
import com.main.CoreWorks.moveset.Move;

public class Defender extends Building{

    protected Queue<Resource> magazine;
    protected int magSize;
    protected float baseDef = 1;

    public Defender(int coolDown, int magSize, boolean[][] shape) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "defender");
        this.magazine = new Queue<>(magSize);
        this.magSize = magSize;
    }

    public Defender(JsonValue data) {
        super(data);
        this.magSize = data.getInt("MagSize");
        this.magazine = new Queue<>(magSize);
        this.baseDef = data.getFloat("BaseDef");
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name)
            .append("\nSpeedMult ")
            .append(speedMultiplier)
            .append('\n')
            .append("Magazine\n")
            .append("<-First   Last->\n")
            .append(magazine)
            .toString();
    }

    @Override
    public Move updateTick() {
        currCooldown += speedMultiplier;
        if (currCooldown >= cooldownTimer) {
            currCooldown = cooldownTimer - speedMultiplier;
            if (magazine.notEmpty()) {
                currCooldown = 0;
                return defend();
            }
        }
        return null;
    }

    public void addToAnythingQueue(Resource x) {
        addToMag(x);
    }

    public void addToMag(Resource x) {
        magazine.addLast(x);
    }

    public HealMove defend() {
        return new HealMove((int) (magazine.removeFirst().getDmgMult() * baseDef), 0);
    }

    @Override
    public Array<ResourceRequest> generateDemandRequests() {
        Array<ResourceRequest> requests = new Array<>();
        int magMissing = magSize - magazine.size;
        if (magMissing > 0) {
            requests.add(new AnythingRequest(this, magMissing));
        }
        return requests;
    }
}
