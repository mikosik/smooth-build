package org.smoothbuild.function.def;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class InvalidNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  InvalidNode invalidNode = new InvalidNode(Type.STRING, new FakeCodeLocation());

  @Test(expected = NullPointerException.class)
  public void null_type_is_forbidden() throws Exception {
    new InvalidNode(null, new FakeCodeLocation());
  }

  @Test
  public void type() {
    when(invalidNode.type());
    thenReturned(Type.STRING);
  }

  public void generate_task_throws_exception() throws Exception {
    when(invalidNode).generateTask(taskGenerator);
    thenThrown(RuntimeException.class);
  }
}
