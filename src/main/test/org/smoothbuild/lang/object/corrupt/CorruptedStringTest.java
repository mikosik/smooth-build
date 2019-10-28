package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.lang.object.db.ObjectsDbException.corruptedObjectException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SString;

import okio.ByteString;

public class CorruptedStringTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;

  @Test
  public void learning_test_create_string() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth bool
     * in HashedDb.
     */
    given(instanceHash =
        hash(
            hash(stringType()),
            hash("aaa")));
    when(() -> ((SString) objectsDb().get(instanceHash)).data());
    thenReturned("aaa");
  }

  @Test
  public void string_with_data_being_invalid_utf8_sequence_is_corrupted() throws Exception {
    given(instanceHash =
        hash(
            hash(stringType()),
            hash(ByteString.of((byte) -64))));
    when(() -> ((SString) objectsDb().get(instanceHash)).data());
    thenThrown(exception(corruptedObjectException(instanceHash,
        "It is an instance of a String which data cannot be decoded using UTF-8 encoding.")));
  }
}