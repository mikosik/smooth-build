package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.function.base.FakeSignature.testSignature;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.fs.base.exc.FileSystemError;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.task.base.NativeCallTask;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeCallTaskTest {
  Invoker invoker = mock(Invoker.class);
  FakeSandbox sandbox = new FakeSandbox();
  CodeLocation codeLocation = codeLocation(1, 2, 4);
  HashCode hash = HashCode.fromInt(33);
  NativeFunction function1 = new NativeFunction(testSignature(), invoker);
  NativeFunction function2 = new NativeFunction(testSignature(), invoker);

  String name1 = "name1";
  String name2 = "name2";
  HashCode hash1 = HashCode.fromInt(1);
  HashCode hash2 = HashCode.fromInt(2);

  ImmutableList<Param> params = ImmutableList.of(param(STRING, name1), param(STRING, name2));

  NativeCallTask nativeCallTask = new NativeCallTask(function1, codeLocation, Empty.stringHashMap());

  @Test
  public void location() throws Exception {
    assertThat(nativeCallTask.location().name()).isEqualTo(testSignature().name());
    assertThat(nativeCallTask.location().location()).isEqualTo(codeLocation);
  }

  @Test
  public void calculateResult() throws IllegalAccessException, InvocationTargetException {
    Object argValue = "subTaskResult";
    Task subTask = new FakeTask(argValue);

    String name = "param";
    NativeCallTask nativeCallTask = new NativeCallTask(function1, codeLocation, ImmutableMap.of(
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
    function1 = new NativeFunction(signature, invoker);
    nativeCallTask = new NativeCallTask(function1, codeLocation, Empty.stringHashMap());
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

  @Test
  public void functionWithTheSameNameButDifferentArgsHaveDifferentHashes() throws Exception {
    function1 = new NativeFunction(new Signature(STRING, simpleName("func"), params), invoker);

    ImmutableMap<String, HashCode> args1 = ImmutableMap.of(name1, hash1);
    NativeCallTask task1 = new NativeCallTask(function1, codeLocation, args1);

    ImmutableMap<String, HashCode> args2 = ImmutableMap.of(name2, hash2);
    NativeCallTask task2 = new NativeCallTask(function1, codeLocation, args2);

    assertThat(task1.hash()).isNotEqualTo(task2.hash());
  }

  @Test
  public void functionWithDifferentNamesButSameArgsHaveDifferentHashes() throws Exception {
    function1 = new NativeFunction(new Signature(STRING, simpleName("func1"), params), invoker);
    function2 = new NativeFunction(new Signature(STRING, simpleName("func2"), params), invoker);

    ImmutableMap<String, HashCode> args1 = ImmutableMap.of(name1, hash1);
    NativeCallTask task1 = new NativeCallTask(function1, codeLocation, args1);
    NativeCallTask task2 = new NativeCallTask(function2, codeLocation, args1);

    assertThat(task1.hash()).isNotEqualTo(task2.hash());
  }
}
