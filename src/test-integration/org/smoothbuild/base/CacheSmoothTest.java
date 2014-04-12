package org.smoothbuild.base;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenCalledTimes;

import java.util.Map;

import javax.inject.Singleton;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
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
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;

@SuppressWarnings("unchecked")
public class CacheSmoothTest extends IntegrationTestCase {
  String name = "myFunction";
  Invoker<SString> invoker = mock(Invoker.class);

  @Provides
  @Singleton
  @Builtin
  public Module provideBuiltinModule(ModuleBuilder builder) throws Exception {
    given(new Handler() {
      @Override
      public Object handle(Invocation invocation) throws Throwable {
        NativeApi nativeApi = (NativeApi) invocation.arguments.get(0);
        return nativeApi.string("abc");
      }
    }, invoker).invoke(any(NativeApi.class), any(Map.class));
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
    userConsole.messages().assertNoProblems();
    thenCalledTimes(1, invoker).invoke(any(NativeApi.class), any(Map.class));
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
    userConsole.messages().assertNoProblems();
    thenCalledTimes(2, invoker).invoke(any(NativeApi.class), any(Map.class));
  }

  private static NativeFunction<SString> function(String name, Invoker<SString> invoker) {
    ImmutableList<Param> params = ImmutableList.of(param(FILE, "file"));
    Signature<SString> signature = new Signature<>(STRING, name(name), params);
    return new NativeFunction<>(signature, invoker, true);
  }
}
