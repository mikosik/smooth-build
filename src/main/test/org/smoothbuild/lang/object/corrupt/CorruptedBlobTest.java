package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.db.ObjectDbException;

import okio.ByteString;

public class CorruptedBlobTest  extends AbstractCorruptedTestCase {
  private Hash instanceHash;
  private Hash dataHash;
  private ByteString byteString;

  @Test
  public void learning_test_create_bool() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth blob
     * in HashedDb.
     */
    given(byteString = ByteString.of((byte) 1, (byte) 2));
    given(() -> instanceHash =
        hash(
            hash(blobType()),
            hash(byteString)));
    when(() -> ((Blob) objectDb().get(instanceHash)).source().readByteString());
    thenReturned(byteString);
  }

  @Test
  public void bool_with_data_hash_pointing_nowhere_is_corrupted() {
    given(() -> dataHash = Hash.of(33));
    given(() -> instanceHash =
        hash(
            hash(blobType()),
            dataHash));
    when(() -> ((Blob) objectDb().get(instanceHash)).source());
    thenThrown(exception(new ObjectDbException(instanceHash, new NoSuchDataException(dataHash))));
  }
}
