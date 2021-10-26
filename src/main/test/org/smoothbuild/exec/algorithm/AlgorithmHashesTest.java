package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.arrayAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.callNativeAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.createStructAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedBlobAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedIntAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.fixedStringAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readStructItemAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.referenceAlgorithmHash;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.testing.TestingContextImpl;

import okio.ByteString;

public class AlgorithmHashesTest extends TestingContextImpl {
  @Test
  public void each_algorithm_has_different_hash() {
    Set<Hash> hashes = new HashSet<>();
    StructSpec constructedType = structSpec();

    hashes.add(arrayAlgorithmHash());
    hashes.add(callNativeAlgorithmHash("referencableName"));
    hashes.add(convertAlgorithmHash(strSpec()));
    hashes.add(createStructAlgorithmHash(constructedType));
    hashes.add(readStructItemAlgorithmHash(0));
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
    assertThat(convertAlgorithmHash(strSpec()))
        .isNotEqualTo(convertAlgorithmHash(blobSpec()));
  }

  @Test
  public void create_struct_algorithm_has_different_hash_for_different_fields() {
    StructSpec constructedType = structSpec(list());
    StructSpec constructedType2 = structSpec(list(named("field", blobSpec())));

    assertThat(createStructAlgorithmHash(constructedType))
        .isNotEqualTo(createStructAlgorithmHash(constructedType2));
  }

  @Test
  public void read_struct_item_algorithm_has_different_hash_for_different_field_indexes() {
    assertThat(readStructItemAlgorithmHash(0))
        .isNotEqualTo(readStructItemAlgorithmHash(1));
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
