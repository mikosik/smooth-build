package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectsDbException;

import okio.ByteString;

public class CorruptedStringTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;
  private Hash notStringHash;

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
    when(() -> ((SString) objectsDb().get(instanceHash)).jValue());
    thenReturned("aaa");
  }

  @Test
  public void string_with_data_being_invalid_utf8_sequence_is_corrupted() throws Exception {
    given(() -> notStringHash = hash(ByteString.of((byte) -64)));
    given(() -> instanceHash =
        hash(
            hash(stringType()),
            notStringHash));
    when(() -> ((SString) objectsDb().get(instanceHash)).jValue());
    thenThrown(exception(new ObjectsDbException(instanceHash,
        new DecodingStringException(notStringHash, null))));
  }
}