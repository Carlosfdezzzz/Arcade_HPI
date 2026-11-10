package es.ulpgc.hpi.arcadeservice.architecture;

import es.ulpgc.hpi.arcadeservice.architecture.ports.PlayerRepositoryImpl;
import es.ulpgc.hpi.arcadeservice.architecture.usecases.PlayerUseCaseImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardUseCaseTest {
    @Test
    void accumulatesAndSavesPoints() {
        var useCase = new PlayerUseCaseImpl(new PlayerRepositoryImpl());
        var player = useCase.create("Ada");

        useCase.addScore(player.id(), 10);
        var updated = useCase.addScore(player.id(), 5).orElseThrow();

        assertEquals(15, updated.score());
        assertEquals(updated, useCase.findById(player.id()).orElseThrow());
    }

    @Test
    void rejectsZeroAndNegativePoints() {
        var useCase = new PlayerUseCaseImpl(new PlayerRepositoryImpl());
        var player = useCase.create("Ada");

        assertThrows(IllegalArgumentException.class,
                () -> useCase.addScore(player.id(), 0));

        assertThrows(IllegalArgumentException.class,
                () -> useCase.addScore(player.id(), -5));

        assertEquals(0,
                useCase.findById(player.id()).orElseThrow().score());
    }

    @Test
    void returnsEmptyForUnknownPlayer() {
        var useCase = new PlayerUseCaseImpl(new PlayerRepositoryImpl());

        assertTrue(useCase.addScore(999, 10).isEmpty());
    }

    @Test
    void ordersByScoreThenNicknameAndAppliesLimit() {
        var useCase = new PlayerUseCaseImpl(new PlayerRepositoryImpl());

        var bob = useCase.create("Bob");
        var zoe = useCase.create("Zoe");
        var ada = useCase.create("Ada");

        useCase.addScore(bob.id(), 20);
        useCase.addScore(zoe.id(), 10);
        useCase.addScore(ada.id(), 20);

        assertEquals(List.of("Ada", "Bob", "Zoe"),
                useCase.leaderboard(null).stream()
                        .map(p -> p.nickname()).toList());

        assertEquals(List.of("Ada", "Bob"),
                useCase.leaderboard(2).stream()
                        .map(p -> p.nickname()).toList());

        assertEquals(3, useCase.leaderboard(10).size());
    }

    @Test
    void rejectsInvalidLimits() {
        var useCase = new PlayerUseCaseImpl(new PlayerRepositoryImpl());

        assertThrows(IllegalArgumentException.class,
                () -> useCase.leaderboard(0));

        assertThrows(IllegalArgumentException.class,
                () -> useCase.leaderboard(-1));
    }

    @Test
    void returnsEmptyLeaderboardWithoutPlayers() {
        var useCase = new PlayerUseCaseImpl(new PlayerRepositoryImpl());

        assertTrue(useCase.leaderboard(null).isEmpty());
    }
}