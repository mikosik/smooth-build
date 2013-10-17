package org.smoothbuild.hash;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.function.base.Name;

public class HashTest {
  String string = "some string";
  String string2 = "some other string";

  Name name;
  Name name2;

  // Hash.nativeFunction()

  @Test
  public void hash_of_given_native_function_is_always_the_same() {
    given(name = Name.qualifiedName("abc.def.Function"));
    when(Hash.nativeFunction(name));
    thenReturned(Hash.nativeFunction(name));
  }

  @Test
  public void hashes_of_different_native_functions_are_different() {
    given(name = Name.qualifiedName("abc.def.Function"));
    given(name2 = Name.qualifiedName("abc.def.Function2"));
    when(Hash.nativeFunction(name));
    thenReturned(not(Hash.nativeFunction(name2)));
  }

  // Hash.string()

  @Test
  public void hash_of_given_string_is_always_the_same() {
    when(Hash.string(string));
    thenReturned(Hash.string(string));
  }

  @Test
  public void hashes_of_different_strings_are_different() {
    when(Hash.string(string));
    thenReturned(not(Hash.string(string2)));
  }

  // Hash.bytes()

  @Test
  public void hash_of_given_bytes_is_always_the_same() {
    when(Hash.bytes(string.getBytes()));
    thenReturned(Hash.bytes(string.getBytes()));
  }

  @Test
  public void hashes_of_different_bytes_are_different() {
    when(Hash.bytes(string.getBytes()));
    thenReturned(not(Hash.bytes(string2.getBytes())));
  }
}
