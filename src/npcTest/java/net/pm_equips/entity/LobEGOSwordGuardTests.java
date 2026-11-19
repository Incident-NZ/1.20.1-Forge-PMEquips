package net.pm_equips.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.pm_equips.ItemInit;

@GameTestHolder("pm_equips")
@PrefixGameTestTemplate(false)
public class LobEGOSwordGuardTests {
    private static Item[] swords() {
        return new Item[]{ItemInit.W5_MIMICRY.get(), ItemInit.W5_JUSTITIA.get()};
    }

    private static Player player(GameTestHelper helper, Item item, InteractionHand hand) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(helper.absoluteVec(new Vec3(2.5, 2, 2.5)));
        player.setNoGravity(true);
        player.setItemInHand(hand, new ItemStack(item));
        player.tick();
        return player;
    }

    private static void raise(Player player, InteractionHand hand) {
        player.getItemInHand(hand).getItem().use(player.level(), player, hand);
        for (int tick = 0; tick < 6; tick++) player.tick();
        player.setYRot(0);
        player.setYHeadRot(0);
        player.setXRot(0);
    }

    private static Zombie attacker(Player player, boolean inFront) {
        Zombie attacker = new Zombie(player.level());
        attacker.setPos(player.position().add(0, 0, inFront ? 2 : -2));
        return attacker;
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void bothSwordsBlockRepeatedFrontalMeleeInEitherHand(GameTestHelper helper) {
        for (Item sword : swords()) for (InteractionHand hand : InteractionHand.values()) {
            Player player = player(helper, sword, hand);
            ItemStack stack = player.getItemInHand(hand);
            helper.assertTrue(stack.canPerformAction(ItemAbilities.SWORD_SWEEP), "Sword sweeping remains supported");
            raise(player, hand);
            helper.assertTrue(player.isBlocking(), sword + " enters the native guard in " + hand);
            Zombie attacker = attacker(player, true);
            for (int hit = 0; hit < 2; hit++) {
                player.invulnerableTime = 0;
                player.hurt(player.damageSources().mobAttack(attacker), 8);
                helper.assertTrue(player.getHealth() == player.getMaxHealth(), "Raised sword blocks frontal melee");
                helper.assertTrue(player.isBlocking(), "Blocking can continue while right click is held");
            }
            helper.assertTrue(stack.getDamageValue() > 0, "Blocked attacks use native shield durability");
        }
        helper.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void guardDoesNotProtectTheRearOrPersistAfterRelease(GameTestHelper helper) {
        for (Item sword : swords()) for (InteractionHand hand : InteractionHand.values()) {
            Player rear = player(helper, sword, hand);
            raise(rear, hand);
            rear.hurt(rear.damageSources().mobAttack(attacker(rear, false)), 8);
            helper.assertTrue(rear.getHealth() < rear.getMaxHealth(), "A sword guard does not block rear attacks");

            Player released = player(helper, sword, hand);
            raise(released, hand);
            released.releaseUsingItem();
            helper.assertTrue(!released.isBlocking(), "Releasing right click ends the guard");
            released.hurt(released.damageSources().mobAttack(attacker(released, true)), 8);
            helper.assertTrue(released.getHealth() < released.getMaxHealth(), "Damage resumes after releasing the guard");
        }
        helper.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void guardBlocksArrowsButRespectsPiercingAndShieldBypass(GameTestHelper helper) {
        for (Item sword : swords()) for (int mode = 0; mode < 3; mode++) {
            Player player = player(helper, sword, InteractionHand.MAIN_HAND);
            raise(player, InteractionHand.MAIN_HAND);
            Zombie attacker = attacker(player, true);
            ItemStack crossbow = new ItemStack(Items.CROSSBOW);
            if (mode == 1) crossbow.enchant(helper.getLevel().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.PIERCING), 1);
            Arrow arrow = new Arrow(player.level(), attacker, new ItemStack(Items.ARROW), crossbow);
            arrow.setPos(attacker.position());
            DamageSource source = mode == 2 ? player.damageSources().sonicBoom(attacker)
                    : player.damageSources().arrow(arrow, attacker);
            player.hurt(source, 8);
            helper.assertTrue(mode == 0 ? player.getHealth() == player.getMaxHealth()
                    : player.getHealth() < player.getMaxHealth(),
                    "Native guard blocks regular arrows and lets piercing arrows / shield-bypassing damage through");
        }
        helper.succeed();
    }

    @GameTest(template = "npc_empty", timeoutTicks = 60)
    public static void holdingOrStartingToRaiseASwordDoesNotGrantImmediateInvulnerability(GameTestHelper helper) {
        for (Item sword : swords()) for (int mode = 0; mode < 2; mode++) {
            Player player = player(helper, sword, InteractionHand.MAIN_HAND);
            if (mode == 1) sword.use(player.level(), player, InteractionHand.MAIN_HAND);
            helper.assertTrue(!player.isBlocking(), "Guard requires the native raising delay");
            player.hurt(player.damageSources().mobAttack(attacker(player, true)), 8);
            helper.assertTrue(player.getHealth() < player.getMaxHealth(), "Holding or just raising the sword does not block");
        }
        Player ordinary = player(helper, Items.DIAMOND_SWORD, InteractionHand.MAIN_HAND);
        ordinary.startUsingItem(InteractionHand.MAIN_HAND);
        for (int tick = 0; tick < 6; tick++) ordinary.tick();
        helper.assertTrue(!ordinary.isBlocking(), "Ordinary swords retain their original behavior");
        helper.succeed();
    }
}

