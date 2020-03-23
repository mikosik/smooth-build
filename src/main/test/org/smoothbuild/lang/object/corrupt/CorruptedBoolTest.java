package org.smoothbuild.lang.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.hashed.DecodingBooleanException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.db.ObjectDbException;

import okio.ByteString;

public class CorruptedBoolTest extends AbstractCorruptedTestCase {
  /*
   * This test makes sure that other tests in this class use proper scheme to save smooth bool
   * in HashedDb.
   */
  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void run_learning_test(boolean value) throws Exception {
    Hash instanceHash =
        hash(
            hash(boolType()),
            hash(value));
    assertThat(((Bool) objectDb().get(instanceHash)).jValue())
        .isEqualTo(value);
  }

  @Test
  public void bool_with_empty_bytes_as_data_is_corrupted() throws Exception {
    Hash dataHash = hash(ByteString.of());
    Hash instanceHash =
        hash(
            hash(boolType()),
            dataHash);
    assertCall(() -> ((Bool) objectDb().get(instanceHash)).jValue())
        .throwsException(new ObjectDbException(instanceHash))
        .withCause(new DecodingBooleanException(dataHash));
  }

  @Test
  public void bool_with_more_than_one_byte_as_data_is_corrupted() throws Exception {
    Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
    Hash instanceHash =
        hash(
            hash(boolType()),
            dataHash);
    assertCall(() -> ((Bool) objectDb().get(instanceHash)).jValue())
        .throwsException(new ObjectDbException(instanceHash))
        .withCause(new DecodingBooleanException(dataHash));
  }

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
    Hash dataHash = hash(ByteString.of((value)));
    Hash instanceHash =
        hash(
            hash(boolType()),
            dataHash);
    assertCall(() -> ((Bool) objectDb().get(instanceHash)).jValue())
        .throwsException(new ObjectDbException(instanceHash))
        .withCause(new DecodingBooleanException(dataHash));
  }
}
