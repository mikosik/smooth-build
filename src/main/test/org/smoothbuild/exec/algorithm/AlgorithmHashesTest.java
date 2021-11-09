package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constructAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedBlobAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedIntAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedStringAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.orderAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.referenceAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.selectAlgorithmHash;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class AlgorithmHashesTest extends TestingContext {
  @Test
  public void each_algorithm_has_different_hash() {
    Set<Hash> hashes = new HashSet<>();
    TupleTypeH constructedType = tupleOT();

    hashes.add(orderAlgorithmHash());
    hashes.add(callNativeAlgorithmHash("referencableName"));
    hashes.add(convertAlgorithmHash(stringOT()));
    hashes.add(constructAlgorithmHash(constructedType));
    hashes.add(selectAlgorithmHash(0));
    hashes.add(fixedStringAlgorithmHash("abc"));
    hashes.add(fixedBlobAlgorithmHash(ByteString.of((byte) 0xAB)));
    hashes.add(referenceAlgorithmHash(Hash.of(""), "global-referencable-name"));
    hashes.add(fixedIntAlgorithmHash(BigInteger.valueOf(123)));

    assertThat(hashes.size())
        .isEqualTo(9);
  }

  @Test
  public void call_native_algorithm_has_different_hash_for_different_referencable_names() {
    assertThat(callNativeAlgorithmHash("referencableName1"))
        .isNotEqualTo(callNativeAlgorithmHash("referencableName2"));
  }

  @Test
  public void convert_algorithm_has_different_hash_for_different_types() {
    assertThat(convertAlgorithmHash(stringOT()))
        .isNotEqualTo(convertAlgorithmHash(blobOT()));
  }

  @Test
  public void construct_algorithm_has_different_hash_for_different_fields() {
    TupleTypeH constructedType = tupleOT(list());
    TupleTypeH constructedType2 = tupleOT(list(blobOT()));

    assertThat(constructAlgorithmHash(constructedType))
        .isNotEqualTo(constructAlgorithmHash(constructedType2));
  }

  @Test
  public void read_tuple_item_algorithm_has_different_hash_for_different_field_indexes() {
    assertThat(selectAlgorithmHash(0))
        .isNotEqualTo(selectAlgorithmHash(1));
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

  @Test
  public void reference_algorithm_has_different_hash_for_different_modules() {
    assertThat(referenceAlgorithmHash(Hash.of(123), "referencable-name"))
        .isNotEqualTo(referenceAlgorithmHash(Hash.of(345), "referencable-name"));
  }

  @Test
  public void reference_algorithm_has_different_hash_for_different_function_names() {
    assertThat(referenceAlgorithmHash(Hash.of(123), "referencable-name"))
        .isNotEqualTo(referenceAlgorithmHash(Hash.of(123), "other-name"));
  }

  @Test
  public void fixed_int_algorithm_has_different_hash_for_different_integers() {
    assertThat(fixedIntAlgorithmHash(BigInteger.valueOf(123)))
        .isNotEqualTo(fixedIntAlgorithmHash(BigInteger.valueOf(124)));
  }
}
