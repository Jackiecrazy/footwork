package jackiecrazy.footwork.entity;

import jackiecrazy.footwork.Footwork;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Footwork.MODID, bus = EventBusSubscriber.Bus.MOD)
public class FootworkEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Footwork.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<DummyEntity>> DUMMY = ENTITIES.register("dummy", () -> EntityType.Builder
            .of(DummyEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .build("dummy"));

    @SubscribeEvent
    public static void attribute(EntityAttributeCreationEvent e){
        e.put(DUMMY.get(), DummyEntity.createAttributes().build());
    }
}
