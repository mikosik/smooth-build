package org.smoothbuild.app.testing;

import static org.smoothbuild.backendcompile.testing.TestingBsMapping.bsMapping;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;

import java.io.PrintWriter;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.out.report.SystemOutReporter;
import org.smoothbuild.run.eval.report.TaskReporterImpl;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TestingTaskReporter extends TestingVirtualMachine {
  @Override
  public TaskReporterImpl taskReporter() {
    return taskReporter(reporter());
  }

  public TaskReporterImpl taskReporter(Reporter reporter) {
    return new TaskReporterImpl(ALL, reporter, bsMapping());
  }

  public SystemOutReporter reporter() {
    return new SystemOutReporter(new PrintWriter(inMemorySystemOut(), true), Level.INFO);
  }
}
