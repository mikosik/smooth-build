package org.smoothbuild.run.eval.report;

import static com.google.common.base.Strings.padEnd;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.Log.info;
import static org.smoothbuild.common.log.Log.warning;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;
import static org.smoothbuild.run.eval.report.TaskMatchers.NONE;
import static org.smoothbuild.run.eval.report.TaskReporterImpl.NAME_LENGTH_LIMIT;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.DISK;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.EXECUTION;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.MEMORY;
import static org.smoothbuild.vm.evaluate.compute.ResultSource.NOOP;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.backend.BsMapping;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.evaluate.compute.ResultSource;
import org.smoothbuild.vm.evaluate.task.Task;

public class TaskReporterImplTest extends TestContext {
  @Test
  public void combineTaskHeader() throws Exception {
    testHeader(combineTask(), header("(,)"));
  }

  @Test
  public void constIntTaskHeader() throws Exception {
    testHeader(constTask(intB()), header("Int"));
  }

  @Test
  public void constStringTaskHeader() throws Exception {
    testHeader(constTask(stringB()), header("String"));
  }

  @Test
  public void invokeTaskHeaderForFuncWithoutMappedName() throws Exception {
    testHeader(invokeTask(), header("???()"));
  }

  @Test
  public void invokeTaskHeaderForFuncWithMappedName() throws Exception {
    var task = invokeTask();
    var funcHash = task.nativeFunc().hash();
    testHeader(bsMapping(funcHash, "myFunc"), task, header("myFunc()"));
  }

  @Test
  public void orderTaskHeader() throws Exception {
    testHeader(orderTask(), header("[,]"));
  }

  @Test
  public void pickTaskHeader() throws Exception {
    testHeader(pickTask(), header("[]."));
  }

  @Test
  public void selectTaskHeader() throws Exception {
    testHeader(selectTask(), header("{}."));
  }

  @Test
  public void header_with_location() throws Exception {
    var task = selectTask();
    var exprHash = task.exprB().hash();
    testHeader(
        bsMapping(exprHash, location(7)),
        task,
        padEnd("::Evaluating::{}.", NAME_LENGTH_LIMIT + 1, ' ') + "build.smooth:7                 "
            + "exec");
  }

  @Test
  public void header_with_execution_source() throws Exception {
    testHeader(selectTask(), EXECUTION, header("{}.", "exec"));
  }

  @Test
  public void header_with_noop_source() throws Exception {
    var header = padEnd("::Evaluating::{}.", NAME_LENGTH_LIMIT + 1, ' ') + "unknown";
    testHeader(selectTask(), NOOP, header);
  }

  @Test
  public void header_with_disk_source() throws Exception {
    testHeader(selectTask(), DISK, header("{}.", "cache"));
  }

  @Test
  public void header_with_memory_source() throws Exception {
    testHeader(selectTask(), MEMORY, header("{}.", "mem"));
  }

  private static String header(String string) {
    return header(string, "exec");
  }

  private static String header(String string, String result) {
    return padEnd("::Evaluating::" + string, NAME_LENGTH_LIMIT + 1, ' ') + "unknown              "
        + "          " + result;
  }

  private void testHeader(Task task, String header) throws Exception {
    testHeader(bsMapping(), task, header);
  }

  private void testHeader(BsMapping bsMapping, Task task, String header) throws BytecodeException {
    testHeader(bsMapping, task, EXECUTION, header);
  }

  private void testHeader(Task task, ResultSource source, String header) throws BytecodeException {
    testHeader(bsMapping(), task, source, header);
  }

  private void testHeader(BsMapping bsMapping, Task task, ResultSource source, String header)
      throws BytecodeException {
    var reporter = mock(Reporter.class);
    var taskReporter = new TaskReporterImpl(ALL, reporter, bsMapping);
    taskReporter.report(task, computationResult(intB(), source));
    verify(reporter).report(true, header, list());
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
    var messages = arrayB(fatalMessage(), errorMessage(), warningMessage(), infoMessage());
    taskReporter.report(task(), computationResultWithMessages(messages));
    var header =
        """
        ::Evaluating::[,]                           unknown                        exec
          @ ??? ???""";
    var logs = list(
        fatal("fatal message"),
        error("error message"),
        warning("warning message"),
        info("info message"));
    verify(reporter).report(visible, header, logs);
  }
}
