package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableMap;

public class FunctionNodeTest {
  Function function = mock(Function.class);

  @Test
  public void type() throws Exception {
    when(function.type()).thenReturn(Type.STRING);
    ImmutableMap<String, DefinitionNode> empty = ImmutableMap.<String, DefinitionNode> of();

    assertThat(new FunctionNode(function, empty).type()).isEqualTo(Type.STRING);
  }

  @Test
  public void generateTask() throws Exception {
    Function function = mock(Function.class);
    DefinitionNode node = mock(DefinitionNode.class);
    Task argTask = mock(Task.class);
    Task task = mock(Task.class);
    String name = "name";
    Map<String, DefinitionNode> argNodes = ImmutableMap.of(name, node);

    when(node.generateTask()).thenReturn(argTask);
    when(function.generateTask(ImmutableMap.of(name, argTask))).thenReturn(task);

    Task actual = new FunctionNode(function, argNodes).generateTask();

    assertThat(actual).isSameAs(task);
  }
}
