package org.smoothbuild.task.exec;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.message.message.MessageType.ERROR;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;

public class TaskGraphExecutorTest {
  Task task1 = task(1);
  Task task2 = task(2);
  UserConsole userConsole = new FakeUserConsole();
  TaskExecutor taskExecutor = mock(TaskExecutor.class);

  TaskGraphExecutor taskGraphExecutor = new TaskGraphExecutor(userConsole, taskExecutor);

  @Test
  public void dependency_task_is_executed_before_our_task() {
    when(task1.dependencies()).thenReturn(ImmutableList.of(task2));
    when(task2.dependencies()).thenReturn(Empty.taskList());

    taskGraphExecutor.execute(task1);

    InOrder inOrder = inOrder(taskExecutor);
    inOrder.verify(taskExecutor).execute(task2);
    inOrder.verify(taskExecutor).execute(task1);
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void no_more_task_is_executed_once_error_has_been_reported() {
    when(task1.dependencies()).thenReturn(ImmutableList.of(task2));
    when(task2.dependencies()).thenReturn(Empty.taskList());
    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        MessageGroup messageGroup = new MessageGroup("group");
        messageGroup.report(new Message(ERROR, "message"));
        userConsole.report(messageGroup);
        return null;
      }
    }).when(taskExecutor).execute(task2);

    taskGraphExecutor.execute(task1);

    InOrder inOrder = inOrder(taskExecutor);
    inOrder.verify(taskExecutor).execute(task2);
    inOrder.verifyNoMoreInteractions();
  }

  private Task task(int value) {
    return mock(Task.class);
  }
}
