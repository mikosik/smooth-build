package org.smoothbuild.exec.comp;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.comp.AlgorithmHashes.accessorCallAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.arrayAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.constructorCallAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.nativeCallAlgorithmHash;
import static org.smoothbuild.exec.comp.AlgorithmHashes.stringLiteralAlgorithmHash;
import static org.smoothbuild.util.Lists.list;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.testing.TestingContext;

public class AlgorithmHashesTest extends TestingContext {
  @Test
  public void each_algorithm_has_different_hash() {
    Set<Hash> hashes = new HashSet<>();
    NativeFunction function = nativeFunctionWithHash(Hash.of(0));
    StructType constructedType = constructedType("MyStruct2");
    Accessor accessor = accessor(0);

    hashes.add(arrayAlgorithmHash());
    hashes.add(nativeCallAlgorithmHash(function));
    hashes.add(convertAlgorithmHash(stringType()));
    hashes.add(constructorCallAlgorithmHash(constructedType));
    hashes.add(accessorCallAlgorithmHash(accessor));
    hashes.add(stringLiteralAlgorithmHash("abc"));

    assertThat(hashes.size())
        .isEqualTo(6);
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
    StructType constructedType = constructedType("MyStruct1");
    StructType constructedType2 = constructedType("MyStruct2");

    assertThat(constructorCallAlgorithmHash(constructedType))
        .isNotEqualTo(constructorCallAlgorithmHash(constructedType2));
  }

  @Test
  public void accessor_call_algorithm_has_different_hash_for_different_field_indexes() {
    Accessor accessor = accessor(0);
    Accessor accessor2 = accessor(1);

    assertThat(accessorCallAlgorithmHash(accessor))
        .isNotEqualTo(accessorCallAlgorithmHash(accessor2));
  }

  @Test
  public void string_literal_algorithm_has_different_hash_for_different_strings() {
    assertThat(stringLiteralAlgorithmHash("abc"))
        .isNotEqualTo(stringLiteralAlgorithmHash("def"));
  }

  private static Accessor accessor(int index) {
    Accessor accessor = mock(Accessor.class);
    when(accessor.fieldIndex()).thenReturn(index);
    return accessor;
  }

  private static NativeFunction nativeFunctionWithHash(Hash hash) {
    NativeFunction function = mock(NativeFunction.class);
    when(function.hash()).thenReturn(hash);
    return function;
  }

  private StructType constructedType(String typeName) {
    return structType(typeName, list());
  }
}
