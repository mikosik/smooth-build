package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.TaskGenerator;

public class EmptySetNodeTest {
  EmptySetNode emptySetNode = new EmptySetNode();

  @Test
  public void type() {
    assertThat(emptySetNode.type()).isEqualTo(Type.EMPTY_SET);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void generateTaskThrowsException() throws Exception {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);

    emptySetNode.generateTask(taskGenerator);
  }
}
