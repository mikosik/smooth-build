package org.smoothbuild.db.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;

import okio.ByteString;

public class CorruptedStringTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_string() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth bool
     * in HashedDb.
     */
    Hash objectHash =
        hash(
            hash(stringSpec()),
            hash("aaa"));
    assertThat(((RString) objectDb().get(objectHash)).jValue())
        .isEqualTo("aaa");
  }

  @Test
  public void string_with_data_being_invalid_utf8_sequence_is_corrupted() throws Exception {
    Hash notStringHash = hash(ByteString.of((byte) -64));
    Hash objectHash =
        hash(
            hash(stringSpec()),
            notStringHash);
    assertCall(() -> ((RString) objectDb().get(objectHash)).jValue())
        .throwsException(new CannotDecodeObjectException(objectHash))
        .withCause(new DecodingStringException(notStringHash, null));
  }
}
