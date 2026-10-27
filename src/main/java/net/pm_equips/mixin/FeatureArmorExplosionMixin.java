package net.pm_equips.mixin;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.items.HeadArbiterArmor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class FeatureArmorExplosionMixin {
    @Shadow @Final private Level level;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow @Final private float radius;
    @Shadow public abstract Map<Player,Vec3> getHitPlayers();
    @Unique private final Map<LivingEntity,Vec3> pmEquips$protectedMotion=new HashMap<>();
    @Inject(method="explode",at=@At("HEAD"))
    private void pmEquips$rememberMotion(CallbackInfo ci){
        for(LivingEntity wearer:level.getEntitiesOfClass(LivingEntity.class,new AABB(x,y,z,x,y,z).inflate(radius*2+1),
                p->p.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof HeadArbiterArmor))
            pmEquips$protectedMotion.put(wearer,wearer.getDeltaMovement());
    }
    @Inject(method="explode",at=@At("TAIL"))
    private void pmEquips$cancelKnockback(CallbackInfo ci){
        pmEquips$protectedMotion.forEach((wearer,motion)->{
            wearer.setDeltaMovement(motion);
            if (wearer instanceof Player player) getHitPlayers().remove(player);
        });
        pmEquips$protectedMotion.clear();
    }
}
