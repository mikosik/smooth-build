package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.arrayAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedBlobAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedStringAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readTupleElementAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.tupleAlgorithmHash;
import static org.smoothbuild.util.Lists.list;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.exec.nativ.Native;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class AlgorithmHashesTest extends TestingContext {
  @Test
  public void each_algorithm_has_different_hash() {
    Set<Hash> hashes = new HashSet<>();
    Native nativ = nativeWithHash(Hash.of(0));
    TupleSpec constructedType = tupleSpec(list());

    hashes.add(arrayAlgorithmHash());
    hashes.add(callNativeAlgorithmHash(nativ, "referencableName"));
    hashes.add(convertAlgorithmHash(stringSpec()));
    hashes.add(tupleAlgorithmHash(constructedType));
    hashes.add(readTupleElementAlgorithmHash(0));
    hashes.add(fixedStringAlgorithmHash("abc"));
    hashes.add(fixedBlobAlgorithmHash(ByteString.of((byte) 0xAB)));

    assertThat(hashes.size())
        .isEqualTo(7);
  }

  @Test
  public void call_native_algorithm_has_different_hash_for_different_natives() {
    Native native1 = nativeWithHash(Hash.of(1));
    Native native2 = nativeWithHash(Hash.of(2));

    assertThat(callNativeAlgorithmHash(native1, "referencableName"))
        .isNotEqualTo(callNativeAlgorithmHash(native2, "referencableName"));
  }

  @Test
  public void call_native_algorithm_has_different_hash_for_different_referencable_names() {
    Native nativ = nativeWithHash(Hash.of(1));

    assertThat(callNativeAlgorithmHash(nativ, "referencableName1"))
        .isNotEqualTo(callNativeAlgorithmHash(nativ, "referencableName2"));
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

    assertThat(tupleAlgorithmHash(constructedType))
        .isNotEqualTo(tupleAlgorithmHash(constructedType2));
  }

  @Test
  public void read_tuple_element_algorithm_has_different_hash_for_different_field_indexes() {
    assertThat(readTupleElementAlgorithmHash(0))
        .isNotEqualTo(readTupleElementAlgorithmHash(1));
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

  private static Native nativeWithHash(Hash hash) {
    Native nativ = mock(Native.class);
    when(nativ.hash()).thenReturn(hash);
    return nativ;
  }
}
