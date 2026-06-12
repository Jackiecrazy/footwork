package jackiecrazy.footwork.capability.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import net.minecraft.world.entity.Entity;

public class DummyAttachActionCap implements IAttachAction {
//    private static final MovesetManager literallyNothing=new MovesetManager(null);
//
//    @Override
//    public void setMovesetManager(MovesetManager mm) {
//
//    }
//
//    @Override
//    public MovesetManager getMovesetManager() {
//        return literallyNothing;
//    }

    @Override
    public void mark(Entity from, ActionSetWrapper d) {

    }

    @Override
    public void triggerCallback(String s, ArgumentContext additionalContext) {

    }

    @Override
    public void update() {

    }
}
