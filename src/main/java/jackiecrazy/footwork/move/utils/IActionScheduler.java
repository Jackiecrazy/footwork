package jackiecrazy.footwork.move.utils;

import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import net.minecraft.world.entity.Entity;

import java.util.List;

public interface IActionScheduler {

    List<Action> getGraveyard();

    boolean executing();

    void start(Entity performer, Entity target);

    void reset();

    void tick(Entity performer, Entity target);

    void jumpTo(int jumpCode, Entity performer, Entity target);

    int trigger(Action action, Action parent, Entity performer, Entity target);

    int getTimer(TimerAction action);

    void immediatelyExpire(TimerAction action);

    <T> T getData(Action a);

    void setData(Action a, Object b);

    class DataWrapper<T> {
        private T instance;

        public DataWrapper(T item) {
            instance = item;
        }

        public T getInstance() {
            return instance;
        }

        public void setInstance(T instance) {
            this.instance = instance;
        }
    }
}