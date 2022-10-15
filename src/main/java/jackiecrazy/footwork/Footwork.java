package jackiecrazy.footwork;

import jackiecrazy.footwork.api.WarAttributes;
import jackiecrazy.footwork.capability.goal.IGoalHelper;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
import jackiecrazy.footwork.capability.weaponry.ICombatItemCapability;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("footwork")
public class Footwork {

    public static final String MODID="footwork";

    public static final Random rand=new Random();

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Footwork() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::attribute);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        WarAttributes.ATTRIBUTES.register(bus);
        FootworkEffects.EFFECTS.register(bus);
    }

    private void setup(final RegisterCapabilitiesEvent event) {
        event.register(ICombatCapability.class);
        event.register(ICombatItemCapability.class);
        event.register(IGoalHelper.class);
    }

    private void attribute(EntityAttributeModificationEvent e) {
        for (EntityType<? extends LivingEntity> type:e.getTypes()){
            if(!e.has(type, Attributes.f_22277_))
                e.add(type, Attributes.f_22277_, 32);
            if(!e.has(type, Attributes.f_22283_))
                e.add(type, Attributes.f_22283_);
            if(!e.has(type, Attributes.f_22286_))
                e.add(type, Attributes.f_22286_);
            for(RegistryObject<Attribute> a: WarAttributes.ATTRIBUTES.getEntries())
                e.add(type, a.get());
        }
    }
}
