package org.smoothbuild.task.exec;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Task;

public class TaskContainerCreatorTest {
  Value value = mock(Value.class);
  Task task = mock(Task.class);
  TaskExecutor taskExecutor = mock(TaskExecutor.class);

  TaskContainer taskContainer;
  TaskContainerCreator taskContainerCreator = new TaskContainerCreator(taskExecutor);

  @Test
  public void test() {
    BDDMockito.given(taskExecutor.execute(task)).willReturn(value);

    given(taskContainer = taskContainerCreator.create(task));
    when(taskContainer.result());
    thenReturned(value);
  }
}
