package net.blay09.mods.replikaentropie.api;

import java.lang.reflect.InvocationTargetException;

public class ReplikaEntropieAPI {
    public static final String MOD_ID = "replikaentropie";

    private static final InternalMethods __internalMethods;

    static {
        try {
            __internalMethods = (InternalMethods) Class.forName("net.blay09.mods.replikaentropie.InternalMethodsImpl").getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
