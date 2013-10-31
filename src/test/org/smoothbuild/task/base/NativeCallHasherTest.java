package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.testing.plugin.FakeString;
import org.smoothbuild.testing.task.base.FakeResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class NativeCallHasherTest {
  Result value1 = new FakeResult(new FakeString("abc"));
  Result value2 = new FakeResult(new FakeString("def"));

  Param param1 = param(STRING, "name1", false);
  Param param2 = param(STRING, "name2", false);

  ImmutableList<Param> params = ImmutableList.of(param1, param2);
  NativeFunction function = createFunction(params, "func1");
  NativeFunction function2 = createFunction(params, "func2");

  ImmutableMap<String, Result> arguments;
  ImmutableMap<String, Result> arguments2;

  NativeCallHasher nativeCallHasher;

  @Test
  public void hash_of_given_call_is_always_the_same() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(new NativeCallHasher(function, arguments).hash());
    thenReturned(new NativeCallHasher(function, arguments).hash());
  }

  @Test
  public void hash_of_function_call_with_different_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    given(arguments2 = ImmutableMap.of(param1.name(), value1, param2.name(), value2));
    when(new NativeCallHasher(function, arguments).hash());
    thenReturned(not(new NativeCallHasher(function, arguments2).hash()));
  }

  @Test
  public void hash_of_different_function_call_with_the_same_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(new NativeCallHasher(function, arguments).hash());
    thenReturned(not(new NativeCallHasher(function2, arguments).hash()));
  }

  // helpers

  private static NativeFunction createFunction(Iterable<Param> params, String name) {
    NativeFunction function = mock(NativeFunction.class);
    Signature signature = new Signature(STRING, name(name), params);
    Mockito.when(function.signature()).thenReturn(signature);
    Mockito.when(function.params()).thenReturn(signature.params());
    return function;
  }
}
