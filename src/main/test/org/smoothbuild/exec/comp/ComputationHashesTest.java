package org.smoothbuild.exec.comp;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.comp.ComputationHashes.accessorCallComputationHash;
import static org.smoothbuild.exec.comp.ComputationHashes.arrayComputationHash;
import static org.smoothbuild.exec.comp.ComputationHashes.constructorCallComputationHash;
import static org.smoothbuild.exec.comp.ComputationHashes.convertComputationHash;
import static org.smoothbuild.exec.comp.ComputationHashes.identityComputationHash;
import static org.smoothbuild.exec.comp.ComputationHashes.nativeCallComputationHash;
import static org.smoothbuild.exec.comp.ComputationHashes.valueComputationHash;
import static org.smoothbuild.util.Lists.list;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.testing.TestingContext;

public class ComputationHashesTest extends TestingContext {
  @Test
  public void each_computation_has_different_hash() {
    Set<Hash> hashes = new HashSet<>();
    NativeFunction function = nativeFunctionWithHash(Hash.of(0));
    Constructor constructor = constructor("MyStruct2");
    Accessor accessor = accessor("myField");
    SObject object = object(Hash.of(0));

    hashes.add(valueComputationHash(object));
    hashes.add(arrayComputationHash());
    hashes.add(identityComputationHash());
    hashes.add(nativeCallComputationHash(function));
    hashes.add(convertComputationHash(stringType()));
    hashes.add(constructorCallComputationHash(constructor));
    hashes.add(accessorCallComputationHash(accessor));

    assertThat(hashes.size())
        .isEqualTo(7);
  }

  @Test
  public void value_computation_has_different_hash_for_different_values() {
    SObject object = object(Hash.of(1));
    SObject object2 = object(Hash.of(2));

    assertThat(valueComputationHash(object))
        .isNotEqualTo(valueComputationHash(object2));
  }

  @Test
  public void native_call_computation_has_different_hash_for_different_functions() {
    NativeFunction function = nativeFunctionWithHash(Hash.of(1));
    NativeFunction function2 = nativeFunctionWithHash(Hash.of(2));

    assertThat(nativeCallComputationHash(function))
        .isNotEqualTo(nativeCallComputationHash(function2));
  }

  @Test
  public void convert_computation_has_different_hash_for_different_types() {
    assertThat(convertComputationHash(stringType()))
        .isNotEqualTo(convertComputationHash(blobType()));
  }

  @Test
  public void constructor_call_computation_has_different_hash_for_different_types() {
    Constructor constructor = constructor("MyStruct1");
    Constructor constructor2 = constructor("MyStruct2");

    assertThat(constructorCallComputationHash(constructor))
        .isNotEqualTo(constructorCallComputationHash(constructor2));
  }

  @Test
  public void accessor_call_computation_has_different_hash_for_different_types() {
    Accessor accessor = accessor("myField");
    Accessor accessor2 = accessor("myField2");

    assertThat(accessorCallComputationHash(accessor))
        .isNotEqualTo(accessorCallComputationHash(accessor2));
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

  private static SObject object(Hash hash) {
    SObject object = mock(SObject.class);
    when(object.hash()).thenReturn(hash);
    return object;
  }
}
