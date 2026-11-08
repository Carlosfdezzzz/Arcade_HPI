package es.ulpgc.hpi.arcadeservice.architecture.domain;

import es.ulpgc.hpi.arcadeservice.architecture.model.Player;
import java.util.function.LongSupplier;

public final class PlayerFactory {
    private PlayerFactory() {
    }

    public static Player create(String nickname, LongSupplier nextId) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname is required");
        }
        return new Player(nextId.getAsLong(), nickname.trim(), 0);
    }
}
