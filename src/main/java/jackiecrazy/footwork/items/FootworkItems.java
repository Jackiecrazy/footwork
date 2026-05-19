package jackiecrazy.footwork.items;

import jackiecrazy.footwork.Footwork;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FootworkItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Footwork.MODID);

    public static final RegistryObject<Item> TESTER = ITEMS.register("animation_tester", AnimationTesterItem::new);
}
