package org.smoothbuild.base;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Map;

import javax.inject.Singleton;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.lang.builtin.Builtin;
import org.smoothbuild.lang.builtin.BuiltinFunctions;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.ModuleBuilder;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.NativeFunctionFactory;
import org.smoothbuild.lang.plugin.PluginApi;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.integration.IntegrationTestCase;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;

public class ErrorStopsBuildingSmoothTest extends IntegrationTestCase {
  String normalFunction = "myFunction";
  String erroneousFunction = "erroneous";
  Invoker normalInvoker = mock(Invoker.class);
  Invoker erroneousInvoker = mock(Invoker.class);

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws Exception {
    Mockito.when(
        normalInvoker.invoke(Matchers.<PluginApi> any(), Matchers.<Map<String, SValue>> any()))
        .thenAnswer(new Answer<SString>() {
          @Override
          public SString answer(InvocationOnMock invocation) throws Throwable {
            PluginApi pluginApi = (PluginApi) invocation.getArguments()[0];
            return pluginApi.string("abc");
          }
        });
    builder.addFunction(function(normalFunction, normalInvoker));

    Mockito.when(
        erroneousInvoker.invoke(Matchers.<PluginApi> any(), Matchers.<Map<String, SValue>> any()))
        .thenAnswer(new Answer<SString>() {
          @Override
          public SString answer(InvocationOnMock invocation) throws Throwable {
            PluginApi pluginApi = (PluginApi) invocation.getArguments()[0];
            pluginApi.log(new Message(ERROR, "message"));
            return null;
          }
        });
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
    verifyZeroInteractions(normalInvoker);
  }

  private static NativeFunction function(String name, Invoker invoker) {
    ImmutableList<Param> params = ImmutableList.of(param(STRING, "value"));
    Signature signature = new Signature(STRING, name(name), params);
    return new NativeFunction(signature, invoker, true);
  }
}
