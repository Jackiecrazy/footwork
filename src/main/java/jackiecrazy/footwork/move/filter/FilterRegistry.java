package jackiecrazy.footwork.move.filter;

import jackiecrazy.footwork.Footwork;
import jackiecrazy.footwork.utils.ActionJsonAdapters;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class FilterRegistry {
    public static ResourceLocation REGISTRY_NAME = new ResourceLocation(Footwork.MODID, "filters");
    public static Supplier<IForgeRegistry<FilterType>> SUPPLIER;
    public static DeferredRegister<FilterType> FILTERS = DeferredRegister.create(REGISTRY_NAME, Footwork.MODID);

    public static final RegistryObject<FilterType> NONE = FILTERS.register("none", () -> (SingletonFilterType) a -> NoFilter.INSTANCE);
    public static final RegistryObject<FilterType> RANDOM = FILTERS.register("random", () -> (SingletonFilterType) a -> RandomFilter.INSTANCE);
    public static final RegistryObject<FilterType> CONDITION = FILTERS.register("condition", () -> (a) -> ActionJsonAdapters.gson.fromJson(a, ConditionFilter.class));
}
