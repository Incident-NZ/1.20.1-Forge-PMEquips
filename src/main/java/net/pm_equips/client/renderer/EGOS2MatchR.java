package net.pm_equips.client.renderer;

import net.pm_equips.client.models.EGOS2MatchM;
import net.pm_equips.items.LCorpEGOL2Match;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class EGOS2MatchR extends GeoArmorRenderer<LCorpEGOL2Match> {
    public EGOS2MatchR() {
        super(new EGOS2MatchM());
    }
}
