package org.smoothbuild.lang.function.base;

import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.testing.lang.type.FakeString;
import org.smoothbuild.testing.task.base.FakeResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CallHasherTest {
  String name1 = "name1";
  String name2 = "name2";
  String string1 = "abc";
  String string2 = "def";

  Result value1 = new FakeResult(new FakeString(string1));
  Result value2 = new FakeResult(new FakeString(string2));
  Result valueLong = new FakeResult(new FakeString(string1 + name2 + string2));

  Param param1 = param(STRING, name1, false);
  Param param2 = param(STRING, name2, false);

  ImmutableList<Param> params = ImmutableList.of(param1, param2);
  Function function = createFunction(params, "func1");
  Function function2 = createFunction(params, "func2");

  ImmutableMap<String, Result> arguments;
  ImmutableMap<String, Result> arguments2;

  CallHasher callHasher;

  @Test
  public void hash_of_given_call_is_always_the_same() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(new CallHasher(function, arguments).hash());
    thenReturned(new CallHasher(function, arguments).hash());
  }

  @Test
  public void hash_of_function_call_with_different_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    given(arguments2 = ImmutableMap.of(param1.name(), value1, param2.name(), value2));
    when(new CallHasher(function, arguments).hash());
    thenReturned(not(new CallHasher(function, arguments2).hash()));
  }

  @Test
  public void hash_of_function_with_two_args_is_different_from_hash_of_function_with_one_which_value_contains_second_param_name_and_its_value() {
    given(arguments = ImmutableMap.of(param1.name(), valueLong));
    given(arguments2 = ImmutableMap.of(param1.name(), value1, param2.name(), value2));
    when(new CallHasher(function, arguments).hash());
    thenReturned(not(new CallHasher(function, arguments2).hash()));
  }

  @Test
  public void hash_of_different_function_call_with_the_same_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(new CallHasher(function, arguments).hash());
    thenReturned(not(new CallHasher(function2, arguments).hash()));
  }

  // helpers

  private static Function createFunction(Iterable<Param> params, String name) {
    Function function = mock(Function.class);
    Signature signature = new Signature(STRING, name(name), params);
    Mockito.when(function.signature()).thenReturn(signature);
    Mockito.when(function.params()).thenReturn(signature.params());
    return function;
  }
}
