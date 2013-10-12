package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;

public class StringNodeTest {
  String string = "string value";
  StringNode stringNode = new StringNode(string);

  @Test(expected = NullPointerException.class)
  public void nullExpressionIsForbidden() throws Exception {
    new StringNode(null);
  }

  @Test
  public void type() throws Exception {
    assertThat(stringNode.type()).isEqualTo(Type.STRING);
  }

  @Test
  public void generateTask() throws Exception {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);

    Task task = stringNode.generateTask(taskGenerator);

    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(string);
  }
}
