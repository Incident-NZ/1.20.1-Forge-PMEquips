package net.pm_equips.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.pm_equips.util.ItemStackData;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class AromaWeaponTests {
    private static Player player(GameTestHelper helper, InteractionHand hand) {
        Player player = ForgeTestPlayers.makeMockPlayer(helper, GameType.SURVIVAL);
        player.setPos(helper.absoluteVec(new Vec3(2.5, 2, 5.5)));
        player.setYRot(-90); player.setXRot(0); player.setNoGravity(true);
        player.setItemInHand(hand, new ItemStack(ItemInit.W4_AROMA.get()));
        player.tick(); return player;
    }
    private static Villager target(GameTestHelper helper, int x) {
        Villager target = helper.spawn(EntityType.VILLAGER, x, 2, 5);
        target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(200); target.setHealth(200);
        return target;
    }
    private static InteractionResult fire(Player player, InteractionHand hand) {
        return player.getItemInHand(hand).getItem().use(player.level(), player, hand).getResult();
    }
    private static int projectiles(Player player) {
        return player.level().getEntitiesOfClass(Projectile.class, player.getBoundingBox().inflate(16)).size();
    }
    private static void ready(Player player) { player.getCooldowns().removeCooldown(ItemInit.W4_AROMA.get()); }
    private static void damageInRange(GameTestHelper helper, float damage) {
        helper.assertTrue(damage >= 10 && damage <= 20, "Particle damage remains in the agreed 10-20 range: " + damage);
    }

    @GameTest(template="npc_empty", timeoutTicks=60)
    public static void firesInventoryArrowsImmediatelyWithoutReloadAndWaitsTwentyTicks(GameTestHelper helper) {
        Player player = player(helper, InteractionHand.MAIN_HAND);
        ItemStack arrows = new ItemStack(Items.ARROW, 3), gun = player.getMainHandItem();
        player.getInventory().setItem(10, arrows);
        Villager target = target(helper, 8);
        int before = projectiles(player);
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND).consumesAction(), "First click fires without a reload command");
        damageInRange(helper, 200 - target.getHealth());
        helper.assertTrue(arrows.getCount() == 2 && gun.getDamageValue() == 1, "A shot spends one arrow and one durability");
        helper.assertTrue(projectiles(player) == before, "A shot never adds a projectile entity");
        float health = target.getHealth();
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND) == InteractionResult.FAIL, "Cooldown rejects an immediate repeat");
        helper.assertTrue(arrows.getCount() == 2 && target.getHealth() == health, "Rejected repeats spend nothing");
        for (int tick = 0; tick < 19; tick++) player.getCooldowns().tick();
        helper.assertTrue(player.getCooldowns().isOnCooldown(gun.getItem()), "Cooldown remains at tick nineteen");
        player.getCooldowns().tick(); target.invulnerableTime = 0;
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND).consumesAction(), "Tick twenty allows the next shot");
        damageInRange(helper, health - target.getHealth());
        helper.assertTrue(arrows.getCount() == 1 && gun.getDamageValue() == 2, "Repeated firing still consumes exactly once");
        helper.succeed();
    }
    @GameTest(template="npc_empty", timeoutTicks=60)
    public static void supportsBothHandsAndSpectralArrowsKeepTheirGlow(GameTestHelper helper) {
        for (InteractionHand hand : InteractionHand.values()) {
            Player player = player(helper, hand);
            ItemStack arrows = new ItemStack(Items.SPECTRAL_ARROW, 2);
            InteractionHand ammoHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            player.setItemInHand(ammoHand, arrows);
            Villager target = target(helper, 8);
            int before = projectiles(player);
            helper.assertTrue(fire(player, hand).consumesAction(), "Either gun hand can use arrows in the other hand");
            damageInRange(helper, 200 - target.getHealth());
            var glow = target.getEffect(MobEffects.GLOWING);
            helper.assertTrue(glow != null && glow.getAmplifier() == 0 && glow.getDuration() == 200, "Spectral ammunition preserves ten seconds of Glowing");
            helper.assertTrue(arrows.getCount() == 1 && projectiles(player) == before, "Spectral ammunition is consumed without spawning an arrow");
            target.discard();
        }
        helper.succeed();
    }
    @GameTest(template="npc_empty", timeoutTicks=60)
    public static void rejectsMissingAndTippedAmmunitionWithoutDurabilityOrCooldown(GameTestHelper helper) {
        Player player = player(helper, InteractionHand.MAIN_HAND);
        ItemStack gun = player.getMainHandItem();
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND) == InteractionResult.FAIL, "Survival firing needs an arrow");
        ItemStack tipped = new ItemStack(Items.TIPPED_ARROW, 3); player.getInventory().setItem(10, tipped);
        helper.assertTrue(!((ProjectileWeaponItem)gun.getItem()).getAllSupportedProjectiles().test(tipped), "Tipped arrows remain unsupported");
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND) == InteractionResult.FAIL, "Unsupported arrows cannot fire");
        helper.assertTrue(tipped.getCount() == 3 && gun.getDamageValue() == 0 && !player.getCooldowns().isOnCooldown(gun.getItem()),
                "Dry firing cannot consume unsupported ammunition, durability or cooldown");
        helper.succeed();
    }
    @GameTest(template="npc_empty", timeoutTicks=60)
    public static void clipsBlocksBeforeEntitiesAndOnlyHitsTheNearestTarget(GameTestHelper helper) {
        Player player = player(helper, InteractionHand.MAIN_HAND);
        ItemStack arrows = new ItemStack(Items.ARROW, 3); player.getInventory().setItem(10, arrows);
        Villager near = target(helper, 5), far = target(helper, 8);
        fire(player, InteractionHand.MAIN_HAND);
        damageInRange(helper, 200 - near.getHealth());
        helper.assertTrue(far.getHealth() == 200 && !near.hasEffect(MobEffects.GLOWING), "A regular arrow hits only the first target without spectral effects");
        ready(player);
        for (int y = 2; y <= 5; y++) for (int z = 4; z <= 6; z++) helper.setBlock(new BlockPos(4, y, z), Blocks.STONE);
        float health = near.getHealth(); fire(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(near.getHealth() == health && far.getHealth() == 200 && arrows.getCount() == 1, "A solid wall blocks targets while a fired round is still consumed");
        helper.succeed();
    }
    @GameTest(template="npc_empty", timeoutTicks=60)
    public static void creativeUsesInfiniteAmmoWithoutWearingTheGun(GameTestHelper helper) {
        Player player = player(helper, InteractionHand.MAIN_HAND); player.getAbilities().instabuild = true;
        Villager target = target(helper, 8);
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND).consumesAction(), "Creative can fire a regular round without inventory ammunition");
        damageInRange(helper, 200 - target.getHealth());
        ItemStack arrows = new ItemStack(Items.SPECTRAL_ARROW); player.getInventory().setItem(10, arrows);
        ready(player); target.invulnerableTime = 0; fire(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(arrows.getCount() == 1 && player.getMainHandItem().getDamageValue() == 0 && target.hasEffect(MobEffects.GLOWING),
                "Creative preserves ammunition and durability, including spectral effects");
        helper.succeed();
    }
    @GameTest(template="npc_empty", timeoutTicks=60)
    public static void convertsPreviouslyLoadedAmmoOnceAndRetainsUnrelatedItemData(GameTestHelper helper) {
        Player player = player(helper, InteractionHand.MAIN_HAND);
        ItemStack gun = player.getMainHandItem();
        ItemStackData.update(gun, tag -> {
            tag.put("LoadedArrow", new ItemStack(Items.SPECTRAL_ARROW).save(new CompoundTag()));
            tag.putInt("Reload", 10); tag.putInt("unrelated_test_value", 42);
        });
        Villager target = target(helper, 8);
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND).consumesAction() && target.hasEffect(MobEffects.GLOWING), "An old loaded arrow is recovered and usable immediately");
        helper.assertTrue(!ItemStackData.contains(gun, "LoadedArrow") && !ItemStackData.contains(gun, "Reload")
                && ItemStackData.getInt(gun, "unrelated_test_value") == 42, "Only obsolete magazine state is removed");
        ready(player);
        helper.assertTrue(fire(player, InteractionHand.MAIN_HAND) == InteractionResult.FAIL, "Migration cannot refund or reuse the same arrow twice");
        helper.succeed();
    }
    @GameTest(template="npc_empty", timeoutTicks=60)
    public static void protectedNpcsCannotBeDamagedOrMarkedBySpectralShots(GameTestHelper helper) {
        Player player = player(helper, InteractionHand.MAIN_HAND);
        player.getInventory().setItem(10, new ItemStack(Items.SPECTRAL_ARROW));
        MobNPC1 target = helper.spawn(EntityInit.MOB_NPC_1.get(), 8, 2, 5);
        target.setOwnerUUID(player.getUUID()); target.setTame(true); target.setNoAi(true); target.setNoGravity(true);
        fire(player, InteractionHand.MAIN_HAND);
        helper.assertTrue(target.getHealth() == target.getMaxHealth() && !target.hasEffect(MobEffects.GLOWING), "A particle round respects NPC ownership protection");
        helper.succeed();
    }
}
