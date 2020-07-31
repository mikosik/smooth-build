package org.smoothbuild.exec.parallel;

import static com.google.common.base.Strings.padEnd;
import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.exec.base.MessageStruct.level;
import static org.smoothbuild.exec.base.MessageStruct.text;
import static org.smoothbuild.exec.compute.ResultSource.EXECUTION;
import static org.smoothbuild.exec.compute.Task.NAME_LENGTH_LIMIT;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.exec.base.MaybeOutput;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.ResultSource;
import org.smoothbuild.exec.compute.Task;

import com.google.common.collect.Streams;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  private final Reporter reporter;

  @Inject
  public ExecutionReporter(Reporter reporter) {
    this.reporter = reporter;
  }

  public void report(Task task, Computed computed) {
    MaybeOutput maybeOutput = computed.computed();
    ResultSource resultSource = computed.resultSource();
    if (maybeOutput.hasOutput()) {
      print(task, resultSource, maybeOutput.output().messages());
    } else {
      Log error = error(
          "Execution failed with:\n" + getStackTraceAsString(maybeOutput.exception()));
      print(task, resultSource, List.of(error));
    }
  }

  public void reportComputerException(Task task, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    reporter.report(task, header(task, EXECUTION), List.of(fatal));
  }

  private void print(Task task, ResultSource resultSource, Array messages) {
    List<Log> logs = Streams.stream(messages.asIterable(Tuple.class))
        .map(m -> new Log(level(m), text(m)))
        .collect(toList());
    print(task, resultSource, logs);
  }

  public void print(Task task, ResultSource resultSource, List<Log> logs) {
    reporter.report(task, header(task, resultSource), logs);
  }

  private static String header(Task task, ResultSource resultSource) {
    String name = padEnd(task.name(), NAME_LENGTH_LIMIT + 1, ' ');
    String location = padEnd(task.location().toString(), 30, ' ');
    String source = resultSource.toString();
    return name + location + (source.isEmpty() ? "" : " ") + source;
  }
}
