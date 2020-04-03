package org.smoothbuild.exec.comp;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.comp.AlgorithmHashes.accessorCallAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.arrayAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.constructorCallAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.nativeCallAlgorithmHash;
import static org.smoothbuild.util.Lists.list;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.testing.TestingContext;

public class AlgorithmHashesTest extends TestingContext {
  @Test
  public void each_algorithm_has_different_hash() {
    Set<Hash> hashes = new HashSet<>();
    NativeFunction function = nativeFunctionWithHash(Hash.of(0));
    Constructor constructor = constructor("MyStruct2");
    Accessor accessor = accessor("myField");

    hashes.add(arrayAlgorithmHash());
    hashes.add(nativeCallAlgorithmHash(function));
    hashes.add(convertAlgorithmHash(stringType()));
    hashes.add(constructorCallAlgorithmHash(constructor));
    hashes.add(accessorCallAlgorithmHash(accessor));

    assertThat(hashes.size())
        .isEqualTo(5);
  }

  @Test
  public void native_call_algorithm_has_different_hash_for_different_functions() {
    NativeFunction function = nativeFunctionWithHash(Hash.of(1));
    NativeFunction function2 = nativeFunctionWithHash(Hash.of(2));

    assertThat(nativeCallAlgorithmHash(function))
        .isNotEqualTo(nativeCallAlgorithmHash(function2));
  }

  @Test
  public void convert_algorithm_has_different_hash_for_different_types() {
    assertThat(convertAlgorithmHash(stringType()))
        .isNotEqualTo(convertAlgorithmHash(blobType()));
  }

  @Test
  public void constructor_call_algorithm_has_different_hash_for_different_types() {
    Constructor constructor = constructor("MyStruct1");
    Constructor constructor2 = constructor("MyStruct2");

    assertThat(constructorCallAlgorithmHash(constructor))
        .isNotEqualTo(constructorCallAlgorithmHash(constructor2));
  }

  @Test
  public void accessor_call_algorithm_has_different_hash_for_different_types() {
    Accessor accessor = accessor("myField");
    Accessor accessor2 = accessor("myField2");

    assertThat(accessorCallAlgorithmHash(accessor))
        .isNotEqualTo(accessorCallAlgorithmHash(accessor2));
  }

  private static Accessor accessor(String fieldName) {
    Accessor accessor = mock(Accessor.class);
    when(accessor.fieldName()).thenReturn(fieldName);
    return accessor;
  }

  private static NativeFunction nativeFunctionWithHash(Hash hash) {
    NativeFunction function = mock(NativeFunction.class);
    when(function.hash()).thenReturn(hash);
    return function;
  }

  private Constructor constructor(String typeName) {
    Constructor constructor = mock(Constructor.class);
    when(constructor.type()).thenReturn(structType(typeName, list()));
    return constructor;
  }
}
