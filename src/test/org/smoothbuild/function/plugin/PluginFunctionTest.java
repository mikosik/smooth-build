package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.TestingSignature.testingSignature;

import org.junit.Test;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.task.Task;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class PluginFunctionTest {
  Sandbox sandbox = mock(Sandbox.class);
  String name = "functionName";

  Signature signature = testingSignature("functionName");
  PluginInvoker invoker = mock(PluginInvoker.class);

  PluginFunction function = new PluginFunction(signature, invoker);

  @Test
  public void generateTaskReturnsTaskWithNoResultCalculated() throws Exception {
    Task task = function.generateTask(Empty.stringTaskMap());
    assertThat(task.isResultCalculated()).isFalse();
  }

  @Test
  public void generateTaskReturnsTaskWithPassedDependencies() throws Exception {
    ImmutableMap<String, Task> dependencies = Empty.stringTaskMap();
    Task task = function.generateTask(dependencies);
    assertThat(task.dependencies()).isSameAs(dependencies);
  }

  @Test
  public void generatedTaskUsesPluginInvokerForCalculatingResult() throws Exception {
    String result = "result";

    // given
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(result);

    // when
    Task task = function.generateTask(Empty.stringTaskMap());
    task.calculateResult(sandbox);

    // then
    assertThat(task.isResultCalculated()).isTrue();
    assertThat(task.result()).isSameAs(result);
  }
}
