package net.pm_equips.entity;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

final class ForgeTestPlayers {
    private ForgeTestPlayers() {}
    static Player makeMockPlayer(GameTestHelper helper,GameType mode){
        return new Player(helper.getLevel(),BlockPos.ZERO,0,new GameProfile(UUID.randomUUID(),"test-mock-player")){
            @Override public boolean isSpectator(){return mode==GameType.SPECTATOR;}
            @Override public boolean isCreative(){return mode==GameType.CREATIVE;}
        };
    }
}
