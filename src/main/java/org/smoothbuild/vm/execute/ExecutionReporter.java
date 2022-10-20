package org.smoothbuild.vm.execute;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.run.eval.MessageStruct.level;
import static org.smoothbuild.run.eval.MessageStruct.text;
import static org.smoothbuild.util.Strings.limitedWithEllipsis;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.compute.ResultSource.EXECUTION;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.vm.compute.ComputationResult;
import org.smoothbuild.vm.compute.ResultSource;
import org.smoothbuild.vm.task.Task;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  public static final int NAME_LENGTH_LIMIT = 43;
  private final TaskReporter taskReporter;

  @Inject
  public ExecutionReporter(TaskReporter taskReporter) {
    this.taskReporter = taskReporter;
  }

  public void report(Task task, ComputationResult result) {
    var source = result.source();
    if (result.hasOutput()) {
      print(task, source, result.output().messages());
    } else {
      Log error = error("Execution failed with:\n" + getStackTraceAsString(result.exception()));
      print(task, list(error), source);
    }
  }

  public void reportComputerException(Task task, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    print(task, list(fatal), EXECUTION);
  }

  private void print(Task task, ResultSource source, ArrayB messages) {
    var logs = map(messages.elems(TupleB.class), m -> new Log(level(m), text(m)));
    print(task, logs, source);
  }

  public void print(Task task, List<Log> logs, ResultSource source) {
    taskReporter.report(task, header(task, source.toString()), logs);
  }

  // Visible for testing
  static String header(Task task, String resultSource) {
    var tag = task.tag();
    var loc = task.loc().toString();
    var trimmedTag = limitedWithEllipsis(tag, NAME_LENGTH_LIMIT);
    var nameColumn = padEnd(trimmedTag, NAME_LENGTH_LIMIT + 1, ' ');
    var locColumn = resultSource.isEmpty()
        ? loc
        : padEnd(loc, 30, ' ') + " ";
    return nameColumn + locColumn + resultSource;
  }
}
