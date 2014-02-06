package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Task;

public class TaskContainerTest {
  TaskExecutor taskExecutor = mock(TaskExecutor.class);
  Task task = mock(Task.class);
  SValue value = mock(SValue.class);

  TaskContainer taskContainer = new TaskContainer(taskExecutor, task);

  @Test
  public void result_is_calculated_by_passing_task_to_task_executor() {
    given(willReturn(value), taskExecutor).execute(task);
    assertThat(taskContainer.value()).isEqualTo(value);
  }

  @Test
  public void second_call_to_execute_returns_cached_value() {
    given(willReturn(value), taskExecutor).execute(task);
    assertThat(taskContainer.value()).isEqualTo(value);
    assertThat(taskContainer.value()).isEqualTo(value);
    thenCalledTimes(1, taskExecutor).execute(task);
  }
}
