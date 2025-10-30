package jackiecrazy.footwork.capability.stylish;

import net.minecraft.nbt.CompoundTag;

import java.awt.*;

public class NoStyleCap implements IStyleCapability {


    @Override
    public boolean isCombatMode() {
        return true;
    }

    @Override
    public void toggleCombatMode(boolean on) {

    }

    @Override
    public float getAdrenaline() {
        return 0;
    }

    @Override
    public void setAdrenaline(float to) {

    }

    @Override
    public float addAdrenaline(float amount) {
        return 0;
    }

    @Override
    public void tick() {

    }

    @Override
    public void processAttack(boolean melee) {

    }

    @Override
    public float getCombo() {
        return 1;
    }

    @Override
    public void addCombo(float amnt, String source) {

    }

    @Override
    public void resetCombo() {

    }

    @Override
    public void refresh() {

    }

    @Override
    public int getTriggerTime(boolean melee) {
        return 0;
    }

    @Override
    public void setTriggerTime(int time, boolean melee) {

    }

    @Override
    public void addTriggerTime(int time, boolean melee) {

    }

    @Override
    public int getTriggerBar() {
        return 0;
    }

    @Override
    public void setTriggerBar(int amnt) {

    }

    @Override
    public void resetTriggerBar() {

    }

    @Override
    public void addTriggerBar(int amnt) {

    }

    @Override
    public boolean canTrigger() {
        return false;
    }

    @Override
    public void addOrb(Color of) {

    }

    @Override
    public boolean hasOrb(Color of) {
        return false;
    }

    @Override
    public void removeOrb(Color of) {

    }

    @Override
    public CompoundTag write() {
        return null;
    }

    @Override
    public void read(CompoundTag from) {

    }
}
