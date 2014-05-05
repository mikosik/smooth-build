package org.smoothbuild.builtin.file;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.PathTesting;

public class PathArgValidatorTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  @Test
  public void illegalPathsAreReported() {
    String name = "name";
    for (String path : PathTesting.listOfInvalidPaths()) {
      try {
        PathArgValidator.validatedPath(name, objectsDb.string(path));
        fail("exception should be thrown");
      } catch (IllegalPathError e) {
        // expected
      }
    }
  }

  @Test
  public void validPathsAreAccepted() {
    for (String path : PathTesting.listOfCorrectPaths()) {
      PathArgValidator.validatedPath("name", objectsDb.string(path));
    }
  }
}
