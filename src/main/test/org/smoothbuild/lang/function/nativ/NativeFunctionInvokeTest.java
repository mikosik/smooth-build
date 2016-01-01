package org.smoothbuild.lang.function.nativ;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class NativeFunctionInvokeTest {
  private NativeFunction function;
  private ContainerImpl container;
  private SString string;
  private Method method;

  @Test
  public void invoke_returns_result_from_invokation_of_native_java_method() throws Exception {
    given(container = containerImpl());
    given(function = function(StringFunction.class));
    when(function).invoke(container, asList());
    thenReturned(container.create().string("abc"));
  }

  public static class StringFunction {
    @SmoothFunction
    public static SString stringFunction(Container container) {
      return container.create().string("abc");
    }
  }

  @Test
  public void invoke_passes_arguments_to_java_method() throws Exception {
    given(container = containerImpl());
    given(function = function(StringIdentity.class));
    given(string = container.create().string("abc"));
    when(function).invoke(container, Arrays.<Value> asList(string));
    thenReturned(container.create().string("abc"));
  }

  public static class StringIdentity {
    @SmoothFunction
    public static SString stringIdentity(Container container, SString string) {
      return string;
    }
  }

  @Test
  public void error_reported_by_java_method_is_logged() throws Exception {
    given(container = containerImpl());
    given(function = function(ErrorReporting.class));
    when(function).invoke(container, asList());
    then(container.messages(), contains(instanceOf(MyError.class)));
  }

  public static class ErrorReporting {
    @SmoothFunction
    public static SString errorReporting(Container container) {
      container.log(new MyError());
      return null;
    }
  }

  public static class MyError extends ErrorMessage {
    public MyError() {
      super("");
    }
  }

  @Test
  public void invoke_logs_error_when_java_method_returns_null_without_logging_error()
      throws Exception {
    given(container = containerImpl());
    given(function = function(NullReturning.class));
    when(function).invoke(container, asList());
    then(container.messages(), contains(instanceOf(Message.class)));
  }

  public static class NullReturning {
    @SmoothFunction
    public static SString nullReturning(Container container) {
      return null;
    }
  }

  @Test
  public void invoke_logs_error_when_java_method_returns_null_and_logs_only_warning()
      throws Exception {
    given(container = containerImpl());
    given(function = function(WarningReporting.class));
    when(function).invoke(container, asList());
    then(container.messages(), contains(instanceOf(MyWarning.class), instanceOf(Message.class)));
  }

  public static class WarningReporting {
    @SmoothFunction
    public static SString warningReporting(Container container) {
      container.log(new MyWarning());
      return null;
    }
  }

  public static class MyWarning extends WarningMessage {
    public MyWarning() {
      super("");
    }
  }

  @Test
  public void invoke_rethrows_illegal_access_exception_wrapped_inside_runtimeException()
      throws Exception {
    given(container = containerImpl());
    given(function = function(NormalFunction.class));
    given(method = PrivateMethod.class.getDeclaredMethods()[0]);
    given(function = new NativeFunction(method, function.signature(), true, HashCode.fromInt(13)));
    when(function).invoke(container, asList());
    thenThrown(RuntimeException.class);
  }

  public static class NormalFunction {
    @SmoothFunction
    public static SString function(Container container) {
      return null;
    }
  }

  public static class PrivateMethod {
    @SuppressWarnings("unused")
    private static SString function(Container container) {
      return null;
    }
  }

  @Test
  public void invoke_rethrows_normal_exception_wrapped_inside_runtime_exception() throws Exception {
    given(container = containerImpl());
    given(function = function(ThrowNormalExceptiton.class));
    when(function).invoke(container, asList());
    thenThrown(RuntimeException.class);
  }

  public static class ThrowNormalExceptiton {
    @SmoothFunction
    public static SString throwNormalException(Container container) throws Exception {
      throw new Exception();
    }
  }

  @Test
  public void invoke_rethrows_runtime_exception_as_wrapped_inside_runtime_exception()
      throws Exception {
    given(container = containerImpl());
    given(function = function(ThrowRuntimeExceptiton.class));
    when(function).invoke(container, asList());
    thenThrown(RuntimeException.class);
  }

  public static class ThrowRuntimeExceptiton {
    @SmoothFunction
    public static SString throwRuntimeException(Container container) {
      throw new RuntimeException();
    }
  }

  @Test
  public void invoke_adds_thrown_messages_to_logged_messages() throws Exception {
    given(container = containerImpl());
    given(function = function(ThrowMessage.class));
    when(function).invoke(container, asList());
    then(container.messages(), contains(instanceOf(MyError.class)));
  }

  public static class ThrowMessage {
    @SmoothFunction
    public static SString throwMessage(Container container) {
      throw new MyError();
    }
  }
}
