package net.pm_equips.client;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.pm_equips.BlockEntityInit;
import net.pm_equips.EntityInit;
import net.pm_equips.KeyBindInit;
import net.pm_equips.MenuInit;
import net.pm_equips.client.renderer.*;
import net.pm_equips.client.screen.LobotomyEGOCraftScreen;
import net.pm_equips.client.screen.LobotomyEGOExtractionScreen;
import net.pm_equips.items.CorePageItem;
import net.pm_equips.PMEquipsMain;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod.EventBusSubscriber(
        modid = PMEquipsMain.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)
public class ClientEvents {


    //Menu Registry
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ForgeRegistries.ITEMS.getValues().stream()
                .filter(CorePageItem.class::isInstance)
                .forEach(item -> CuriosRendererRegistry.register(item, CorePageCurioRenderer::new)));
        event.enqueueWork(() -> MenuScreens.register(MenuInit.LOBOTOMY_EGO_EXTRACTION_TABLE.get(), LobotomyEGOExtractionScreen::new));

        event.enqueueWork(() ->
                MenuScreens.register(MenuInit.GUN_WORKBENCH.get(), LobotomyEGOCraftScreen::new)
        );
    }

    //EntityRenderer
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.W5_SOUND_OF_A_STAR_PROJECTILE.get(),
                ctx -> new ThrownItemRenderer<>(ctx, 1.0f, true));
        event.registerEntityRenderer(EntityInit.HEAVEN_PROJECTILE.get(),
                ctx -> new ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(EntityInit.WHITENIGHT_PROJECTILE.get(),
                PWhiteNightR::new);
        event.registerBlockEntityRenderer(BlockEntityInit.EBOX_GEN.get(), EBoxGenR::new);
    }

    //KeyBind
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindInit.RELOAD_KEY);
        event.register(KeyBindInit.SCOPE_KEY);
        event.register(KeyBindInit.CORE_PAGE_ABILITY_KEY);
        event.register(KeyBindInit.WEAPON_ABILITY_KEY);
    }
}

