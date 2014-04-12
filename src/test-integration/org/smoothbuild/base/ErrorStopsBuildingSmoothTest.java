package org.smoothbuild.base;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalledTimes;

import java.util.Map;

import javax.inject.Singleton;

import org.junit.Test;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.builtin.Builtin;
import org.smoothbuild.lang.builtin.BuiltinFunctions;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.ModuleBuilder;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.NativeFunctionFactory;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.testory.common.Nullable;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;

public class ErrorStopsBuildingSmoothTest extends IntegrationTestCase {
  String normalFunction = "myFunction";
  String erroneousFunction = "erroneous";
  @SuppressWarnings("unchecked")
  Invoker<SString> normalInvoker = mock(Invoker.class);
  @SuppressWarnings("unchecked")
  Invoker<SString> erroneousInvoker = mock(Invoker.class);

  @SuppressWarnings("unchecked")
  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws Exception {
    given(new Handler() {
      @Override
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        NativeApi nativeApi = (NativeApi) invocation.arguments.get(0);
        return nativeApi.string("abc");
      }
    }, normalInvoker).invoke(any(NativeApi.class), any(Map.class));

    builder.addFunction(function(normalFunction, normalInvoker));

    given(new Handler() {
      @Override
      @Nullable
      public Object handle(Invocation invocation) throws Throwable {
        NativeApi nativeApi = (NativeApi) invocation.arguments.get(0);
        nativeApi.log(new Message(ERROR, "message"));
        return null;
      }
    }, erroneousInvoker).invoke(any(NativeApi.class), any(Map.class));

    builder.addFunction(function(erroneousFunction, erroneousInvoker));

    for (Class<?> klass : BuiltinFunctions.BUILTIN_FUNCTION_CLASSES) {
      builder.addFunction(NativeFunctionFactory.create(klass, true));
    }
    return builder.build();
  }

  @Test
  public void error_stops_building() throws Exception {
    // given
    script("run: " + erroneousFunction + " | " + normalFunction + " ;");

    // when
    build("run");

    // then
    userConsole.messages().assertContainsOnly(Message.class);
    thenCalledTimes(0, onInstance(normalInvoker));
  }

  private static NativeFunction<SString> function(String name, Invoker<SString> invoker) {
    ImmutableList<Param> params = ImmutableList.of(param(STRING, "value"));
    Signature<SString> signature = new Signature<>(STRING, name(name), params);
    return new NativeFunction<>(signature, invoker, true);
  }
}
