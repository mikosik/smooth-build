package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.task.base.ComputationHashes.accessorCallComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.arrayComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.constructorCallComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.convertComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.identityComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.nativeCallComputationHash;
import static org.smoothbuild.task.base.ComputationHashes.valueComputationHash;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.util.HashSet;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.testing.TestingContext;

public class ComputationHashesTest extends TestingContext {
  private HashSet<Hash> hashes;
  private NativeFunction function;
  private NativeFunction function2;
  private Constructor constructor;
  private Constructor constructor2;
  private Accessor accessor;
  private Accessor accessor2;
  private SObject object;
  private SObject object2;

  @Test
  public void each_computation_has_different_hash() {
    given(hashes = new HashSet<>());
    given(function = mock(NativeFunction.class));
    given(willReturn(Hash.of(0)), function).hash();
    given(constructor = mock(Constructor.class));
    given(willReturn(structType("MyStruct2", list())), constructor).type();
    given(accessor = mock(Accessor.class));
    given(willReturn("myField"), accessor).fieldName();
    given(object = mock(SObject.class));
    given(willReturn(Hash.of(0)), object).hash();
    given(hashes.add(valueComputationHash(object)));
    given(hashes.add(arrayComputationHash()));
    given(hashes.add(identityComputationHash()));
    given(hashes.add(nativeCallComputationHash(function)));
    given(hashes.add(convertComputationHash(stringType())));
    given(hashes.add(constructorCallComputationHash(constructor)));
    given(hashes.add(accessorCallComputationHash(accessor)));

    when(hashes).size();
    thenReturned(7);
  }

  @Test
  public void value_computation_has_different_hash_for_different_values() throws Exception {
    given(object = mock(SObject.class));
    given(willReturn(Hash.of(1)), object).hash();
    given(object2 = mock(SObject.class));
    given(willReturn(Hash.of(2)), object2).hash();
    when(valueComputationHash(object));
    thenReturned(not(valueComputationHash(object2)));
  }

  @Test
  public void native_call_computation_has_different_hash_for_different_functions()
      throws Exception {
    given(function = mock(NativeFunction.class));
    given(willReturn(Hash.of(1)), function).hash();
    given(function2 = mock(NativeFunction.class));
    given(willReturn(Hash.of(2)), function2).hash();
    when(nativeCallComputationHash(function));
    thenReturned(not(nativeCallComputationHash(function2)));
  }

  @Test
  public void convert_computation_has_different_hash_for_different_types() throws Exception {
    when(convertComputationHash(stringType()));
    thenReturned(not(convertComputationHash(blobType())));
  }

  @Test
  public void constructor_call_computation_has_different_hash_for_different_types()
      throws Exception {
    given(constructor = mock(Constructor.class));
    given(willReturn(structType("MyStruct1", list())), constructor).type();
    given(constructor2 = mock(Constructor.class));
    given(willReturn(structType("MyStruct2", list())), constructor2).type();
    when(constructorCallComputationHash(constructor));
    thenReturned(not(constructorCallComputationHash(constructor2)));
  }

  @Test
  public void accessor_call_computation_has_different_hash_for_different_types()
      throws Exception {
    given(accessor = mock(Accessor.class));
    given(willReturn("myField"), accessor).fieldName();
    given(accessor2 = mock(Accessor.class));
    given(willReturn("myField2"), accessor2).fieldName();
    when(accessorCallComputationHash(accessor));
    thenReturned(not(accessorCallComputationHash(accessor2)));
  }
}
