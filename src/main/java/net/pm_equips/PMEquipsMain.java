package net.pm_equips;

import net.pm_equips.config.CommonConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pm_equips.network.NetworkPacketInit;
import net.pm_equips.loot.EquipmentLootModifier;
import net.pm_equips.entity.MobNPC3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(PMEquipsMain.MOD_ID)
public class PMEquipsMain {

    public static final String MOD_ID = "pm_equips";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public PMEquipsMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockInit.Blocks.BLOCKS.register(modEventBus);
        BlockInit.BlockItems.BLOCK_ITEMS.register(modEventBus);
        BlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
        MenuInit.MENUS.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
        TabInit.CREATIVE_TABS.register(modEventBus);
        EntityInit.ENTITY_TYPES.register(modEventBus);
        MobEffectInit.MOB_EFFECTS.register(modEventBus);
        SoundInit.SOUNDS.register(modEventBus);
        ParticleInit.PARTICLE_TYPES.register(modEventBus);
        EquipmentLootModifier.SERIALIZERS.register(modEventBus);
        modEventBus.addListener(MobNPC3::createAttributes);
        MinecraftForge.EVENT_BUS.register(MobNPC3.class);
        MobNPC3.prepareSkinDirectory();

        modEventBus.addListener(this::setup);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON,
                CommonConfig.COMMON_CONFIG, MOD_ID + "-common.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkPacketInit::register);
    }

    private void setup(final FMLCommonSetupEvent event) {
        GeckoLib.initialize();
        NetworkPacketInit.register();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {}

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {}
}
