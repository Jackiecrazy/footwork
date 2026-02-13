package jackiecrazy.footwork.move.argument;

import com.google.gson.JsonObject;
import jackiecrazy.footwork.move.utils.ArgumentContext;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
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
    T resolve(ArgumentContext argumentContext);

}
