package org.smoothbuild.lang.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.type.Type.STRING;

import java.util.Map;

import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;

import com.google.common.collect.ImmutableMap;

public class CallNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  CodeLocation codeLocation = new FakeCodeLocation();
  Function function = mock(Function.class);

  @Test
  public void type() throws Exception {
    BDDMockito.willReturn(STRING).given(function).type();
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
    willReturn(STRING).given(function).type();

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
