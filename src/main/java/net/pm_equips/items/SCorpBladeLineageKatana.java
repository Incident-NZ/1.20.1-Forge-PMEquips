package net.pm_equips.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.UUID;

/**
 * 納刀 / 抜刀で性能が変わる刀
 * 納刀時: 攻撃力1 / 速度1.5 / 耐久2000 / 攻撃で耐久消費なし
 *   - 右クリック単押し → 抜刀
 *   - 右クリック長押し → カウンター（30tick）成功時相手に30ダメージ
 *   - カウンター終了後 5秒は長押し不可（単押しのみ）
 * 抜刀時: 攻撃力12 / 速度1.5 / 耐久2000
 *   - 右クリック長押し → 納刀
 */
public class SCorpBladeLineageKatana extends Item {

    private static final String TAG_DRAWN = "Drawn";
    private static final String TAG_COUNTER = "CounterTicks";
    private static final String TAG_SKILL_LOCK = "SkillLock";

    private static final int COUNTER_DURATION = 30;      // カウンター有効時間
    private static final int SKILL_LOCK_TICKS = 100;     // 終了後5秒（20*5）
    private static final int LONG_PRESS_TICKS = 15;      // これ以上押し続けたら長押し
    private static final float COUNTER_DAMAGE = 30.0F;

    private static final float SHEATHED_DAMAGE = 1.0F;   // 表示用・属性用（基本1 + 0）
    private static final float DRAWN_DAMAGE = 12.0F;     // 基本1 + 11
    private static final double ATTACK_SPEED_MOD = -2.5D; // 4.0 + (-2.5) = 1.5

    private static final UUID DAMAGE_UUID = Item.BASE_ATTACK_DAMAGE_UUID;
    private static final UUID SPEED_UUID = Item.BASE_ATTACK_SPEED_UUID;

    public SCorpBladeLineageKatana(Properties properties) {
        super(properties.durability(2000));
        MinecraftForge.EVENT_BUS.register(new CounterHandler());
    }

    // -------------------------------------------------------------------------
    // NBT ヘルパー
    // -------------------------------------------------------------------------

    public static boolean isDrawn(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(TAG_DRAWN);
    }

    public static void setDrawn(ItemStack stack, boolean drawn) {
        stack.getOrCreateTag().putBoolean(TAG_DRAWN, drawn);
    }

    public static int getCounterTicks(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TAG_COUNTER);
    }

    public static void setCounterTicks(ItemStack stack, int ticks) {
        stack.getOrCreateTag().putInt(TAG_COUNTER, Math.max(0, ticks));
    }

    public static int getSkillLock(ItemStack stack) {
        return stack.getOrCreateTag().getInt(TAG_SKILL_LOCK);
    }

    public static void setSkillLock(ItemStack stack, int ticks) {
        stack.getOrCreateTag().putInt(TAG_SKILL_LOCK, Math.max(0, ticks));
    }

    public static boolean isCounterActive(ItemStack stack) {
        return getCounterTicks(stack) > 0;
    }

    // -------------------------------------------------------------------------
    // 属性（納刀 / 抜刀で攻撃力切替）
    // -------------------------------------------------------------------------

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot != EquipmentSlot.MAINHAND) {
            return super.getAttributeModifiers(slot, stack);
        }

        double damageBonus = isDrawn(stack) ? (DRAWN_DAMAGE - 1.0) : (SHEATHED_DAMAGE - 1.0);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(DAMAGE_UUID, "Katana damage", damageBonus, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED,
                new AttributeModifier(SPEED_UUID, "Katana speed", ATTACK_SPEED_MOD, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }

    // -------------------------------------------------------------------------
    // 右クリック（単押し / 長押し）
    // -------------------------------------------------------------------------

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        // カウンター準備中っぽくブロックモーション
        return UseAnim.BLOCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return;
        }

        int usedTicks = getUseDuration(stack) - timeLeft;
        boolean longPress = usedTicks >= LONG_PRESS_TICKS;

        if (isDrawn(stack)) {
            // 抜刀中：長押しで納刀
            if (longPress) {
                setDrawn(stack, false);
                setCounterTicks(stack, 0);
                level.playSound(null, player.blockPosition(), SoundEvents.IRON_DOOR_CLOSE, SoundSource.PLAYERS, 1.0F, 1.2F);
                player.displayClientMessage(Component.literal("納刀 / Sheathed"), true);
            }
        } else {
            // 納刀中
            if (longPress) {
                // スキルロック中は長押し無効
                if (getSkillLock(stack) > 0) {
                    player.displayClientMessage(Component.literal("スキルCT中 / Skill on cooldown"), true);
                    level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.2F);
                    return;
                }
                // カウンター開始
                setCounterTicks(stack, COUNTER_DURATION);
                level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.4F);
                player.displayClientMessage(Component.literal("カウンター構え / Counter ready"), true);
            } else {
                // 単押しで抜刀
                setDrawn(stack, true);
                setCounterTicks(stack, 0);
                level.playSound(null, player.blockPosition(), SoundEvents.IRON_DOOR_OPEN, SoundSource.PLAYERS, 1.0F, 1.4F);
                player.displayClientMessage(Component.literal("抜刀 / Drawn"), true);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 毎tick：カウンター・スキルロック更新
    // -------------------------------------------------------------------------

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (level.isClientSide) {
            super.inventoryTick(stack, level, entity, slot, selected);
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();

        int counter = tag.getInt(TAG_COUNTER);
        if (counter > 0) {
            counter--;
            tag.putInt(TAG_COUNTER, counter);
            // 成功・失敗を問わず時間切れでスキルロック
            if (counter <= 0) {
                tag.putInt(TAG_SKILL_LOCK, SKILL_LOCK_TICKS);
                if (entity instanceof Player player) {
                    player.displayClientMessage(Component.literal("カウンター終了 / Counter ended"), true);
                    level.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BREAK, SoundSource.PLAYERS, 0.7F, 1.0F);
                }
            }
        }

        int lock = tag.getInt(TAG_SKILL_LOCK);
        if (lock > 0) {
            tag.putInt(TAG_SKILL_LOCK, lock - 1);
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    // -------------------------------------------------------------------------
    // 攻撃時の耐久：納刀中は減らさない
    // -------------------------------------------------------------------------

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (isDrawn(stack)) {
            stack.hurtAndBreak(1, attacker, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        // 納刀中は耐久を消費しない
        return true;
    }

    // -------------------------------------------------------------------------
    // ツールチップ
    // -------------------------------------------------------------------------

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if (isDrawn(stack)) {
            tooltip.add(Component.literal("§c抜刀 / Drawn"));
            tooltip.add(Component.literal("攻撃力 12 / 速度 1.5"));
            tooltip.add(Component.literal("長押しで納刀"));
        } else {
            tooltip.add(Component.literal("§7納刀 / Sheathed"));
            tooltip.add(Component.literal("攻撃力 1 / 速度 1.5（耐久消費なし）"));
            tooltip.add(Component.literal("単押し: 抜刀 / 長押し: カウンター"));
            if (isCounterActive(stack)) {
                tooltip.add(Component.literal("§eカウンター残り: " + getCounterTicks(stack) + "t"));
            }
            if (getSkillLock(stack) > 0) {
                tooltip.add(Component.literal("§8スキル冷却: " + (getSkillLock(stack) / 20.0) + "s"));
            }
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }

    // -------------------------------------------------------------------------
    // カウンター成功判定
    // -------------------------------------------------------------------------

    public static class CounterHandler {
        @SubscribeEvent
        public void onLivingAttack(LivingAttackEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (player.level().isClientSide) return;

            ItemStack main = player.getMainHandItem();
            if (!(main.getItem() instanceof SCorpBladeLineageKatana)) return;
            if (!isCounterActive(main)) return;

            // ダメージを無効化
            event.setCanceled(true);

            // 攻撃者にカウンターダメージ
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                attacker.invulnerableTime = 0;
                attacker.hurtTime = 0;
                attacker.hurt(player.level().damageSources().playerAttack(player), COUNTER_DAMAGE);

                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 1.2F, 1.0F);
                player.displayClientMessage(Component.literal("カウンター成功！ / Counter hit!"), true);
            }

            // 成功したら即座にカウンター終了 → スキルロック
            setCounterTicks(main, 0);
            setSkillLock(main, SKILL_LOCK_TICKS);
        }
    }
}
