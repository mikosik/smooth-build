package org.smoothbuild.function.nativ;

import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.testing.plugin.FakeString;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CallHashingTest {
  Value value1 = new FakeString("abc");
  Value value2 = new FakeString("def");

  Param param1 = param(STRING, "name1", false);
  Param param2 = param(STRING, "name2", false);

  ImmutableList<Param> params = ImmutableList.of(param1, param2);
  NativeFunction function = createFunction(params, "func1");
  NativeFunction function2 = createFunction(params, "func2");

  ImmutableMap<String, Value> arguments;
  ImmutableMap<String, Value> arguments2;

  // call

  @Test
  public void hash_of_given_call_is_always_the_same() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(CallHashing.hashCall(function, arguments));
    thenReturned(CallHashing.hashCall(function, arguments));
  }

  @Test
  public void hash_of_function_call_with_different_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    given(arguments2 = ImmutableMap.of(param1.name(), value1, param2.name(), value2));
    when(CallHashing.hashCall(function, arguments));
    thenReturned(not(CallHashing.hashCall(function, arguments2)));
  }

  @Test
  public void hash_of_different_function_call_with_the_same_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(CallHashing.hashCall(function, arguments));
    thenReturned(not(CallHashing.hashCall(function2, arguments)));
  }

  // helpers

  private static NativeFunction createFunction(Iterable<Param> params, String name) {
    NativeFunction function = mock(NativeFunction.class);
    Signature signature = new Signature(STRING, simpleName(name), params);
    Mockito.when(function.signature()).thenReturn(signature);
    Mockito.when(function.params()).thenReturn(signature.params());
    return function;
  }
}
