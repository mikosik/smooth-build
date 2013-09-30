package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.function.base.TestSignature.testSignature;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.fs.base.exc.FileSystemError;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.Error;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.task.err.NullResultError;
import org.smoothbuild.task.err.ReflexiveInternalError;
import org.smoothbuild.task.err.UnexpectedError;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class InvokeTaskTest {
  Invoker invoker = mock(Invoker.class);
  TestSandbox sandbox = new TestSandbox();
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  InvokeTask invokeTask = new InvokeTask(testSignature(), codeLocation, invoker,
      Empty.stringTaskMap());

  @Test
  public void location() throws Exception {
    assertThat(invokeTask.location().name()).isEqualTo(testSignature().name());
    assertThat(invokeTask.location().location()).isEqualTo(codeLocation);
  }

  @Test
  public void calculateResult() throws IllegalAccessException, InvocationTargetException {
    Object argValue = "subTaskResult";
    Task subTask = mock(Task.class);
    when(subTask.result()).thenReturn(argValue);

    String name = "param";
    InvokeTask invokeTask = new InvokeTask(testSignature(), codeLocation, invoker, ImmutableMap.of(
        name, subTask));

    String result = "result";
    when(invoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenReturn(result);

    invokeTask.execute(sandbox);
    assertThat(invokeTask.result()).isSameAs(result);
  }

  @Test
  public void nullResultErrorIsReportedWhenNullIsReturnByFunctionReturningNonVoidType()
      throws Exception {
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    invokeTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(NullResultError.class);
    assertThat(invokeTask.isResultCalculated()).isFalse();
  }

  @Test
  public void nullCanBeReturnedByFunctionOfVoidType() throws Exception {
    Signature signature = new Signature(VOID, simpleName("name"), Empty.stringParamMap());
    invokeTask = new InvokeTask(signature, codeLocation, invoker, Empty.stringTaskMap());
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    invokeTask.execute(sandbox);

    sandbox.messages().assertNoProblems();
    assertThat(invokeTask.isResultCalculated()).isTrue();
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
  public void messageThrownIsReported() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new MyError());
    assertExceptionIsReportedAsProblem(exception, MyError.class);
  }

  @Test
  public void unexpectedErrorIsReportedForUnexpectedRuntimeException() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new RuntimeException());
    assertExceptionIsReportedAsProblem(exception, UnexpectedError.class);
  }

  @SuppressWarnings("serial")
  private static class MyError extends Error {
    public MyError() {
      super("message");
    }
  }

  private void assertExceptionIsReportedAsProblem(Throwable thrown,
      Class<? extends Message> expected) throws Exception {
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenThrow(thrown);

    invokeTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(expected);
    assertThat(invokeTask.isResultCalculated()).isFalse();
  }

}
