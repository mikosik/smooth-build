package org.smoothbuild.hash;

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
import org.smoothbuild.function.nativ.NativeFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class HashTaskTest {
  String string = "abc";
  String otherString = "def";

  HashCode hash1 = HashCode.fromInt(1);
  HashCode hash2 = HashCode.fromInt(2);

  Param param1 = param(STRING, "name1", false);
  Param param2 = param(STRING, "name2", false);

  ImmutableList<Param> params = ImmutableList.of(param1, param2);
  NativeFunction function = createFunction(params, "func1");
  NativeFunction function2 = createFunction(params, "func2");

  ImmutableMap<String, HashCode> arguments;
  ImmutableMap<String, HashCode> arguments2;

  // string

  @Test
  public void hash_of_given_string_is_always_the_same() {
    when(HashTask.string(string));
    thenReturned(HashTask.string(string));
  }

  @Test
  public void hashes_of_different_strings_are_different() {
    when(HashTask.string(string));
    thenReturned(not(HashTask.string(otherString)));
  }

  // empty set

  @Test
  public void hash_of_empty_set_is_always_the_same() {
    when(HashTask.emptySet());
    thenReturned(HashTask.emptySet());
  }

  // file set

  @Test
  public void hash_of_given_file_set_is_always_the_same() {
    when(HashTask.fileSet(ImmutableList.of(hash1)));
    thenReturned(HashTask.fileSet(ImmutableList.of(hash1)));
  }

  @Test
  public void hashes_of_different_file_sets_are_different() {
    when(HashTask.fileSet(ImmutableList.of(hash1)));
    thenReturned(not(HashTask.fileSet(ImmutableList.of(hash2))));
  }

  @Test
  public void hash_of_bigger_file_set_is_different() {
    when(HashTask.fileSet(ImmutableList.of(hash1)));
    thenReturned(not(HashTask.fileSet(ImmutableList.of(hash1, hash2))));
  }

  @Test
  public void hash_of_file_set_is_different_than_its_only_element() {
    when(HashTask.fileSet(ImmutableList.of(hash1)));
    thenReturned(not(hash1));
  }

  // string set

  @Test
  public void hash_of_given_string_set_is_always_the_same() {
    when(HashTask.stringSet(ImmutableList.of(hash1)));
    thenReturned(HashTask.stringSet(ImmutableList.of(hash1)));
  }

  @Test
  public void hashes_of_different_string_sets_are_different() {
    when(HashTask.stringSet(ImmutableList.of(hash1)));
    thenReturned(not(HashTask.stringSet(ImmutableList.of(hash2))));
  }

  @Test
  public void hash_of_bigger_string_set_is_different() {
    when(HashTask.stringSet(ImmutableList.of(hash1)));
    thenReturned(not(HashTask.stringSet(ImmutableList.of(hash1, hash2))));
  }

  @Test
  public void hash_of_string_set_is_different_than_its_only_element() {
    when(HashTask.stringSet(ImmutableList.of(hash1)));
    thenReturned(not(hash1));
  }

  // string set vs file set

  @Test
  public void hash_of_file_set_is_different_from_hash_of_string_set() {
    when(HashTask.fileSet(ImmutableList.of(hash1)));
    thenReturned(not(HashTask.stringSet(ImmutableList.of(hash1, hash2))));
  }

  // call

  @Test
  public void hash_of_given_call_is_always_the_same() {
    given(arguments = ImmutableMap.of(param1.name(), hash1));
    when(HashTask.call(function, arguments));
    thenReturned(HashTask.call(function, arguments));
  }

  @Test
  public void hash_of_function_call_with_different_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), hash1));
    given(arguments2 = ImmutableMap.of(param1.name(), hash1, param2.name(), hash2));
    when(HashTask.call(function, arguments));
    thenReturned(not(HashTask.call(function, arguments2)));
  }

  @Test
  public void hash_of_different_function_call_with_the_same_arguments_is_different() {
    given(arguments = ImmutableMap.of(param1.name(), hash1));
    when(HashTask.call(function, arguments));
    thenReturned(not(HashTask.call(function2, arguments)));
  }

  // mixed

  @Test
  public void hash_of_function_is_different_than_hash_of_its_name_as_string() {
    given(arguments = ImmutableMap.of());
    when(HashTask.call(function, arguments));
    thenReturned(not(HashTask.string(function.signature().name().simple())));
  }

  @Test
  public void hash_of_function_is_different_than_hash_of_its_only_argument() {
    given(arguments = ImmutableMap.of(param1.name(), hash1));
    when(HashTask.call(function, arguments));
    thenReturned(not(hash1));
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
