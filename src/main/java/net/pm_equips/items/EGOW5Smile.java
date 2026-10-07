package net.pm_equips.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.pm_equips.BlockInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EGOW5Smile extends SwordItem {

    private static final String ATTACK_COUNT_TAG = "smile_attack_count";
    /** この武器固有ボーナス（0〜30）。他ステータスと合算せず、この値だけが上限 */
    private static final String BONUS_TAG = "smile_stat_bonus";
    private static final int BONUS_PER_KILL = 2;
    private static final int BONUS_CAP = 30;

    private static final UUID MAX_HP_UUID = UUID.fromString("a1b2c3d4-e5f6-7890-ab12-cd34ef567801");
    private static final UUID ATK_UUID   = UUID.fromString("a1b2c3d4-e5f6-7890-ab12-cd34ef567802");

    public EGOW5Smile() {
        super(new CustomTier(), 17, -2.1f, new Properties().durability(4000));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (result && !attacker.level().isClientSide()) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;
        }

        if (!attacker.level().isClientSide && attacker instanceof Player player) {

            target.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 100, 6));

            var tag = stack.getOrCreateTag();
            int count = tag.getInt(ATTACK_COUNT_TAG) + 1;

            if (count >= 5) {
                count = 0;
                double radius = 16.0;
                AABB area = new AABB(
                        player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                        player.getX() + radius, player.getY() + radius, player.getZ() + radius
                );
                List<LivingEntity> entities = player.level().getEntitiesOfClass(
                        LivingEntity.class, area, e -> e != player && e.isAlive()
                );
                for (LivingEntity entity : entities) {
                    entity.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 100, 6));
                }
                target.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 60, 4));
            }
            tag.putInt(ATTACK_COUNT_TAG, count);

            // この武器の攻撃で HP が 0 になった（撃破）→ 固有ボーナス +2（上限30）
            if (target.isDeadOrDying() || target.getHealth() <= 0.0F) {
                addKillBonus(stack, player);
            }
        }

        stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(attacker.getUsedItemHand()));
        return true;
    }

    /** 撃破ボーナスを武器NBTに加算（この効果のみで最大30） */
    private void addKillBonus(ItemStack stack, Player player) {
        var tag = stack.getOrCreateTag();
        int bonus = tag.getInt(BONUS_TAG);
        if (bonus >= BONUS_CAP) {
            return;
        }
        int next = Math.min(BONUS_CAP, bonus + BONUS_PER_KILL);
        tag.putInt(BONUS_TAG, next);

        // 所持中なら即座に属性へ反映
        applyBonusAttributes(player, next);

        // 最大HPが上がった分、現在HPも少し回復（任意だが体感が良い）
        player.setHealth(Math.min(player.getHealth() + BONUS_PER_KILL, player.getMaxHealth()));
    }

    public static int getBonus(ItemStack stack) {
        return stack.getOrCreateTag().getInt(BONUS_TAG);
    }

    /** 所持中のみ属性付与。未所持なら除去（装備解除で上限＝元のステータスに戻る） */
    private void applyBonusAttributes(Player player, int bonus) {
        AttributeInstance maxHp = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance atk = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (maxHp == null || atk == null) return;

        maxHp.removeModifier(MAX_HP_UUID);
        atk.removeModifier(ATK_UUID);

        if (bonus > 0) {
            maxHp.addTransientModifier(new AttributeModifier(
                    MAX_HP_UUID, "egow5_smile_max_hp", bonus, AttributeModifier.Operation.ADDITION
            ));
            atk.addTransientModifier(new AttributeModifier(
                    ATK_UUID, "egow5_smile_attack", bonus, AttributeModifier.Operation.ADDITION
            ));
        }

        // 最大HP減少時にはみ出したHPを切り詰め
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private void clearBonusAttributes(Player player) {
        AttributeInstance maxHp = player.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance atk = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (maxHp != null) maxHp.removeModifier(MAX_HP_UUID);
        if (atk != null) atk.removeModifier(ATK_UUID);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean holding = (selected && player.getMainHandItem() == stack)
                || player.getOffhandItem() == stack;

        if (holding) {
            applyBonusAttributes(player, getBonus(stack));
        } else {
            // このスタックを持っていないとき、他に同じ武器を持っていなければクリア
            if (!isHoldingAnySmile(player)) {
                clearBonusAttributes(player);
            }
        }
    }

    private static boolean isHoldingAnySmile(Player player) {
        return player.getMainHandItem().getItem() instanceof EGOW5Smile
                || player.getOffhandItem().getItem() instanceof EGOW5Smile;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 4000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(BlockInit.BlockItems.ALEPH_PE_BOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int bonus = getBonus(stack);
        tooltip.add(Component.literal("§e固有ボーナス: +" + bonus + " / " + BONUS_CAP + "（最大HP・攻撃力）"));
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.SMILE_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}