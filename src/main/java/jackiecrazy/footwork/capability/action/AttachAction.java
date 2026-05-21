package jackiecrazy.footwork.capability.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.CallbackActionWrapper;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AttachAction implements IAttachAction {
//    private static final MovesetManager literallyNothing = new MovesetManager(null);
    private final Entity tiedTo;
    private final HashMap<Entity, List<ActionSetWrapper>> marks = new HashMap<>();
    private final HashMap<Entity, List<CallbackActionWrapper>> temp = new HashMap<>();
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
        marks.putIfAbsent(en, new ArrayList<>());
        marks.get(en).add(d);
        d.start(en, tiedTo);
    }

    @Override
    public void triggerCallback(String s) {
        temp.clear();
        marks.forEach((en, actionSetWrappers) -> actionSetWrappers.forEach(a->{
            if(a instanceof CallbackActionWrapper b){
                temp.putIfAbsent(en, new ArrayList<>());
                temp.get(en).add(b);
            }
        }));
        temp.forEach((entity, actionSetWrappers) -> actionSetWrappers.forEach(a->a.triggerCallback(entity, tiedTo, s)));
    }

    @Override
    public void update() {
        if(marks.isEmpty())return;
        marks.forEach((entity, lists) -> lists.forEach(ms -> ms.tick(entity, tiedTo)));
        marks.values().forEach(wrappers -> wrappers.removeIf(wrapper -> !wrapper.executing()));
        marks.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
