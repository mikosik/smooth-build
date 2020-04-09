package org.smoothbuild.cli;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_ENV_VARIABLE;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

import org.smoothbuild.MainModule;
import org.smoothbuild.SmoothPaths;

import com.google.inject.Injector;

public class CommandHelper {
  public static Optional<SmoothPaths> smoothPaths() {
    String homeDir = System.getenv(SMOOTH_HOME_ENV_VARIABLE);
    if (homeDir == null) {
      return Optional.empty();
    }
    return Optional.of(new SmoothPaths(Paths.get(homeDir)));
  }

  public static Integer runCommand(Function<Injector, Integer> invoker) {
    Optional<SmoothPaths> smoothPaths = smoothPaths();
    if (smoothPaths.isPresent()) {
      Injector injector = createInjector(new MainModule(smoothPaths.get()));
      return invoker.apply(injector);
    } else {
      System.out.println(
          "smooth: Environment variable '" + SMOOTH_HOME_ENV_VARIABLE + "' not set.");
      return EXIT_CODE_ERROR;
    }
  }
}
