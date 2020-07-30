package org.smoothbuild.db.record.corrupt;

import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.db.record.db.RecordDbException;

public class CorruptedNothingTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_string() throws Exception {
    Hash recordHash =
        hash(
            hash(nothingSpec()),
            hash("aaa"));
    assertCall(() -> ((RString) recordDb().get(recordHash)).jValue())
        .throwsException(new RecordDbException("Cannot create java object for 'NOTHING' spec."));
  }
}
