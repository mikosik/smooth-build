package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.exec.TaskGenerator;

public class InvalidNodeTest {
  InvalidNode invalidNode = new InvalidNode(Type.STRING);

  @Test(expected = NullPointerException.class)
  public void nullTypeIsForbidden() throws Exception {
    new InvalidNode(null);
  }

  @Test
  public void type() {
    assertThat(invalidNode.type()).isEqualTo(Type.STRING);
  }

  @Test(expected = RuntimeException.class)
  public void generateTaskThrowsException() throws Exception {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    invalidNode.generateTask(taskGenerator);
  }
}
