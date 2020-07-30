package org.smoothbuild.db.record.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.db.CannotDecodeRecordException;

import okio.ByteString;

public class CorruptedStringTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_string() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth bool
     * in HashedDb.
     */
    Hash recordHash =
        hash(
            hash(stringSpec()),
            hash("aaa"));
    assertThat(((RString) recordDb().get(recordHash)).jValue())
        .isEqualTo("aaa");
  }

  @Test
  public void string_with_data_being_invalid_utf8_sequence_is_corrupted() throws Exception {
    Hash notStringHash = hash(ByteString.of((byte) -64));
    Hash recordHash =
        hash(
            hash(stringSpec()),
            notStringHash);
    assertCall(() -> ((RString) recordDb().get(recordHash)).jValue())
        .throwsException(new CannotDecodeRecordException(recordHash))
        .withCause(new DecodingStringException(notStringHash, null));
  }
}
