package org.smoothbuild.builtin.file;

import static org.junit.Assert.fail;
import static org.smoothbuild.db.objects.ObjectsDb.objectsDb;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.testing.io.fs.base.PathTesting;

public class PathArgValidatorTest {
  private final ObjectsDb objectsDb = objectsDb();

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
