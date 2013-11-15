package org.smoothbuild.base;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Type.FILE;
import static org.smoothbuild.lang.function.base.Type.STRING;

import java.util.Map;

import javax.inject.Singleton;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.Builtin;
import org.smoothbuild.lang.builtin.BuiltinFunctions;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.ModuleBuilder;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.function.nativ.NativeFunctionFactory;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.lang.function.value.Value;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.testing.integration.IntegrationTestCase;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;

public class CacheSmoothTest extends IntegrationTestCase {
  String name = "myFunction";
  Invoker invoker = mock(Invoker.class);

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws Exception {
    Mockito.when(invoker.invoke(Matchers.<Sandbox> any(), Matchers.<Map<String, Value>> any()))
        .thenAnswer(new Answer<StringValue>() {
          @Override
          public StringValue answer(InvocationOnMock invocation) throws Throwable {
            Sandbox sandbox = (Sandbox) invocation.getArguments()[0];
            return sandbox.string("abc");
          }
        });
    builder.addFunction(function(name, invoker));

    for (Class<?> klass : BuiltinFunctions.BUILTIN_FUNCTION_CLASSES) {
      builder.addFunction(NativeFunctionFactory.create(klass, true));
    }
    return builder.build();
  }

  @Test
  public void result_from_cacheable_function_is_cached() throws Exception {
    // given
    script("run: " + name + " ;");

    // when
    build("run");
    build("run");

    // then
    userConsole.assertNoProblems();
    verify(invoker, times(1)).invoke(Matchers.<Sandbox> any(), Matchers.<Map<String, Value>> any());
  }

  @Test
  public void cache_is_not_used_when_argument_changed() throws Exception {
    // given
    Path path = path("my/file.txt");
    fileSystem.createFile(path, "abc");
    script("run: file(" + path + ") | " + name + " ;");

    // when
    build("run");
    fileSystem.createFile(path, "def");
    build("run");

    // then
    userConsole.assertNoProblems();
    verify(invoker, times(2)).invoke(Matchers.<Sandbox> any(), Matchers.<Map<String, Value>> any());
  }

  private static NativeFunction function(String name, Invoker invoker) {
    ImmutableList<Param> params = ImmutableList.of(param(FILE, "file"));
    Signature signature = new Signature(STRING, name(name), params);
    return new NativeFunction(signature, invoker, true);
  }
}
