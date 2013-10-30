package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.db.ValueDb;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.db.FakeObjectDb;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCallLocation;
import org.smoothbuild.testing.message.FakeUserConsole;

public class TaskExecutorTest {
  Task task1 = mock(Task.class);
  Value value = mock(Value.class);
  FakeCallLocation callLocation = new FakeCallLocation();
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeUserConsole userConsole = new FakeUserConsole();
  ValueDb valueDb = new FakeObjectDb(fileSystem);

  TaskExecutor taskExecutor = new TaskExecutor(fileSystem, valueDb, userConsole);

  @Test
  public void execute_invokes_task_execute() {
    Mockito.when(task1.execute(Matchers.<Sandbox> any())).thenReturn(value);
    assertThat(taskExecutor.execute(task1, callLocation)).isEqualTo(value);
  }

  @Test
  public void message_can_be_reported_via_sandbox() throws Exception {
    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        Sandbox sandbox = (Sandbox) invocation.getArguments()[0];
        sandbox.report(new Message(MessageType.ERROR, "message"));
        return null;
      }
    }).when(task1).execute(Matchers.<Sandbox> any());

    taskExecutor.execute(task1, callLocation);

    userConsole.assertProblemsFound();
  }
}
