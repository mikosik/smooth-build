package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.smoothbuild.function.plugin.PluginInvoker;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.run.err.FunctionError;
import org.smoothbuild.util.FunctionReflectionException;

import com.google.common.collect.ImmutableMap;

public class PluginTaskTest {
  PluginInvoker pluginInvoker = mock(PluginInvoker.class);
  Task subTask = mock(Task.class);
  TestingSandbox sandbox = new TestingSandbox();
  String name = "param";
  Object argValue = "argValue";

  Path tempDir = Path.path("temp/dir");

  PluginTask pluginTask = new PluginTask(pluginInvoker, ImmutableMap.of(name, subTask));

  @Test
  public void calculateResult() throws FunctionReflectionException {
    when(subTask.result()).thenReturn(argValue);

    String result = "result";
    when(pluginInvoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenReturn(result);

    pluginTask.calculateResult(sandbox);
    assertThat(pluginTask.result()).isSameAs(result);
  }

  @Test
  public void calculateResultReportErrorWhenExceptionIsThrownFromPluginInvoker()
      throws FunctionReflectionException {
    when(subTask.result()).thenReturn(argValue);
    when(pluginInvoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenThrow(
        new FunctionReflectionException(""));

    pluginTask.calculateResult(sandbox);

    sandbox.problems().assertOnlyProblem(FunctionError.class);
    assertThat(pluginTask.isResultCalculated()).isFalse();
  }

}
