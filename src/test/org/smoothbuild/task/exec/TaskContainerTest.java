package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Task;

public class TaskContainerTest {
  TaskExecutor taskExecutor = mock(TaskExecutor.class);
  Task task = mock(Task.class);
  SValue value = mock(SValue.class);

  TaskContainer taskContainer = new TaskContainer(taskExecutor, task);

  @Test
  public void result_is_calculated_by_passing_task_to_task_executor() {
    Mockito.when(taskExecutor.execute(task)).thenReturn(value);
    assertThat(taskContainer.value()).isEqualTo(value);
  }

  @Test
  public void second_call_to_execute_returns_cached_value() {
    Mockito.when(taskExecutor.execute(task)).thenReturn(value);
    assertThat(taskContainer.value()).isEqualTo(value);
    assertThat(taskContainer.value()).isEqualTo(value);
    verify(taskExecutor, times(1)).execute(task);
  }
}
