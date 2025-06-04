package jackiecrazy.footwork.move.argument;

import com.google.gson.JsonObject;
import jackiecrazy.footwork.move.MovesetWrapper;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.utils.JsonAdapters;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface Argument<T> {

    default String serializeToJson(JsonObject to) {
        return JsonAdapters.gson.toJson(this);
    }

    default void readFromJson(JsonObject from){
        //chat is this real//
        JsonAdapters.gson.fromJson(from, this.getClass());
    }

    @Nullable
    T resolve(MovesetWrapper wrapper, Action parent, Entity caster, Entity target);
}
