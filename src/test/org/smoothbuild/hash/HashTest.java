package org.smoothbuild.hash;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.function.base.Name;

public class HashTest {
  private Name name;
  private Name name2;

  @Test
  public void hash_of_given_native_function_is_always_the_same() {
    given(name = Name.qualifiedName("abc.def.Function"));
    when(Hash.nativeFunction(name));
    thenReturned(Hash.nativeFunction(name));
  }

  @Test
  public void hashes_of_different_strings_are_different() {
    given(name = Name.qualifiedName("abc.def.Function"));
    given(name2 = Name.qualifiedName("abc.def.Function2"));
    when(Hash.nativeFunction(name));
    thenReturned(not(Hash.nativeFunction(name2)));
  }
}
