package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.LocatedTask;

public class TaskContainerTest {
  TaskExecutor taskExecutor = mock(TaskExecutor.class);
  LocatedTask task = mock(LocatedTask.class);
  Value value = mock(Value.class);

  TaskContainer taskContainer = new TaskContainer(taskExecutor, task);

  @Test
  public void test() {
    Mockito.when(taskExecutor.execute(task)).thenReturn(value);
    assertThat(taskContainer.result()).isEqualTo(value);
  }
}
