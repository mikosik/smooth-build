package org.smoothbuild.common.schedule;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;

import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;

public class ConstantTask<R> implements Task0<R> {
  private final R constant;
  private final Label label;

  public ConstantTask(R constant, Label label) {
    this.constant = constant;
    this.label = label;
  }

  @Override
  public Output<R> execute() {
    return output(constant, report(label, new Trace(), EXECUTION, list()));
  }
}
