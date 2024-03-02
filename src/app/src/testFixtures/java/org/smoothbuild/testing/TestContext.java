package org.smoothbuild.testing;

import static org.smoothbuild.backendcompile.testing.TestingBsMapping.bsMapping;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;

import java.io.PrintWriter;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.out.report.SystemOutReporter;
import org.smoothbuild.run.eval.report.TaskReporterImpl;
import org.smoothbuild.virtualmachine.testing.TestVirtualMachine;

public class TestContext extends TestVirtualMachine {

  @Override
  public TaskReporterImpl taskReporter() {
    return taskReporter(reporter());
  }

  public TaskReporterImpl taskReporter(Reporter reporter) {
    return new TaskReporterImpl(ALL, reporter, bsMapping());
  }

  public SystemOutReporter reporter() {
    return new SystemOutReporter(new PrintWriter(systemOut(), true), Level.INFO);
  }
}
