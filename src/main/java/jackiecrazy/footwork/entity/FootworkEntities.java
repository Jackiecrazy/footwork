package jackiecrazy.footwork.entity;

import jackiecrazy.footwork.Footwork;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod.EventBusSubscriber(modid = Footwork.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FootworkEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Footwork.MODID);

    public static final RegistryObject<EntityType<DummyEntity>> DUMMY = ENTITIES.register("dummy", () -> EntityType.Builder
            .of(DummyEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .build("dummy"));

    @SubscribeEvent
    public static void attribute(EntityAttributeCreationEvent e){
        e.put(DUMMY.get(), DummyEntity.createAttributes().build());
    }
}
