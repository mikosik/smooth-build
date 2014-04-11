package org.smoothbuild.task.exec;

import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.smoothbuild.task.exec.TaskReporter.header;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;
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

  Task task;

  @Before
  public void before() {
    given(willReturn(messages), pluginApi).loggedMessages();
  }

  @Test
  public void messages_of_internal_task_are_printed() {
    given(task = createTask(true));
    given(messages).log(new Message(WARNING, "message"));

    when(taskReporter).report(task, pluginApi);

    thenCalled(userConsole).print(header(task, false), messages);
  }

  @Test
  public void internal_task_without_messages_are_not_printed() {
    given(task = createTask(true));
    when(taskReporter).report(task, pluginApi);
    thenCalledTimes(0, onInstance(userConsole));
  }

  @Test
  public void messages_of_non_internal_tasks_are_printed() {
    given(task = createTask(false));
    given(messages).log(new Message(WARNING, "message"));

    when(taskReporter).report(task, pluginApi);

    thenCalled(userConsole).print(header(task, false), messages);
  }

  @Test
  public void non_internal_task_without_messages_is_printed() {
    given(task = createTask(false));
    when(taskReporter).report(task, pluginApi);
    thenCalled(userConsole).print(header(task, false), messages);
  }

  private static Task createTask(boolean isInternal) {
    return new Task(STRING, "name", isInternal, codeLocation(13)) {
      @Override
      public SValue execute(PluginApiImpl pluginApi) {
        return null;
      }
    };
  }
}
