package org.smoothbuild.cli.base;

import static org.smoothbuild.common.tuple.Tuples.tuple;
import static org.smoothbuild.out.log.Try.success;

import jakarta.inject.Inject;
import java.util.function.Function;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.out.log.Try;
import org.smoothbuild.out.report.Console;

public class PrintResult implements Function<String, Try<Tuple0>> {
  private final Console console;

  @Inject
  public PrintResult(Console console) {
    this.console = console;
  }

  @Override
  public Try<Tuple0> apply(String string) {
    console.println(string);
    return success(tuple());
  }
}
