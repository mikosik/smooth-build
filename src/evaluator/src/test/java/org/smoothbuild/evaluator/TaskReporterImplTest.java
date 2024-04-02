package org.smoothbuild.evaluator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.backendcompile.testing.TestingBsMapping.bsMapping;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.base.ResultSource.MEMORY;
import static org.smoothbuild.common.log.base.ResultSource.NOOP;
import static org.smoothbuild.common.log.report.Report.report;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.compilerbackend.BsMapping;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.evaluate.task.Task;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TaskReporterImplTest extends TestingVirtualMachine {
  @Test
  public void combineTaskHeader() throws Exception {
    testHeader(combineTask(), label("combine"));
  }

  @Test
  public void constTaskHeader() throws Exception {
    testHeader(constTask(bInt()), label("const"));
  }

  @Test
  public void invokeTaskHeaderForFuncWithMappedName() throws Exception {
    testHeader(invokeTask(), label("invoke"));
  }

  @Test
  public void orderTaskHeader() throws Exception {
    testHeader(orderTask(), label("order"));
  }

  @Test
  public void pickTaskHeader() throws Exception {
    testHeader(pickTask(), label("pick"));
  }

  @Test
  public void selectTaskHeader() throws Exception {
    testHeader(selectTask(), label("select"));
  }

  @Test
  public void header_with_execution_source() throws Exception {
    testHeader(selectTask(), EXECUTION, label("select"));
  }

  @Test
  public void header_with_noop_source() throws Exception {
    testHeader(selectTask(), NOOP, label("select"));
  }

  @Test
  public void header_with_disk_source() throws Exception {
    testHeader(selectTask(), DISK, label("select"));
  }

  @Test
  public void header_with_memory_source() throws Exception {
    testHeader(selectTask(), MEMORY, label("select"));
  }

  private void testHeader(Task task, Label label) throws Exception {
    testHeader(bsMapping(), task, label);
  }

  private void testHeader(BsMapping bsMapping, Task task, Label label) throws BytecodeException {
    testHeader(bsMapping, task, EXECUTION, label);
  }

  private void testHeader(Task task, ResultSource source, Label label) throws BytecodeException {
    testHeader(bsMapping(), task, source, label);
  }

  private void testHeader(BsMapping bsMapping, Task task, ResultSource source, Label label)
      throws BytecodeException {
    var reporter = mock(Reporter.class);
    var taskReporter = new TaskReporterImpl(reporter, new BsTranslator(bsMapping));
    taskReporter.report(task, computationResult(bInt(), source));
    var prefixedLabel = label("evaluate").append(label);
    verify(reporter).report(report(prefixedLabel, "", source, list()));
  }
}
