package org.smoothbuild.db.values.corrupt;

import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.value.Bool;

import com.google.common.hash.HashCode;

import okio.ByteString;

public class CorruptedBoolTest extends AbstractCorruptedTestCase {
  private HashCode instanceHash;

  @Test
  public void learning_test_create_bool() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth bool
     * in HashedDb.
     */
    run_learning_test(true);
    run_learning_test(false);
  }

  private void run_learning_test(boolean value) throws IOException {
    given(instanceHash =
        hash(
            hash(valuesDb.boolType()),
            hash(value)));
    when(() -> ((Bool) valuesDb.get(instanceHash)).data());
    thenReturned(value);
  }

  @Test
  public void bool_with_empty_bytes_as_data_is_corrupted() throws Exception {
    given(instanceHash =
        hash(
            hash(valuesDb.boolType()),
            hash(ByteString.of())));
    when(() -> ((Bool) valuesDb.get(instanceHash)).data());
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is Bool which value stored in ValuesDb has zero bytes.")));
  }

  @Test
  public void bool_with_more_than_one_byte_as_data_is_corrupted() throws Exception {
    given(instanceHash =
        hash(
            hash(valuesDb.boolType()),
            hash(ByteString.of((byte) 0, (byte) 0))));
    when(() -> ((Bool) valuesDb.get(instanceHash)).data());
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is Bool which value stored in ValuesDb has more than one byte.")));
  }

  @Test
  public void bool_with_one_byte_data_not_equal_zero_nor_one_is_corrupted() throws Exception {
    for (int i = -128; i <= 127; i++) {
      if (i != 0 && i != 1) {
        run_bool_with_one_byte_data_not_equal_zero_nor_one_is_corrupted((byte) i);
      }
    }
  }

  private void run_bool_with_one_byte_data_not_equal_zero_nor_one_is_corrupted(byte value) throws
      IOException {
    given(instanceHash =
        hash(
            hash(valuesDb.boolType()),
            hash(ByteString.of((value)))));
    when(() -> ((Bool) valuesDb.get(instanceHash)).data());
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is Bool which value stored in ValuesDb is illegal (=" + value + ").")));
  }
}
