package jackiecrazy.footwork.capability.action;

import jackiecrazy.footwork.move.ActionSetWrapper;
import net.minecraft.world.entity.Entity;

public interface IAttachAction {
//    void setMovesetManager(MovesetManager mm);
//    MovesetManager getMovesetManager();
    void mark(Entity from, ActionSetWrapper d);

    void update();
}
