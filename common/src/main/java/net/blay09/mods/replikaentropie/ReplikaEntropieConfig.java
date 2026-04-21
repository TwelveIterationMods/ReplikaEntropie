package net.blay09.mods.replikaentropie;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;

@Config(ReplikaEntropie.MOD_ID)
public class ReplikaEntropieConfig {

    @Comment("This is an example int property")
    public int exampleInt = 1234;

    public static ReplikaEntropieConfig getActive() {
        return Balm.config().getActiveConfig(ReplikaEntropieConfig.class);
    }
}
