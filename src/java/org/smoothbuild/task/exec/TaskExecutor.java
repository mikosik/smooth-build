package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

public class TaskExecutor {
  private final PluginApiFactory pluginApiFactory;
  private final UserConsole userConsole;

  @Inject
  public TaskExecutor(PluginApiFactory pluginApiFactory, UserConsole userConsole) {
    this.pluginApiFactory = pluginApiFactory;
    this.userConsole = userConsole;
  }

  public SValue execute(Task task) {
    PluginApiImpl pluginApi = pluginApiFactory.createPluginApi(task);
    SValue result = task.execute(pluginApi);

    MessageGroup messageGroup = pluginApi.messageGroup();
    if (!task.isInternal() || messageGroup.containsMessages()) {
      userConsole.report(messageGroup);
    }
    if (messageGroup.containsProblems()) {
      throw new BuildInterruptedException();
    }
    return result;
  }
}
