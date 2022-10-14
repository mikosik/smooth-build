package org.smoothbuild.vm.execute;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.run.eval.MessageStruct.level;
import static org.smoothbuild.run.eval.MessageStruct.text;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.compute.ResSource.EXECUTION;
import static org.smoothbuild.vm.execute.TaskInfo.NAME_LENGTH_LIMIT;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.vm.compute.CompRes;
import org.smoothbuild.vm.compute.ResSource;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  private final TaskReporter taskReporter;

  @Inject
  public ExecutionReporter(TaskReporter taskReporter) {
    this.taskReporter = taskReporter;
  }

  public void report(TaskInfo taskInfo, CompRes compRes) {
    ResSource resSource = compRes.resSource();
    if (compRes.hasOutput()) {
      print(taskInfo, resSource, compRes.output().messages());
    } else {
      Log error = error("Execution failed with:\n" + getStackTraceAsString(compRes.exception()));
      print(taskInfo, list(error), resSource);
    }
  }

  public void reportComputerException(TaskInfo taskInfo, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    print(taskInfo, list(fatal), EXECUTION.toString());
  }

  private void print(TaskInfo taskInfo, ResSource resSource, ArrayB messages) {
    var logs = map(messages.elems(TupleB.class), m -> new Log(level(m), text(m)));
    print(taskInfo, logs, resSource);
  }

  public void print(TaskInfo taskInfo, List<Log> logs) {
    print(taskInfo, logs, "");
  }

  public void print(TaskInfo taskInfo, List<Log> logs, ResSource resSource) {
    print(taskInfo, logs, resSource.toString());
  }

  private void print(TaskInfo taskInfo, List<Log> logs, String resultSource) {
    taskReporter.report(taskInfo, header(taskInfo, resultSource), logs);
  }

  // Visible for testing
  static String header(TaskInfo taskInfo, String resultSource) {
    String tag = taskInfo.tag();
    String loc = taskInfo.loc().toString();

    String nameColumn = padEnd(tag, NAME_LENGTH_LIMIT + 1, ' ');
    String locColumn = resultSource.isEmpty()
        ? loc
        : padEnd(loc, 30, ' ') + " ";
    return nameColumn + locColumn + resultSource;
  }
}
