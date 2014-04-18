package org.smoothbuild.lang.function.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.base.FakeResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CallHasherTest {
  String name1 = "name1";
  String name2 = "name2";
  String string1 = "abc";
  String string2 = "def";

  FakeObjectsDb objectsDb = new FakeObjectsDb();
  Result<SString> value1 = new FakeResult<SString>(objectsDb.string(string1));
  Result<SString> value2 = new FakeResult<SString>(objectsDb.string(string2));
  Result<SString> valueLong = new FakeResult<SString>(objectsDb.string(string1 + name2 + string2));

  Param param1 = param(STRING, name1, false);
  Param param2 = param(STRING, name2, false);

  ImmutableList<Param> params = ImmutableList.of(param1, param2);
  NativeFunction<SString> function = createFunction(params, "func1");
  NativeFunction<SString> function2 = createFunction(params, "func2");

  Map<String, ? extends Result<?>> arguments;
  Map<String, ? extends Result<?>> arguments2;

  CallHasher<?> callHasher;

  @Test
  public void hash_of_given_call_is_always_the_same() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(new CallHasher<>(function, arguments).hash());
    thenReturned(new CallHasher<>(function, arguments).hash());
  }

  @Test
  public void hash_of_function_call_with_different_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    given(arguments2 = ImmutableMap.of(param1.name(), value1, param2.name(), value2));
    when(new CallHasher<>(function, arguments).hash());
    thenReturned(not(new CallHasher<>(function, arguments2).hash()));
  }

  @Test
  public void hash_of_function_with_two_args_is_different_from_hash_of_function_with_one_which_value_contains_second_param_name_and_its_value() {
    given(arguments = ImmutableMap.of(param1.name(), valueLong));
    given(arguments2 = ImmutableMap.of(param1.name(), value1, param2.name(), value2));
    when(new CallHasher<>(function, arguments).hash());
    thenReturned(not(new CallHasher<>(function, arguments2).hash()));
  }

  @Test
  public void hash_of_different_function_call_with_the_same_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), value1));
    when(new CallHasher<>(function, arguments).hash());
    thenReturned(not(new CallHasher<>(function2, arguments).hash()));
  }

  // helpers

  private static NativeFunction<SString> createFunction(Iterable<Param> params, String name) {
    @SuppressWarnings("unchecked")
    NativeFunction<SString> function = mock(NativeFunction.class);
    Signature<SString> signature = new Signature<>(STRING, name(name), params);
    given(willReturn(signature), function).signature();
    given(willReturn(signature.params()), function).params();
    return function;
  }
}
