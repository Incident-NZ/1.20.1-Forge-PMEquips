package net.pm_equips.client;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.items.FixerRolandMook;

@net.neoforged.fml.common.EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = net.neoforged.fml.common.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RolandMookClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemProperties.register(PMERegistryItem.FIXER_ROLAND_MOOK.get(),
                ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "drawn"),
                (stack, level, entity, seed) -> stack.getItem() instanceof FixerRolandMook mook && mook.isDrawn(stack) ? 1.0F : 0.0F);
    }
}
