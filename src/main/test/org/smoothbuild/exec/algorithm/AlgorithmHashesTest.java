package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.ReadTupleElementAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.createArrayAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.createTupleAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedBlobAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedStringAlgorithmHash;
import static org.smoothbuild.util.Lists.list;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.spec.TupleSpec;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class AlgorithmHashesTest extends TestingContext {
  @Test
  public void each_algorithm_has_different_hash() {
    Set<Hash> hashes = new HashSet<>();
    NativeFunction function = nativeFunctionWithHash(Hash.of(0));
    TupleSpec constructedType = tupleSpec(list());
    Accessor accessor = accessor(0);

    hashes.add(createArrayAlgorithmHash());
    hashes.add(callNativeAlgorithmHash(function));
    hashes.add(convertAlgorithmHash(stringSpec()));
    hashes.add(createTupleAlgorithmHash(constructedType));
    hashes.add(ReadTupleElementAlgorithmHash(accessor));
    hashes.add(fixedStringAlgorithmHash("abc"));
    hashes.add(fixedBlobAlgorithmHash(ByteString.of((byte) 0xAB)));

    assertThat(hashes.size())
        .isEqualTo(7);
  }

  @Test
  public void call_native_algorithm_has_different_hash_for_different_functions() {
    NativeFunction function = nativeFunctionWithHash(Hash.of(1));
    NativeFunction function2 = nativeFunctionWithHash(Hash.of(2));

    assertThat(callNativeAlgorithmHash(function))
        .isNotEqualTo(callNativeAlgorithmHash(function2));
  }

  @Test
  public void convert_algorithm_has_different_hash_for_different_types() {
    assertThat(convertAlgorithmHash(stringSpec()))
        .isNotEqualTo(convertAlgorithmHash(blobSpec()));
  }

  @Test
  public void create_tuple_algorithm_has_different_hash_for_different_types() {
    TupleSpec constructedType = tupleSpec(list(stringSpec()));
    TupleSpec constructedType2 = tupleSpec(list(blobSpec()));

    assertThat(createTupleAlgorithmHash(constructedType))
        .isNotEqualTo(createTupleAlgorithmHash(constructedType2));
  }

  @Test
  public void read_tuple_element_algorithm_has_different_hash_for_different_field_indexes() {
    Accessor accessor = accessor(0);
    Accessor accessor2 = accessor(1);

    assertThat(ReadTupleElementAlgorithmHash(accessor))
        .isNotEqualTo(ReadTupleElementAlgorithmHash(accessor2));
  }

  @Test
  public void fixed_string_algorithm_has_different_hash_for_different_strings() {
    assertThat(fixedStringAlgorithmHash("abc"))
        .isNotEqualTo(fixedStringAlgorithmHash("def"));
  }

  @Test
  public void fixed_blob_algorithm_has_different_hash_for_different_byte_strings() {
    assertThat(fixedBlobAlgorithmHash(ByteString.of((byte) 1)))
        .isNotEqualTo(fixedBlobAlgorithmHash(ByteString.of((byte) 2)));
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
}
