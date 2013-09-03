package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.TestingSignature.testingSignature;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.function.plugin.PluginInvoker;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.task.err.ReflexivePluginError;

import com.google.common.collect.ImmutableMap;

public class PluginTaskTest {
  PluginInvoker pluginInvoker = mock(PluginInvoker.class);
  Task subTask = mock(Task.class);
  TestingSandbox sandbox = new TestingSandbox();
  String name = "param";
  Object argValue = "argValue";

  Path tempDir = Path.path("temp/dir");

  PluginTask pluginTask = new PluginTask(testingSignature(), pluginInvoker, ImmutableMap.of(name,
      subTask));

  @Test
  public void calculateResult() throws IllegalAccessException, InvocationTargetException {
    when(subTask.result()).thenReturn(argValue);

    String result = "result";
    when(pluginInvoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenReturn(result);

    pluginTask.calculateResult(sandbox);
    assertThat(pluginTask.result()).isSameAs(result);
  }

  @Test
  public void calculateResultReportErrorWhenExceptionIsThrownFromPluginInvoker()
      throws IllegalAccessException, InvocationTargetException {
    when(subTask.result()).thenReturn(argValue);
    when(pluginInvoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenThrow(
        new IllegalAccessException(""));

    pluginTask.calculateResult(sandbox);

    sandbox.problems().assertOnlyProblem(ReflexivePluginError.class);
    assertThat(pluginTask.isResultCalculated()).isFalse();
  }

}
