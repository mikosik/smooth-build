package org.smoothbuild.exec.algorithm;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constructAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.convertAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.invokeAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.orderAlgorithmHash;
import static org.smoothbuild.exec.algorithm.AlgorithmHashes.selectAlgorithmHash;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class AlgorithmHashesTest extends TestingContext {
  @Test
  public void each_algorithm_has_different_hash() {
    HashSet<Hash> hashes = new HashSet<>();
    var constructedType = tupleHT();

    hashes.add(constAlgorithmHash(intH(0)));
    hashes.add(constructAlgorithmHash(constructedType));
    hashes.add(convertAlgorithmHash(stringHT()));
    hashes.add(invokeAlgorithmHash(natFuncH(blobH(), stringH("class"))));
    hashes.add(orderAlgorithmHash());
    hashes.add(selectAlgorithmHash(intH(0)));

    assertThat(hashes.size())
        .isEqualTo(6);
  }

  @Test
  public void const_algorithm_has_different_hash_for_different_byte_strings() {
    assertThat(constAlgorithmHash(intH(1)))
        .isNotEqualTo(constAlgorithmHash(intH(2)));
  }

  @Test
  public void construct_algorithm_has_different_hash_for_different_fields() {
    var tuple1 = tupleHT(list(boolHT()));
    var tuple2 = tupleHT(list(blobHT()));
    assertThat(constructAlgorithmHash(tuple1))
        .isNotEqualTo(constructAlgorithmHash(tuple2));
  }

  @Test
  public void convert_algorithm_has_different_hash_for_different_types() {
    assertThat(convertAlgorithmHash(stringHT()))
        .isNotEqualTo(convertAlgorithmHash(blobHT()));
  }

  @Test
  public void invoke_algorithm_has_different_hash_for_different_jars() {
    var natFunc1 = natFuncH(blobH(ByteString.of((byte) 1)), stringH("class"));
    var natFunc2 = natFuncH(blobH(ByteString.of((byte) 2)), stringH("class"));
    assertThat(invokeAlgorithmHash(natFunc1))
        .isNotEqualTo(invokeAlgorithmHash(natFunc2));
  }

  @Test
  public void invoke_algorithm_has_different_hash_for_class_binary_name() {
    var natFunc1 = natFuncH(blobH(), stringH("class 1"));
    var natFunc2 = natFuncH(blobH(), stringH("class 2"));
    assertThat(invokeAlgorithmHash(natFunc1))
        .isNotEqualTo(invokeAlgorithmHash(natFunc2));
  }

  @Test
  public void select_algorithm_has_different_hash_for_different_field_indexes() {
    assertThat(selectAlgorithmHash(intH(1)))
        .isNotEqualTo(selectAlgorithmHash(intH(2)));
  }
}
