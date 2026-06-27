package jackiecrazy.footwork.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.argument.Argument;
import jackiecrazy.footwork.move.argument.misc.RenderItemArgument;
import jackiecrazy.footwork.move.argument.misc.RenderNodeArgument;
import jackiecrazy.footwork.move.argument.stack.RawItemStackArgument;
import jackiecrazy.footwork.move.motionframe.*;
import jackiecrazy.footwork.move.motionframe.render.RenderNode;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4d;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class JsonAdapters {
    public static final Gson NAIVE = new GsonBuilder()
            .registerTypeAdapter(CompoundTag.class, new ActionJsonAdapters.NBTAdapter())
            .registerTypeAdapter(Vec3.class, new Vec3TypeAdapter())
            .registerTypeAdapter(RenderItemArgument.class, new ActionJsonAdapters.RenderItemAdapter())
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .registerTypeAdapter(BlockState.class, BlockStateAdapter.INSTANCE)
            .registerTypeAdapter(RenderNode.class, new JsonAdapters.RenderNodeAdapter())
            .registerTypeAdapterFactory(new HitInfoAdapterFactory())
            .setPrettyPrinting()
            .create();


    public static class HitInfoAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != HitInfo.class)
                return null;

            TypeAdapter<HitInfo> delegate =
                    gson.getDelegateAdapter(this, TypeToken.get(HitInfo.class));

            TypeAdapter<JsonElement> elementAdapter =
                    gson.getAdapter(JsonElement.class);

            return (TypeAdapter<T>) new TypeAdapter<HitInfo>(){

                @Override
                public void write(JsonWriter out, HitInfo value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public HitInfo read(JsonReader in) throws IOException {
                    JsonObject obj = elementAdapter.read(in).getAsJsonObject();
                    for (String first : new String[]{"hit", "damage"}) {
                        for (String second : new String[]{"_self", "_other"}) {
                            for (String third : new String[]{"command", "velocity", "set_velocity"}) {
                                final String joined = first + second + "_" + third;
                                if (obj.has(joined)) {
                                    //add property if not there
                                    final String key = first + second;
                                    if (!obj.has(key))
                                        obj.add(key, new JsonObject());
                                    obj.get(key).getAsJsonObject().add(third, obj.get(joined));
                                }
                            }
                        }
                    }
                    return delegate.fromJsonTree(obj);
                }
            };
        }
    }

    public static class ColorAdapter extends TypeAdapter<Color> {

        @Override
        public void write(JsonWriter out, Color value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            out.value(value.getRed());
            out.value(value.getGreen());
            out.value(value.getBlue());
            out.value(value.getAlpha());
            out.endArray();
        }

        @Override
        public Color read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            in.beginArray();
            int x = in.nextInt();
            int y = in.nextInt();
            int z = in.nextInt();
            int a =255;
            if(in.hasNext())
                a=in.nextInt();
            in.endArray();
            return new Color(x, y, z, a);
        }
    }

    public static class Vec3TypeAdapter extends TypeAdapter<Vec3> {

        @Override
        public void write(JsonWriter out, Vec3 value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginArray();
            out.value(value.x);
            out.value(value.y);
            out.value(value.z);
            out.endArray();
        }

        @Override
        public Vec3 read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            in.beginArray();
            double x = in.nextDouble();
            double y = in.nextDouble();
            double z = in.nextDouble();
            in.endArray();
            return new Vec3(x, y, z);
        }
    }

    public static class MotionFrameAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != MotionFrame.class)
                return null;

            TypeAdapter<MotionFrame> delegate =
                    gson.getDelegateAdapter(this, TypeToken.get(MotionFrame.class));

            TypeAdapter<JsonElement> elementAdapter =
                    gson.getAdapter(JsonElement.class);

            return (TypeAdapter<T>) new TypeAdapter<MotionFrame>() {

                @Override
                public void write(JsonWriter out, MotionFrame value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public MotionFrame read(JsonReader in) throws IOException {
                    JsonElement json = elementAdapter.read(in);
                    if (!json.isJsonObject()) throw new JsonParseException(json + " is not a json object");
                    JsonObject o = json.getAsJsonObject();
                    MotionFrame mf = delegate.fromJsonTree(json);
                    assert mf.direction() != null;
                    assert mf.offset() != null;
                    if (mf.effects() == null)
                        mf.setEffects(JsonUtils.extractRootFrameEffects(o, false));
                    if (mf.renderOrientation() == null) {
                        //try checking other fields: int rotation or vec4f dir+rot
                        if (o.has("render_rotation")) {
                            JsonElement spinElem = o.get("render_rotation");
                            if (spinElem.isJsonPrimitive() && spinElem.getAsJsonPrimitive().isNumber()) {
                                // Scalar number → apply to X-axis only (common convention for yaw spin)
                                int scalar = spinElem.getAsInt();
                                mf = new MotionFrame(mf.direction(), mf.offset(), scalar).setEffects(mf.effects());

                            } else if (spinElem.isJsonArray()) {
                                // [x, y, z]
                                JsonArray arr = spinElem.getAsJsonArray();
                                if (arr.size() < 3 || arr.size() > 4) {
                                    throw new JsonParseException("render_rotation array must have either 3 or 4 elements.");
                                }
                                Vector4d spinVector = new Vector4d(
                                        arr.get(0).getAsDouble(),
                                        arr.get(1).getAsDouble(),
                                        arr.get(2).getAsDouble(),
                                        0
                                );
                                if (arr.size() > 3) spinVector.w = arr.get(3).getAsDouble();
                                if (spinVector.x == 0 && spinVector.y == 0 && spinVector.z == 0) {
                                    throw new JsonParseException("render_rotation array must define a nonzero length vector with its xyz");
                                }
                                mf = new MotionFrame(mf.direction(), mf.offset(), spinVector).setEffects(mf.effects());

                            } else if (spinElem.isJsonObject()) {
                                // {"x":, "y":, "z":}
                                JsonObject spinObj = spinElem.getAsJsonObject();
                                Vector4d spinVector = new Vector4d(
                                        spinObj.has("x") ? spinObj.get("x").getAsDouble() : 0,
                                        spinObj.has("y") ? spinObj.get("y").getAsDouble() : 0,
                                        spinObj.has("z") ? spinObj.get("z").getAsDouble() : 0,
                                        spinObj.has("w") ? spinObj.get("w").getAsDouble() : 0
                                );

                                if (spinVector.x == 0 && spinVector.y == 0 && spinVector.z == 0) {
                                    throw new JsonParseException("render_rotation array must define a nonzero length vector with its xyz");
                                }
                                mf = new MotionFrame(mf.direction(), mf.offset(), spinVector).setEffects(mf.effects());

                            } else {
                                throw new JsonParseException("'render_rotation' must be a number, array[4], or object{x,y,z,w}");
                            }
                        } else if (!o.has("_skipOrientationCalculation"))
                            mf = new MotionFrame(mf.direction(), mf.offset()).setEffects(mf.effects());
                    }
                    return mf;
                }
            };
        }
    }

    public static class MotionManagerDeserializer implements JsonDeserializer<MotionManager> {

        public static double fuck(Vec3 prevTip, Vec3 currTip) {
            if (currTip.lengthSqr() < 1e-6) {
                return 0.0; // Degenerate case
            }

            Vec3 swordDir = currTip.normalize();           // Forward (+Z local) in world
            Vec3 movement = currTip.subtract(prevTip);

            if (movement.lengthSqr() < 1e-6) {
                return 0.0;
            }

            // Desired edge direction: movement projected perpendicular to swordDir
            Vec3 desiredEdgeWorld = movement.subtract(swordDir.scale(movement.dot(swordDir))).normalize();

            if (desiredEdgeWorld.lengthSqr() < 1e-6) {
                return 0.0; // Movement parallel to sword (rare)
            }

            // Local edge vector in model space: (0, -1, 0)
            // We need to find the signed angle around swordDir between
            // a "reference" orientation and the desired one.

            // Create a reference "up" perpendicular to swordDir for roll=0.
            // Common choice: world up projected, or a consistent right vector.
            Vec3 worldUp = new Vec3(0, -1, 0);
            Vec3 refRight = swordDir.cross(worldUp).normalize(); // tentative right
            if (refRight.lengthSqr() < 1e-6) { // sword pointing straight up/down
                refRight = new Vec3(1, 0, 0); // arbitrary
            }
            Vec3 refEdge = refRight.cross(swordDir).normalize(); // should approximate local -Y for roll=0

            // Better: compute the angle directly between current reference edge and desired
            double dot = refEdge.dot(desiredEdgeWorld);
            double angleRad = Math.acos(Mth.clamp(dot, -1.0, 1.0));

            // Signed angle using cross product (right-hand rule around swordDir)
            Vec3 cross = refEdge.cross(desiredEdgeWorld);
            double sign = cross.dot(swordDir); // positive if CCW looking along swordDir

            double rollRad = (sign >= 0 ? -angleRad : angleRad);

            return Math.toDegrees(rollRad);
        }

        private static double signedAngle(Vec3 a, Vec3 b, Vec3 edgeNormal) {
            Vec3 cross = a.cross(b);
            double dot = a.dot(b);
            double sign = edgeNormal.dot(cross);
            return Math.atan2(sign, dot);
        }

        @Override
        public MotionManager deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            if (!json.isJsonObject()) {
                throw new JsonParseException("MotionManager must be a JSON object");
            }

            JsonObject obj = json.getAsJsonObject();
            MotionManager ret;

            // ────────────────────────────────────────────────
            // Case 1: Has "frames" → DefinitionMM + MotionGroup
            // ────────────────────────────────────────────────
            if (obj.has("frames") && obj.get("frames").isJsonArray()) {
                JsonArray framesArray = obj.getAsJsonArray("frames");

                List<MotionFrame> frames = new ArrayList<>();
                boolean manuallyCalculate = framesArray.size() > 1;
                for (JsonElement elem : framesArray) {
                    if (manuallyCalculate) elem.getAsJsonObject().addProperty("_skipOrientationCalculation", true);
                    MotionFrame frame = context.deserialize(elem, MotionFrame.class);
                    frames.add(frame);
                }

                if (frames.isEmpty()) {
                    throw new JsonParseException("frames array cannot be empty for DefinitionMM");
                }

                //compute frames from bottom to top.
                if (manuallyCalculate) {
                    for (int x = frames.size() - 1; x >= 1; x--) {
                        //If they have no render orientation, set their orientation to their direction, and their angling to the angle of the frame change
                        final MotionFrame mf = frames.get(x);
                        if (mf.renderOrientation() != null)
                            continue;
                        Vec3 endFrame = mf.direction();
                        Vec3 startFrame = frames.get(x - 1).direction();
                        final Vec3 down = new Vec3(0, -1, 0);
                        final Vec3 movement = endFrame.subtract(startFrame).normalize();
                        //fixme doesn't work on horizontal slashes
                        double angleRadians = signedAngle(startFrame, endFrame, new Vec3(0, 0, -1));
                        double angleDegrees = fuck(startFrame, endFrame);
                        mf._setRenderRotationRaw(MotionFrame.buildLocalRotation(new Vector4d(endFrame.x, endFrame.y, endFrame.z, angleDegrees)));
                    }
                    //finally compute the first frame from the second frame
                    final MotionFrame mf = frames.get(0);
                    if (mf.renderOrientation() == null) {
                        Vec3 startFrame = mf.direction();
                        Vec3 endFrame = frames.get(1).direction();
                        final Vec3 down = new Vec3(0, -1, 0);
                        final Vec3 movement = endFrame.subtract(startFrame).normalize();
                        double angleRadians = signedAngle(startFrame, endFrame, new Vec3(0, 0, -1));
                        double angleDegrees = fuck(startFrame, endFrame);
                        mf._setRenderRotationRaw(MotionFrame.buildLocalRotation(new Vector4d(startFrame.x, startFrame.y, startFrame.z, angleDegrees)));
                    }
                }

                // Extract MotionGroup fields (easing, duration)
                EasingFunctionEnum easing = obj.has("easing")
                        ? context.deserialize(obj.get("easing"), EasingFunctionEnum.class)
                        : EasingFunctionEnum.LINEAR;  // or your default

                int dur;
                if (obj.has("duration"))
                    dur = obj.get("duration").getAsInt();
                else
                    throw new JsonParseException("no duration defined for " + json);  // or 20 / throw if you want stricter


                MotionGroup group = new MotionGroup(frames, easing, dur);
                MotionManagers.DefinitionMM defMM = new MotionManagers.DefinitionMM(group);
                ret = defMM;
            } else {

                // ────────────────────────────────────────────────
                // Case 2: No "frames" → single MotionFrame → FixedMM
                // ────────────────────────────────────────────────
                // Deserialize the whole object as a MotionFrame
                MotionFrame singleFrame = context.deserialize(obj, MotionFrame.class);

                // Determine duration (fallback or from root)
                int singleDuration = 5;
                if (obj.has("duration"))
                    singleDuration = obj.get("duration").getAsInt();
                else
                    throw new JsonParseException("no duration defined for " + json);  // or 20 / throw if you want stricter

                MotionManagers.FixedMM fixed = new MotionManagers.FixedMM(singleFrame, singleDuration);
                ret = fixed;
            }
            applySpinIfPresent(obj, ret, context);
            return ret;
        }

        private void applySpinIfPresent(JsonObject obj, MotionManager targetFrame, JsonDeserializationContext context) {
            if (!obj.has("spin")) {
                return;
            }

            JsonElement spinElem = obj.get("spin");
            Vector3f spinVector;

            if (spinElem.isJsonPrimitive() && spinElem.getAsJsonPrimitive().isNumber()) {
                // Scalar number → apply to X-axis only (common convention for yaw spin)
                float scalar = spinElem.getAsFloat();
                spinVector = new Vector3f(scalar, 0, 0);

            } else if (spinElem.isJsonArray()) {
                // [x, y, z]
                JsonArray arr = spinElem.getAsJsonArray();
                if (arr.size() != 3) {
                    throw new JsonParseException("'spin' array must have exactly 3 elements");
                }
                spinVector = new Vector3f(
                        arr.get(0).getAsFloat(),
                        arr.get(1).getAsFloat(),
                        arr.get(2).getAsFloat()
                );

            } else if (spinElem.isJsonObject()) {
                // {"x":, "y":, "z":}
                JsonObject spinObj = spinElem.getAsJsonObject();
                spinVector = new Vector3f(
                        spinObj.has("x") ? spinObj.get("x").getAsFloat() : 0,
                        spinObj.has("y") ? spinObj.get("y").getAsFloat() : 0,
                        spinObj.has("z") ? spinObj.get("z").getAsFloat() : 0
                );

            } else {
                throw new JsonParseException("'spin' must be a number, array[3], or object{x,y,z}");
            }

            targetFrame.setAngularVelocity(spinVector);
        }

    }

    public static class BlockStateAdapter implements JsonSerializer<BlockState>, JsonDeserializer<BlockState> {

        public static final BlockStateAdapter INSTANCE = new BlockStateAdapter();

        @Override
        public JsonElement serialize(BlockState state, Type typeOfSrc, JsonSerializationContext context) {
            if (state == null) {
                return JsonNull.INSTANCE;
            }
            // This produces the exact string format used by /setblock and /fill
            return new JsonPrimitive(BlockStateParser.serialize(state));
        }

        @Override
        public BlockState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            if (json == null || json.isJsonNull()) {
                return null;
            }

            String blockString = json.getAsString().trim();

            try {
                // This is exactly how Minecraft parses /setblock arguments
                StringReader reader = new StringReader(blockString);
                BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(
                        // Use the server or level's registry lookup if available, otherwise:
                        net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(),
                        reader,
                        false   // allow incomplete = false for strict parsing
                );
                return result.blockState();
            } catch (CommandSyntaxException e) {
                throw new JsonParseException("Failed to parse BlockState: " + blockString, e);
            }
        }
    }

    public static class RenderNodeAdapter implements JsonDeserializer<RenderNode> {

        @Override
        public RenderNode deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) {
            Vec3 rotation = Vec3.ZERO;
            Vec3 translation = Vec3.ZERO;
            if (json.isJsonPrimitive()) {
                //the name of an item.
                String name = json.getAsString();
                if(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)) instanceof BlockItem bc){
                    return new RenderNode.BlockNode(bc.getBlock().defaultBlockState(), rotation, translation);
                }
                ItemStack risa = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)));
                return new RenderNode.ItemNode(risa, rotation, translation);
            }
            if (json.isJsonObject()) {
                JsonObject obj=json.getAsJsonObject();
                rotation=ctx.deserialize(obj.get("rotation"), Vec3.class);
                translation=ctx.deserialize(obj.get("translation"), Vec3.class);
                //single item node, deserialize that
                if(!obj.has("stack"))
                    return new RenderNode.BaseItemNode(rotation, translation);
                String name = obj.get("stack").getAsString();
                if(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)) instanceof BlockItem bc){
                    return new RenderNode.BlockNode(bc.getBlock().defaultBlockState(), rotation, translation);
                }
                ItemStack risa = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)));
                return new RenderNode.ItemNode(risa, rotation, translation);
            }
            throw new JsonParseException("item render argument is unreadable: " + json);
        }
    }
}
