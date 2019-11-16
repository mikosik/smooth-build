package org.smoothbuild.lang.object.corrupt;

import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectsDbException;

public class CorruptedNothingTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;

  @Test
  public void learning_test_create_string() throws Exception {
    given(instanceHash =
        hash(
            hash(nothingType()),
            hash("aaa")));
    when(() -> ((SString) objectsDb().get(instanceHash)).jValue());
    thenThrown(exception(new ObjectsDbException(instanceHash,
        "Object type is Nothing so such object cannot exist.")));
  }
}
