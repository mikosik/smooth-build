package org.smoothbuild.exec.task.parallel;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.object.base.Messages.isEmpty;
import static org.smoothbuild.lang.object.base.Messages.level;
import static org.smoothbuild.lang.object.base.Messages.text;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.task.base.MaybeComputed;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Struct;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.Streams;

/**
 * This class is thread-safe.
 */
public class ExecutionReporter {
  private final Console console;

  @Inject
  public ExecutionReporter(Console console) {
    this.console = console;
  }

  public void report(Task task, MaybeComputed maybeComputed, boolean fromCache) {
    if (maybeComputed.hasComputed()) {
      MaybeOutput maybeOutput = maybeComputed.computed();
      if (maybeOutput.hasOutput()) {
        Array messages = maybeOutput.output().messages();
        if (!isEmpty(messages)) {
          print(task, fromCache, messages);
        }
      } else {
        console.show(header(task, fromCache), maybeOutput.exception());
      }
    } else {
      report(maybeComputed.throwable());
    }
  }

  private void print(Task task, boolean fromCache, Array messages) {
    List<Log> logs = Streams.stream(messages.asIterable(Struct.class))
        .map(m -> new Log(level(m), text(m)))
        .collect(toList());
    console.show(header(task, fromCache), logs);
  }

  public void report(Throwable throwable) {
    console.show("Execution failed with:\n", throwable);
  }

  @VisibleForTesting
  static String header(Task task, boolean fromCache) {
    String locationString = task.location().toString();
    int paddedLength = Console.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    return name + locationString + (fromCache ? " CACHED" : "");
  }
}
