package net.pm_equips.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class LCorpEGOL5Twilight extends GeoArmorItem {
    public LCorpEGOL5Twilight(Properties props) {
        super(props);
    }

    public static void tick(Player player) {
        Level level = player.level();
        if (!level.isClientSide) {
            boolean hasTwilightWeapon = player.getMainHandItem().getItem() instanceof LCorpEGOR5Twilight;

            if (hasTwilightWeapon) {
                float missingHp = player.getMaxHealth() - player.getHealth();
                if (missingHp > 0) {
                    int amplifier = (int) Math.floor(missingHp / 4); // 4HP減少ごとに+1段階
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, amplifier, true, false, true));
                }
                if (player.tickCount % 100 == 0) { // 100tick = 5秒
                    List<LivingEntity> nearby = level.getEntitiesOfClass(
                            LivingEntity.class,
                            player.getBoundingBox().inflate(5.0D),
                            e -> e != player
                    );
                    for (LivingEntity target : nearby) {
                        target.invulnerableTime = 0;
                        target.hurt(level.damageSources().magic(), 6.0F);
                    }
                }
            }
        }
    }

}

