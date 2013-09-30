package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.function.base.TestSignature.testSignature;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.fs.base.exc.FileSystemError;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.task.err.NullResultError;
import org.smoothbuild.task.err.ReflexiveInternalError;
import org.smoothbuild.task.err.UnexpectedError;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;

public class NativeCallTaskTest {
  Invoker invoker = mock(Invoker.class);
  TestSandbox sandbox = new TestSandbox();
  CodeLocation codeLocation = codeLocation(1, 2, 4);

  NativeCallTask nativeCallTask = new NativeCallTask(testSignature(), codeLocation, invoker,
      Empty.stringTaskMap());

  @Test
  public void location() throws Exception {
    assertThat(nativeCallTask.location().name()).isEqualTo(testSignature().name());
    assertThat(nativeCallTask.location().location()).isEqualTo(codeLocation);
  }

  @Test
  public void calculateResult() throws IllegalAccessException, InvocationTargetException {
    Object argValue = "subTaskResult";
    Task subTask = mock(Task.class);
    when(subTask.result()).thenReturn(argValue);

    String name = "param";
    NativeCallTask nativeCallTask = new NativeCallTask(testSignature(), codeLocation, invoker,
        ImmutableMap.of(name, subTask));

    String result = "result";
    when(invoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenReturn(result);

    nativeCallTask.execute(sandbox);
    assertThat(nativeCallTask.result()).isSameAs(result);
  }

  @Test
  public void nullResultErrorIsReportedWhenNullIsReturnByFunctionReturningNonVoidType()
      throws Exception {
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    nativeCallTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(NullResultError.class);
    assertThat(nativeCallTask.isResultCalculated()).isFalse();
  }

  @Test
  public void nullCanBeReturnedByFunctionOfVoidType() throws Exception {
    Signature signature = new Signature(VOID, simpleName("name"), Empty.stringParamMap());
    nativeCallTask = new NativeCallTask(signature, codeLocation, invoker, Empty.stringTaskMap());
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    nativeCallTask.execute(sandbox);

    sandbox.messages().assertNoProblems();
    assertThat(nativeCallTask.isResultCalculated()).isTrue();
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
    InvocationTargetException exception = new InvocationTargetException(new ErrorMessageException(
        new MyError()));
    assertExceptionIsReportedAsProblem(exception, MyError.class);
  }

  @Test
  public void unexpectedErrorIsReportedForUnexpectedRuntimeException() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new RuntimeException());
    assertExceptionIsReportedAsProblem(exception, UnexpectedError.class);
  }

  private static class MyError extends Message {
    public MyError() {
      super(ERROR, "message");
    }
  }

  private void assertExceptionIsReportedAsProblem(Throwable thrown,
      Class<? extends Message> expected) throws Exception {
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenThrow(thrown);

    nativeCallTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(expected);
    assertThat(nativeCallTask.isResultCalculated()).isFalse();
  }

}
