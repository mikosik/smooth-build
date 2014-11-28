package org.smoothbuild.lang.function.nativ;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

public class NativeFunctionTest {
  private Signature<SString> signature = signature("name");
  @SuppressWarnings("unchecked")
  private final Invoker<SString> invoker = mock(Invoker.class);

  private NativeFunction<?> function;
  private ImmutableMap<String, Value> args;
  private HashCode jarHash;
  private NativeFunction<SString> function2;

  @Test(expected = NullPointerException.class)
  public void null_signature_is_forbidden() throws Exception {
    new NativeFunction<>(Hash.integer(33), null, invoker, true);
  }

  @Test(expected = NullPointerException.class)
  public void null_invoker_is_forbidden() throws Exception {
    new NativeFunction<>(Hash.integer(33), signature, null, true);
  }

  @Test
  public void functions_with_same_name_in_the_same_jar_have_same_hash() throws Exception {
    given(jarHash = Hash.integer(33));
    given(function = new NativeFunction<>(jarHash, signature, invoker, false));
    given(function2 = new NativeFunction<>(jarHash, signature, invoker, false));
    when(function).hash();
    thenReturned(function2.hash());
  }

  @Test
  public void functions_with_different_names_in_the_same_jar_have_different_hash() throws Exception {
    given(jarHash = Hash.integer(33));
    given(function = new NativeFunction<>(jarHash, signature("name"), invoker, false));
    given(function2 = new NativeFunction<>(jarHash, signature("name2"), invoker, false));
    when(function).hash();
    thenReturned(not(function2.hash()));
  }

  @Test
  public void functions_with_same_names_in_different_jars_have_different_hash() throws Exception {
    given(signature = signature("name"));
    given(function = new NativeFunction<>(Hash.integer(33), signature, invoker, false));
    given(function2 = new NativeFunction<>(Hash.integer(44), signature, invoker, false));
    when(function).hash();
    thenReturned(not(function2.hash()));
  }

  private static Signature<SString> signature(String name) {
    return new Signature<>(STRING, name(name), Empty.paramList());
  }
}
