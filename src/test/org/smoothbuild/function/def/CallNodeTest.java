package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableMap;

public class CallNodeTest {
  CodeLocation codeLocation = codeLocation(1, 2, 4);
  Function function = mock(Function.class);

  @Test
  public void type() throws Exception {
    when(function.type()).thenReturn(Type.STRING);
    ImmutableMap<String, DefinitionNode> empty = ImmutableMap.<String, DefinitionNode> of();

    assertThat(new CallNode(function, codeLocation, empty).type()).isEqualTo(Type.STRING);
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
    when(function.generateTask(ImmutableMap.of(name, argTask), codeLocation)).thenReturn(task);

    Task actual = new CallNode(function, codeLocation, argNodes).generateTask();

    assertThat(actual).isSameAs(task);
  }
}
