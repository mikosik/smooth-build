package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;

public class TaskReporter {
  private final UserConsole userConsole;

  @Inject
  public TaskReporter(UserConsole userConsole) {
    this.userConsole = userConsole;
  }

  public void report(Task task, PluginApiImpl pluginApi) {
    MessageGroup messages = pluginApi.messages();
    if (!(task.isInternal() && messages.isEmpty())) {
      String header = header(task, pluginApi.isResultFromCache());
      userConsole.report(header, messages);
    }
  }

  @VisibleForTesting
  static String header(Task task, boolean isResultFromCached) {
    String locationString = task.codeLocation().toString();
    int paddedLength = UserConsole.MESSAGE_GROUP_NAME_HEADER_LENGTH - locationString.length();
    String name = Strings.padEnd(task.name(), paddedLength, ' ');
    String cached = isResultFromCached ? " CACHED" : "";
    return name + locationString + cached;
  }
}
