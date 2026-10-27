package net.pm_equips;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.pm_equips.config.CommonConfig;
import net.pm_equips.energy.WeaponEnergyProvider;
import net.pm_equips.entity.MobNPC3;
import net.pm_equips.items.materials.ArmorEquips;
import net.pm_equips.items.HeadArbiterArmor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PMEquipsMain.MOD_ID)
public class PMEquipsMain {
    public static final String MOD_ID = "pm_equips";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public PMEquipsMain(IEventBus modEventBus, ModContainer modContainer) {
        ArmorEquips.MATERIALS.register(modEventBus);
        BlockInit.Blocks.BLOCKS.register(modEventBus);
        BlockInit.BlockItems.BLOCK_ITEMS.register(modEventBus);
        BlockEntityInit.BLOCK_ENTITIES.register(modEventBus);
        MenuInit.MENUS.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
        TabInit.CREATIVE_TABS.register(modEventBus);
        EntityInit.ENTITY_TYPES.register(modEventBus);
        ParticleInit.PARTICLE_TYPES.register(modEventBus);
        MobEffectInit.MOB_EFFECTS.register(modEventBus);
        SoundInit.SOUNDS.register(modEventBus);
        modEventBus.addListener(HeadArbiterArmor::setup);
        modEventBus.addListener(WeaponEnergyProvider::register);
        modEventBus.addListener(MobNPC3::createAttributes);
        modEventBus.addListener(MobNPC3::registerPayloads);
        NeoForge.EVENT_BUS.register(MobNPC3.class);
        MobNPC3.prepareSkinDirectory();
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG, MOD_ID + "-common.toml");
    }
}
