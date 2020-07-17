package org.smoothbuild.record.corrupt;

import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.record.base.SString;
import org.smoothbuild.record.db.ObjectDbException;

public class CorruptedNothingTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_string() throws Exception {
    Hash instanceHash =
        hash(
            hash(nothingType()),
            hash("aaa"));
    assertCall(() -> ((SString) objectDb().get(instanceHash)).jValue())
        .throwsException(new ObjectDbException(
            instanceHash, "Object type is Nothing so such object cannot exist."));
  }
}
