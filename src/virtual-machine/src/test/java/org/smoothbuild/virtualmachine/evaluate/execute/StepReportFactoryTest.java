package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.common.log.report.Report.report;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.evaluate.step.Step;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class StepReportFactoryTest extends TestingVirtualMachine {
  @Test
  void combine_task_report() throws Exception {
    testReport(combineTask(), label("combine"));
  }

  @Test
  void const_task_report() throws Exception {
    testReport(constTask(bInt()), label("const"));
  }

  @Test
  void invoke_task_report() throws Exception {
    testReport(invokeTask(), label("invoke"));
  }

  @Test
  void order_task_report() throws Exception {
    testReport(orderTask(), label("order"));
  }

  @Test
  void pick_task_report() throws Exception {
    testReport(pickTask(), label("pick"));
  }

  @Test
  void select_task_report() throws Exception {
    testReport(selectTask(), label("select"));
  }

  @Test
  void header_with_execution_source() throws Exception {
    testReport(selectTask(), EXECUTION, label("select"));
  }

  @Test
  void header_with_disk_source() throws Exception {
    testReport(selectTask(), DISK, label("select"));
  }

  @Test
  void header_with_memory_source() throws Exception {
    testReport(selectTask(), MEMORY, label("select"));
  }

  private void testReport(Step step, Label label) throws BytecodeException {
    testReport(step, EXECUTION, label);
  }

  private void testReport(Step step, ResultSource source, Label label) throws BytecodeException {
    var computationResult = computationResult(bInt(), source);
    var expected = report(label("evaluate").append(label), bTrace(), source, list());
    assertThat(StepReportFactory.create(step, computationResult)).isEqualTo(expected);
  }
}
