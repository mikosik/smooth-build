package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.testing.lang.function.base.FakeSignature.fakeSignature;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.io.fs.base.exc.FileSystemError;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakePluginApi;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeCallTaskTest {
  Invoker invoker = mock(Invoker.class);
  FakePluginApi pluginApi = new FakePluginApi();
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
    SString argValue = new FakeString("subTaskResult");
    Result subTask = new FakeResult(argValue);

    String name = "param";
    NativeCallTask nativeCallTask =
        new NativeCallTask(function1, ImmutableMap.of(name, subTask), codeLocation);

    SString result = new FakeString("result");
    when(invoker.invoke(pluginApi, ImmutableMap.<String, SValue> of(name, argValue))).thenReturn(
        result);

    assertThat(nativeCallTask.execute(pluginApi)).isSameAs(result);
  }

  @Test
  public void null_result_is_reported_when_functio_has_non_void_return_type() throws Exception {
    when(invoker.invoke(pluginApi, Empty.stringValueMap())).thenReturn(null);

    nativeCallTask.execute(pluginApi);

    pluginApi.loggedMessages().assertContainsOnly(NullResultError.class);
  }

  @Test
  public void null_can_be_returned_when_function_reported_errors() throws Exception {
    ImmutableList<Param> params = ImmutableList.of();
    Signature signature = new Signature(FILE, name("name"), params);
    function1 = new NativeFunction(signature, invoker, true);
    nativeCallTask = new NativeCallTask(function1, Empty.stringTaskResultMap(), codeLocation);
    when(invoker.invoke(pluginApi, Empty.stringValueMap())).thenAnswer(new Answer<SFile>() {
      @Override
      public SFile answer(InvocationOnMock invocation) throws Throwable {
        PluginApi pluginApi = (PluginApi) invocation.getArguments()[0];
        pluginApi.log(new CodeMessage(ERROR, new FakeCodeLocation(), "message"));
        return null;
      }
    });

    nativeCallTask.execute(pluginApi);

    pluginApi.loggedMessages().assertContainsOnly(CodeMessage.class);
  }

  @Test
  public void reflexive_internal_error_is_reported_for_illegal_access_exception() throws Exception {
    assertExceptionIsReportedAsProblem(new IllegalAccessException(""), ReflexiveInternalError.class);
  }

  @Test
  public void file_system_error_is_reported_for_file_system_exception() throws Exception {
    InvocationTargetException exception =
        new InvocationTargetException(new FileSystemException(""));
    assertExceptionIsReportedAsProblem(exception, FileSystemError.class);
  }

  @Test
  public void message_thrown_as_error_message_exception_is_reported() throws Exception {
    InvocationTargetException exception =
        new InvocationTargetException(new ErrorMessageException(new MyError()));
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
    when(invoker.invoke(pluginApi, Empty.stringValueMap())).thenThrow(thrown);

    nativeCallTask.execute(pluginApi);

    pluginApi.loggedMessages().assertContainsOnly(expected);
  }
}
