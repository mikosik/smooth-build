package org.smoothbuild.task.exec;

import static org.smoothbuild.lang.message.Messages.isEmpty;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.TaskResult;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

public class TaskReporter {
  private final Console console;

  @Inject
  public TaskReporter(Console console) {
    this.console = console;
  }

  public void report(Task task) {
    TaskResult result = task.result();
    if (result.hasOutput()) {
      Array messages = task.output().messages();
      if (!isEmpty(messages)) {
        console.print(header(task), messages);
      }
    } else {
      console.print(header(task), task.result().failure());
    }
  }

  @VisibleForTesting
  static String header(Task task) {
    String locationString = task.location().toString();
    int paddedLength = Console.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    String cached = task.result().isFromCache() ? " CACHED" : "";
    return name + locationString + cached;
  }
}
