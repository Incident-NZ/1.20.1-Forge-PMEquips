package net.pm_equips;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.pm_equips.config.CommonConfig;
import net.pm_equips.energy.EnergyCapabilities;
import net.pm_equips.network.ModPackets;
import software.bernie.geckolib.GeckoLib;

@Mod(PMEquipsMain.MOD_ID)
public class PMEquipsMain {

    public static final String MOD_ID = "pm_equips";

    public PMEquipsMain(IEventBus modEventBus, ModContainer modContainer) {
        PMERegistryBlock.Blocks.BLOCKS.register(modEventBus);
        PMERegistryBlock.BlockItems.BLOCK_ITEMS.register(modEventBus);
        PMERegistryBlockEntity.BLOCK_ENTITIES.register(modEventBus);
        PMERegistryMenu.MENUS.register(modEventBus);
        PMERegistryItem.ITEMS.register(modEventBus);
        PMERegistryTab.CREATIVE_TABS.register(modEventBus);
        PMERegistryEntity.ENTITY_TYPES.register(modEventBus);
        PMERegistryMobEffect.MOB_EFFECTS.register(modEventBus);
        PMERegistrySound.SOUNDS.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(ModPackets::register);
        modEventBus.addListener(EnergyCapabilities::register);
        modContainer.registerConfig(ModConfig.Type.COMMON,
                CommonConfig.COMMON_CONFIG, MOD_ID + "-common.toml");
    }

    private void setup(final FMLCommonSetupEvent event) {
    }
}
