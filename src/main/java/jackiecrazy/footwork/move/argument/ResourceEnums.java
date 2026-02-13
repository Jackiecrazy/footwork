package jackiecrazy.footwork.move.argument;

import jackiecrazy.footwork.capability.resources.CombatData;
import jackiecrazy.footwork.capability.stylish.StylishData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

public class ResourceEnums {
    public enum FORMAT {
        CURRENT((current, max) -> current),

        PERCENTAGE((current, max) -> max == 0 ? 0 : current / max),

        MAX((current, max) -> max);

        private final ToDoubleBiFunction<Double, Double> computer;

        FORMAT(ToDoubleBiFunction<Double, Double> computer) {
            this.computer = computer;
        }

        /**
         * Compute the formatted value given current and max.
         * Handles max == 0 safely (returns 0 for percentage, etc.).
         */
        public double apply(double current, double max) {
            return computer.applyAsDouble(current, max);
        }

        public double resolve(LivingEntity entity, TYPE type) {
            double current = type.getCurrent(entity);
            double max = type.getMax(entity);
            return apply(current, max);
        }
    }

    public enum TYPE {
        HEALTH(LivingEntity::getHealth, LivingEntity::getMaxHealth),
        POSTURE(a -> CombatData.getCap(a).getPosture(), a -> CombatData.getCap(a).getMaxPosture()),
        SPIRIT(a -> CombatData.getCap(a).getSpirit(), a -> CombatData.getCap(a).getMaxSpirit()),
        ADRENALINE(a -> StylishData.getCap(a).getAdrenaline(), a -> 1d),
        COMBO(a -> StylishData.getCap(a).getCombo(), a -> 3d),
        INTERNAL_DAMAGE(a -> CombatData.getCap(a).getRecordedDamage(), LivingEntity::getMaxHealth),
        RALLY(a -> CombatData.getCap(a).getRally(), a -> CombatData.getCap(a).getMaxPosture());
        private ToDoubleFunction<LivingEntity> current, max;

        TYPE(ToDoubleFunction<LivingEntity> current,
             ToDoubleFunction<LivingEntity> max) {
            this.current = current;
            this.max = max;
        }

        public double getCurrent(LivingEntity reference) {
            return current.applyAsDouble(reference);
        }

        public double getMax(LivingEntity reference) {
            return max.applyAsDouble(reference);
        }

        public double getPerc(LivingEntity reference) {
            return getCurrent(reference) / getMax(reference);
        }

    }

    public enum ResourceFormat {
        NUMBER,
        PERCENTAGE;

        public double apply(Double value, double max) {
            if (this == NUMBER) return value;
            return max * value;
        }
    }

    public enum ResourceOperation {

        SET((entity, type, value, format) -> {
            double max = type.getMax(entity);
            double effectiveValue = format.apply(value, max);  // e.g. percentage → value/100 * max
            setValue(entity, type, effectiveValue);            // your setter
            return true;                                       // set always succeeds
        }),

        ADD((le, type, value, format) -> {
            double max = type.getMax(le);
            float f = (float) format.apply(value, max);  // e.g. percentage → value/100 * max
            double current = type.getCurrent(le);
            double newValue = Math.min(Math.max(current + f, 0), max);  // clamp
            switch (type) {
                case POSTURE -> CombatData.getCap(le).addPosture(f);
                case RALLY -> CombatData.getCap(le).addRally(f);
                case SPIRIT -> CombatData.getCap(le).addSpirit(f);
                case ADRENALINE -> StylishData.getCap(le).addAdrenaline(f);
                case COMBO -> StylishData.getCap(le).addCombo(f, "resource operation");
                default -> setValue(le, type, newValue);
            }
            return true;  // always succeeds (can go negative if you allow)
        }),

        CONSUME((le, type, value, format) -> {
            double max = type.getMax(le);
            double effectiveCost = format.apply(value, max);   // e.g. percentage → value/100 * max
            double current = type.getCurrent(le);
            float f = value.floatValue();
            switch (type) {
                case POSTURE -> {
                    return CombatData.getCap(le).consumePosture(f) == 0;
                }
                case RALLY -> {
                    CombatData.getCap(le).rally(f);
                    return true;
                }
                case SPIRIT -> {
                    return CombatData.getCap(le).consumeSpirit(f);
                }
                case ADRENALINE -> {
                    if (StylishData.getCap(le).getAdrenaline() < 1) return false;
                    StylishData.getCap(le).resetAdrenaline();
                    return true;
                }
                case COMBO -> {
                    if (StylishData.getCap(le).getAdrenaline() < effectiveCost) return false;
                    StylishData.getCap(le).resetCombo();
                    return true;
                }
                default -> {
                    if (current >= value) {
                        setValue(le, type, current - value);
                        return true;
                    }
                    return false;
                }
            }
        });

        private final QuadFunction<LivingEntity, TYPE, Double, ResourceFormat, Boolean> operator;

        ResourceOperation(QuadFunction<LivingEntity, TYPE, Double, ResourceFormat, Boolean> operator) {
            this.operator = operator;
        }

        // ── Helpers (adapt to your actual setters/getters) ──
        private static void setValue(LivingEntity le, TYPE type, double newValue) {
            float f = (float) newValue;
            switch (type) {
                case HEALTH -> le.setHealth(f);  // Minecraft uses float
                case POSTURE -> CombatData.getCap(le).setPosture(f);
                case RALLY -> CombatData.getCap(le).setRally(f);
                case SPIRIT -> CombatData.getCap(le).setSpirit(f);
                case ADRENALINE -> StylishData.getCap(le).setAdrenaline(f);
            }
        }

        /**
         * Perform the operation.
         *
         * @param entity The target entity.
         * @param type   Resource type (HEALTH, QI, etc.).
         * @param value  The amount (absolute or percentage raw value).
         * @param format How to interpret value (CURRENT=absolute, PERCENTAGE= % of max).
         * @return true if success (always for SET/ADD, conditional for CONSUME).
         */
        public boolean apply(LivingEntity entity, TYPE type, double value, ResourceFormat format) {
            return operator.apply(entity, type, value, format);
        }

    }

    // Helper functional interface (or use lambda directly)
    @FunctionalInterface
    interface QuadFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
}
