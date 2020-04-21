package org.smoothbuild.exec.task.parallel;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.lang.object.base.Messages.isEmpty;
import static org.smoothbuild.lang.object.base.Messages.level;
import static org.smoothbuild.lang.object.base.Messages.text;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.Computed;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Struct;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
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

  public void report(BuildTask task, Computed computed) {
    MaybeOutput maybeOutput = computed.computed();
    boolean fromCache = computed.isFromCache();
    if (maybeOutput.hasOutput()) {
      Array messages = maybeOutput.output().messages();
      if (!isEmpty(messages)) {
        print(task, fromCache, messages);
      }
    } else {
      Log error = error(
          "Execution failed with:\n" + getStackTraceAsString(maybeOutput.exception()));
      reporter.report(header(task, fromCache), List.of(error));
    }
  }

  public void reportComputerException(BuildTask task, Throwable throwable) {
    Log fatal = fatal(
        "Internal smooth error, computation failed with:" + getStackTraceAsString(throwable));
    reporter.report(header(task, ""), List.of(fatal));
  }

  private void print(BuildTask task, boolean fromCache, Array messages) {
    List<Log> logs = Streams.stream(messages.asIterable(Struct.class))
        .map(m -> new Log(level(m), text(m)))
        .collect(toList());
    reporter.report(header(task, fromCache), logs);
  }

  @VisibleForTesting
  static String header(BuildTask task, boolean fromCache) {
    return header(task, fromCache ? " CACHED" : "");
  }

  private static String header(BuildTask task, String cacheStatus) {
    String locationString = task.location().toString();
    int paddedLength = Console.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    return name + locationString + cacheStatus;
  }
}
