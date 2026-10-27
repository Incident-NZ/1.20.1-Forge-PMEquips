package net.pm_equips.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class ArbiterParticle extends TextureSheetParticle {
    public ArbiterParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        xd = 0; yd = 0; zd = 0;
        gravity = 0;
        hasPhysics = false;
        lifetime = 12;
        quadSize = .18F;
        pickSprite(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        alpha = 1 - (float) age / lifetime;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
