package org.smoothbuild.db.object.corrupt;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.hashed.DecodingBooleanException;
import org.smoothbuild.db.hashed.DecodingByteException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;

import com.google.common.truth.Truth;

import okio.ByteString;

public class CorruptedBoolTest extends AbstractCorruptedTestCase {
  /*
   * This test makes sure that other tests in this class use proper scheme to save smooth bool
   * in HashedDb.
   */
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void run_learning_test(boolean value) throws Exception {
    Hash objectHash =
        hash(
            hash(boolSpec()),
            hash(value));
    Truth.assertThat(((Bool) objectDb().get(objectHash)).jValue())
        .isEqualTo(value);
  }

  @Test
  public void bool_with_empty_bytes_as_data_is_corrupted() throws Exception {
    Hash dataHash = hash(ByteString.of());
    Hash objectHash =
        hash(
            hash(boolSpec()),
            dataHash);
    assertCall(() -> ((Bool) objectDb().get(objectHash)).jValue())
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new DecodingBooleanException(dataHash, new DecodingByteException(dataHash)));
  }

  @Test
  public void bool_with_more_than_one_byte_as_data_is_corrupted() throws Exception {
    Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
    Hash objectHash =
        hash(
            hash(boolSpec()),
            dataHash);
    assertCall(() -> ((Bool) objectDb().get(objectHash)).jValue())
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new DecodingBooleanException(dataHash, new DecodingByteException(dataHash)));
  }

  @SuppressWarnings("unused")
  private static List<Byte> all_byte_values_except_zero_and_one() {
    return IntStream.rangeClosed(-128, 127)
        .filter(v -> v != 0 && v != 1)
        .boxed()
        .map(Integer::byteValue)
        .collect(toList());
  }

  @ParameterizedTest
  @MethodSource("all_byte_values_except_zero_and_one")
  public void bool_with_one_byte_data_not_equal_zero_nor_one_is_corrupted(byte value)
      throws Exception {
    Hash dataHash = hash(ByteString.of(value));
    Hash objectHash =
        hash(
            hash(boolSpec()),
            dataHash);
    assertCall(() -> ((Bool) objectDb().get(objectHash)).jValue())
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new DecodingBooleanException(dataHash));
  }
}
