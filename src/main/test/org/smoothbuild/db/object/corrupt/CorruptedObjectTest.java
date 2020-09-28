package org.smoothbuild.db.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.db.object.base.Str;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;
import org.smoothbuild.db.object.db.CannotDecodeSpecException;

import okio.ByteString;

public class CorruptedObjectTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_any_value() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth value
     * in HashedDb.
     */
    Hash objectHash =
        hash(
            hash(stringSpec()),
            hash("aaa"));
    assertThat(((Str) objectDb().get(objectHash)).jValue())
        .isEqualTo("aaa");
  }

  public static IntStream illegal_array_byte_sizes() {
    return IntStream.rangeClosed(1, Hash.hashesSize() * 3 + 1)
        .filter(i -> i % Hash.hashesSize() != 0);
  }

  @ParameterizedTest
  @MethodSource("illegal_array_byte_sizes")
  public void run_object_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(
      int byteCount) throws IOException, HashedDbException {
    Hash objectHash =
        hash(ByteString.of(new byte[byteCount]));
    assertCall(() -> objectDb().get(objectHash))
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new DecodingHashSequenceException(objectHash));
  }

  @Test
  public void object_which_spec_is_corrupted_is_corrupted() throws Exception {
    Hash specHash = Hash.of("not a spec");
    Hash objectHash =
        hash(
            specHash,
            hash("aaa"));
    assertCall(() -> objectDb().get(objectHash))
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new CannotDecodeSpecException(specHash));
  }

  @Test
  public void reading_elements_from_not_stored_object_throws_exception() {
    Hash objectHash = Hash.of(33);
    assertCall(() -> objectDb().get(objectHash))
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new NoSuchDataException(objectHash));
  }
}