package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.task.base.Task;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class TaskReporter {
  private final Console console;

  @Inject
  public TaskReporter(Console console) {
    this.console = console;
  }

  public void report(Task task, boolean resultFromCache) {
    if (task.hasOutput()) {
      ImmutableList<Message> messages = task.output().messages();
      if (!messages.isEmpty()) {
        console.print(header(task, resultFromCache), messages);
      }
    } else if (task.failure() != null){
      console.print(header(task, resultFromCache), task.failure());
    }
  }

  @VisibleForTesting
  static String header(Task task, boolean isResultFromCached) {
    String locationString = task.location().toString();
    int paddedLength = Console.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    String cached = isResultFromCached ? " CACHED" : "";
    return name + locationString + cached;
  }
}
