package jackiecrazy.footwork;

import jackiecrazy.footwork.api.FootworkAttributes;
import jackiecrazy.footwork.capability.goal.IGoalHelper;
import jackiecrazy.footwork.capability.resources.ICombatCapability;
import jackiecrazy.footwork.capability.weaponry.ICombatItemCapability;
import jackiecrazy.footwork.client.particle.CustomSweepParticle;
import jackiecrazy.footwork.client.particle.FootworkParticles;
import jackiecrazy.footwork.client.render.NothingRender;
import jackiecrazy.footwork.command.AttributizeCommand;
import jackiecrazy.footwork.compat.FootworkCompat;
import jackiecrazy.footwork.entity.FootworkEntities;
import jackiecrazy.footwork.potion.FootworkEffects;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("footwork")
public class Footwork {

    public static final String MODID = "footwork";
    public static File configDirPath;

    public static final Random rand = new Random();

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Footwork() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::attribute);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        FootworkAttributes.ATTRIBUTES.register(bus);
        FootworkEffects.EFFECTS.register(bus);
        FootworkEntities.ENTITIES.register(bus);
        FootworkParticles.PARTICLES.register(bus);
        MinecraftForge.EVENT_BUS.addListener(this::commands);
    }

    private void setup(final RegisterCapabilitiesEvent event) {
        event.register(ICombatCapability.class);
        event.register(ICombatItemCapability.class);
        event.register(IGoalHelper.class);
    }

    private void attribute(EntityAttributeModificationEvent e) {
        for (EntityType<? extends LivingEntity> type : e.getTypes()) {
            if (!e.has(type, Attributes.FOLLOW_RANGE))
                e.add(type, Attributes.FOLLOW_RANGE, 32);
            if (!e.has(type, Attributes.ATTACK_SPEED))
                e.add(type, Attributes.ATTACK_SPEED);
            if (!e.has(type, Attributes.LUCK))
                e.add(type, Attributes.LUCK);
            for (RegistryObject<Attribute> a : FootworkAttributes.ATTRIBUTES.getEntries())
                e.add(type, a.get());
        }
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(FootworkEntities.DUMMY.get(), NothingRender::new);
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        FootworkCompat.checkCompatStatus();
    }


    private void commands(final RegisterCommandsEvent event) {
        AttributizeCommand.register(event.getDispatcher());
    }
}
