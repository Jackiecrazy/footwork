package jackiecrazy.footwork.move;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.action.timer.ProjectHitboxAction;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.move.utils.ActionContext;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class ActionSetWrapper {
    public final Stack<DataWrapper<?>> stack = new Stack<>();
    public final HashMap<String, Object> context = new HashMap<>();
    protected final List<Action> graveyard = new ArrayList<>();
    protected final List<Action> actions;
    private final List<Tuple<TimerAction, Integer>> activeTimers = new ArrayList<>();
    private final HashMap<Action, Object> extraData = new HashMap<>();
    private TimerAction currentMove;
    private int index = 0;

    public ActionSetWrapper(List<Action> actions) {
        this.actions = actions;
    }

    public List<Tuple<TimerAction, Integer>> getActiveTimers() {
        return activeTimers;
    }

    public <T> T getContext(String a) {
        return (T) (context.get(a));
    }

    public ActionSetWrapper addContext(String a, Object b) {
        context.put(a, b);
        return this;
    }

    public ActionSetWrapper appendContext(HashMap<String, Object> ctx) {
        context.putAll(ctx);
        return this;
    }

    public ActionSetWrapper appendContext(ArgumentContext ctx) {
        if (ctx != null)
            context.putAll(ctx.context);
        return this;
    }

    public List<Action> getGraveyard() {
        return graveyard;
    }

    public TimerAction getCurrentMove() {
        return currentMove;
    }

    public boolean executing() {
        return index < actions.size() && index >= 0;
    }

    public void start(Entity performer, Entity target) {
        index = 0;
        currentMove = continueUntilValid(performer, target);
    }

    public void reset() {
        index = 0;
        extraData.clear();
        activeTimers.clear();
        graveyard.clear();
    }

    public void tick(Entity performer, Entity target) {
        int jumpCode = 0;
        for (Tuple<TimerAction, Integer> tuple : activeTimers) {
            //fixme CME
            tuple.setB(tuple.getB() + 1);
            int tickResult = tuple.getA().tick(this, performer, target);
            if (tickResult > 0) {
                jumpCode = tickResult;
                break;
            }
        }
        if (currentMove == null || currentMove.isFinished(this, performer, target)) {
            //natural progression//
            index++;
            currentMove = continueUntilValid(performer, target);
        }
        //clearing happens after natural progression to prevent clears breaking timers
        activeTimers.removeIf((entry) -> {
            if (entry.getA().isFinished(this, performer, target)) {
                graveyard.add(entry.getA());
                entry.getA().stop(generateContext(performer, target, entry.getA()), false);
                return true;
            }
            return false;
        });
        if (jumpCode > 0) {
            jumpTo(jumpCode, performer, target);
        }
    }

    public @NotNull ActionContext generateContext(Entity performer,
                                                  Entity target,
                                                  Action parent) {
        ActionContext ret = new ActionContext(this, parent, performer, target);
        ret.addContext(context);
        return ret;
    }

    public void jumpTo(int jumpCode, Entity performer, Entity target) {
        //goto, reset everything//
        reset();
        index = jumpCode - 1;
        currentMove = continueUntilValid(performer, target);
    }

    private TimerAction continueUntilValid(Entity performer, Entity target) {
        Action act;
        for (; index < actions.size(); index++) {
            act = actions.get(index % actions.size());
            if (act.canRun(generateContext(performer, target, null))) trigger(act, null, performer, target);
            if (act instanceof TimerAction ta) return ta;
        }
        return null;
    }


    public int trigger(Action action, Action parent, Entity performer, Entity target) {
        //if action and not in graveyard, execute. If not repeatable, put in graveyard.
        //if timer action and not in graveyard, if not active, place and start, then if not repeatable, put in graveyard.
        if (graveyard.contains(action)) return 0;
        final ActionContext ctx = generateContext(performer, target, parent);
        try {
            if (action instanceof TimerAction ta) {
                if (activeTimers.stream().noneMatch(a -> a.getA() == ta)) {
                    activeTimers.add(new Tuple<>(ta, 0));
                    ta.start(this, performer, target);
                    ta.tick(this, performer, target);
                }
                return 0;
            }
            return action.perform(ctx);
        }catch (Exception e){
            if(action.logsErrors())
                Footwork.LOGGER.error("failed to execute action block "+action.serializeToJson(), e.fillInStackTrace());
        }finally {
            if (!action.repeatable(ctx) && !ProjectHitboxAction.multihit_override)
                graveyard.add(action);//continuous tasks are handled by active timers
        }
        return 0;
    }

    public int getTimer(TimerAction action) {
        return activeTimers.stream().filter(a -> a.getA() == action).findFirst().map(Tuple::getB).orElse(-1);
    }

    public void immediatelyExpire(TimerAction action) {
        activeTimers.stream().filter(a -> a.getA() == action).findFirst().ifPresent(a -> a.setB(99999));
    }

    public <T> T getData(Action a) {
        return (T) (extraData.get(a));
    }

    public void setData(Action a, Object b) {
        extraData.put(a, b);
    }

    public static class DataWrapper<T> {
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
