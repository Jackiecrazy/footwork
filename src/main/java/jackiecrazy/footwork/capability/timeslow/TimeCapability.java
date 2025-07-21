package jackiecrazy.footwork.capability.timeslow;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeCapability implements ITimeChange {
    private final ArrayList<Tuple<Integer, Double>> modify = new ArrayList<>();
    private double speed = 1;
    private int longest;
    private double partialTick = 0;

    public TimeCapability() {
    }

    private void recalculateSpeed() {
        double spd = 1;
        longest = 0;
        for (Tuple<Integer, Double> entry : modify) {
            if (entry.getB() < spd) spd = entry.getB();
            if (entry.getA() > longest) longest = entry.getA();
        }
        speed = spd;
    }

    @Override
    public void alterSpeed(int ticks, double speed) {
        modify.add(new Tuple<>(ticks, speed));
        recalculateSpeed();
    }

    @Override
    public int tickDown(int currentTick) {
        modify.forEach(a -> a.setA(a.getA() - 1));
        if (modify.stream().anyMatch(a -> a.getA() <= 0)) {
            modify.removeIf(a -> a.getA() <= 0);
            recalculateSpeed();
        }
        partialTick += speed;
        int ret = -1;
        while (partialTick >= 1) {
            partialTick -= 1;
            ret += 1;
        }
        return ret;
    }

    @Override
    public void resetSpeed() {
        speed = 1;
    }

    @Override
    public void setRawSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public float getPartialTick(float originalPT) {
        if (speed >= 1) return originalPT;
        return (float) Math.min(1, partialTick + originalPT * speed);
    }

    @Override
    public double getEffectiveSpeed() {
        return speed;
    }

    @Override
    public int getTimeRemaining() {
        return longest;
    }
}
