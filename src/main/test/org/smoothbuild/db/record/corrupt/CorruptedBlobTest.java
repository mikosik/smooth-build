package org.smoothbuild.db.record.corrupt;

import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.db.record.base.Blob;
import org.smoothbuild.db.record.db.RecordDbException;

import com.google.common.truth.Truth;

import okio.ByteString;

public class CorruptedBlobTest  extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_bool() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth blob
     * in HashedDb.
     */
    ByteString byteString = ByteString.of((byte) 1, (byte) 2);
    Hash recordHash =
        hash(
            hash(blobSpec()),
            hash(byteString));
    Truth.assertThat(((Blob) recordDb().get(recordHash)).source().readByteString())
        .isEqualTo(byteString);
  }

  @Test
  public void bool_with_data_hash_pointing_nowhere_is_corrupted() throws Exception {
    Hash dataHash = Hash.of(33);
    Hash recordHash =
        hash(
            hash(blobSpec()),
            dataHash);
    assertCall(() -> ((Blob) recordDb().get(recordHash)).source())
        .throwsException(new RecordDbException(recordHash))
        .withCause(new NoSuchDataException(dataHash));
  }
}
