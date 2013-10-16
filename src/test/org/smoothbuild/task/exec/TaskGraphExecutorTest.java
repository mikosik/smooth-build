package org.smoothbuild.task.exec;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.message.TestUserConsole;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskGraphExecutorTest {
  Task task1 = task(1);
  Task task2 = task(2);
  HashedTasks hashedTasks = hashedTasks(task1, task2);
  UserConsole userConsole = new TestUserConsole();
  TaskExecutor taskExecutor = mock(TaskExecutor.class);

  TaskGraphExecutor taskGraphExecutor = new TaskGraphExecutor(hashedTasks, userConsole,
      taskExecutor);

  @Test
  public void dependency_task_is_executed_before_our_task() {
    HashCode hash2 = task2.hash();
    when(task1.dependencies()).thenReturn(ImmutableList.of(hash2));
    when(task2.dependencies()).thenReturn(ImmutableList.<HashCode> of());

    taskGraphExecutor.execute(task1.hash());

    InOrder inOrder = inOrder(taskExecutor);
    inOrder.verify(taskExecutor).execute(hash2);
    inOrder.verify(taskExecutor).execute(task1.hash());
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void no_more_task_is_executed_once_error_has_been_reported() {
    HashCode hash2 = task2.hash();
    when(task1.dependencies()).thenReturn(ImmutableList.of(hash2));
    when(task2.dependencies()).thenReturn(ImmutableList.<HashCode> of());
    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        MessageGroup messageGroup = new MessageGroup("group");
        messageGroup.report(new Message(ERROR, "message"));
        userConsole.report(messageGroup);
        return null;
      }
    }).when(taskExecutor).execute(hash2);

    taskGraphExecutor.execute(task1.hash());

    InOrder inOrder = inOrder(taskExecutor);
    inOrder.verify(taskExecutor).execute(hash2);
    inOrder.verifyNoMoreInteractions();
  }

  private Task task(int value) {
    Task task = mock(Task.class);
    when(task.hash()).thenReturn(HashCode.fromInt(value));
    return task;
  }
}
