package es.ulpgc.hpi.arcadeservice.architecture.usecases;

import es.ulpgc.hpi.arcadeservice.architecture.domain.PlayerFactory;
import es.ulpgc.hpi.arcadeservice.architecture.ports.PlayerRepository;
import es.ulpgc.hpi.arcadeservice.architecture.model.Player;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Comparator;

public final class PlayerUseCaseImpl implements PlayerUseCase {
    private final PlayerRepository repository;
    private final AtomicLong nextId = new AtomicLong(1);

    public PlayerUseCaseImpl(PlayerRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public List<Player> list() {
        return repository.findAll();
    }

    @Override
    public Optional<Player> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public Player create(String nickname) {
        var player = PlayerFactory.create(nickname, nextId::getAndIncrement);

        boolean duplicate = repository.findAll().stream()
                .anyMatch(existing ->
                        existing.nickname().equals(player.nickname()));

        if (duplicate) {
            throw new IllegalArgumentException("Nickname already exists");
        }

        repository.save(player);
        return player;
    }

    @Override
    public Optional<Player> addScore(long id, int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points must be positive");
        }

        return repository.findById(id).map(player -> {
            long total = (long) player.score() + points;

            if (total > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Score too large");
            }

            var updated = new Player(
                    player.id(),
                    player.nickname(),
                    (int) total
            );

            repository.save(updated);
            return updated;
        });
    }

    @Override
    public List<Player> leaderboard(Integer limit) {
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive");
        }

        var ordered = repository.findAll().stream()
                .sorted(Comparator.comparingInt(Player::score).reversed()
                        .thenComparing(Player::nickname));

        return limit == null
                ? ordered.toList()
                : ordered.limit(limit).toList();
    }

}
