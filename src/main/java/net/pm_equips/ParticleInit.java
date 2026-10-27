package net.pm_equips;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ParticleInit {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, PMEquipsMain.MOD_ID);
    public static final RegistryObject<SimpleParticleType> ARBITER_TRAIL =
            PARTICLE_TYPES.register("arbiter_trail", () -> new SimpleParticleType(false));

    private ParticleInit() {}
}
