package es.ulpgc.hpi.arcadeservice;

import es.ulpgc.hpi.arcadeservice.architecture.usecases.PlayerUseCaseImpl;
import es.ulpgc.hpi.arcadeservice.infrastructure.App;
import es.ulpgc.hpi.arcadeservice.architecture.ports.PlayerRepositoryImpl;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
        var repository = new PlayerRepositoryImpl();
        var useCase = new PlayerUseCaseImpl(repository);
        new App(useCase).start(port);
    }
}
