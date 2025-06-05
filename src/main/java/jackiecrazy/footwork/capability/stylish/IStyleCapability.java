package jackiecrazy.footwork.capability.stylish;

import net.minecraft.nbt.CompoundTag;

import java.awt.*;

public interface IStyleCapability {
    //melee trigger timer, ranged trigger timer, trigger bar, orb set
    //there's a ranged counter and a melee counter. Each goes up by 1 per tick, up to 20.
    //If you attack with melee/ranged, the counter goes back to 0 and you get 1 charge per 10 ticks on the counter.
    //skills can add extra trigger counter, resolve either trigger counter, or directly add to the trigger bar.
    //if trigger bar is at 10, next attack is a trigger attack. Trigger attack code is handled by pwd.
    //also track orbs here. There can be one orb of each color, so just hold a hash set of colors.

    float getAdrenaline();
    void setAdrenaline(float to);
    float addAdrenaline(float amount);

    void tick();

    void processAttack(boolean melee);

    int getTriggerTime(boolean melee);
    void setTriggerTime(int time, boolean melee);
    void addTriggerTime(int time, boolean melee);

    int getTriggerBar();
    void setTriggerBar(int amnt);
    void rsetTriggerBar();
    void addTriggerBar(int amnt);
    boolean canTrigger();

    void addOrb(Color of);
    boolean hasOrb(Color of);
    void removeOrb(Color of);

    CompoundTag write();

    void read(CompoundTag from);
}
