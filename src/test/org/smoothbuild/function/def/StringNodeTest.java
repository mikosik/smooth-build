package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class StringNodeTest {
  StringValue string = mock(StringValue.class);
  StringNode stringNode = new StringNode(string, new FakeCodeLocation());

  @Test(expected = NullPointerException.class)
  public void null_string_value_is_forbidden() throws Exception {
    new StringNode(null, new FakeCodeLocation());
  }

  @Test(expected = NullPointerException.class)
  public void null_code_location_is_forbidden() throws Exception {
    new StringNode(string, null);
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
