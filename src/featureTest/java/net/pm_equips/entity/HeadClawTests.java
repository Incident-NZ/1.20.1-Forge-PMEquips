package net.pm_equips.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.items.HeadClawGauntlet;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class HeadClawTests {
    private static Player player(GameTestHelper h) {
        Player player = ForgeTestPlayers.makeMockPlayer(h, GameType.SURVIVAL);
        player.setPos(h.absoluteVec(new Vec3(2.5, 2, 2.5))); player.setNoGravity(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemInit.HEAD_CLAW.get()));
        player.tick(); return player;
    }
    private static void select(Player player, int mode) {
        for (int i = 0; i < 3 && HeadClawGauntlet.serum(player.getMainHandItem()) != mode; i++) HeadClawGauntlet.cycleSerum(player);
    }
    private static void use(Player player) { player.getMainHandItem().getItem().use(player.level(), player, InteractionHand.MAIN_HAND); }
    private static Villager target(GameTestHelper h) {
        Villager target = h.spawn(EntityType.VILLAGER, 5, 2, 5); target.setNoAi(true); target.setNoGravity(true);
        target.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1000); target.setHealth(1000); return target;
    }
    private static float hit(Player player, Villager target) {
        float before = target.getHealth(); target.invulnerableTime = 0;
        target.hurt(player.damageSources().playerAttack(player), (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE));
        return before - target.getHealth();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void baseStatsAndUnbreakability(GameTestHelper h) {
        Player player = player(h); ItemStack stack = player.getMainHandItem();
        h.assertTrue(player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 11 && player.getAttributeValue(Attributes.ATTACK_SPEED) == 2,
                "The gauntlet has eleven attack damage and two attack speed");
        stack.hurtAndBreak(10000000, player, p -> {});
        stack.getItem().hurtEnemy(stack, target(h), player);
        h.assertTrue(!stack.isDamageableItem() && stack.getDamageValue() == 0 && stack.getCount() == 1, "The gauntlet cannot wear out");
        select(player, 1); ItemStack copy = stack.copy();
        h.assertTrue(HeadClawGauntlet.serum(copy) == 1 && HeadClawGauntlet.pendingDamage(copy) == 0, "Mode is stored per stack");
        h.succeed();
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void serumRIsExactly27ThenReturnsToNormalWithSingularity(GameTestHelper h) {
        Player player = player(h); select(player, 1); use(player);
        MobEffectInstance effect = player.getEffect(MobEffectInit.RCORP_SIN.get());
        h.assertTrue(effect != null && effect.getAmplifier() == 0 && effect.getDuration() == 18000, "R Singularity I lasts fifteen minutes");
        h.assertTrue(player.getAttributeValue(Attributes.ATTACK_DAMAGE) == 22, "R normally doubles the eleven-damage weapon");
        Villager target = target(h);
        h.assertTrue(Math.abs(hit(player, target) - 27) < .001, "The empowered strike includes R's multiplier and deals 27, not 54");
        h.assertTrue(HeadClawGauntlet.pendingDamage(player.getMainHandItem()) == 0 && Math.abs(hit(player, target) - 22) < .001,
                "Only the first hit is empowered; the fifteen-minute effect remains");
        player.getCooldowns().removeCooldown(ItemInit.HEAD_CLAW.get()); select(player, 2); use(player);
        h.assertTrue(Math.abs(hit(player, target) - 16) < .001 && Math.abs(hit(player, target) - 22) < .001,
                "K still deals exactly sixteen while R Singularity remains active");
        player.getCooldowns().removeCooldown(ItemInit.HEAD_CLAW.get()); select(player, 1); use(player);
        for (int i = 0; i < 20; i++) player.tick();
        player.setOnGround(true); target.invulnerableTime = 0; float before = target.getHealth(); player.attack(target);
        h.assertTrue(Math.abs(before - target.getHealth() - 27) < .001,
                "The actual vanilla left-click attack also deals 27; damage=" + (before - target.getHealth()));
        for (int i = 0; i < 20; i++) player.tick();
        player.setOnGround(true); target.invulnerableTime = 0; before = target.getHealth(); player.attack(target);
        h.assertTrue(Math.abs(before - target.getHealth() - 22) < .001,
                "The following actual left-click uses the normal R-boosted stat; damage=" + (before - target.getHealth()));
        h.succeed();
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void sharedCooldownCannotBeBypassedByChangingSerum(GameTestHelper h) {
        Player player = player(h); select(player, 1); use(player); select(player, 2); player.setHealth(2); use(player);
        h.assertTrue(player.getHealth() == 2 && !player.hasEffect(MobEffectInit.KCORP_SIN.get()), "Changing serum does not bypass the shared cooldown");
        for (int i = 0; i < 1199; i++) player.getCooldowns().tick();
        h.assertTrue(player.getCooldowns().isOnCooldown(ItemInit.HEAD_CLAW.get()), "Cooldown remains through tick 1199");
        player.getCooldowns().tick();
        h.assertTrue(!player.getCooldowns().isOnCooldown(ItemInit.HEAD_CLAW.get()), "Cooldown ends at sixty seconds");
        h.succeed();
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void serumKHealsHalfAndEmpowersOneHit(GameTestHelper h) {
        Player player = player(h); select(player, 2); player.setHealth(2); use(player);
        h.assertTrue(player.getHealth() == 12 && player.getMaxHealth() == 40, "Heal is half the maximum at activation, then K raises maximum health");
        MobEffectInstance effect = player.getEffect(MobEffectInit.KCORP_SIN.get());
        h.assertTrue(effect != null && effect.getAmplifier() == 0 && effect.getDuration() == 18000, "K Singularity I lasts fifteen minutes");
        Villager target = target(h);
        h.assertTrue(Math.abs(hit(player, target) - 16) < .001 && Math.abs(hit(player, target) - 11) < .001, "K empowers exactly one hit to sixteen");
        h.succeed();
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void serumWReaches128InClearLoadedSpace(GameTestHelper h) {
        Player player = player(h);
        player.setPos(h.absoluteVec(new Vec3(7.5, 160, 7.5))); player.setYRot(-90); player.setXRot(0);
        Vec3 start = player.position();
        for (int x = 0; x <= 144; x += 8) h.getLevel().getChunk(BlockPos.containing(start.add(x, 0, 0)));
        use(player);
        h.assertTrue(Math.abs(player.position().distanceTo(start) - 128) < .001, "W teleports exactly 128 blocks in open loaded space");
        MobEffectInstance effect = player.getEffect(MobEffectInit.WCORP_SIN.get());
        h.assertTrue(effect != null && effect.getAmplifier() == 0 && effect.getDuration() == 18000, "W Singularity I lasts fifteen minutes");
        Villager target = target(h);
        h.assertTrue(Math.abs(hit(player, target) - 30) < .001 && Math.abs(hit(player, target) - 11) < .001, "W empowers only one strike to thirty");
        h.assertTrue(player.fallDistance == 0, "Warp resets fall distance"); h.succeed();
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void serumWStopsBeforeSolidWall(GameTestHelper h) {
        Player player = player(h); player.setYRot(-90); player.setXRot(0);
        for (int y = 1; y <= 5; y++) for (int z = 1; z <= 4; z++) h.setBlock(new BlockPos(6, y, z), Blocks.STONE);
        double start = player.getX(); use(player);
        h.assertTrue(player.getX() > start && player.getBoundingBox().maxX < h.absolutePos(new BlockPos(6, 0, 0)).getX(),
                "Warp stops before the wall and leaves the player's full bounding box outside it");
        h.succeed();
    }
    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void protectionAndRangedDamageDoNotConsumeTheNextMeleeStrike(GameTestHelper h) {
        Player player = player(h); select(player, 1); use(player);
        MobNPC1 npc = h.spawn(EntityInit.MOB_NPC_1.get(), 4, 2, 4); npc.setOwnerUUID(player.getUUID()); npc.setTame(true);
        npc.hurt(player.damageSources().playerAttack(player), 22);
        h.assertTrue(npc.getHealth() == 20 && HeadClawGauntlet.pendingDamage(player.getMainHandItem()) == 27, "Protected allies cannot consume or receive the boosted hit");
        Arrow arrow = new Arrow(h.getLevel(), player);
        Villager target = target(h); target.hurt(player.damageSources().arrow(arrow, player), 3);
        h.assertTrue(HeadClawGauntlet.pendingDamage(player.getMainHandItem()) == 27 && Math.abs(hit(player, target) - 27) < .001,
                "Projectile damage is not empowered and leaves the melee strike ready");
        h.succeed();
    }
}
