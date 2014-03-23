package org.smoothbuild.task.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.testing.lang.function.base.FakeSignature.fakeSignature;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.exc.FileSystemError;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakePluginApi;
import org.smoothbuild.util.Empty;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

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
    given(willReturn(result), invoker).invoke(pluginApi,
        ImmutableMap.<String, SValue> of(name, argValue));

    assertThat(nativeCallTask.execute(pluginApi)).isSameAs(result);
  }

  @Test
  public void null_result_is_logged_when_functio_has_non_void_return_type() throws Exception {
    given(willReturn(null), invoker).invoke(pluginApi, Empty.stringValueMap());

    nativeCallTask.execute(pluginApi);

    pluginApi.loggedMessages().assertContainsOnly(NullResultError.class);
  }

  @Test
  public void null_can_be_returned_when_function_logged_errors() throws Exception {
    ImmutableList<Param> params = ImmutableList.of();
    Signature signature = new Signature(FILE, name("name"), params);
    function1 = new NativeFunction(signature, invoker, true);
    nativeCallTask = new NativeCallTask(function1, Empty.stringTaskResultMap(), codeLocation);
    given(new Handler() {
      @Override
      public Object handle(Invocation invocation) throws Throwable {
        PluginApi pluginApi = (PluginApi) invocation.arguments.get(0);
        pluginApi.log(new CodeMessage(ERROR, new FakeCodeLocation(), "message"));
        return null;
      }
    }, invoker).invoke(pluginApi, Empty.stringValueMap());

    nativeCallTask.execute(pluginApi);

    pluginApi.loggedMessages().assertContainsOnly(CodeMessage.class);
  }

  @Test
  public void reflexive_internal_error_is_logged_for_illegal_access_exception() throws Exception {
    assertExceptionIsLoggedAsProblem(new IllegalAccessException(""), ReflexiveInternalError.class);
  }

  @Test
  public void file_system_error_is_logged_for_file_system_exception() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new FileSystemError(""));
    assertExceptionIsLoggedAsProblem(exception, FileSystemError.class);
  }

  @Test
  public void message_thrown_as_error_message_exception_is_logged() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new MyError());
    assertExceptionIsLoggedAsProblem(exception, MyError.class);
  }

  @Test
  public void unexpected_error_is_logged_for_unexpected_runtime_exception() throws Exception {
    InvocationTargetException exception = new InvocationTargetException(new RuntimeException());
    assertExceptionIsLoggedAsProblem(exception, UnexpectedError.class);
  }

  @SuppressWarnings("serial")
  private static class MyError extends Message {
    public MyError() {
      super(ERROR, "message");
    }
  }

  private void assertExceptionIsLoggedAsProblem(Throwable thrown, Class<? extends Message> expected)
      throws Exception {
    given(willThrow(thrown), invoker).invoke(pluginApi, Empty.stringValueMap());

    nativeCallTask.execute(pluginApi);

    pluginApi.loggedMessages().assertContainsOnly(expected);
  }
}
