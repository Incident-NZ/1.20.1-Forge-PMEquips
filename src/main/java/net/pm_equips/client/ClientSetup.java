package net.pm_equips.client;

import net.pm_equips.PMERegistryBlock;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.PMERegistryTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber.Bus;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@net.neoforged.fml.common.EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        try {
            setTabIcon(PMERegistryTab.PM_LOBOTOMY_EGO.get(), () -> PMERegistryItem.W1_PENITENCE.get().getDefaultInstance());
            setTabIcon(PMERegistryTab.PM_BASE.get(), () -> new ItemStack(PMERegistryBlock.BlockItems.ZAYIN_PE_BOX.get()));
            setTabIcon(PMERegistryTab.PM_FIXERS.get(), () -> PMERegistryItem.EX_DURANDAL.get().getDefaultInstance());
            setTabIcon(PMERegistryTab.PM_WCORP.get(), () -> PMERegistryItem.WCORP_WEAPON_1.get().getDefaultInstance());
        } catch (Throwable t) {
            // ignore to avoid crashing client if reflection fails
        }
    }

    private static void setTabIcon(CreativeModeTab tab, Supplier<ItemStack> supplier) {
        try {
            Field[] fields = CreativeModeTab.class.getDeclaredFields();
            for (Field f : fields) {
                if (Supplier.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    f.set(tab, supplier);
                    return;
                }
            }
        } catch (Throwable t) {
            // ignore
        }
    }
}
