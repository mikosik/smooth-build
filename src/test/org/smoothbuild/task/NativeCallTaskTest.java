package org.smoothbuild.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.function.base.TestSignature.testSignature;
import static org.smoothbuild.testing.task.HashedTasksTester.hashedTasks;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.fs.base.exc.FileSystemError;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.task.err.NullResultError;
import org.smoothbuild.task.err.ReflexiveInternalError;
import org.smoothbuild.task.err.UnexpectedError;
import org.smoothbuild.testing.task.TestSandbox;
import org.smoothbuild.testing.task.TestTask;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeCallTaskTest {
  Invoker invoker = mock(Invoker.class);
  TestSandbox sandbox = new TestSandbox();
  CodeLocation codeLocation = codeLocation(1, 2, 4);
  HashCode hash = HashCode.fromInt(33);
  NativeFunction function = new NativeFunction(testSignature(), invoker);

  NativeCallTask nativeCallTask = new NativeCallTask(function, codeLocation, Empty.stringHashMap());

  @Test
  public void location() throws Exception {
    assertThat(nativeCallTask.location().name()).isEqualTo(testSignature().name());
    assertThat(nativeCallTask.location().location()).isEqualTo(codeLocation);
  }

  @Test
  public void calculateResult() throws IllegalAccessException, InvocationTargetException {
    Object argValue = "subTaskResult";
    Task subTask = new TestTask(argValue);

    String name = "param";
    NativeCallTask nativeCallTask = new NativeCallTask(function, codeLocation, ImmutableMap.of(
        name, subTask.hash()));

    String result = "result";
    when(invoker.invoke(sandbox, ImmutableMap.of(name, argValue))).thenReturn(result);

    nativeCallTask.execute(sandbox, hashedTasks(subTask));
    assertThat(nativeCallTask.result()).isSameAs(result);
  }

  @Test
  public void nullResultErrorIsReportedWhenNullIsReturnByFunctionReturningNonVoidType()
      throws Exception {
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    nativeCallTask.execute(sandbox, hashedTasks());

    sandbox.messages().assertOnlyProblem(NullResultError.class);
    assertThat(nativeCallTask.isResultCalculated()).isFalse();
  }

  @Test
  public void nullCanBeReturnedByFunctionOfVoidType() throws Exception {
    ImmutableList<Param> params = ImmutableList.of();
    Signature signature = new Signature(VOID, simpleName("name"), params);
    function = new NativeFunction(signature, invoker);
    nativeCallTask = new NativeCallTask(function, codeLocation, Empty.stringHashMap());
    when(invoker.invoke(sandbox, Empty.stringObjectMap())).thenReturn(null);

    nativeCallTask.execute(sandbox, hashedTasks());

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

    nativeCallTask.execute(sandbox, hashedTasks());

    sandbox.messages().assertOnlyProblem(expected);
    assertThat(nativeCallTask.isResultCalculated()).isFalse();
  }

}
