package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.lang.object.db.ObjectsDbException.corruptedObjectException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.base.SString;

import okio.ByteString;

public class CorruptedObjectTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;

  @Test
  public void learning_test_create_any_value() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth value
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
  public void object_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted() throws
      Exception {
    for (int i = 0; i <= Hash.hashesSize() * 3 + 1; i++) {
      if (i % Hash.hashesSize() != 0) {
        run_object_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(1);
      }
    }
  }

  private void run_object_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(
      int byteCount) throws IOException, HashedDbException {
    given(instanceHash =
        hash(ByteString.of(new byte[byteCount])));
    when(() -> objectsDb().get(instanceHash));
    thenThrown(exception(corruptedObjectException(instanceHash,
        "Its Merkle tree root is hash of byte sequence which size is not multiple of hash size.")));
  }
}