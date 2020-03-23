package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectDbException;

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
