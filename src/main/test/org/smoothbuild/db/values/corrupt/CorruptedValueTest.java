package org.smoothbuild.db.values.corrupt;

import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

import okio.ByteString;

public class CorruptedValueTest extends AbstractCorruptedTestCase {
  private HashCode instanceHash;

  @Test
  public void learning_test_create_any_value() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth value
     * in HashedDb.
     */
    given(instanceHash =
        hash(
            hash(valuesDb.stringType()),
            hash("aaa")));
    when(() -> ((SString) valuesDb.get(instanceHash)).data());
    thenReturned("aaa");
  }

  @Test
  public void value_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted() throws
      Exception {
    for (int i = 0; i <= Hash.size() * 3 + 1; i++) {
      if (i % Hash.size() != 0) {
        run_value_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(1);
      }
    }
  }

  private void run_value_which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(
      int byteCount) throws IOException {
    given(instanceHash =
        hash(ByteString.of(new byte[byteCount])));
    when(() -> valuesDb.get(instanceHash));
    thenThrown(exception(corruptedValueException(instanceHash,
        "Its Merkle tree root is hash of byte sequence which size is not multiple of hash size.")));
  }
}