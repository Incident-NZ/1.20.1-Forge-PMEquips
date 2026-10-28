package net.pm_equips.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.pm_equips.PMEquipsMain;

/** Rolls a separate addition table without replacing loot or recursively applying modifiers. */
public final class EquipmentLootModifier extends LootModifier {
    public static final Codec<EquipmentLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).and(ResourceLocation.CODEC.fieldOf("table").forGetter(modifier -> modifier.table))
                    .apply(instance, EquipmentLootModifier::new));
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, PMEquipsMain.MOD_ID);
    static { SERIALIZERS.register("equipment_additions", () -> CODEC); }
    private final ResourceLocation table;

    public EquipmentLootModifier(LootItemCondition[] conditions, ResourceLocation table) {
        super(conditions);
        this.table = table;
    }

    @Override protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        context.getLevel().getServer().getLootData().getLootTable(table).getRandomItemsRaw(context, loot::add);
        return loot;
    }

    @Override public Codec<? extends IGlobalLootModifier> codec() { return CODEC; }
}
