package jackiecrazy.footwork.capability.stylish;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public interface IStyleCapability {
    //melee trigger timer, ranged trigger timer, trigger bar, orb set
    //there's a ranged counter and a melee counter. Each goes up by 1 per tick, up to 20.
    //If you attack with melee/ranged, the counter goes back to 0 and you get 1 charge per 10 ticks on the counter.
    //skills can add extra trigger counter, resolve either trigger counter, or directly add to the trigger bar.
    //if trigger bar is at 10, next attack is a trigger attack. Trigger attack code is handled by pwd.
    //also track orbs here. There can be one orb of each color, so just hold a hash set of colors.

    boolean isCombatMode();
    void toggleCombatMode(boolean on);

    float getAdrenaline();
    default boolean maxAdrenaline(){
        return getAdrenaline()>=1;
    }
    void setAdrenaline(float to);
    float addAdrenaline(float amount);
    default void resetAdrenaline(){
        setAdrenaline(0);
    }

    void tick();

    void processAttack(boolean melee);

    float getCombo();
    void addCombo(float amount, String source);
    //how freshness?
    void resetCombo();
    void refresh();

    int getTriggerTime(boolean melee);
    void setTriggerTime(int time, boolean melee);
    void addTriggerTime(int time, boolean melee);

    int getTriggerBar();
    void setTriggerBar(int amnt);
    void resetTriggerBar();
    void addTriggerBar(int amnt);
    boolean canTrigger();

    boolean isDeathDoor();
    boolean avoidDeath();
    boolean isDyingFast();
    void stabilize();

    Collection<String> getFreshness();
    float getFreshness(String source);

    CompoundTag write();

    void read(CompoundTag from);
}
