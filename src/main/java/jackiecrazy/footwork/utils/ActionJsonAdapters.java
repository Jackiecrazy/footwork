package jackiecrazy.footwork.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.action.Action;
import jackiecrazy.footwork.move.action.ActionRegistry;
import jackiecrazy.footwork.move.action.MetaAction;
import jackiecrazy.footwork.move.action.timer.TimerAction;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.ArgumentRegistry;
import jackiecrazy.footwork.move.argument.SingletonArgumentType;
import jackiecrazy.footwork.move.argument.misc.RenderNodeArgument;
import jackiecrazy.footwork.move.argument.misc.RenderItemArgument;
import jackiecrazy.footwork.move.argument.number.FixedNumberArgument;
import jackiecrazy.footwork.move.argument.number.NumberArgument;
import jackiecrazy.footwork.move.argument.resourcelocation.ResourceLocationArgument;
import jackiecrazy.footwork.move.argument.stack.RawItemStackArgument;
import jackiecrazy.footwork.move.argument.vector.RawVectorArgument;
import jackiecrazy.footwork.move.argument.vector.VectorArgument;
import jackiecrazy.footwork.move.condition.*;
import jackiecrazy.footwork.move.filter.Filter;
import jackiecrazy.footwork.move.filter.FilterRegistry;
import jackiecrazy.footwork.move.filter.SingletonFilterType;
import jackiecrazy.footwork.move.motionframe.*;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActionJsonAdapters {
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Class.class, new ClassAdapter())
            .registerTypeAdapter(Supplier.class, new SupplierAdapter())
            .registerTypeAdapter(Action.class, new ActionAdapter())
            .registerTypeAdapter(TimerAction.class, new ActionAdapter())
            .registerTypeAdapter(Argument.class, new ArgumentAdapter<>())
            .registerTypeAdapter(NumberArgument.class, new NumberAdapter())
            .registerTypeAdapter(VectorArgument.class, new VectorAdapter())
            .registerTypeAdapter(ResourceLocationArgument.class, new ResourceAdapter())
            .registerTypeAdapter(RenderItemArgument.class, new RenderItemAdapter())
            // .registerTypeAdapter(EntityArgument.class, new ArgumentAdapter())
            .registerTypeAdapter(Condition.class, new ConditionAdapter())
            .registerTypeAdapter(Filter.class, new FilterAdapter())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(CompoundTag.class, new NBTAdapter())
            .registerTypeAdapter(Vec3.class, new JsonAdapters.Vec3TypeAdapter())
            .registerTypeAdapter(BlockState.class, JsonAdapters.BlockStateAdapter.INSTANCE)
            .registerTypeAdapter(MotionManager.class, new JsonAdapters.MotionManagerDeserializer())
            .registerTypeAdapter(RenderNode.class, new JsonAdapters.RenderNodeAdapter())
            .registerTypeAdapter(Color.class, new JsonAdapters.ColorAdapter())
            .registerTypeAdapterFactory(new JsonAdapters.MotionFrameAdapterFactory())
            .registerTypeAdapterFactory(new JsonAdapters.HitInfoAdapterFactory())
            .setPrettyPrinting().excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT)
            .create();

    private static void testRequired(JsonElement je, Type type) throws JsonParseException {
        Object pojo = new Gson().fromJson(je, type);

        Field[] fields = pojo.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getAnnotation(JsonRequired.class) != null) {
                try {
                    f.setAccessible(true);
                    if (f.get(pojo) == null) {
                        throw new JsonParseException("Missing field in JSON: " + f.getName());
                    }
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(ActionJsonAdapters.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ActionJsonAdapters.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static @NotNull String enforceNamespace(String id, String namespace) {
        if (!id.contains(":")) id = namespace + ":" + id;
        return id;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface JsonRequired {
    }

    public static class ClassAdapter implements JsonDeserializer<Class> {
        @Override
        public Class deserialize(JsonElement json,
                                 Type type,
                                 JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            try {
                return Class.forName(json.getAsString());
            } catch (ClassNotFoundException e) {
                throw new JsonParseException("cannot find class " + json);
            }
        }
    }

    public static class SupplierAdapter implements JsonDeserializer<Supplier<String>> {
        @Override
        public Supplier<String> deserialize(JsonElement json,
                                            Type type,
                                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return json::getAsString;
        }
    }

    public static class ActionAdapter implements JsonDeserializer<Action> {
        @Override
        public Action deserialize(JsonElement json,
                                  Type type,
                                  JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (json.isJsonArray()) {
                List<Action> la = new ArrayList<>();
                JsonArray a = json.getAsJsonArray();
                a.forEach(b -> la.add(parseAction(b)));
                return new MetaAction(la);
            } else if (!json.isJsonObject())
                //not a json object, assume it is borked
                throw new JsonParseException("action must be a Json object: " + json);
            return parseAction(json);
        }

        private Action parseAction(JsonElement json) {
            JsonObject sub = json.getAsJsonObject();
            if (sub.has("ID")) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(sub.get("ID").getAsString(), Footwork.MODID));
                if (ActionRegistry.SUPPLIER.get().containsKey(rl)) {
                    return ActionRegistry.SUPPLIER.get().getValue(rl).bake(sub);
                }
                throw new JsonParseException("invalid ID " + rl + " defined for action object: " + sub);
            }
            throw new JsonParseException("no ID defined for action object: " + sub);
        }
    }

    @SuppressWarnings("unchecked")
    public static class ArgumentAdapter<T> implements JsonDeserializer<Argument<T>> {
        @Override
        public Argument<T> deserialize(JsonElement json,
                                       Type type,
                                       JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!json.isJsonObject()) {
                try {
                    //everything is awful
                    //fixme the very act of getting it as string will break vec3s
                    ResourceLocation rl = new ResourceLocation(enforceNamespace(json.getAsString(), Footwork.MODID));
                    if (ArgumentRegistry.SUPPLIER.get().getValue(rl) instanceof SingletonArgumentType<?> sat) {
                        return (Argument<T>) sat.bake(null);
                    }
                    Class<?> grabby = (Class<?>) ((ParameterizedType) TypeToken.of(type).getType()).getActualTypeArguments()[0];
                    if (grabby == Boolean.class) {
                        return json.getAsBoolean() ? (Argument<T>) TrueCondition.INSTANCE : (Argument<T>) FalseCondition.INSTANCE;
                    } else if (grabby == Double.class) {
                        if (json.isJsonPrimitive()) return (Argument<T>) new FixedNumberArgument(json.getAsDouble());
                        if (!json.isJsonObject()) return (Argument<T>) FixedNumberArgument.ZERO;
                    } else if (grabby == Vec3.class) {
                        if (!json.isJsonObject()) return (Argument<T>) RawVectorArgument.ZERO;
                    } else if (grabby == ItemStack.class) {
                        //try to see if it's a proper ID first. If not, assume it's an item.
                        ResourceLocation id = new ResourceLocation(enforceNamespace(json.getAsString(), Footwork.MODID));
                        if (ArgumentRegistry.SUPPLIER.get().containsKey(id)) {
                            try {
                                return (Argument<T>) ArgumentRegistry.SUPPLIER.get().getValue(rl).bake(new JsonObject());
                            } catch (ClassCastException ignored) {
                            }
                        }
                        return (Argument<T>) new RawItemStackArgument().setItem(enforceNamespace(json.getAsString(), "minecraft"));
                    } else if (grabby == ResourceLocation.class) {
                        String id = json.getAsString();
                        if (!json.isJsonObject())
                            return (Argument<T>) new ResourceLocationArgument.Raw(enforceNamespace(id, Footwork.MODID));
                    }
                } catch (Exception e) {
                    throw new JsonParseException("unreadable argument: " + json);
                }
                throw new JsonParseException("argument is not a singleton: " + json);
            }
            JsonObject sub = json.getAsJsonObject();
            if (sub.has("ID")) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(sub.get("ID").getAsString(), Footwork.MODID));
                if (ArgumentRegistry.SUPPLIER.get().containsKey(rl)) {
                    try {
                        return (Argument<T>) ArgumentRegistry.SUPPLIER.get().getValue(rl).bake(sub);
                    } catch (ClassCastException e) {
                        throw new JsonParseException("incorrect argument type: " + json, e);
                    }
                }
                throw new JsonParseException("invalid ID " + rl + " defined for argument object: " + json);
            }
            throw new JsonParseException("no ID defined for argument object: " + sub);
        }
    }

    public static class VectorAdapter implements JsonDeserializer<Argument<Vec3>> {
        @Override
        public Argument<Vec3> deserialize(JsonElement json,
                                          Type type,
                                          JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!json.isJsonObject()) return RawVectorArgument.ZERO;
            JsonObject sub = json.getAsJsonObject();
            if (sub.has("ID")) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(sub.get("ID").getAsString(), Footwork.MODID));
                if (ArgumentRegistry.SUPPLIER.get().containsKey(rl)) {
                    try {
                        return (Argument<Vec3>) ArgumentRegistry.SUPPLIER.get().getValue(rl).bake(sub);
                    } catch (ClassCastException e) {
                        throw new JsonParseException("ID defined for vector object must be a vector argument type: " + json);
                    }
                }
                throw new JsonParseException("invalid ID " + rl + " defined for vector object: " + sub);
            }
            throw new JsonParseException("no ID defined for vector object: " + sub);
        }
    }

    public static class NumberAdapter implements JsonDeserializer<Argument<Double>> {
        @Override
        public Argument<Double> deserialize(JsonElement json,
                                            Type type,
                                            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (json.isJsonPrimitive()) return new FixedNumberArgument(json.getAsDouble());
            if (!json.isJsonObject()) return new FixedNumberArgument(0);
            JsonObject sub = json.getAsJsonObject();
            if (sub.has("ID")) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(sub.get("ID").getAsString(), Footwork.MODID));
                if (ArgumentRegistry.SUPPLIER.get().containsKey(rl)) {
                    try {
                        return (Argument<Double>) ArgumentRegistry.SUPPLIER.get().getValue(rl).bake(sub);
                    } catch (ClassCastException e) {
                        throw new JsonParseException("ID defined for number object must be a number argument type: " + json);
                    }
                }
                throw new JsonParseException("invalid ID " + rl + " defined for number object: " + sub);
            }
            throw new JsonParseException("number argument is unreadable: " + sub);
        }
    }

    public static class ResourceAdapter implements JsonDeserializer<Argument<ResourceLocation>> {
        @Override
        public Argument<ResourceLocation> deserialize(JsonElement json,
                                                      Type type,
                                                      JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (json.isJsonPrimitive())
                return new ResourceLocationArgument.Raw(enforceNamespace(json.getAsString(), Footwork.MODID));
            JsonObject sub = json.getAsJsonObject();
            if (sub.has("ID")) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(sub.get("ID").getAsString(), Footwork.MODID));
                if (ArgumentRegistry.SUPPLIER.get().containsKey(rl)) {
                    try {
                        return (Argument<ResourceLocation>) ArgumentRegistry.SUPPLIER.get().getValue(rl).bake(sub);
                    } catch (ClassCastException e) {
                        throw new JsonParseException("ID defined for resource location object must be a resource location argument type: " + json);
                    }
                }
                throw new JsonParseException("invalid ID " + rl + " defined for resource location object: " + sub);
            }
            throw new JsonParseException("resource location argument is unreadable: " + sub);
        }
    }

    public static class RenderItemAdapter implements JsonDeserializer<RenderItemArgument> {

        @Override
        public RenderItemArgument deserialize(JsonElement json,
                                              Type typeOfT,
                                              JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                //the name of an item.
                String name = json.getAsString();
                if (ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)) instanceof BlockItem bc) {
                    return new RenderItemArgument().withNodes(new RenderNodeArgument().setBlockstate(bc.getBlock().defaultBlockState()));
                }
                RawItemStackArgument risa = new RawItemStackArgument().setItem(json.getAsString());
                return new RenderItemArgument().withNodes(new RenderNodeArgument().setStack(risa));
            }
            if (json.isJsonObject()) {
                //single item node, deserialize that
                JsonObject obj = json.getAsJsonObject();
                RenderNodeArgument deserialize = gson.fromJson(json, RenderNodeArgument.class);
                if (deserialize.getStack() == null && deserialize.getBlockstate() == null && obj.has("ID"))
                    deserialize.setStack(gson.fromJson(json, Argument.class));
                return new RenderItemArgument().withNodes(deserialize);
            }
            if (json.isJsonArray()) {
                List<RenderNodeArgument> nodes = gson.fromJson(json.getAsJsonArray(), new TypeToken<ArrayList<RenderNodeArgument>>() {
                }.getType());
                return new RenderItemArgument().withNodes(nodes.toArray(new RenderNodeArgument[0]));
            }
            throw new JsonParseException("item render argument is unreadable: " + json);
        }
    }

    public static class ConditionAdapter implements JsonDeserializer<Argument<Boolean>> {
        @Override
        public Argument<Boolean> deserialize(JsonElement json,
                                             Type type,
                                             JsonDeserializationContext ctx) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return json.getAsBoolean() ? TrueCondition.INSTANCE : FalseCondition.INSTANCE;
            }
            if (json.isJsonArray() && json.getAsJsonArray().get(0).isJsonObject()) {
                return new AndCondition(ctx.deserialize(json.getAsJsonArray(), new TypeToken<ArrayList<Condition>>() {
                }.getType()));
            }
            if (!json.isJsonObject()) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(json.getAsString(), Footwork.MODID));
                if (ConditionRegistry.SUPPLIER.get().getValue(rl) instanceof SingletonConditionType sat) {
                    return sat.bake(null);
                } else throw new JsonParseException("condition is not a singleton: " + json);
            }
            JsonObject sub = json.getAsJsonObject();
            if (sub.has("ID")) {
                boolean flip = false;
                String id = sub.get("ID").getAsString();
                JsonObject tempOperation = id.startsWith("!")? sub.deepCopy():sub;
                while (id.startsWith("!")) {
                    flip = !flip;
                    id = id.substring(1);
                    tempOperation.addProperty("ID", id);
                }
                ResourceLocation rl = new ResourceLocation(enforceNamespace(id, Footwork.MODID));
                if (ConditionRegistry.SUPPLIER.get().containsKey(rl)) {
                    final Condition bake = (Condition) ConditionRegistry.SUPPLIER.get().getValue(rl).bake(tempOperation);
                    return flip ? NotCondition.of(bake) : bake;
                }
                Argument<?> a=ctx.deserialize(tempOperation, Argument.class);
                if (a != null) {
                    Condition bake = ExistsCondition.create(a);
                    return flip ? NotCondition.of(bake) : bake;
                }
                throw new JsonParseException("invalid ID " + rl + " defined for condition object: " + json);
            }
            //throw new JsonParseException("no ID, cannot read condition object: " + json);
            return TrueCondition.INSTANCE;
        }
    }

    public static class FilterAdapter implements JsonDeserializer<Filter> {
        @Override
        public Filter deserialize(JsonElement json,
                                  Type type,
                                  JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!json.isJsonObject()) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(json.getAsString(), Footwork.MODID));
                if (FilterRegistry.SUPPLIER.get().getValue(rl) instanceof SingletonFilterType sat) {
                    return sat.bake(null);
                } else throw new JsonParseException("filter is not a singleton: " + json);
            }
            JsonObject sub = json.getAsJsonObject();
            if (sub.has("ID")) {
                ResourceLocation rl = new ResourceLocation(enforceNamespace(sub.get("ID").getAsString(), Footwork.MODID));
                if (FilterRegistry.SUPPLIER.get().containsKey(rl)) {
                    return FilterRegistry.SUPPLIER.get().getValue(rl).bake(sub);
                }
                throw new JsonParseException("invalid ID " + rl + " defined for filter object: " + json);
            }
            throw new JsonParseException("no ID defined for filter object: " + json);
        }
    }

    public static class NBTAdapter implements JsonDeserializer<CompoundTag> {

        @Override
        public CompoundTag deserialize(JsonElement json,
                                       Type type,
                                       JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!json.isJsonPrimitive()) throw new JsonParseException("not an NBT tag: " + json);
            try {
                return CompoundTagArgument.compoundTag().parse(new StringReader(enforceNamespace(json.getAsString(), Footwork.MODID)));
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("invalid NBT definition: " + json);
            }
        }
    }
}
