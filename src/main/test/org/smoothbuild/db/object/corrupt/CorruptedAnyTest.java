package org.smoothbuild.db.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.db.object.base.Any;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;

public class CorruptedAnyTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_any() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth any
     * in HashedDb.
     */
    Hash value = Hash.of(1234);
    Hash objectHash =
        hash(
            hash(anySpec()),
            hash(value));
    assertThat(((Any) objectDb().get(objectHash)).wrappedHash())
        .isEqualTo(value);
  }

  @Test
  public void any_with_data_hash_pointing_nowhere_is_corrupted() throws Exception {
    Hash dataHash = Hash.of(33);
    Hash objectHash =
        hash(
            hash(anySpec()),
            dataHash);
    assertCall(() -> ((Any) objectDb().get(objectHash)).wrappedHash())
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new NoSuchDataException(dataHash));
  }
}
