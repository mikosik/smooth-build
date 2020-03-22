package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectDbException;

public class CorruptedNothingTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;

  @Test
  public void learning_test_create_string() throws Exception {
    given(instanceHash =
        hash(
            hash(nothingType()),
            hash("aaa")));
    when(() -> ((SString) objectDb().get(instanceHash)).jValue());
    thenThrown(exception(new ObjectDbException(instanceHash,
        "Object type is Nothing so such object cannot exist.")));
  }
}
