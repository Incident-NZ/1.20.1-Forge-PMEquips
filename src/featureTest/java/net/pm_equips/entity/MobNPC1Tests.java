package net.pm_equips.entity;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.nbt.CompoundTag;
import net.pm_equips.config.CommonConfig;
import net.minecraft.world.item.enchantment.Enchantments;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class MobNPC1Tests {
    private static MobNPC1 npc(GameTestHelper h) {
        MobNPC1 npc = h.spawn(EntityInit.MOB_NPC_1.get(), 2, 1, 2);
        npc.setNoAi(true); npc.setNoGravity(true); return npc;
    }
    private static void give(MobNPC1 npc, Player player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack); npc.mobInteract(player, InteractionHand.MAIN_HAND);
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void recruitmentBooksOrdersAndSave(GameTestHelper h) {
        MobNPC1 npc = npc(h); Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        h.assertTrue(npc.getMaxHealth() == 20 && npc.getArmorValue() == 0 && npc.getAttributeValue(Attributes.FOLLOW_RANGE) == 15, "Initial stats");
        ItemStack gold = new ItemStack(Items.GOLD_NUGGET, 2); give(npc, player, gold);
        h.assertTrue(npc.isOwnedBy(player) && gold.getCount() == 1 && npc.getOrder() == MobNPC1.FOLLOW, "Recruitment consumes one gold and follows");
        for (int order : new int[]{MobNPC1.WAIT, MobNPC1.FREE, MobNPC1.FOLLOW}) {
            AttackEntityEvent event = new AttackEntityEvent(player, npc); MobNPC3.onAttack(event);
            h.assertTrue(event.isCanceled() && npc.getOrder() == order, "Left-click order cycle");
        }
        give(npc, player, new ItemStack(Items.PAPER));
        give(npc, player, new ItemStack(Items.BOOK));
        give(npc, player, new ItemStack(Items.WRITABLE_BOOK));
        give(npc, player, new ItemStack(Items.WRITTEN_BOOK));
        give(npc, player, new ItemStack(Items.ENCHANTED_BOOK));
        h.assertTrue(npc.getNPCLevel() == 16 && npc.getRank() == 2 && npc.getMaxHealth() == 50, "All five book types level 1+2+3+4+5");
        h.assertTrue(npc.getAttributeValue(Attributes.ATTACK_DAMAGE) == 17 && npc.getAttributeValue(Attributes.MOVEMENT_SPEED) == MobNPC1.MOVEMENT_SPEED
                && npc.getAttributeValue(Attributes.FOLLOW_RANGE) == 20, "Level and rank scaling");
        CompoundTag saved = new CompoundTag(); npc.addAdditionalSaveData(saved);
        MobNPC1 copy = EntityInit.MOB_NPC_1.get().create(h.getLevel()); copy.readAdditionalSaveData(saved);
        h.assertTrue(copy.getNPCLevel() == 16 && copy.getRank() == 2 && copy.isOwnedBy(player) && copy.getOrder() == MobNPC1.FOLLOW, "Persistent companion state");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void levelCapAndFixedSpeedSurviveOldSaves(GameTestHelper h) {
        MobNPC1 npc = npc(h);
        Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        h.assertTrue(npc.getAttributeValue(Attributes.MOVEMENT_SPEED) == MobNPC1.MOVEMENT_SPEED, "Fixed movement speed matches the configured base");
        npc.gainLevels(98);
        h.assertTrue(npc.getNPCLevel() == 99 && npc.getAttributeValue(Attributes.MOVEMENT_SPEED) == MobNPC1.MOVEMENT_SPEED, "Leveling does not increase speed");
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK); give(npc, player, book);
        h.assertTrue(npc.getNPCLevel() == 100 && book.isEmpty() && npc.getMaxHealth() == 218 && npc.isEXRank()
                && npc.getRank() == 5 && npc.getRankDisplay().equals("EX"), "Books clamp at level 100 with EX rank");
        for (var item : List.of(Items.PAPER, Items.BOOK, Items.WRITABLE_BOOK, Items.WRITTEN_BOOK, Items.ENCHANTED_BOOK)) {
            ItemStack held = new ItemStack(item); give(npc, player, held);
            h.assertTrue(held.getCount() == 1, "Books are not consumed at the level cap");
        }
        npc.gainLevels(Integer.MAX_VALUE);
        for (int i = 0; i < 400; i++) npc.recordKill();
        h.assertTrue(npc.getNPCLevel() == 100 && npc.getKillsTowardLevel() == 0, "Kills and large rewards cannot exceed cap");
        npc.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1024); npc.setHealth(900);
        npc.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(300);
        CompoundTag old = new CompoundTag(); npc.addAdditionalSaveData(old);
        old.putInt("NPCLevel", 500); old.putInt("NPCKills", 1200);
        MobNPC1 copy = EntityInit.MOB_NPC_1.get().create(h.getLevel()); copy.readAdditionalSaveData(old);
        h.assertTrue(copy.getNPCLevel() == 100 && copy.getKillsTowardLevel() == 0 && copy.getHealth() == 218
                && copy.getAttributeValue(Attributes.MOVEMENT_SPEED) == MobNPC1.MOVEMENT_SPEED, "Old saves migrate level, health, kills and speed");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void ranksStopAtFiveAndBecomeEX(GameTestHelper h) {
        MobNPC1 npc = npc(h);
        for (int level = 1; level <= MobNPC1.MAX_LEVEL; level++) {
            if (level > 1) npc.gainLevels(1);
            int numbered = Math.min(5, 1 + (level - 1) / 15);
            h.assertTrue(npc.getRank() == numbered && npc.getRank() <= 5, "Numbered rank never exceeds five at level " + level);
            h.assertTrue(npc.isEXRank() == (level >= 76), "EX starts at level 76");
            h.assertTrue(npc.getRankDisplay().equals(level >= 76 ? "EX" : Integer.toString(numbered)), "Rank display has no six or seven");
            double range = 15 + Math.min(5, (level - 1) / 15) * 5;
            h.assertTrue(npc.getAttributeValue(Attributes.FOLLOW_RANGE) == range, "EX range stays at forty");
            h.assertTrue(npc.getAttributeValue(Attributes.MOVEMENT_SPEED) == MobNPC1.MOVEMENT_SPEED, "Every level keeps the fixed base speed");
        }
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void healingUsesLevelRegardlessOfFoodNutrition(GameTestHelper h) {
        Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        for (int level : new int[]{1, 16, 100}) {
            MobNPC1 npc = npc(h); npc.gainLevels(level - 1);
            for (Item food : List.of(Items.APPLE, Items.BREAD, Items.CAKE, Items.BAKED_POTATO, Items.COOKED_BEEF, Items.ROTTEN_FLESH)) {
                npc.setHealth(1);
                ItemStack stack = new ItemStack(food, 2); give(npc, player, stack);
                h.assertTrue(npc.getHealth() == Math.min(npc.getMaxHealth(), 1 + 8 + level), "Food heals eight plus level: " + food + " at " + level);
                h.assertTrue(stack.getCount() == 1 && !npc.isInLove(), "Healing consumes one food and never starts breeding");
            }
            npc.setHealth(npc.getMaxHealth() - 1);
            give(npc, player, new ItemStack(Items.BREAD));
            h.assertTrue(npc.getHealth() == npc.getMaxHealth() && !npc.isInLove(), "Healing is capped at max HP and requires another feeding to breed");
        }
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void onlySpecifiedFoodsBreedFullHealthAdults(GameTestHelper h) {
        Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        for (Item food : List.of(Items.BREAD, Items.CAKE, Items.COOKED_BEEF, Items.COOKED_PORKCHOP,
                Items.COOKED_CHICKEN, Items.COOKED_MUTTON, Items.COOKED_RABBIT)) {
            MobNPC1 npc = npc(h);
            ItemStack stack = new ItemStack(food, 2); give(npc, player, stack);
            h.assertTrue(npc.isInLove() && stack.getCount() == 1, "Full-health adults accept breeding food: " + food);
            npc.setHealth(npc.getMaxHealth() - 1); give(npc, player, stack);
            h.assertTrue(npc.getHealth() == npc.getMaxHealth() && !npc.isInLove() && stack.isEmpty(), "Healing clears any previous breeding readiness");
        }
        for (Item food : List.of(Items.BAKED_POTATO, Items.APPLE, Items.BEEF, Items.COOKED_COD)) {
            MobNPC1 npc = npc(h);
            ItemStack stack = new ItemStack(food, 2); give(npc, player, stack);
            h.assertTrue(!npc.isInLove() && stack.getCount() == 2, "Other foods do not breed or get consumed at full HP: " + food);
        }
        MobNPC1 child = npc(h); child.setBaby(true);
        ItemStack bread = new ItemStack(Items.BREAD, 2); give(child, player, bread);
        h.assertTrue(!child.isInLove() && bread.getCount() == 2, "Children cannot enter breeding state");
        child.setHealth(1); give(child, player, bread);
        h.assertTrue(child.getHealth() == 10 && bread.getCount() == 1, "Children can still be healed");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void breedingRequiresTwoHealthyParents(GameTestHelper h) {
        MobNPC1 first = npc(h), second = npc(h);
        Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        give(first, player, new ItemStack(Items.BREAD));
        first.spawnChildFromBreeding(h.getLevel(), second);
        h.assertTrue(h.getLevel().getEntitiesOfClass(MobNPC1.class, first.getBoundingBox().inflate(5), MobNPC1::isBaby).isEmpty(), "One fed parent cannot produce a child");
        give(second, player, new ItemStack(Items.CAKE)); second.setHealth(10);
        h.assertTrue(!first.canMate(second), "A damaged partner cannot breed");
        first.spawnChildFromBreeding(h.getLevel(), second);
        h.assertTrue(h.getLevel().getEntitiesOfClass(MobNPC1.class, first.getBoundingBox().inflate(5), MobNPC1::isBaby).isEmpty(), "Damage also blocks birth at the final spawn step");
        second.aiStep();
        h.assertTrue(!second.isInLove(), "Damage clears breeding readiness");
        give(second, player, new ItemStack(Items.BREAD)); give(second, player, new ItemStack(Items.BREAD));
        h.assertTrue(second.getHealth() == 20 && !second.isInLove(), "Recovery alone does not restore breeding readiness");
        give(second, player, new ItemStack(Items.COOKED_CHICKEN));
        h.assertTrue(first.canMate(second), "Both healthy adults must receive breeding food");
        first.spawnChildFromBreeding(h.getLevel(), second);
        h.assertTrue(h.getLevel().getEntitiesOfClass(MobNPC1.class, first.getBoundingBox().inflate(5), MobNPC1::isBaby).size() == 1, "The healthy pair produces exactly one child");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void dualMeleeUsesEachWeaponAndSavesBothHands(GameTestHelper h) {
        MobNPC1 npc = npc(h); Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        give(npc, player, new ItemStack(Items.IRON_SWORD)); give(npc, player, new ItemStack(Items.DIAMOND_SWORD));
        h.assertTrue(npc.getMainHandItem().is(Items.IRON_SWORD) && npc.getOffhandItem().is(Items.DIAMOND_SWORD), "Second weapon enters offhand");
        Zombie target = h.spawn(EntityType.ZOMBIE, 4, 1, 2); target.setNoAi(true);
        target.getAttribute(Attributes.ARMOR).setBaseValue(0);
        double liveDamage = npc.getAttributeValue(Attributes.ATTACK_DAMAGE);
        h.assertTrue(npc.attackWithHand(target, InteractionHand.MAIN_HAND), "Main-hand melee attack");
        h.assertTrue(Math.abs(target.getHealth() - 13) < .001, "Iron sword uses its own damage plus NPC base");
        target.invulnerableTime = 0;
        h.assertTrue(npc.attackWithHand(target, InteractionHand.OFF_HAND), "Offhand melee attack");
        h.assertTrue(Math.abs(target.getHealth() - 5) < .001, "Diamond sword does not reuse the iron sword damage");
        h.assertTrue(npc.getMainHandItem().getDamageValue() == 1 && npc.getOffhandItem().getDamageValue() == 1, "Each hand wears its own weapon");
        h.assertTrue(npc.getAttributeValue(Attributes.ATTACK_DAMAGE) == liveDamage, "Offhand combat does not alter live attributes");
        CompoundTag saved = new CompoundTag(); npc.addAdditionalSaveData(saved);
        MobNPC1 copy = EntityInit.MOB_NPC_1.get().create(h.getLevel()); copy.readAdditionalSaveData(saved);
        h.assertTrue(copy.getMainHandItem().is(Items.IRON_SWORD) && copy.getOffhandItem().is(Items.DIAMOND_SWORD), "Both weapons persist");
        give(npc, player, new ItemStack(Items.GOLDEN_SWORD));
        h.assertTrue(npc.getMainHandItem().is(Items.IRON_SWORD) && npc.getOffhandItem().is(Items.GOLDEN_SWORD), "Third weapon replaces only offhand");
        give(npc, player, ItemStack.EMPTY); give(npc, player, ItemStack.EMPTY);
        var drops = h.getLevel().getEntitiesOfClass(ItemEntity.class, npc.getBoundingBox().inflate(5));
        h.assertTrue(drops.stream().mapToInt(drop -> drop.getItem().getCount()).sum() == 3, "Replacement and unequip drop each weapon once");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void offhandUsesItsOwnEnchantments(GameTestHelper h) {
        MobNPC1 npc = npc(h);
        npc.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        ItemStack enchanted = new ItemStack(Items.DIAMOND_SWORD);
        enchanted.enchant(Enchantments.SHARPNESS, 2);
        enchanted.enchant(Enchantments.FIRE_ASPECT, 1);
        npc.setItemSlot(EquipmentSlot.OFFHAND, enchanted);
        Zombie target = h.spawn(EntityType.ZOMBIE, 4, 1, 2); target.setNoAi(true);
        target.getAttribute(Attributes.ARMOR).setBaseValue(0);
        h.assertTrue(npc.attackWithHand(target, InteractionHand.OFF_HAND), "Enchanted offhand hits");
        h.assertTrue(Math.abs(target.getHealth() - 10.5) < .001 && target.isOnFire(),
                "Offhand sharpness and fire aspect are applied without reading the main hand");
        h.assertTrue(npc.getMainHandItem().getDamageValue() == 0 && enchanted.getDamageValue() == 1,
                "Only the enchanted offhand loses durability");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 100)
    public static void combatAIUsesBothMeleeHands(GameTestHelper h) {
        MobNPC1 npc = npc(h); Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        give(npc, player, new ItemStack(Items.IRON_SWORD)); give(npc, player, new ItemStack(Items.DIAMOND_SWORD));
        Zombie target = h.spawn(EntityType.ZOMBIE, 3, 1, 2); target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(500); target.setHealth(500);
        npc.setTarget(target); npc.setNoAi(false);
        h.runAfterDelay(45, () -> {
            h.assertTrue(npc.getMainHandItem().getDamageValue() > 0 && npc.getOffhandItem().getDamageValue() > 0, "AI alternates hands without losing offhand hits to invulnerability frames");
            h.assertTrue(target.getHealth() < 480, "Both weapons deal damage during normal AI ticks");
            h.succeed();
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void dualRangedHasIndependentCooldownsAndMixedCombat(GameTestHelper h) {
        MobNPC1 npc = npc(h); Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        Zombie target = h.spawn(EntityType.ZOMBIE, 5, 1, 2); target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(500); target.setHealth(500);
        give(npc, player, new ItemStack(ItemInit.W4_MAGIC_BULLET.get())); give(npc, player, new ItemStack(ItemInit.W4_MAGIC_BULLET.get()));
        npc.performRangedAttack(target, 1);
        h.assertTrue(npc.getMainHandItem().getDamageValue() == 1 && npc.getOffhandItem().getDamageValue() == 1, "Identical guns both fire with no supplied ammunition");
        float afterBoth = target.getHealth();
        h.assertTrue(afterBoth < 465, "Both projectiles inflict damage");
        npc.performRangedAttack(target, 1);
        h.assertTrue(target.getHealth() == afterBoth, "Both cooldowns prevent an immediate repeat");
        for (int i = 0; i < 40; i++) npc.aiStep();
        npc.performRangedAttack(target, 1);
        h.assertTrue(npc.getMainHandItem().getDamageValue() == 2 && npc.getOffhandItem().getDamageValue() == 2, "Both hand cooldowns tick independently");
        npc.dropEquipment();
        give(npc, player, new ItemStack(Items.IRON_SWORD)); give(npc, player, new ItemStack(ItemInit.W4_MAGIC_BULLET.get()));
        target.moveTo(npc.getX() + 2, npc.getY(), npc.getZ(), 0, 0);
        target.invulnerableTime = 0; target.hurtTime = 0;
        npc.useWeapons(target, npc.distanceToSqr(target));
        h.assertTrue(npc.getMainHandItem().getDamageValue() == 1 && npc.getOffhandItem().getDamageValue() == 1,
                "Mixed melee/ranged loadout uses both weapons within their range (main=" + npc.getMainHandItem().getDamageValue() + ", off=" + npc.getOffhandItem().getDamageValue() + ")");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void equipmentAndWakeDoNotDuplicate(GameTestHelper h) {
        MobNPC1 npc = npc(h); Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        give(npc, player, new ItemStack(Items.GOLD_INGOT));
        give(npc, player, new ItemStack(Items.DIAMOND_HELMET));
        give(npc, player, new ItemStack(Items.IRON_HORSE_ARMOR));
        h.assertTrue(npc.getItemBySlot(EquipmentSlot.HEAD).is(Items.DIAMOND_HELMET) && npc.getMainHandItem().isEmpty(), "Unsupported animal armor does not replace equipment or appear as a weapon");
        give(npc, player, new ItemStack(Items.IRON_CHESTPLATE));
        h.assertTrue(npc.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && npc.getItemBySlot(EquipmentSlot.CHEST).is(Items.IRON_CHESTPLATE), "One armor across different slots");
        give(npc, player, new ItemStack(Items.IRON_SWORD));
        npc.setPose(Pose.SLEEPING);
        give(npc, player, ItemStack.EMPTY);
        h.assertTrue(!npc.isResting() && !npc.getMainHandItem().isEmpty() && !npc.getItemBySlot(EquipmentSlot.CHEST).isEmpty(), "Waking does not drop equipment");
        give(npc, player, ItemStack.EMPTY);
        give(npc, player, ItemStack.EMPTY);
        var drops = h.getLevel().getEntitiesOfClass(ItemEntity.class, npc.getBoundingBox().inflate(5));
        h.assertTrue(drops.stream().mapToInt(drop -> drop.getItem().getCount()).sum() == 3, "Helmet, chestplate, sword each drop exactly once");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void foodBreedingAndSevenDayChildhood(GameTestHelper h) {
        MobNPC1 first = npc(h), second = npc(h); Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        first.setHealth(2); give(first, player, new ItemStack(Items.APPLE));
        h.assertTrue(first.getHealth() == 11, "Each food heals eight plus the NPC level");
        give(first, player, new ItemStack(Items.BREAD));
        h.assertTrue(first.getHealth() == 20 && !first.isInLove(), "The food that restores full health does not start breeding");
        give(first, player, new ItemStack(Items.BREAD)); give(second, player, new ItemStack(Items.COOKED_BEEF));
        h.assertTrue(first.canMate(second), "Full-health adults breed after another food item");
        first.spawnChildFromBreeding(h.getLevel(), second);
        List<MobNPC1> children = h.getLevel().getEntitiesOfClass(MobNPC1.class, first.getBoundingBox().inflate(5), MobNPC1::isBaby);
        h.assertTrue(children.size() == 1, "Breeding spawns one child");
        MobNPC1 child = children.get(0);
        h.assertTrue(child.getAge() == -MobNPC1.CHILDHOOD_TICKS && !child.isTame(), "Childhood is seven full game days");
        ItemStack gold = new ItemStack(Items.GOLD_BLOCK, 2); give(child, player, gold);
        h.assertTrue(!child.isTame() && gold.getCount() == 2, "Children cannot be recruited or consume gold");
        CompoundTag tag = new CompoundTag(); child.addAdditionalSaveData(tag);
        tag.putLong("NPCAdultAt", h.getLevel().getGameTime() - 1); child.readAdditionalSaveData(tag); child.aiStep();
        h.assertTrue(!child.isBaby(), "Adult time is based on elapsed world time, including unloaded time");
        give(child, player, gold);
        h.assertTrue(child.isTame() && gold.getCount() == 1, "Adults can be recruited");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void killsAndPlayerProtection(GameTestHelper h) {
        MobNPC1 npc = npc(h); Player owner = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        give(npc, owner, new ItemStack(Items.GOLD_BLOCK));
        float health = npc.getHealth();
        h.assertTrue(!npc.hurt(h.getLevel().damageSources().playerAttack(owner), 100) && npc.getHealth() == health, "Owner damage is blocked");
        h.assertTrue(!npc.canAttack(owner), "NPC never attacks owner");
        for (int i = 0; i < 3; i++) {
            Zombie zombie = h.spawn(EntityType.ZOMBIE, 4, 1, 2); zombie.setNoAi(true); zombie.setHealth(1);
            h.assertTrue(npc.doHurtTarget(zombie), "NPC can attack a hostile mob");
        }
        h.assertTrue(npc.getNPCLevel() == 2 && npc.getKillsTowardLevel() == 0, "Level one requires three kills");
        for (int i = 0; i < 5; i++) npc.recordKill();
        h.assertTrue(npc.getNPCLevel() == 2, "Level two needs six new kills");
        npc.recordKill(); h.assertTrue(npc.getNPCLevel() == 3, "Level two threshold is level times three");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void rangedWeaponsNeedNoSuppliedAmmo(GameTestHelper h) {
        MobNPC1 npc = npc(h);
        Zombie target = h.spawn(EntityType.ZOMBIE, 5, 1, 2); target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10000); target.setHealth(target.getMaxHealth());
        float initial = target.getHealth();
        npc.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.W4_MAGIC_BULLET.get()));
        npc.performRangedAttack(target, 1);
        h.assertTrue(target.getHealth() < initial, "Magic Bullet fires without a player or supplied ammo");
        List<Item> weapons = new ArrayList<>(List.of(Items.BOW, Items.CROSSBOW));
        for (var holder : ItemInit.ITEMS.getEntries()) if (holder.get() instanceof ProjectileWeaponItem) weapons.add(holder.get());
        for (Item item : weapons) for (InteractionHand hand : InteractionHand.values()) {
            target.setHealth(target.getMaxHealth()); target.invulnerableTime = 0; target.hurtTime = 0;
            int beforeProjectiles = h.getLevel().getEntitiesOfClass(Projectile.class, npc.getBoundingBox().inflate(32)).size();
            float beforeHealth = target.getHealth();
            npc.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY); npc.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            npc.setItemSlot(MobNPC1.getSlotForHand(hand), new ItemStack(item)); npc.setWeaponUser(hand, null);
            npc.performRangedAttack(target, 1);
            int afterProjectiles = h.getLevel().getEntitiesOfClass(Projectile.class, npc.getBoundingBox().inflate(32)).size();
            h.assertTrue(target.getHealth() < beforeHealth || afterProjectiles > beforeProjectiles, "Weapon must damage target or emit a projectile: " + item + " in " + hand);
        }
        npc.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ItemInit.W5_SOUND_OF_A_STAR.get()));
        npc.weaponUser = null; npc.performRangedAttack(target, 1);
        h.assertTrue(weapons.size() >= 20, "All registered projectile weapons and vanilla bows are exercised in both hands");
        h.runAfterDelay(20, () -> {
            h.assertTrue(target.getHealth() < initial, "Server ranged damage is applied");
            h.assertTrue(h.getLevel().getEntitiesOfClass(AbstractArrow.class, npc.getBoundingBox().inflate(12)).stream()
                    .noneMatch(arrow -> arrow.getOwner() == npc && arrow.pickup == AbstractArrow.Pickup.ALLOWED), "Infinite NPC ammunition cannot be farmed as dropped arrows");
            h.succeed();
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void pvpModesAndOtherOwnersCannotChangeEquipment(GameTestHelper h) {
        MobNPC1 npc = npc(h), otherNpc = npc(h);
        Player owner = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL), other = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        give(npc, owner, new ItemStack(Items.GOLD_INGOT)); give(otherNpc, other, new ItemStack(Items.GOLD_INGOT));
        give(npc, owner, new ItemStack(Items.DIAMOND_SWORD)); give(npc, other, ItemStack.EMPTY);
        h.assertTrue(npc.getMainHandItem().is(Items.DIAMOND_SWORD), "Another player cannot steal equipment");
        boolean pvp = h.getLevel().getServer().isPvpAllowed();
        var disposition = CommonConfig.NPC_PVP_DISPOSITION.get();
        try {
            h.getLevel().getServer().setPvpAllowed(false);
            CommonConfig.NPC_PVP_DISPOSITION.set(CommonConfig.NPCDisposition.HOSTILE);
            h.assertTrue(!npc.canAttack(other) && !npc.canAttack(otherNpc), "PvP off protects other players and NPCs even in hostile mode");
            h.getLevel().getServer().setPvpAllowed(true);
            CommonConfig.NPC_PVP_DISPOSITION.set(CommonConfig.NPCDisposition.NEUTRAL);
            h.assertTrue(!npc.canAttack(other), "Neutral mode does not initiate combat");
            npc.setLastHurtByMob(other);
            h.assertTrue(npc.canAttack(other), "Neutral mode can retaliate in PvP");
            CommonConfig.NPC_PVP_DISPOSITION.set(CommonConfig.NPCDisposition.HOSTILE);
            h.assertTrue(npc.canAttack(other) && npc.canAttack(otherNpc) && !npc.canAttack(owner), "Hostile mode only attacks other owners");
        } finally { h.getLevel().getServer().setPvpAllowed(pvp); CommonConfig.NPC_PVP_DISPOSITION.set(disposition); }
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void nightSleepAndBareHandWake(GameTestHelper h) {
        buildHouse(h, 1, true);
        MobNPC1 npc = npc(h); npc.moveTo(h.absolutePos(new BlockPos(3, 1, 3)).getCenter()); npc.setNoAi(false);
        Player player = ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL);
        give(npc, player, new ItemStack(Items.IRON_HELMET));
        long time = h.getLevel().getDayTime(); h.getLevel().setDayTime(18000);
        h.runAfterDelay(15, () -> {
            try {
                h.assertTrue(npc.isResting() && npc.isSleeping(), "Wild NPC actually enters a bed at night");
                give(npc, player, ItemStack.EMPTY);
                h.assertTrue(!npc.isResting() && npc.getItemBySlot(EquipmentSlot.HEAD).is(Items.IRON_HELMET), "A sleeping NPC wakes without dropping armor");
                h.succeed();
            } finally { h.getLevel().setDayTime(time); }
        });
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void woolAndCarpetPrecedeBareFloor(GameTestHelper h) {
        MobNPC1 npc = npc(h);
        BlockPos high = h.absolutePos(new BlockPos(3, 96, 3));
        npc.moveTo(high.getX() + .5, high.getY(), high.getZ() + .5, 0, 0);
        npc.setOwnerUUID(ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL).getUUID()); npc.setTame(true);
        h.getLevel().setBlock(high.below(), Blocks.WHITE_WOOL.defaultBlockState(), 3);
        h.assertTrue(MobNPC3.findRestPlace(npc).equals(high), "Wool is selected without a bed in range");
        h.getLevel().setBlock(high.below(), Blocks.STONE.defaultBlockState(), 3);
        h.getLevel().setBlock(high, Blocks.WHITE_CARPET.defaultBlockState(), 3);
        h.assertTrue(MobNPC3.findRestPlace(npc).equals(high), "Carpet is selected before a bare floor");
        h.getLevel().setBlock(high, Blocks.AIR.defaultBlockState(), 3);
        h.assertTrue(MobNPC3.findRestPlace(npc).equals(high), "No bed or wool falls back to the current floor");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 80)
    public static void villagerConversionOnlyReplacesNitwits(GameTestHelper h) {
        Villager nitwit = h.spawn(EntityType.VILLAGER, 3, 1, 3); nitwit.setNoAi(true); nitwit.setBaby(true);
        nitwit.setVillagerData(nitwit.getVillagerData().setProfession(VillagerProfession.NITWIT));
        h.assertTrue(MobNPC3.replaceVillager(nitwit) && nitwit.isRemoved(), "Nitwit villager is replaced atomically");
        h.assertTrue(h.getLevel().getEntitiesOfClass(MobNPC1.class, nitwit.getBoundingBox().inflate(2), MobNPC1::isBaby).size() == 1, "Nitwit child becomes a seven-day NPC child");
        Villager jobseeker = h.spawn(EntityType.VILLAGER, 8, 1, 3);
        h.assertTrue(jobseeker.getVillagerData().getProfession() == VillagerProfession.NONE
                && !MobNPC3.replaceVillager(jobseeker) && !jobseeker.isRemoved(), "Unemployed job seekers are preserved");
        Villager worker = h.spawn(EntityType.VILLAGER, 6, 1, 3); worker.setVillagerData(worker.getVillagerData().setProfession(VillagerProfession.FARMER));
        h.assertTrue(!MobNPC3.replaceVillager(worker) && !worker.isRemoved(), "Employed villagers are preserved");
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void externalSkinConfigurationAndInvalidPNGs(GameTestHelper h) throws IOException {
        byte[] png;
        try (var input = MobNPC1Tests.class.getResourceAsStream("/assets/pm_equips/textures/entity/npc_default_1.png")) { png = input.readAllBytes(); }
        h.assertTrue(MobNPC3.validPNG(png) && !MobNPC3.validPNG(new byte[64]), "PNG signature and dimensions are checked");
        var file = MobNPC3.skinDirectory().resolve("npc_test.slim.png");
        String fixed = CommonConfig.NPC_FIXED_SKIN.get(); boolean defaults = CommonConfig.NPC_DEFAULT_SKINS.get();
        try {
            Files.write(file, png); MobNPC3.reloadSkins();
            CommonConfig.NPC_DEFAULT_SKINS.set(false); CommonConfig.NPC_FIXED_SKIN.set("npc_test.slim.png");
            MobNPC1 npc = npc(h); MobNPC3.ensureSkin(npc);
            h.assertTrue(npc.getSkinName().equals("npc_test.slim.png"), "Fixed external filename overrides defaults");
            CommonConfig.NPC_FIXED_SKIN.set(""); npc.setSkinName("builtin:default_1"); MobNPC3.ensureSkin(npc);
            h.assertTrue(npc.getSkinName().equals("npc_test.slim.png"), "Disabled defaults are excluded from random selection");
        } finally {
            Files.deleteIfExists(file); CommonConfig.NPC_FIXED_SKIN.set(fixed); CommonConfig.NPC_DEFAULT_SKINS.set(defaults); MobNPC3.reloadSkins();
        }
        h.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void houseNeedsDoorWindowBedAndThreeCube(GameTestHelper h) {
        buildHouse(h, 1, true); buildHouse(h, 8, false);
        BlockPos good = h.absolutePos(new BlockPos(3, 1, 3));
        BlockPos bad = h.absolutePos(new BlockPos(10, 1, 3));
        h.assertTrue(MobNPC3.inspectHome(h.getLevel(), good) != null, "Enclosed 3x3x3 house with door, window, bed");
        h.assertTrue(MobNPC3.inspectHome(h.getLevel(), bad) == null, "House without window is rejected");
        MobNPC1 npc = npc(h); npc.setOwnerUUID(ForgeTestPlayers.makeMockPlayer(h,GameType.SURVIVAL).getUUID()); npc.setTame(true);
        BlockPos rest = MobNPC3.findRestPlace(npc);
        h.assertTrue(rest.equals(good), "Tamed NPC selects a valid base bed within four chunks");
        h.succeed();
    }
    private static void buildHouse(GameTestHelper h, int x0, boolean window) {
        for (int x = 0; x < 5; x++) for (int y = 0; y < 5; y++) for (int z = 0; z < 5; z++)
            h.setBlock(x0 + x, y, 1 + z, x == 0 || x == 4 || y == 0 || y == 4 || z == 0 || z == 4 ? Blocks.OAK_PLANKS : Blocks.AIR);
        h.setBlock(x0 + 2, 1, 1, Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        h.setBlock(x0 + 2, 2, 1, Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        if (window) h.setBlock(x0, 2, 3, Blocks.GLASS);
        h.setBlock(x0 + 2, 1, 2, Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.SOUTH).setValue(BedBlock.PART, BedPart.FOOT));
        h.setBlock(x0 + 2, 1, 3, Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, Direction.SOUTH).setValue(BedBlock.PART, BedPart.HEAD));
    }
}
