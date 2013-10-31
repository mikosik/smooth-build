package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.testing.message.FakeUserConsole;

public class TaskExecutorTest {
  LocatedTask task = mock(LocatedTask.class);
  Value value = mock(Value.class);
  FakeUserConsole userConsole = new FakeUserConsole();
  MessageGroup messageGroup = new MessageGroup("name");
  SandboxImpl sandbox = mock(SandboxImpl.class);
  SandboxFactory sandboxFactory = mock(SandboxFactory.class);

  TaskExecutor taskExecutor = new TaskExecutor(sandboxFactory, userConsole);

  @Test
  public void execute_invokes_task_execute() {
    Mockito.when(sandboxFactory.createSandbox(task)).thenReturn(sandbox);
    Mockito.when(sandbox.messageGroup()).thenReturn(messageGroup);
    Mockito.when(task.execute(sandbox)).thenReturn(value);

    assertThat(taskExecutor.execute(task)).isEqualTo(value);
  }
}
