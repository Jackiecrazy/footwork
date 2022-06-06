package jackiecrazy.footwork.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraftforge.eventbus.api.Event.HasResult;

public class LuckEvent extends LivingEvent {
    private final float origChance;

    public LuckEvent(LivingEntity entity, float chance) {
        super(entity);
        origChance=chance;
    }

    public float getOriginalChance() {
        return origChance;
    }
    @HasResult
    public static class Pre extends LuckEvent{
        private float chance;

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
