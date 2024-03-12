package org.smoothbuild.common.log.report;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.ResultSource;

public class PrefixingReporter implements Reporter {
  private final Reporter reporter;
  private final Label prefix;

  public PrefixingReporter(Reporter reporter, Label prefix) {
    this.reporter = reporter;
    this.prefix = prefix;
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    reporter.report(prefix.append(label), details, source, logs);
  }
}
