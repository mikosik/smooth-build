package org.smoothbuild.virtualmachine.evaluate.execute;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.common.log.base.ResultSource.NOOP;
import static org.smoothbuild.virtualmachine.evaluate.execute.TaskReport.taskReport;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.evaluate.task.Task;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TaskReportTest extends TestingVirtualMachine {
  @Test
  public void combine_task_report() throws Exception {
    testReport(combineTask(), label("combine"));
  }

  @Test
  public void const_task_report() throws Exception {
    testReport(constTask(bInt()), label("const"));
  }

  @Test
  public void invoke_task_report() throws Exception {
    testReport(invokeTask(), label("invoke"));
  }

  @Test
  public void order_task_report() throws Exception {
    testReport(orderTask(), label("order"));
  }

  @Test
  public void pick_task_report() throws Exception {
    testReport(pickTask(), label("pick"));
  }

  @Test
  public void select_task_report() throws Exception {
    testReport(selectTask(), label("select"));
  }

  @Test
  public void header_with_execution_source() throws Exception {
    testReport(selectTask(), EXECUTION, label("select"));
  }

  @Test
  public void header_with_noop_source() throws Exception {
    testReport(selectTask(), NOOP, label("select"));
  }

  @Test
  public void header_with_disk_source() throws Exception {
    testReport(selectTask(), DISK, label("select"));
  }

  @Test
  public void header_with_memory_source() throws Exception {
    testReport(selectTask(), MEMORY, label("select"));
  }

  private void testReport(Task task, Label label) throws BytecodeException {
    testReport(task, EXECUTION, label);
  }

  private void testReport(Task task, ResultSource source, Label label) throws BytecodeException {
    var computationResult = computationResult(bInt(), source);
    var expected = new TaskReport(label("evaluate").append(label), bTrace(), source, list());
    assertThat(taskReport(task, computationResult)).isEqualTo(expected);
  }
}
