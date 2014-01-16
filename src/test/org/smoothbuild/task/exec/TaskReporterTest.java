package org.smoothbuild.task.exec;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.smoothbuild.task.exec.TaskReporter.header;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

public class TaskReporterTest {
  LoggedMessages messages = new LoggedMessages();
  UserConsole userConsole = mock(UserConsole.class);
  PluginApiImpl pluginApi = mock(PluginApiImpl.class);

  TaskReporter taskReporter = new TaskReporter(userConsole);

  @Before
  public void before() {
    Mockito.when(pluginApi.loggedMessages()).thenReturn(messages);
  }

  @Test
  public void messages_of_internal_task_are_printed() {
    Task task = createTask(true);
    messages.log(new Message(WARNING, "message"));

    taskReporter.report(task, pluginApi);

    verify(userConsole).report(header(task, false), messages);
  }

  @Test
  public void internal_task_without_messages_are_not_printed() {
    Task task = createTask(true);
    taskReporter.report(task, pluginApi);
    verifyZeroInteractions(userConsole);
  }

  @Test
  public void messages_of_non_internal_tasks_are_printed() {
    Task task = createTask(false);
    messages.log(new Message(WARNING, "message"));

    taskReporter.report(task, pluginApi);

    verify(userConsole).report(header(task, false), messages);
  }

  @Test
  public void non_internal_task_without_messages_is_printed() {
    Task task = createTask(false);
    taskReporter.report(task, pluginApi);
    verify(userConsole).report(header(task, false), messages);
  }

  private static Task createTask(boolean isInternal) {
    return new Task("name", isInternal, codeLocation(13)) {
      @Override
      public SValue execute(PluginApiImpl pluginApi) {
        return null;
      }
    };
  }
}
