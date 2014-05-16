package org.smoothbuild.task.exec;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.message.base.MessageType.WARNING;
import static org.smoothbuild.task.exec.TaskReporter.header;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;

public class TaskReporterTest {
  UserConsole userConsole = mock(UserConsole.class);
  TaskReporter taskReporter = new TaskReporter(userConsole);
  ImmutableList<Message> messages;
  Task<SString> task;

  @Test
  public void internal_task_with_message_is_printed() {
    given(task = createTask(true));
    given(messages = ImmutableList.of(new Message(WARNING, "message")));
    given(task).setOutput(new TaskOutput<SString>(messages));
    when(taskReporter).report(task, false);
    thenCalled(userConsole).print(header(task, false), messages);
  }

  @Test
  public void internal_task_without_message_is_not_printed() {
    given(task = createTask(true));
    when(taskReporter).report(task, false);
    thenCalledTimes(0, onInstance(userConsole));
  }

  @Test
  public void non_internal_task_with_message_is_printed() {
    given(messages = ImmutableList.of(new Message(WARNING, "message")));
    given(task = createTask(false));
    given(task).setOutput(new TaskOutput<SString>(messages));
    when(taskReporter).report(task, false);
    thenCalled(userConsole).print(header(task, false), messages);
  }

  @Test
  public void non_internal_task_without_message_is_printed() {
    given(task = createTask(false));
    given(messages = ImmutableList.of());
    given(task).setOutput(new TaskOutput<SString>(messages));
    when(taskReporter).report(task, false);
    thenCalled(userConsole).print(header(task, false), messages);
  }

  private static Task<SString> createTask(boolean isInternal) {
    return new Task<>(new MyTaskWorker(isInternal), Empty.taskList());
  }

  private static final class MyTaskWorker extends TaskWorker<SString> {
    private MyTaskWorker(boolean isInternal) {
      super(null, STRING, "name", isInternal, true, CodeLocation.codeLocation(2));
    }

    @Override
    public TaskOutput<SString> execute(TaskInput input, NativeApiImpl nativeApi) {
      return null;
    }
  }
}
