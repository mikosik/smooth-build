package org.smoothbuild.task.exec;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Task;

public class TaskExecutor {
  private final Provider<PluginApiImpl> pluginApiProvider;
  private final TaskReporter taskReporter;

  @Inject
  public TaskExecutor(Provider<PluginApiImpl> pluginApiProvider, TaskReporter taskReporter) {
    this.pluginApiProvider = pluginApiProvider;
    this.taskReporter = taskReporter;
  }

  public SValue execute(Task task) {
    PluginApiImpl pluginApi = pluginApiProvider.get();
    SValue result = task.execute(pluginApi);
    taskReporter.report(task, pluginApi);

    if (pluginApi.messages().containsProblems()) {
      throw new BuildInterruptedException();
    }

    return result;
  }
}
