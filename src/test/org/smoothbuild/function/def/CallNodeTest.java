package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.function.base.Type.STRING;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.plugin.FakeString;
import org.smoothbuild.testing.task.base.FakeResult;

import com.google.common.collect.ImmutableMap;

public class CallNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  CodeLocation codeLocation = new FakeCodeLocation();
  Function function = mock(Function.class);

  @Test
  public void type() throws Exception {
    when(function.type()).thenReturn(STRING);
    when(function.name()).thenReturn(name("function"));

    ImmutableMap<String, Node> empty = ImmutableMap.<String, Node> of();

    assertThat(new CallNode(function, codeLocation, empty).type()).isEqualTo(STRING);
  }

  @Test
  public void generateTask() throws Exception {
    Function function = mock(Function.class);
    Node node = mock(Node.class);
    Task task = mock(Task.class);
    when(function.name()).thenReturn(name("function"));
    when(function.type()).thenReturn(STRING);

    Result result = new FakeResult(new FakeString("arg"));

    String name = "name";
    Map<String, Node> argNodes = ImmutableMap.of(name, node);

    when(taskGenerator.generateTask(node)).thenReturn(result);
    when(function.generateTask(taskGenerator, ImmutableMap.of(name, result), codeLocation))
        .thenReturn(task);

    Task actual = new CallNode(function, codeLocation, argNodes).generateTask(taskGenerator);

    assertThat(actual).isSameAs(task);
  }
}
