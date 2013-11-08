package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.testing.function.base.FakeSignature.fakeSignature;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.fs.base.exc.FileSystemError;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.plugin.FakeString;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeCallTaskTest {
  Invoker invoker = mock(Invoker.class);
  FakeSandbox sandbox = new FakeSandbox();
  CodeLocation codeLocation = new FakeCodeLocation();
  HashCode hash = HashCode.fromInt(33);
  NativeFunction function1 = new NativeFunction(fakeSignature(), invoker, true);
  NativeFunction function2 = new NativeFunction(fakeSignature(), invoker, true);

  String name1 = "name1";
  String name2 = "name2";
  HashCode hash1 = HashCode.fromInt(1);
  HashCode hash2 = HashCode.fromInt(2);

  ImmutableList<Param> params = ImmutableList.of(param(STRING, name1), param(STRING, name2));

  NativeCallTask nativeCallTask = new NativeCallTask(function1, Empty.stringTaskResultMap(),
      codeLocation);

  @Test
  public void calculate_result() throws IllegalAccessException, InvocationTargetException {
    StringValue argValue = new FakeString("subTaskResult");
    Result subTask = new FakeResult(argValue);

    String name = "param";
    NativeCallTask nativeCallTask = new NativeCallTask(function1, ImmutableMap.of(name, subTask),
        codeLocation);

    StringValue result = new FakeString("result");
    when(invoker.invoke(sandbox, ImmutableMap.<String, Value> of(name, argValue))).thenReturn(
        result);

    assertThat(nativeCallTask.execute(sandbox)).isSameAs(result);
  }

  @Test
  public void null_result_is_reported_when_functio_has_non_void_return_type() throws Exception {
    when(invoker.invoke(sandbox, Empty.stringValueMap())).thenReturn(null);

    nativeCallTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(NullResultError.class);
  }

  @Test
  public void null_can_be_returned_by_function_with_void_result_type() throws Exception {
    ImmutableList<Param> params = ImmutableList.of();
    Signature signature = new Signature(VOID, name("name"), params);
    function1 = new NativeFunction(signature, invoker, true);
    nativeCallTask = new NativeCallTask(function1, Empty.stringTaskResultMap(), codeLocation);
    when(invoker.invoke(sandbox, Empty.stringValueMap())).thenReturn(null);

    nativeCallTask.execute(sandbox);

    sandbox.messages().assertNoProblems();
  }

  @Test
  public void null_can_be_returned_when_function_reported_errors() throws Exception {
    ImmutableList<Param> params = ImmutableList.of();
    Signature signature = new Signature(Type.FILE, name("name"), params);
    function1 = new NativeFunction(signature, invoker, true);
    nativeCallTask = new NativeCallTask(function1, Empty.stringTaskResultMap(), codeLocation);
    when(invoker.invoke(sandbox, Empty.stringValueMap())).thenAnswer(new Answer<File>() {
      @Override
      public File answer(InvocationOnMock invocation) throws Throwable {
        Sandbox sandbox = (Sandbox) invocation.getArguments()[0];
        sandbox.report(new CodeMessage(ERROR, new FakeCodeLocation(), "message"));
        return null;
      }
    });

    nativeCallTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(CodeMessage.class);
  }

  @Test
  public void reflexive_internal_error_is_reported_for_illegal_access_exception() throws Exception {
    assertExceptionIsReportedAsProblem(new IllegalAccessException(""), ReflexiveInternalError.class);
  }

  @Test
  public void file_system_error_is_reported_for_file_system_exception() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new FileSystemException(""));
    assertExceptionIsReportedAsProblem(exception, FileSystemError.class);
  }

  @Test
  public void message_thrown_as_error_message_exception_is_reported() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new ErrorMessageException(
        new MyError()));
    assertExceptionIsReportedAsProblem(exception, MyError.class);
  }

  @Test
  public void unexpected_error_is_reported_for_unexpected_runtime_exception() throws Exception {
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
    when(invoker.invoke(sandbox, Empty.stringValueMap())).thenThrow(thrown);

    nativeCallTask.execute(sandbox);

    sandbox.messages().assertOnlyProblem(expected);
  }
}
