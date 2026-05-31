package jackiecrazy.footwork.move.motionframe.render;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.move.motionframe.HitInfo;
import jackiecrazy.footwork.networking.FootworkChannel;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ItemPreTransforms extends SimpleJsonResourceReloadListener {
    public static List<Item> DESPERATION = new ArrayList<>();
    public static ItemRenderPreTransform DEFAULTMELEE = new ItemRenderPreTransform(Vec3.ZERO, Vec3.ZERO);
    public static HashMap<Item, ItemRenderPreTransform> combatList = new HashMap<>();
    public static HashMap<Item, ItemRenderPreTransform> clientItems = new HashMap<>();
    public static HitInfo info_override = null;
    private static final ResourceLocation air = new ResourceLocation("air");
    private static HashMap<TagKey<Item>, ItemRenderPreTransform> archetypes = new HashMap<>();
    private static HashMap<TagKey<Item>, ItemRenderPreTransform> clientArchetypes = new HashMap<>();

    public ItemPreTransforms() {
        super(ActionJsonAdapters.gson, "war_stats");
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new ItemPreTransforms());
    }

    public static void sendItemData(ServerPlayer p) {
        //duplicated removed automatically
        Set<String> paths = combatList.keySet().stream().map(a -> ForgeRegistries.ITEMS.getKey(a).getNamespace()).collect(Collectors.toSet());
        for (String namespace : paths)
            FootworkChannel.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), new SyncItemDataPacket(Maps.filterEntries(combatList, a -> ForgeRegistries.ITEMS.getKey(a.getKey()).getNamespace().equals(namespace))));
        FootworkChannel.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), new SyncTagDataPacket(archetypes));
    }

    public static void clientWeaponOverride(Map<Item, ItemRenderPreTransform> server) {
        clientItems.putAll(server);//the client doesn't need *that* much info, so we keep its list separate and save packets
    }

    public static void clientTagOverride(Map<TagKey<Item>, ItemRenderPreTransform> server) {
        clientArchetypes = new HashMap<>(server);
    }

    public static void updateItems(Map<ResourceLocation, JsonElement> object,
                                   ResourceManager rm,
                                   ProfilerFiller profiler) {
        combatList = new HashMap<>();
        archetypes = new HashMap<>();

        object.forEach((key, value) -> {
            JsonObject file = value.getAsJsonObject();
            file.entrySet().forEach(entry -> {
                String name = entry.getKey();
                if (name.startsWith("#")) {//register tags separately
                    try {
                        name = name.substring(1);
                        if (!name.contains(":")) name = "footwork:" + name;
                        JsonObject obj = entry.getValue().getAsJsonObject();
                        ItemRenderPreTransform put = parseMeleeInfo(name, obj);
                        archetypes.put(ItemTags.create(new ResourceLocation(name)), put);
                    } catch (Exception x) {
                        Footwork.LOGGER.error("malformed json under " + name + "!");
                        x.printStackTrace();
                    }
                    return;
                }
                ResourceLocation i = new ResourceLocation(name);
                Item item = ForgeRegistries.ITEMS.getValue(i);
                if (item == null || (!i.equals(air) && item == Items.AIR)) {
                    return;
                }
                try {
                    JsonObject obj = entry.getValue().getAsJsonObject();
                    ItemRenderPreTransform put = parseMeleeInfo(name, obj);
                    combatList.put(item, put);
                } catch (Exception x) {
                    Footwork.LOGGER.error("malformed json under " + name + "!");
                    x.printStackTrace();
                }
            });
        });
    }

    @Nonnull
    private static ItemRenderPreTransform parseMeleeInfo(String root, JsonObject obj) {
        ItemRenderPreTransform put = ActionJsonAdapters.gson.fromJson(obj, ItemRenderPreTransform.class);
        return put;
    }

    @Nullable
    public static ItemRenderPreTransform lookupStats(ItemStack is) {
        if (is == null) return null;
        if (combatList.containsKey(is.getItem())) return combatList.get(is.getItem());
        for (TagKey<Item> tag : archetypes.keySet()) {
            if (is.is(tag)) {
                //cache lookup
                combatList.put(is.getItem(), archetypes.get(tag));
                return archetypes.get(tag);
            }
        }
        if (clientItems.containsKey(is.getItem())) return clientItems.get(is.getItem());
        for (TagKey<Item> tag : clientArchetypes.keySet()) {
            if (is.is(tag)) {
                //cache lookup
                clientItems.put(is.getItem(), clientArchetypes.get(tag));
                return clientArchetypes.get(tag);
            }
        }
        return null;
    }


    @Override
    protected void apply(@Nonnull Map<ResourceLocation, JsonElement> object,
                         @Nonnull ResourceManager rm,
                         @Nonnull ProfilerFiller profiler) {
        updateItems(object, rm, profiler);
    }
}
