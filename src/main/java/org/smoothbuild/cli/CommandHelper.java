package org.smoothbuild.cli;

import static com.google.inject.Guice.createInjector;

import java.util.function.Function;

import org.smoothbuild.MainModule;

import com.google.inject.Injector;

public class CommandHelper {
  public static Integer runCommand(Function<Injector, Integer> invoker) {
    Injector injector = createInjector(new MainModule());
    return invoker.apply(injector);
  }
}
