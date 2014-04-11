package org.smoothbuild.task.exec;

import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Task;

public class TaskContainerCreatorTest {
  SValue value = mock(SValue.class);
  Task<?> task = mock(Task.class);
  TaskExecutor taskExecutor = mock(TaskExecutor.class);

  TaskContainer<?> taskContainer;
  TaskContainerCreator taskContainerCreator = new TaskContainerCreator(taskExecutor);

  @Test
  public void test() {
    given(willReturn(value), taskExecutor).execute(task);
    given(taskContainer = taskContainerCreator.create(task));
    when(taskContainer.value());
    thenReturned(value);
  }
}
