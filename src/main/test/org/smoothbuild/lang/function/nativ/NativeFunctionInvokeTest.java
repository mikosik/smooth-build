package org.smoothbuild.lang.function.nativ;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.isA;
import static org.smoothbuild.lang.function.nativ.TestingUtils.function;
import static org.smoothbuild.lang.message.MessageType.ERROR;
import static org.smoothbuild.lang.message.MessageType.WARNING;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;
import org.smoothbuild.lang.function.nativ.err.JavaInvocationError;
import org.smoothbuild.lang.function.nativ.err.NullResultError;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.exec.ContainerImpl;
import org.smoothbuild.util.Empty;

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
    when(function).invoke(container, Empty.valueList());
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
    public static SString stringIdentity(Container container, @Name("string") SString string) {
      return string;
    }
  }

  @Test
  public void error_reported_by_java_method_is_logged() throws Exception {
    given(container = containerImpl());
    given(function = function(ErrorReporting.class));
    when(function).invoke(container, Empty.valueList());
    then(container.messages(), contains(isA(MyError.class)));
  }

  public static class ErrorReporting {
    @SmoothFunction
    public static SString errorReporting(Container container) {
      container.log(new MyError());
      return null;
    }
  }

  public static class MyError extends Message {
    public MyError() {
      super(ERROR, "");
    }
  }

  @Test
  public void invoke_logs_error_when_java_method_returns_null_without_logging_error()
      throws Exception {
    given(container = containerImpl());
    given(function = function(NullReturning.class));
    when(function).invoke(container, Empty.valueList());
    then(container.messages(), contains(isA(NullResultError.class)));
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
    when(function).invoke(container, Empty.valueList());
    then(container.messages(), contains(instanceOf(MyWarning.class), instanceOf(
        NullResultError.class)));
  }

  public static class WarningReporting {
    @SmoothFunction
    public static SString warningReporting(Container container) {
      container.log(new MyWarning());
      return null;
    }
  }

  public static class MyWarning extends Message {
    public MyWarning() {
      super(WARNING, "");
    }
  }

  @Test
  public void invoke_wraps_illegal_access_exception_into_java_invocation_error() throws Exception {
    given(container = containerImpl());
    given(function = function(NormalFunction.class));
    given(method = PrivateMethod.class.getDeclaredMethods()[0]);
    given(function = new NativeFunction(method, function.signature(), true, HashCode.fromInt(13)));
    when(function).invoke(container, Empty.valueList());
    then(container.messages(), contains(instanceOf(JavaInvocationError.class)));
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
  public void invoke_wraps_normal_java_exception_into_java_invocation_error() throws Exception {
    given(container = containerImpl());
    given(function = function(ThrowRuntimeExceptiton.class));
    when(function).invoke(container, Empty.valueList());
    then(container.messages(), contains(instanceOf(JavaInvocationError.class)));
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
    when(function).invoke(container, Empty.valueList());
    then(container.messages(), contains(instanceOf(MyMessage.class)));
  }

  public static class ThrowMessage {
    @SmoothFunction
    public static SString throwMessage(Container container) {
      throw new MyMessage();
    }
  }

  public static class MyMessage extends Message {
    public MyMessage() {
      super(ERROR, "");
    }
  }
}
