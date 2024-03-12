package org.smoothbuild.app.cli.base;

import static org.smoothbuild.common.log.base.Try.success;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;

public class ReportResult implements TryFunction<String, Tuple0> {
  private final PrintWriter printWriter;

  @Inject
  public ReportResult(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  @Override
  public Try<Tuple0> apply(String string) {
    printWriter.println(string);
    return success(tuple());
  }
}
