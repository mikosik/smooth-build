package org.smoothbuild.lang.object.corrupt;

import static com.google.common.collect.Streams.stream;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectDbException;

import okio.ByteString;

public class CorruptedArrayTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_array() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth array
     * in HashedDb.
     */
    Hash instanceHash =
        hash(
            hash(arrayType(stringType())),
            hash(
                hash(
                    hash(stringType()),
                    hash("aaa")
                ),
                hash(
                    hash(stringType()),
                    hash("bbb")
                )
            ));
    List<String> strings = stream(((Array) objectDb().get(instanceHash))
        .asIterable(SString.class))
        .map(SString::jValue)
        .collect(toList());
    assertThat(strings)
        .containsExactly("aaa", "bbb")
        .inOrder();
  }

  public static IntStream illegal_array_byte_sizes() {
    return IntStream.rangeClosed(1, Hash.hashesSize() * 3 + 1)
        .filter(i -> i % Hash.hashesSize() != 0);
  }

  @ParameterizedTest
  @MethodSource("illegal_array_byte_sizes")
  public void array_with_data_size_different_than_multiple_of_hash_size_is_corrupted(
      int byteCount) throws Exception {
    Hash notHashOfHashSequence = hash(ByteString.of(new byte[byteCount]));
    Hash instanceHash =
        hash(
            hash(arrayType(stringType())),
            notHashOfHashSequence
        );
    assertCall(() -> ((Array) objectDb().get(instanceHash)).asIterable(SObject.class))
        .throwsException(new ObjectDbException(instanceHash))
        .withCause(new DecodingHashSequenceException(notHashOfHashSequence));
  }

  @Test
  public void array_with_one_element_of_wrong_type_is_corrupted() throws Exception {
    Hash instanceHash =
        hash(
            hash(arrayType(stringType())),
            hash(
                hash(
                    hash(stringType()),
                    hash("aaa")
                ),
                hash(
                    hash(boolType()),
                    hash(true)
                )
            ));
    assertCall(() -> ((Array) objectDb().get(instanceHash)).asIterable(SString.class))
        .throwsException(new ObjectDbException(instanceHash,
            "It is array with type [STRING] but one of its elements has type BOOL"));
  }
}
