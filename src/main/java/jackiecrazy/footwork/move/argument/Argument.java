package jackiecrazy.footwork.move.argument;

import com.google.gson.JsonObject;
import jackiecrazy.footwork.move.TimerActionsWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface Argument<T> {

    default String serializeToJson(JsonObject to) {
        return ActionJsonAdapters.gson.toJson(this);
    }

    default void readFromJson(JsonObject from){
        //chat is this real//
        ActionJsonAdapters.gson.fromJson(from, this.getClass());
    }

    @Nullable
    T resolve(TimerActionsWrapper wrapper, Action parent, Entity caster, Entity target);
}
