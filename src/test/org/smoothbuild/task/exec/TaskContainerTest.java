package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.message.FakeCallLocation;

public class TaskContainerTest {
  TaskExecutor taskExecutor = mock(TaskExecutor.class);
  Task worker = mock(Task.class);
  CallLocation callLocation = new FakeCallLocation();
  Value value = mock(Value.class);

  TaskContainer taskContainer = new TaskContainer(taskExecutor, worker, callLocation);

  @Test
  public void test() {
    Mockito.when(taskExecutor.execute(worker, callLocation)).thenReturn(value);
    assertThat(taskContainer.result()).isEqualTo(value);
  }
}
