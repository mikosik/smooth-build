package org.smoothbuild.task;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.TaskLocation.taskLocation;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.TaskLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.message.TestMessageListener;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;

public class TaskExecutorTest {
  TestFileSystem fileSystem = new TestFileSystem();
  TestMessageListener messages = new TestMessageListener();
  Task subTask = mock(Task.class);
  Task task = mock(Task.class);
  TaskLocation taskLocation = taskLocation(simpleName("name"), codeLocation(1, 2, 4));

  TaskExecutor taskExecutor = new TaskExecutor(fileSystem);

  @Test
  public void task_with_result_calculated_is_not_executed() {
    when(task.isResultCalculated()).thenReturn(true);

    taskExecutor.execute(messages, task);

    verify(task).isResultCalculated();
    verifyNoMoreInteractions(task);
  }

  @Test
  public void tasks_are_executed_starting_from_dependencies() {
    when(subTask.dependencies()).thenReturn(Empty.taskList());
    when(subTask.location()).thenReturn(taskLocation);
    when(task.dependencies()).thenReturn(ImmutableList.of(subTask));
    when(task.location()).thenReturn(taskLocation);

    taskExecutor.execute(messages, task);

    InOrder inOrder = inOrder(task, subTask);
    inOrder.verify(subTask).execute(any(Sandbox.class));
    inOrder.verify(task).execute(any(Sandbox.class));
  }

  @Test
  public void task_with_problem_stops_execution() {
    when(subTask.dependencies()).thenReturn(Empty.taskList());
    when(subTask.location()).thenReturn(taskLocation);
    when(task.dependencies()).thenReturn(ImmutableList.of(subTask));
    when(task.location()).thenReturn(taskLocation);

    Mockito.doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        MessageListener messages = (MessageListener) invocation.getArguments()[0];
        messages.report(new Message(ERROR, ""));
        return null;
      }
    }).when(subTask).execute(Matchers.<Sandbox> any());

    taskExecutor.execute(messages, task);

    messages.assertOnlyProblem(Message.class);
    verify(task, times(0)).execute(Matchers.<Sandbox> any());
  }
}
