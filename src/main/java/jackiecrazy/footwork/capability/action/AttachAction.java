package jackiecrazy.footwork.capability.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.CallbackActionWrapper;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class AttachAction implements IAttachAction {
    //    private static final MovesetManager literallyNothing = new MovesetManager(null);
    private final Entity tiedTo;
    private final ConcurrentHashMap<Entity, ConcurrentLinkedDeque<ActionSetWrapper>> marks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Entity, ConcurrentLinkedDeque<CallbackActionWrapper>> temp = new ConcurrentHashMap<>();

    //    private MovesetManager manager = literallyNothing;
//
    public AttachAction(Entity linked) {
        tiedTo = linked;
    }
//
//    @Override
//    public MovesetManager getMovesetManager() {
//        return manager;
//    }
//
//    @Override
//    public void setMovesetManager(MovesetManager mm) {
//        manager = mm;
//    }

    @Override
    public void mark(Entity en, ActionSetWrapper d) {
        marks.putIfAbsent(en, new ConcurrentLinkedDeque<>());
        marks.get(en).add(d);
        d.start(en, tiedTo);
    }

    private void flushMarks() {

    }

    @Override
    public void triggerCallback(String s, ArgumentContext additionalContext) {
        temp.clear();
        marks.forEach((en, actionSetWrappers) -> actionSetWrappers.forEach(a -> {
            if (a instanceof CallbackActionWrapper b) {
                temp.putIfAbsent(en, new ConcurrentLinkedDeque<>());
                temp.get(en).add(b);
            }
        }));
        Entity actOn;
        if (additionalContext != null) actOn = additionalContext.target();
        else actOn = tiedTo;
        temp.forEach((entity, actionSetWrappers) -> actionSetWrappers.forEach(a -> a.triggerCallback(entity, actOn, s, additionalContext)));
    }

    @Override
    public void update() {
        if (marks.isEmpty()) return;
        //fixme can CME ASW
        marks.forEach((entity, lists) -> lists.forEach(ms -> ms.tick(entity, tiedTo)));
        marks.values().forEach(wrappers -> wrappers.removeIf(wrapper -> !wrapper.executing()));
        marks.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public void stopEverything(){
        marks.forEach((entity, lists) -> lists.forEach(ms -> ms.stop(entity, tiedTo)));
    }
}
