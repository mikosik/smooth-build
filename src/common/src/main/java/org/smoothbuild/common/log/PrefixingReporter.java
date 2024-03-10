package org.smoothbuild.common.log;

import org.smoothbuild.common.collect.List;

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
