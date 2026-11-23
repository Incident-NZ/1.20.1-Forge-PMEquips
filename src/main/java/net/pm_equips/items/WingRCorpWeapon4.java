package net.pm_equips.items;

import net.minecraft.world.item.TooltipFlag;

import net.minecraft.network.chat.Component;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.entity.MobNPC1;
import net.pm_equips.events.CombatEvents;
import net.pm_equips.util.FingerEquipment;
import net.pm_equips.util.ItemStackData;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class WingRCorpWeapon4 extends SwordItem {
    private static final String KILLS = "rcorp_commander_kills", GROUPS = "rcorp_target_groups";
    private static final String EXECUTING = "rcorp_executing", MAX_LOOT = "rcorp_maximum_loot", AURA = "rcorp_power_aura";
    private static final ResourceLocation MARKS = ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "rcorp_targets");
    private static final Map<ServerLevel, Set<LivingEntity>> SOURCES = new WeakHashMap<>();
    private static final Map<LootTable, LootTable> MAXIMUM_TABLES = new WeakHashMap<>();

    public WingRCorpWeapon4() {
        super(new Tier() {
            @Override public int getUses() { return 4000; }
            @Override public float getSpeed() { return 4; }
            @Override public float getAttackDamageBonus() { return 0; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
            @Override public int getEnchantmentValue() { return 0; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(ItemInit.RCORP_BATTERY.get()); }
        }, new Properties().durability(4000).attributes(SwordItem.createAttributes(Tiers.WOOD, 9, -2.2F)));
    }
    public static int kills(ItemStack stack) { return Math.max(0, Math.min(20, ItemStackData.getInt(stack, KILLS))); }
    public static boolean hasSet(LivingEntity entity) {
        entity = actor(entity);
        return entity.isAlive() && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof WingRCorpArmor3
                && (entity.getMainHandItem().getItem() instanceof WingRCorpWeapon4
                || entity.getOffhandItem().getItem() instanceof WingRCorpWeapon4);
    }
    private static ItemStack weapon(LivingEntity entity) {
        entity = actor(entity);
        ItemStack main = entity.getMainHandItem(), off = entity.getOffhandItem();
        return main.getItem() instanceof WingRCorpWeapon4 ? main
                : off.getItem() instanceof WingRCorpWeapon4 ? off : ItemStack.EMPTY;
    }
    private static LivingEntity actor(LivingEntity source) {
        if (source.getPersistentData().hasUUID("PMENPC") && source.level() instanceof ServerLevel level
                && level.getEntity(source.getPersistentData().getUUID("PMENPC")) instanceof MobNPC1 npc) return npc;
        return source;
    }
    private static Player owner(LivingEntity source) {
        source = actor(source);
        if (source instanceof Player player) return player;
        if (source instanceof TamableAnimal pet && pet.getOwner() instanceof Player player) return player;
        return null;
    }
    public static boolean isAlly(LivingEntity source, LivingEntity target) {
        source = actor(source);
        if (source == target || source.isAlliedTo(target)) return true;
        Player owner = owner(source);
        return owner != null && (target == owner || FingerEquipment.isAlly(owner, target));
    }
    private static String group(LivingEntity source) {
        source = actor(source);
        Player owner = owner(source);
        LivingEntity leader = owner == null ? source : owner;
        if (!leader.getServer().isPvpAllowed()) return "peaceful_players";
        if (leader.getTeam() != null) return "team:" + leader.getTeam().getName();
        if (owner == null && source instanceof TamableAnimal pet && pet.getOwnerUUID() != null) {
            String saved = source.getServer().getCommandStorage().get(MARKS).getCompound("owners").getString(pet.getOwnerUUID().toString());
            return saved.isEmpty() ? "owner:" + pet.getOwnerUUID() : saved;
        }
        return "owner:" + leader.getUUID();
    }
    private static CompoundTag marks(ServerLevel level) { return level.getServer().getCommandStorage().get(MARKS).copy(); }
    public static void mark(LivingEntity attacker, LivingEntity target) {
        if (!(target.level() instanceof ServerLevel level) || !hasSet(attacker) || isAlly(attacker, target) || target.isSpectator()) return;
        String group = group(attacker);
        CompoundTag data = marks(level);
        Player owner = owner(attacker);
        if (owner != null) {
            CompoundTag owners = data.getCompound("owners");
            owners.putString(owner.getUUID().toString(), group); data.put("owners", owners);
        }
        if (data.hasUUID(group)) {
            for (ServerLevel dimension : level.getServer().getAllLevels()) {
                if (dimension.getEntity(data.getUUID(group)) instanceof LivingEntity previous && previous != target) {
                    removeGroup(previous, group); break;
                }
            }
        }
        data.putUUID(group, target.getUUID()); level.getServer().getCommandStorage().set(MARKS, data);
        ListTag groups = target.getPersistentData().getList(GROUPS, Tag.TAG_STRING);
        if (!groups.contains(StringTag.valueOf(group))) groups.add(StringTag.valueOf(group));
        target.getPersistentData().put(GROUPS, groups);
        target.addEffect(new MobEffectInstance(MobEffectInit.TARGET, -1, 0), attacker);
    }
    private static void removeGroup(LivingEntity target, String group) {
        ListTag groups = target.getPersistentData().getList(GROUPS, Tag.TAG_STRING);
        groups.remove(StringTag.valueOf(group));
        if (groups.isEmpty()) { target.getPersistentData().remove(GROUPS); target.removeEffect(MobEffectInit.TARGET); }
        else target.getPersistentData().put(GROUPS, groups);
    }
    public static void prioritizeTarget(MobNPC1 npc) {
        if (!(npc.level() instanceof ServerLevel level) || !npc.isTame()) return;
        CompoundTag data = marks(level);
        String key = group(npc);
        if (data.hasUUID(key) && level.getEntity(data.getUUID(key)) instanceof LivingEntity target && npc.canAttack(target)
                && npc.distanceToSqr(target) <= npc.getAttributeValue(Attributes.FOLLOW_RANGE)
                * npc.getAttributeValue(Attributes.FOLLOW_RANGE)) npc.setTarget(target);
    }
    @SubscribeEvent public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!(entity.level() instanceof ServerLevel level)) return;
        Set<LivingEntity> sources = SOURCES.computeIfAbsent(level, unused -> new HashSet<>());
        if (hasSet(entity)) sources.add(entity); else sources.remove(entity);
        sources.removeIf(source -> source.isRemoved() || source.level() != level || !hasSet(source));
        boolean powered = sources.stream().anyMatch(source -> source.distanceToSqr(entity) <= 16384 && isAlly(source, entity));
        MobEffectInstance current = entity.getEffect(MobEffectInit.POWER);
        boolean owned = entity.getPersistentData().getBoolean(AURA);
        if (powered && (current == null || owned && current.getAmplifier() == 0 && current.getDuration() <= 3)) {
            entity.addEffect(new MobEffectInstance(MobEffectInit.POWER, 3, 0, true, false, true));
            entity.getPersistentData().putBoolean(AURA, true);
        } else if (!powered && owned) {
            if (current != null && current.getAmplifier() == 0 && current.getDuration() <= 3) entity.removeEffect(MobEffectInit.POWER);
            entity.getPersistentData().remove(AURA);
        }
        ListTag groups = entity.getPersistentData().getList(GROUPS, Tag.TAG_STRING);
        if (!groups.isEmpty()) {
            CompoundTag data = marks(level);
            groups.removeIf(tag -> !data.hasUUID(tag.getAsString()) || !data.getUUID(tag.getAsString()).equals(entity.getUUID()));
            entity.getPersistentData().put(GROUPS, groups);
            if (groups.isEmpty()) entity.removeEffect(MobEffectInit.TARGET);
            else if (!entity.hasEffect(MobEffectInit.TARGET)) entity.addEffect(new MobEffectInstance(MobEffectInit.TARGET, -1, 0));
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamage(LivingDamageEvent.Pre event) {
        if (event.getNewDamage() <= 0) return;
        LivingEntity target = event.getEntity();
        if (target.hasEffect(MobEffectInit.TARGET)) event.setNewDamage(event.getNewDamage() * 1.5F);
        if (event.getSource().getEntity() instanceof LivingEntity attacker
                && !weapon(attacker).isEmpty()) mark(attacker, target);
    }
    @SubscribeEvent public static void onDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (!(target.level() instanceof ServerLevel level)) return;
        if (event.getSource().getEntity() instanceof LivingEntity attacker && hasSet(attacker)
                && !attacker.getPersistentData().getBoolean(EXECUTING) && CombatEvents.isHostile(attacker, target)) {
            ItemStack stack = weapon(attacker);
            if (!stack.isEmpty()) ItemStackData.putInt(stack, KILLS, Math.min(20, kills(stack) + 1));
        }
        CompoundTag data = marks(level);
        for (Tag tag : target.getPersistentData().getList(GROUPS, Tag.TAG_STRING)) {
            String key = tag.getAsString();
            if (data.hasUUID(key) && data.getUUID(key).equals(target.getUUID())) data.remove(key);
        }
        level.getServer().getCommandStorage().set(MARKS, data);
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!hasSet(player) || kills(stack) < 20) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand); return InteractionResultHolder.consume(stack);
    }
    @Override public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (!hasSet(player) || kills(stack) < 20) player.stopUsingItem();
            else if (getUseDuration(stack, player) - remaining + 1 >= 40) { execute(player, stack); player.stopUsingItem(); }
        }
    }
    public static boolean execute(Player player, ItemStack stack) {
        if (!(player.level() instanceof ServerLevel level) || !hasSet(player) || kills(stack) < 20) return false;
        ItemStackData.putInt(stack, KILLS, 0);
        player.getPersistentData().putBoolean(EXECUTING, true);
        try {
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(32),
                    target -> target instanceof Enemy && target.isAlive() && target.distanceToSqr(player) <= 1024
                            && CombatEvents.isHostile(player, target))) {
                target.getPersistentData().putBoolean(MAX_LOOT, true);
                target.invulnerableTime = 0; target.stopUsingItem();
                target.setLastHurtByPlayer(player);
                target.hurt(player.damageSources().playerAttack(player), 1_000_000);
                if (target.isAlive()) { target.setHealth(0); target.die(player.damageSources().playerAttack(player)); }
            }
        } finally { player.getPersistentData().remove(EXECUTING); }
        return true;
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDrops(LivingDropsEvent event) {
        if (!event.getEntity().getPersistentData().getBoolean(MAX_LOOT)
                || !(event.getEntity().level() instanceof ServerLevel level)) return;
        LivingEntity target = event.getEntity();
        LootParams.Builder builder = new LootParams.Builder(level).withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position()).withParameter(LootContextParams.DAMAGE_SOURCE, event.getSource())
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, event.getSource().getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, event.getSource().getDirectEntity());
        if (event.getSource().getEntity() instanceof Player player) builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
        LootTable original = level.getServer().reloadableRegistries().getLootTable(target.getLootTable());
        List<ItemStack> maximum = maximumLoot(level, original, builder.create(LootContextParamSets.ENTITY));
        for (ItemStack stack : maximum) {
            int already = event.getDrops().stream().map(ItemEntity::getItem).filter(existing -> ItemStack.isSameItemSameComponents(existing, stack))
                    .mapToInt(ItemStack::getCount).sum();
            int missing = stack.getCount() - already;
            while (missing > 0) {
                int count = Math.min(missing, stack.getMaxStackSize());
                ItemStack addition = stack.copy(); addition.setCount(count);
                event.getDrops().add(new ItemEntity(level, target.getX(), target.getY(), target.getZ(), addition)); missing -= count;
            }
        }
    }
    public static List<ItemStack> maximumLoot(ServerLevel level, LootTable table, LootParams params) {
        LootTable maximum = MAXIMUM_TABLES.get(table);
        if (maximum == null) {
        JsonObject json = lootJson(level, table);
        JsonArray pools = new JsonArray();
        flattenTable(level, json, 1, new JsonArray(), pools, new HashSet<>());
        JsonObject output = new JsonObject(); output.addProperty("type", "minecraft:entity"); output.add("pools", pools);
        maximum = LootTable.DIRECT_CODEC.parse(level.registryAccess().createSerializationContext(JsonOps.INSTANCE), output).getOrThrow();
        MAXIMUM_TABLES.put(table, maximum);
        }
        List<ItemStack> result = new ArrayList<>();
        maximum.getRandomItemsRaw(params, stack -> {
            for (ItemStack existing : result) if (ItemStack.isSameItemSameComponents(existing, stack)) { existing.grow(stack.getCount()); return; }
            result.add(stack.copy());
        });
        return result;
    }
    private static JsonObject lootJson(ServerLevel level, LootTable table) {
        return LootTable.DIRECT_CODEC.encodeStart(level.registryAccess().createSerializationContext(JsonOps.INSTANCE), table).getOrThrow().getAsJsonObject();
    }
    private static void flattenTable(ServerLevel level, JsonObject table, int rolls, JsonArray outerFunctions, JsonArray output, Set<String> visited) {
        JsonArray functions = functions(table, outerFunctions);
        if (!table.has("pools")) return;
        for (JsonElement value : table.getAsJsonArray("pools")) {
            JsonObject pool = value.getAsJsonObject();
            int count = rolls * (int) numberMaximum(pool.get("rolls"));
            if (pool.has("bonus_rolls")) count += (int) numberMaximum(pool.get("bonus_rolls"));
            for (JsonElement entry : pool.getAsJsonArray("entries")) flattenEntry(level, entry.getAsJsonObject(), count, functions(pool, functions), output, visited);
        }
    }
    private static void flattenEntry(ServerLevel level, JsonObject entry, int rolls, JsonArray outerFunctions, JsonArray output, Set<String> visited) {
        if (rolls <= 0) return;
        JsonArray functions = functions(entry, outerFunctions);
        String type = entry.get("type").getAsString();
        if (entry.has("children")) {
            for (JsonElement child : entry.getAsJsonArray("children")) flattenEntry(level, child.getAsJsonObject(), rolls, functions, output, visited);
        } else if (type.endsWith(":loot_table")) {
            JsonElement reference = entry.has("value") ? entry.get("value") : entry.get("name");
            String name = reference.isJsonPrimitive() ? reference.getAsString() : reference.toString();
            if (visited.add(name)) {
                JsonObject nested = reference.isJsonObject() ? reference.getAsJsonObject() : lootJson(level,
                        level.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(name))));
                flattenTable(level, nested, rolls, functions, output, visited);
                visited.remove(name);
            }
        } else if (!type.endsWith(":empty")) {
            JsonObject leaf = entry.deepCopy(); leaf.remove("conditions"); leaf.add("functions", functions);
            if (type.endsWith(":tag")) leaf.addProperty("expand", false);
            JsonArray entries = new JsonArray(); entries.add(leaf);
            JsonObject pool = new JsonObject(); pool.addProperty("rolls", rolls); pool.add("entries", entries); output.add(pool);
            if (type.endsWith(":item") && functions.asList().stream().anyMatch(function ->
                    function.getAsJsonObject().get("function").getAsString().endsWith(":furnace_smelt"))) {
                ItemStack raw = new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(entry.get("name").getAsString())));
                if (level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(raw), level).isPresent()) {
                    JsonObject unheatedPool = pool.deepCopy();
                    JsonArray unheated = unheatedPool.getAsJsonArray("entries").get(0).getAsJsonObject().getAsJsonArray("functions");
                    unheated.asList().removeIf(function -> function.getAsJsonObject().get("function").getAsString().endsWith(":furnace_smelt"));
                    output.add(unheatedPool);
                }
            }
        }
    }
    private static JsonArray functions(JsonObject node, JsonArray outer) {
        JsonArray functions = new JsonArray();
        if (node.has("functions")) for (JsonElement element : node.getAsJsonArray("functions")) functions.add(maximize(element));
        for (JsonElement element : outer) functions.add(element.deepCopy());
        return functions;
    }
    private static double numberMaximum(JsonElement value) {
        if (value == null) return 1;
        if (value.isJsonPrimitive()) return value.getAsDouble();
        JsonObject number = value.getAsJsonObject();
        if (number.has("max")) return numberMaximum(number.get("max"));
        if (number.has("value")) return numberMaximum(number.get("value"));
        if (number.has("n")) return numberMaximum(number.get("n"));
        return 1;
    }
    private static JsonElement maximize(JsonElement value) {
        if (value.isJsonArray()) { JsonArray result = new JsonArray(); for (JsonElement item : value.getAsJsonArray()) result.add(maximize(item)); return result; }
        if (!value.isJsonObject()) return value.deepCopy();
        JsonObject object = value.getAsJsonObject();
        if (object.has("type") && (object.get("type").getAsString().endsWith(":uniform") || object.get("type").getAsString().endsWith(":binomial")))
            return new JsonPrimitive(numberMaximum(object));
        if (object.has("function") && (object.get("function").getAsString().endsWith(":looting_enchant")
                || object.get("function").getAsString().endsWith(":enchanted_count_increase"))) {
            JsonObject function = new JsonObject(); function.addProperty("function", "minecraft:set_count");
            function.addProperty("count", 3 * numberMaximum(object.get("count"))); function.addProperty("add", true); return function;
        }
        JsonObject result = new JsonObject();
        for (var entry : object.entrySet()) if (!entry.getKey().equals("conditions")) result.add(entry.getKey(), maximize(entry.getValue()));
        return result;
    }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }
    @SubscribeEvent public static void onServerStopped(ServerStoppedEvent event) { SOURCES.clear(); MAXIMUM_TABLES.clear(); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pm_equips.rcorp_commander"));
        tooltip.add(Component.translatable("tooltip.pm_equips.rcorp_kills", kills(stack)));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
