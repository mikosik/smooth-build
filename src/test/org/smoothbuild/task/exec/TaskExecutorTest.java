package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

public class TaskExecutorTest {
  Task task = mock(Task.class);
  Value value = mock(Value.class);
  UserConsole userConsole = mock(UserConsole.class);
  MessageGroup messageGroup = new MessageGroup("name");
  SandboxImpl sandbox = mock(SandboxImpl.class);
  SandboxFactory sandboxFactory = mock(SandboxFactory.class);

  TaskExecutor taskExecutor = new TaskExecutor(sandboxFactory, userConsole);

  @Before
  public void before() {
    Mockito.when(sandboxFactory.createSandbox(task)).thenReturn(sandbox);
    Mockito.when(sandbox.messageGroup()).thenReturn(messageGroup);
    Mockito.when(task.execute(sandbox)).thenReturn(value);
  }

  @Test
  public void execute_invokes_task_execute() {
    assertThat(taskExecutor.execute(task)).isEqualTo(value);
  }

  @Test
  public void execute_prints_message_group_for_internal_task_when_message_occurred() {
    Mockito.when(task.isInternal()).thenReturn(true);
    messageGroup.report(new Message(WARNING, "message"));

    taskExecutor.execute(task);

    verify(userConsole).report(messageGroup);
  }

  @Test
  public void execute_prints_no_message_group_for_internal_task_when_no_message_occurred() {
    Mockito.when(task.isInternal()).thenReturn(true);
    taskExecutor.execute(task);
    verifyZeroInteractions(userConsole);
  }

  @Test
  public void execute_prints_message_group_for_non_internal_task_when_message_occurred() {
    Mockito.when(task.isInternal()).thenReturn(false);
    messageGroup.report(new Message(WARNING, "message"));

    taskExecutor.execute(task);

    verify(userConsole).report(messageGroup);
  }

  @Test
  public void execute_prints_message_group_for_non_internal_task_when_no_message_occurred() {
    Mockito.when(task.isInternal()).thenReturn(false);
    taskExecutor.execute(task);
    verify(userConsole).report(messageGroup);
  }

}
