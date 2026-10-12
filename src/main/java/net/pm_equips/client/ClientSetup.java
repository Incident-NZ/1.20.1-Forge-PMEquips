package net.pm_equips.client;

import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.TabInit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        try {
            setTabIcon(TabInit.PM_LOBOTOMY_EGO.get(), () -> ItemInit.W1_PENITENCE.get().getDefaultInstance());
            setTabIcon(TabInit.PM_BASE.get(), () -> new ItemStack(ItemInit.ITEM_ENKEPHALIN.get()));
            setTabIcon(TabInit.PM_FIXERS.get(), () -> ItemInit.EX_DURANDAL.get().getDefaultInstance());
            setTabIcon(TabInit.PM_WCORP.get(), () -> ItemInit.WCORP_WEAPON_1.get().getDefaultInstance());
            setTabIcon(TabInit.PM_LIMBUS.get(), () -> ItemInit.LCA_AUTO_RIFLE.get().getDefaultInstance());

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
