package net.pm_equips.client.renderer;

import net.pm_equips.client.models.LamentYisangM;
import net.pm_equips.items.LimbusEGOYisangLament;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LamentYisangR extends GeoItemRenderer<LimbusEGOYisangLament> {
    public LamentYisangR() {
        super(new LamentYisangM());
    }
}
