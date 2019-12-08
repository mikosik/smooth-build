package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectDbException;

import okio.ByteString;

public class CorruptedObjectTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;
  private Hash typeHash;

  @Test
  public void learning_test_create_any_value() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth value
     * in HashedDb.
     */
    given(() -> instanceHash =
        hash(
            hash(stringType()),
            hash("aaa")));
    when(() -> ((SString) objectDb().get(instanceHash)).jValue());
    thenReturned("aaa");
  }

  @Test
  public void object_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted() throws
      Exception {
    for (int i = 0; i <= Hash.hashesSize() * 3 + 1; i++) {
      if (i % Hash.hashesSize() != 0) {
        run_object_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(i);
      }
    }
  }

  private void run_object_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(
      int byteCount) throws IOException, HashedDbException {
    given(instanceHash =
        hash(ByteString.of(new byte[byteCount])));
    when(() -> objectDb().get(instanceHash));
    thenThrown(exception(new ObjectDbException(instanceHash,
        new DecodingHashSequenceException(instanceHash))));
  }

  @Test
  public void object_which_type_is_corrupted_is_corrupted() {
    given(() -> typeHash = Hash.of("not a type"));
    given(() -> instanceHash =
        hash(
            typeHash,
            hash("aaa")));
    when(() -> objectDb().get(instanceHash));
    thenThrown(exception(new ObjectDbException(instanceHash,
        new ObjectDbException(typeHash, (Exception) null))));
  }

  @Test
  public void reading_elements_from_not_stored_object_throws_exception() {
    given(instanceHash = Hash.of(33));
    when(() -> objectDb().get(instanceHash));
    thenThrown(exception(
        new ObjectDbException(instanceHash, new NoSuchDataException(instanceHash))));
  }
}
