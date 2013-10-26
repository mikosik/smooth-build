package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.Task;

public class StringNodeTest {
  StringValue string = mock(StringValue.class);
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
    Task task = stringNode.generateTask();

    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(string);
  }
}
