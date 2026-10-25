package net.pm_equips.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.pm_equips.PMEquipsMain;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class LobotomyEGORecipeManager extends SimplePreparableReloadListener<Map<ResourceLocation, LobotomyEGORecipe>> {

    private static final Gson GSON = new Gson();
    private static volatile Map<ResourceLocation, LobotomyEGORecipe> RECIPES = Map.of();
    private static volatile List<LobotomyEGORecipe> CLIENT_RECIPES = List.of();

    public static Collection<LobotomyEGORecipe> getAll() {
        return RECIPES.values();
    }

    public static List<LobotomyEGORecipe> getClientRecipes() {
        return CLIENT_RECIPES;
    }

    public static Optional<LobotomyEGORecipe> get(ResourceLocation id) {
        return Optional.ofNullable(RECIPES.get(id));
    }

    @Override
    protected Map<ResourceLocation, LobotomyEGORecipe> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, LobotomyEGORecipe> map = new LinkedHashMap<>();
        String prefix = "lobotomy_ego_recipes";

        for (Map.Entry<ResourceLocation, Resource> entry :
                manager.listResources(prefix, rl -> rl.getPath().endsWith(".json")).entrySet()) {

            ResourceLocation fileId = entry.getKey();
            try (var reader = new InputStreamReader(entry.getValue().open(), StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);

                ResourceLocation id = ResourceLocation.parse(json.get("id").getAsString());

                JsonObject resultJson = json.getAsJsonObject("result");
                String resultKey = resultJson.has("id") ? "id" : "item";
                Item resultItem = registeredItem(resultJson.get(resultKey).getAsString());
                int resultCount = resultJson.has("count") ? resultJson.get("count").getAsInt() : 1;
                ItemStack result = new ItemStack(resultItem, resultCount);

                List<LobotomyEGORecipe.IngredientEntry> ingredients = new ArrayList<>();
                JsonArray arr = json.getAsJsonArray("ingredients");
                for (JsonElement el : arr) {
                    JsonObject o = el.getAsJsonObject();
                    Item item = registeredItem(o.get("item").getAsString());
                    int count = o.get("count").getAsInt();
                    ingredients.add(new LobotomyEGORecipe.IngredientEntry(item, count));
                }

                if (map.putIfAbsent(id, new LobotomyEGORecipe(id, result, ingredients)) != null) {
                    throw new IllegalArgumentException("Duplicate EGO recipe ID: " + id);
                }
            } catch (Exception e) {
                PMEquipsMain.LOGGER.error("Failed to load gun recipe {}", fileId, e);
            }
        }
        return map;
    }

    @Override
    protected void apply(Map<ResourceLocation, LobotomyEGORecipe> object, ResourceManager manager, ProfilerFiller profiler) {
        RECIPES = Collections.unmodifiableMap(new LinkedHashMap<>(object));
        PMEquipsMain.LOGGER.info("Loaded {} gun workbench recipes", RECIPES.size());
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new LobotomyEGORecipeManager());
    }

    public static void replaceAll(Collection<LobotomyEGORecipe> recipes) {
        CLIENT_RECIPES = List.copyOf(recipes);
    }

    private static Item registeredItem(String id) {
        ResourceLocation location = ResourceLocation.parse(id);
        return BuiltInRegistries.ITEM.getOptional(location)
                .orElseThrow(() -> new IllegalArgumentException("Unknown recipe item: " + location));
    }
}
