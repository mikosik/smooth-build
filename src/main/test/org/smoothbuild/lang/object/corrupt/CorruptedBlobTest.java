package org.smoothbuild.lang.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.db.ObjectDbException;

import okio.ByteString;

public class CorruptedBlobTest  extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_bool() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth blob
     * in HashedDb.
     */
    ByteString byteString = ByteString.of((byte) 1, (byte) 2);
    Hash instanceHash =
        hash(
            hash(blobType()),
            hash(byteString));
    assertThat(((Blob) objectDb().get(instanceHash)).source().readByteString())
        .isEqualTo(byteString);
  }

  @Test
  public void bool_with_data_hash_pointing_nowhere_is_corrupted() throws Exception {
    Hash dataHash = Hash.of(33);
    Hash instanceHash =
        hash(
            hash(blobType()),
            dataHash);
    assertCall(() -> ((Blob) objectDb().get(instanceHash)).source())
        .throwsException(new ObjectDbException(instanceHash))
        .withCause(new NoSuchDataException(dataHash));
  }
}
