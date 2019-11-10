package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.DecodingBooleanException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.db.ObjectsDbException;

import okio.ByteString;

public class CorruptedBoolTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;
  private Hash dataHash;

  @Test
  public void learning_test_create_bool() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth bool
     * in HashedDb.
     */
    run_learning_test(true);
    run_learning_test(false);
  }

  private void run_learning_test(boolean value) {
    given(() -> instanceHash =
        hash(
            hash(boolType()),
            hash(value)));
    when(() -> ((Bool) objectsDb().get(instanceHash)).data());
    thenReturned(value);
  }

  @Test
  public void bool_with_empty_bytes_as_data_is_corrupted() {
    given(() -> dataHash = hash(ByteString.of()));
    given(() -> instanceHash =
        hash(
            hash(boolType()),
            dataHash));
    when(() -> ((Bool) objectsDb().get(instanceHash)).data());
    thenThrown(exception(new ObjectsDbException(instanceHash,
        new DecodingBooleanException(dataHash))));
  }

  @Test
  public void bool_with_more_than_one_byte_as_data_is_corrupted() {
    given(() -> dataHash = hash(ByteString.of((byte) 0, (byte) 0)));
    given(() -> instanceHash =
        hash(
            hash(boolType()),
            dataHash));
    when(() -> ((Bool) objectsDb().get(instanceHash)).data());
    thenThrown(exception(new ObjectsDbException(instanceHash,
        new DecodingBooleanException(dataHash))));
  }

  @Test
  public void bool_with_one_byte_data_not_equal_zero_nor_one_is_corrupted() {
    for (int i = -128; i <= 127; i++) {
      if (i != 0 && i != 1) {
        run_bool_with_one_byte_data_not_equal_zero_nor_one_is_corrupted((byte) i);
      }
    }
  }

  private void run_bool_with_one_byte_data_not_equal_zero_nor_one_is_corrupted(byte value) {
    given(() -> dataHash = hash(ByteString.of((value))));
    given(() -> instanceHash =
        hash(
            hash(boolType()),
            dataHash));
    when(() -> ((Bool) objectsDb().get(instanceHash)).data());
    thenThrown(exception(new ObjectsDbException(instanceHash,
        new DecodingBooleanException(dataHash))));
  }
}
