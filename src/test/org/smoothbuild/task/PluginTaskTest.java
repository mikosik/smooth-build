package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.testing.function.base.TestSignature.testSignature;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.plugin.PluginInvoker;
import org.smoothbuild.problem.Message;
import org.smoothbuild.task.err.FileSystemError;
import org.smoothbuild.task.err.NullResultError;
import org.smoothbuild.task.err.ReflexiveInternalError;
import org.smoothbuild.task.err.UnexpectedError;
import org.smoothbuild.testing.plugin.internal.TestSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class PluginTaskTest {
  PluginInvoker pluginInvoker = mock(PluginInvoker.class);
  TestSandbox sandbox = new TestSandbox();

  PluginTask pluginTask = new PluginTask(testSignature(), pluginInvoker, Empty.stringTaskMap());

  @Test
  public void calculateResult() throws IllegalAccessException, InvocationTargetException {
    Object argValue = "subTaskResult";
    Task subTask = mock(Task.class);
    when(subTask.result()).thenReturn(argValue);

    String name = "param";
    PluginTask pluginTask = new PluginTask(testSignature(), pluginInvoker, ImmutableMap.of(name,
        subTask));

    String result = "result";
    when(pluginInvoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenReturn(result);

    pluginTask.execute(sandbox);
    assertThat(pluginTask.result()).isSameAs(result);
  }

  @Test
  public void nullResultErrorIsReportedWhenNullIsReturnByFunctionReturningNonVoidType()
      throws Exception {
    when(pluginInvoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    pluginTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(NullResultError.class);
    assertThat(pluginTask.isResultCalculated()).isFalse();
  }

  @Test
  public void nullCanBeReturnedByFunctionOfVoidType() throws Exception {
    pluginTask = new PluginTask(new Signature(VOID, simpleName("name"), Empty.stringParamMap()),
        pluginInvoker, Empty.stringTaskMap());
    when(pluginInvoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    pluginTask.execute(sandbox);

    sandbox.messages().assertNoProblems();
    assertThat(pluginTask.isResultCalculated()).isTrue();
  }

  @Test
  public void reflexiveInternalErrorIsReportedForIllegalAccessException() throws Exception {
    assertExceptionIsReportedAsProblem(new IllegalAccessException(""), ReflexiveInternalError.class);
  }

  @Test
  public void fileSystemErrorIsReportedForFileSystemException() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new FileSystemException(""));
    assertExceptionIsReportedAsProblem(exception, FileSystemError.class);
  }

  @Test
  public void unexpectedErrorIsReportedForUnexpectedRuntimeException() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new RuntimeException());
    assertExceptionIsReportedAsProblem(exception, UnexpectedError.class);
  }

  private void assertExceptionIsReportedAsProblem(Throwable thrown,
      Class<? extends Message> expected) throws Exception {
    when(pluginInvoker.invoke(sandbox, Empty.stringObjectMap())).thenThrow(thrown);

    pluginTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(expected);
    assertThat(pluginTask.isResultCalculated()).isFalse();
  }

}
