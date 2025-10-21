package net.blay09.mods.replikaentropie.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.blay09.mods.replikaentropie.ReplikaEntropie;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class ModKeyMappings {

    public static ManagedKeyMapping toggleGoggles;

    public static void initialize() {
        toggleGoggles = Kuma.createKeyMapping(id("toggle_goggles"))
                .withDefault(InputBinding.key(InputConstants.KEY_B))
                .handleWorldInput(event -> {
                    // POSTJAM send toggle to server
                    return true;
                })
                .build();
    }
}
