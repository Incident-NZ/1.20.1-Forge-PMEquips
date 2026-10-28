package net.pm_equips.events;

import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.pm_equips.PMEquipsMain;

/** Add pools to the actual reward subtables so vault references retain their rarity selection. */
@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public final class TrialLootEvents {
    private static final Set<String> REWARDS = Set.of("reward", "reward_common", "reward_rare", "reward_unique",
            "reward_ominous_common", "reward_ominous_rare", "reward_ominous_unique");
    public static final String POOL = "pm_equips_trial_equipment";
    private TrialLootEvents() {}

    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        ResourceLocation id = event.getName();
        String prefix = "chests/trial_chambers/";
        if (!id.getNamespace().equals("minecraft") || !id.getPath().startsWith(prefix)) return;
        String reward = id.getPath().substring(prefix.length());
        if (!REWARDS.contains(reward) || event.getTable().getPool(POOL) != null) return;
        ResourceLocation addition = ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "chests/trial_chambers/" + reward);
        event.getTable().addPool(LootPool.lootPool().name(POOL).setRolls(ConstantValue.exactly(1))
                .add(NestedLootTable.lootTableReference(ResourceKey.create(Registries.LOOT_TABLE, addition))).build());
    }
}
