package org.smoothbuild.cli.base;

import static org.smoothbuild.out.log.Maybe.success;

import java.util.function.Function;

import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.report.Console;

import io.vavr.Tuple;
import io.vavr.Tuple0;
import jakarta.inject.Inject;

public class PrintResult implements Function<String, Maybe<Tuple0>> {
  private final Console console;

  @Inject
  public PrintResult(Console console) {
    this.console = console;
  }

  @Override
  public Maybe<Tuple0> apply(String string) {
    console.println(string);
    return success(Tuple.empty());
  }
}
