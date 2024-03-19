package org.smoothbuild.app.cli.base;

import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.io.PrintWriter;
import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Try;

public class ReportResult implements TryFunction1<String, Void> {
  private final PrintWriter printWriter;

  @Inject
  public ReportResult(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  @Override
  public Try<Void> apply(String string) {
    printWriter.println(string);
    return success(null);
  }
}
