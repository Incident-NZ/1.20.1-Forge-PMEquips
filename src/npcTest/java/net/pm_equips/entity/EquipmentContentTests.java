package net.pm_equips.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
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
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.ItemInit;
import net.pm_equips.events.TrialLootEvents;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class EquipmentContentTests {
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void everyModRecipeLoadsAndProducesARegisteredItem(GameTestHelper h) {
        var server = h.getLevel().getServer();
        var resources = server.getResourceManager().listResources("recipe", location ->
                location.getNamespace().equals("pm_equips") && location.getPath().endsWith(".json"));
        h.assertTrue(!resources.isEmpty(), "Mod recipe resources are present");
        for (ResourceLocation resource : resources.keySet()) {
            String name = resource.getPath();
            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), name.substring("recipe/".length(), name.length() - 5));
            var recipe = server.getRecipeManager().byKey(recipeId);
            h.assertTrue(recipe.isPresent(), "Recipe parsed successfully: " + recipeId);
            h.assertTrue(!recipe.orElseThrow().value().getResultItem(h.getLevel().registryAccess()).isEmpty(), "Recipe produces an item: " + recipeId);
        }
        h.succeed();
    }


    private static ResourceLocation id(String name) { return ResourceLocation.parse(name); }
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
        expected.put("finger1_armor2", slots(ItemInit.FINGER1_ARMOR1.get(),1,Items.GOLD_INGOT,6,ItemInit.RIFLE_BULLET_AMMO.get(),2));
        expected.put("finger1_armor3", slots(ItemInit.FINGER1_ARMOR2.get(),1,Items.GOLD_INGOT,6,ItemInit.RIFLE_BULLET_AMMO.get(),2));
        expected.put("finger2_weapon1", slots(Items.IRON_SWORD,1,cloth,1));
        expected.put("finger2_weapon2", slots(ItemInit.FINGER2_WEAPON1.get(),1,Items.CHAIN,1,cloth,1));
        expected.put("finger2_weapon3", slots(ItemInit.FINGER2_WEAPON1.get(),1,Items.TRIAL_KEY,1,cloth,1));
        expected.put("finger2_armor1", slots(cloth,4,Items.LEATHER,2,Items.GOLD_INGOT,3));
        expected.put("finger2_armor2", slots(ItemInit.FINGER2_ARMOR1.get(),1,cloth,6,Items.LEATHER,1,Items.GOLD_INGOT,1));
        for (var entry : expected.entrySet()) {
            var recipe = h.getLevel().getRecipeManager().byKey(id("pm_equips:items/" + entry.getKey())).orElseThrow().value();
            List<ItemStack> remaining = new ArrayList<>(entry.getValue());
            h.assertTrue(recipe.getIngredients().stream().filter(ingredient -> !ingredient.isEmpty()).count() == remaining.size(), "Exact slot count: " + entry.getKey());
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.isEmpty()) continue;
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
        return h.getLevel().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id("minecraft:" + name)));
    }
    @GameTest(template = "npc_empty", timeoutTicks = 100)
    public static void actualChestLootKeepsVanillaAndAddsModEquipment(GameTestHelper h) {
        String[] names = {"chests/trial_chambers/reward", "chests/trial_chambers/reward_common", "chests/trial_chambers/reward_rare",
                "chests/trial_chambers/reward_unique", "chests/trial_chambers/reward_ominous_common",
                "chests/trial_chambers/reward_ominous_rare", "chests/trial_chambers/reward_ominous_unique"};
        LootParams params = new LootParams.Builder(h.getLevel()).withParameter(LootContextParams.ORIGIN, h.absoluteVec(new Vec3(5,2,5)))
                .create(LootContextParamSets.CHEST);
        for (String name : names) {
            LootTable table = table(h, name);
            h.assertTrue(table.getPool(TrialLootEvents.POOL) != null, "The addition pool is installed on the actual subtable: " + name);
            boolean vanilla = false, mod = false;
            for (int seed = 1; seed <= 32; seed++) {
                for (ItemStack stack : table.getRandomItems(params, seed)) {
                    String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
                    vanilla |= namespace.equals("minecraft"); mod |= namespace.equals("pm_equips");
                }
            }
            h.assertTrue(vanilla && mod, "Actual rolls include vanilla loot and the new additions: " + name);
            var resource = h.getLevel().getServer().getResourceManager().getResource(id("minecraft:loot_table/" + name + ".json")).orElseThrow();
            h.assertTrue(!resource.sourcePackId().contains("pm_equips"), "The vanilla JSON is not overridden: " + name);
        }
        long modCount = table(h, "chests/trial_chambers/reward").getRandomItems(params, 42).stream()
                .filter(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("pm_equips")).count();
        h.assertTrue(modCount >= 2, "A vault reward includes both the parent addition and the selected rarity's nested addition");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 100)
    public static void wingEquipmentLootRespectsFactionRarityAndArmorRanks(GameTestHelper h) {
        String[] names = {"trial_chambers/reward", "trial_chambers/reward_common", "trial_chambers/reward_rare", "trial_chambers/reward_unique",
                "trial_chambers/reward_ominous_common", "trial_chambers/reward_ominous_rare", "trial_chambers/reward_ominous_unique"};
        int[] expected = {0, 0, 1, 2, 0, 4, 0};
        int[] tRanks = {0, 0, 5, 0, 0, 0, 0}, rRanks = {0, 0, 0, 5, 0, 0, 0};
        LootParams params = new LootParams.Builder(h.getLevel()).withParameter(LootContextParams.ORIGIN, h.absoluteVec(new Vec3(5, 2, 5)))
                .create(LootContextParamSets.CHEST);
        for (int index = 0; index < names.length; index++) {
            String name = names[index];
            LootTable table = h.getLevel().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id("pm_equips:chests/" + name)));
            h.assertTrue(table.getPool("pm_equips_equipment") != null, "Addition table parsed: " + name);
            int found = 0;
            for (int seed = 1; seed <= 1024; seed++) for (ItemStack stack : table.getRandomItems(params, seed)) {
                ResourceLocation item = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (!item.getNamespace().equals("pm_equips")) continue;
                String value = item.getPath();
                int group = value.startsWith("wing_tcorp_") ? 1
                        : value.equals("wing_rcorp_armor3") || value.equals("wing_rcorp_weapon4") ? 4
                        : value.startsWith("wing_rcorp") ? 2 : 0;
                h.assertTrue((group & expected[index]) == group, "Unexpected faction or commander drop: " + name + "/" + value);
                found |= group;
                if (group == 1 && value.matches(".*_armor_l[1-5]"))
                    h.assertTrue(Character.digit(value.charAt(value.length() - 1), 10) <= tRanks[index], "T armor rank restriction: " + name + "/" + value);
                if (group == 2 && value.matches(".*armor(?:2)?_l[1-5]"))
                    h.assertTrue(Character.digit(value.charAt(value.length() - 1), 10) <= rRanks[index], "R armor rank restriction: " + name + "/" + value);
            }
            h.assertTrue(found == expected[index], "The actual rolls include every intended new equipment group: " + name);
        }
        h.succeed();
    }
}
