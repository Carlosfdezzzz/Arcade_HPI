package es.ulpgc.hpi.arcadeservice.infrastructure;

import es.ulpgc.hpi.arcadeservice.architecture.usecases.PlayerUseCase;
import es.ulpgc.hpi.arcadeservice.infrastructure.web.PlayerHttpAdapter;
import io.javalin.Javalin;

public final class App {
    private final Javalin server;

    public App(PlayerUseCase useCase) {
        this.server = PlayerHttpAdapter.create(useCase);
    }

    public void start(int port) {
        server.start(port);
    }
}
