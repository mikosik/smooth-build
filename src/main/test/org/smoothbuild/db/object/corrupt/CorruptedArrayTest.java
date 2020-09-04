package org.smoothbuild.db.object.corrupt;

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
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;

import okio.ByteString;

public class CorruptedArrayTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_array() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth array
     * in HashedDb.
     */
    Hash objectHash =
        hash(
            hash(arraySpec(stringSpec())),
            hash(
                hash(
                    hash(stringSpec()),
                    hash("aaa")
                ),
                hash(
                    hash(stringSpec()),
                    hash("bbb")
                )
            ));
    List<String> strings = stream(((Array) objectDb().get(objectHash))
        .asIterable(RString.class))
        .map(RString::jValue)
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
    Hash objectHash =
        hash(
            hash(arraySpec(stringSpec())),
            notHashOfHashSequence
        );
    assertCall(() -> ((Array) objectDb().get(objectHash)).asIterable(Obj.class))
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new DecodingHashSequenceException(notHashOfHashSequence));
  }

  @Test
  public void array_with_one_element_of_wrong_spec_is_corrupted() throws Exception {
    Hash objectHash =
        hash(
            hash(arraySpec(stringSpec())),
            hash(
                hash(
                    hash(stringSpec()),
                    hash("aaa")
                ),
                hash(
                    hash(boolSpec()),
                    hash(true)
                )
            ));
    assertCall(() -> ((Array) objectDb().get(objectHash)).asIterable(RString.class))
        .throwsException(new CannotDecodeObjectException(objectHash,
            "It is array which spec == [STRING] but one of its elements has spec == BOOL"));
  }
}
