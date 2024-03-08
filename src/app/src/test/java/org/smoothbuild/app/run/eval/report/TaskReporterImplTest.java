package org.smoothbuild.app.run.eval.report;

import static com.google.common.base.Strings.padEnd;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.app.run.eval.report.TaskMatchers.ALL;
import static org.smoothbuild.app.run.eval.report.TaskMatchers.NONE;
import static org.smoothbuild.app.run.eval.report.TaskReporterImpl.NAME_LENGTH_LIMIT;
import static org.smoothbuild.backendcompile.testing.TestingBsMapping.bsMapping;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.Log.info;
import static org.smoothbuild.common.log.Log.warning;
import static org.smoothbuild.common.log.ResultSource.DISK;
import static org.smoothbuild.common.log.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.ResultSource.MEMORY;
import static org.smoothbuild.common.log.ResultSource.NOOP;

import org.junit.jupiter.api.Test;
import org.smoothbuild.app.report.Reporter;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.ResultSource;
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
  public void constIntTaskHeader() throws Exception {
    testHeader(constTask(intB()), label("const", "Int"));
  }

  @Test
  public void constStringTaskHeader() throws Exception {
    testHeader(constTask(stringB()), label("const", "String"));
  }

  @Test
  public void invokeTaskHeaderForFuncWithoutMappedName() throws Exception {
    testHeader(invokeTask(), label("call", "???"));
  }

  @Test
  public void invokeTaskHeaderForFuncWithMappedName() throws Exception {
    var task = invokeTask();
    var funcHash = task.nativeFunc().hash();
    testHeader(bsMapping(funcHash, "myFunc"), task, label("call", "myFunc"));
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

  private static String header(String string) {
    return header(string, "exec");
  }

  private static String header(String string, String result) {
    return padEnd("::Evaluating::" + string, NAME_LENGTH_LIMIT + 1, ' ') + "unknown              "
        + "          " + result;
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
    var taskReporter = new TaskReporterImpl(ALL, reporter, bsMapping);
    taskReporter.report(task, computationResult(intB(), source));
    verify(reporter).report(true, label("Evaluating").append(label), "", source, list());
  }

  @Test
  public void when_filter_matches() throws Exception {
    testVisibility(ALL, true);
  }

  @Test
  public void when_filter_doesnt_match() throws Exception {
    testVisibility(NONE, false);
  }

  private void testVisibility(TaskMatcher taskMatcher, boolean visible) throws Exception {
    var reporter = mock(Reporter.class);
    var taskReporter = new TaskReporterImpl(taskMatcher, reporter, bsMapping());
    var messages = arrayB(fatalLog(), errorLog(), warningLog(), infoLog());
    taskReporter.report(orderTask(), computationResultWithMessages(messages));
    var logs = list(
        fatal("fatal message"),
        error("error message"),
        warning("warning message"),
        info("info message"));
    verify(reporter).report(visible, label("Evaluating", "order"), "", EXECUTION, logs);
  }
}
