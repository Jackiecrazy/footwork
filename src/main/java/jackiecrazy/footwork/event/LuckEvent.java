package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LuckEvent extends LivingEvent {
    private final float origChance;

    public LuckEvent(LivingEntity entity, float chance) {
        super(entity);
        origChance=chance;
    }

    public float getOriginalChance() {
        return origChance;
    }

    public static class Pre extends LuckEvent{
        private float chance;
        private TriState execute=TriState.DEFAULT;
        public TriState getResult() {
            return execute;
        }

        public void setResult(TriState execute) {
            this.execute = execute;
        }

        public Pre(LivingEntity entity, float chance) {
            super(entity, chance);
            this.chance = chance;
        }

        public float getChance() {
            return chance;
        }

        public void setChance(float chance) {
            this.chance = chance;
        }
    }
    public static class Post extends LuckEvent {
        private final boolean pass;

        public Post(LivingEntity entity, float chance, boolean success) {
            super(entity, chance);
            pass = success;
        }

        public boolean isPass() {
            return pass;
        }
    }
}
