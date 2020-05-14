package org.smoothbuild.cli.base;

import static com.google.inject.Guice.createInjector;
import static com.google.inject.Stage.PRODUCTION;

import java.util.function.Function;

import org.smoothbuild.MainModule;

import com.google.inject.Injector;

public class CommandHelper {
  public static Integer runCommand(Function<Injector, Integer> invoker) {
    return runCommand(new ReportModule(), invoker);
  }

  public static Integer runCommand(ReportModule reportModule, Function<Injector, Integer> invoker) {
    Injector injector = createInjector(PRODUCTION, new MainModule(), reportModule);
    return invoker.apply(injector);
  }
}
