package org.smoothbuild.builtin.file;

import static org.junit.Assert.fail;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.testing.io.fs.base.PathTesting;

public class PathArgValidatorTest {
  private final ValuesDb valuesDb = valuesDb();

  @Test
  public void illegal_paths_are_reported() {
    String name = "name";
    for (String path : PathTesting.listOfInvalidPaths()) {
      try {
        PathArgValidator.validatedPath(name, valuesDb.string(path));
        fail("exception should be thrown");
      } catch (IllegalPathError e) {
        // expected
      }
    }
  }

  @Test
  public void valid_paths_are_accepted() {
    for (String path : PathTesting.listOfCorrectPaths()) {
      PathArgValidator.validatedPath("name", valuesDb.string(path));
    }
  }
}
