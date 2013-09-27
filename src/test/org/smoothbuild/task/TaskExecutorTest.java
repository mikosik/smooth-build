package org.smoothbuild.task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.message.TestMessageListener;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;

public class TaskExecutorTest {
  TestFileSystem fileSystem = new TestFileSystem();
  TestMessageListener messages = new TestMessageListener();

  TaskExecutor taskExecutor = new TaskExecutor(fileSystem);

  @Test
  public void tasksAreExecutedStartingFromDependencies() {
    Task subTask = mock(Task.class);
    Task task = mock(Task.class);

    when(subTask.dependencies()).thenReturn(Empty.taskList());
    when(task.dependencies()).thenReturn(ImmutableList.of(subTask));

    taskExecutor.execute(messages, task);

    InOrder inOrder = inOrder(task, subTask);
    inOrder.verify(subTask).execute(any(Sandbox.class));
    inOrder.verify(task).execute(any(Sandbox.class));
  }

  @Test
  public void taskWithProblemsStopsExecution() {
    Task subTask = mock(Task.class);
    Task task = mock(Task.class);

    when(subTask.dependencies()).thenReturn(Empty.taskList());
    when(task.dependencies()).thenReturn(ImmutableList.of(subTask));

    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        MessageListener messages = (MessageListener) invocation.getArguments()[0];
        messages.report(new Error(""));
        return null;
      }
    }).when(subTask).execute(Matchers.<Sandbox> any());

    taskExecutor.execute(messages, task);

    messages.assertOnlyProblem(Error.class);
    verify(task, times(0)).execute(Matchers.<Sandbox> any());
  }
}
