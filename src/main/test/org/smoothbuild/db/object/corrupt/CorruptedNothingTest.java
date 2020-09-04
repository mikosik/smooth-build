package org.smoothbuild.db.object.corrupt;

import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.RString;
import org.smoothbuild.db.object.db.ObjectDbException;

public class CorruptedNothingTest extends AbstractCorruptedTestCase {
  @Test
  public void learning_test_create_string() throws Exception {
    Hash objectHash =
        hash(
            hash(nothingSpec()),
            hash("aaa"));
    assertCall(() -> ((RString) objectDb().get(objectHash)).jValue())
        .throwsException(new ObjectDbException("Cannot create java object for 'NOTHING' spec."));
  }
}
