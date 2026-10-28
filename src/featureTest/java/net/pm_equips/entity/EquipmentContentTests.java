package net.pm_equips.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.ItemInit;


@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class EquipmentContentTests {
    private static ResourceLocation id(String name) { return new ResourceLocation(name); }
    private static List<ItemStack> slots(Object... values) {
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < values.length; i += 2)
            for (int count = 0; count < (int)values[i + 1]; count++) result.add(new ItemStack((Item)values[i]));
        return result;
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void tenRecipesUseExactlyTheRequestedMaterials(GameTestHelper h) {
        Item cloth = ItemInit.FINGER2_CLOTH.get();
        Map<String,List<ItemStack>> expected = new LinkedHashMap<>();
        expected.put("finger1_weapon1", slots(Items.OAK_PLANKS,3,Items.IRON_INGOT,2));
        expected.put("finger1_weapon2", slots(Items.OAK_PLANKS,3,Items.IRON_INGOT,2,Items.IRON_SWORD,1));
        expected.put("finger1_armor1", slots(Items.LEATHER,6,Items.GOLD_INGOT,2,ItemInit.RIFLE_BULLET_AMMO.get(),1));
        expected.put("finger1_armor2", slots(ItemInit.FINGER1_ARMOR1.get(),1,Items.GOLD_INGOT,8));
        expected.put("finger1_armor3", slots(ItemInit.FINGER1_ARMOR2.get(),1,Items.GOLD_INGOT,8));
        expected.put("finger2_weapon1", slots(Items.IRON_SWORD,1,cloth,1));
        expected.put("finger2_weapon2", slots(Items.IRON_SWORD,1,Items.CHAIN,1,cloth,1));
        expected.put("finger2_weapon3", slots(Items.IRON_SWORD,1,Items.GOLD_INGOT,1,cloth,1));
        expected.put("finger2_armor1", slots(cloth,4,Items.LEATHER,2,Items.GOLD_INGOT,3));
        expected.put("finger2_armor2", slots(ItemInit.FINGER2_ARMOR1.get(),1,cloth,6,Items.LEATHER,1,Items.GOLD_INGOT,1));
        for (var entry : expected.entrySet()) {
            var recipe = h.getLevel().getRecipeManager().byKey(id("pm_equips:equipment/" + entry.getKey())).orElseThrow();
            List<ItemStack> remaining = new ArrayList<>(entry.getValue());
            h.assertTrue(recipe.getIngredients().size() == remaining.size(), "Exact slot count: " + entry.getKey());
            for (Ingredient ingredient : recipe.getIngredients()) {
                int found = -1;
                for (int i = 0; i < remaining.size(); i++) if (ingredient.test(remaining.get(i))) { found = i; break; }
                h.assertTrue(found >= 0, "Every ingredient matches the requested multiset: " + entry.getKey());
                remaining.remove(found);
            }
            ItemStack output = recipe.getResultItem(h.getLevel().registryAccess());
            h.assertTrue(remaining.isEmpty() && output.getCount() == 1
                    && BuiltInRegistries.ITEM.getKey(output.getItem()).equals(id("pm_equips:" + entry.getKey())), "Correct result: " + entry.getKey());
        }
        h.succeed();
    }
    private static LootTable table(GameTestHelper h, String name) {
        return h.getLevel().getServer().getLootData().getLootTable(id("minecraft:" + name));
    }
    @GameTest(template = "npc_empty", timeoutTicks = 100)
    public static void actualChestLootKeepsVanillaAndAddsModEquipment(GameTestHelper h) {
        String[] names = {"chests/pillager_outpost","chests/woodland_mansion","chests/village/village_armorer","chests/village/village_toolsmith","chests/village/village_weaponsmith", "chests/village/village_desert_house","chests/village/village_plains_house","chests/village/village_savanna_house","chests/village/village_snowy_house","chests/village/village_taiga_house","chests/village/village_butcher","chests/village/village_cartographer","chests/village/village_fisher","chests/village/village_fletcher","chests/village/village_mason","chests/village/village_shepherd","chests/village/village_tannery","chests/village/village_temple", "chests/simple_dungeon","chests/abandoned_mineshaft","chests/stronghold_corridor","chests/stronghold_crossing","chests/stronghold_library", "chests/nether_bridge","chests/bastion_bridge","chests/bastion_hoglin_stable","chests/bastion_other","chests/bastion_treasure", "chests/ancient_city","chests/end_city_treasure"};
        LootParams params = new LootParams.Builder(h.getLevel()).withParameter(LootContextParams.ORIGIN, h.absoluteVec(new Vec3(5,2,5)))
                .create(LootContextParamSets.CHEST);
        for (String name : names) {
            LootTable table = table(h, name);
            boolean vanilla = false, mod = false;
            for (int seed = 1; seed <= 32; seed++) {
                for (ItemStack stack : table.getRandomItems(params, seed)) {
                    String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
                    vanilla |= namespace.equals("minecraft"); mod |= namespace.equals("pm_equips");
                }
            }
            h.assertTrue(vanilla && mod, "Actual rolls include vanilla loot and the new additions: " + name);
            var resource = h.getLevel().getServer().getResourceManager().getResource(id("minecraft:loot_tables/" + name + ".json")).orElseThrow();
            h.assertTrue(!resource.sourcePackId().contains("pm_equips"), "The vanilla JSON is not overridden: " + name);
        }
        h.succeed();
    }
}
