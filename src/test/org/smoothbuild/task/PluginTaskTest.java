package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.testing.TestingSignature.testingSignature;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.function.plugin.PluginInvoker;
import org.smoothbuild.plugin.TestingSandbox;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.task.err.ReflexiveInternalError;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class PluginTaskTest {
  PluginInvoker pluginInvoker = mock(PluginInvoker.class);
  TestingSandbox sandbox = new TestingSandbox();

  PluginTask pluginTask = new PluginTask(testingSignature(), pluginInvoker, Empty.stringTaskMap());

  @Test
  public void calculateResult() throws IllegalAccessException, InvocationTargetException {
    Object argValue = "subTaskResult";
    Task subTask = mock(Task.class);
    when(subTask.result()).thenReturn(argValue);

    String name = "param";
    PluginTask pluginTask = new PluginTask(testingSignature(), pluginInvoker, ImmutableMap.of(name,
        subTask));

    String result = "result";
    when(pluginInvoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenReturn(result);

    pluginTask.calculateResult(sandbox);
    assertThat(pluginTask.result()).isSameAs(result);
  }

  @Test
  public void reflexiveInternalErrorIsReportedForIllegalAccessException() throws Exception {
    assertExceptionIsReportedAsProblem(new IllegalAccessException(""), ReflexiveInternalError.class);
  }

  private void assertExceptionIsReportedAsProblem(IllegalAccessException thrown,
      Class<? extends Problem> expected) throws Exception {
    when(pluginInvoker.invoke(sandbox, Empty.stringObjectMap())).thenThrow(thrown);

    pluginTask.calculateResult(sandbox);

    sandbox.problems().assertOnlyProblem(expected);
    assertThat(pluginTask.isResultCalculated()).isFalse();
  }

}
