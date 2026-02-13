package jackiecrazy.footwork.move;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import jackiecrazy.footwork.utils.JsonUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.HashMap;
import java.util.Map;

public class ActionSets extends SimpleJsonResourceReloadListener {
    public static final HashMap<ResourceLocation, JsonArray> moves = new HashMap<>();
    public static Action a;

    public ActionSets(String directory) {
        super(ActionJsonAdapters.gson, directory);
    }

    public static void register(AddReloadListenerEvent event, String dir) {
        event.addListener(new ActionSets(dir));
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object,
                         ResourceManager resourceManager,
                         ProfilerFiller profilerFiller) {
        moves.clear();
        object.forEach((key, value) -> {
            JsonArray file = JsonUtils.parseSyntacticSugar(value.getAsJsonArray()).getAsJsonArray();
            Footwork.LOGGER.debug("loading action set definition found under {}", key);
            try {
                a = ActionJsonAdapters.gson.fromJson(file, Action[].class)[0];
                moves.put(key, file);
            } catch (Exception e) {
                Footwork.LOGGER.error("{} is an invalid action set, it will not be registered!", key);
                e.printStackTrace();
            }
        });
    }
}
