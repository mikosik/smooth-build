package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class EmptySetNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  EmptySetNode emptySetNode = new EmptySetNode();

  @Test
  public void type() {
    assertThat(emptySetNode.type()).isEqualTo(Type.EMPTY_SET);
  }

  public void generateTaskThrowsException() throws Exception {
    Task task = emptySetNode.generateTask(taskGenerator);
    Value result = task.execute(new FakeSandbox());
    assertThat((FileSet) result).isEmpty();
  }
}
