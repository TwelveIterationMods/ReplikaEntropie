package net.blay09.mods.replikaentropie.menu;

import net.blay09.mods.replikaentropie.power.MakeshiftPsu;

public interface MakeshiftPoweredMenu {
    MakeshiftPsu getMakeshiftPsu();

    boolean isMakeshiftPsuOverheated();
}
