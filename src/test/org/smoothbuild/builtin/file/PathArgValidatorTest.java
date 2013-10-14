package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.fs.base.TestPath;

public class PathArgValidatorTest {
  @Test
  public void illegalPathsAreReported() {
    String name = "name";
    for (String path : TestPath.listOfInvalidPaths()) {
      try {
        PathArgValidator.validatedPath(name, path);
        fail("exception should be thrown");
      } catch (ErrorMessageException e) {
        // expected
        assertThat(e.errorMessage()).isInstanceOf(IllegalPathError.class);
      }
    }
  }

  @Test
  public void validPathsAreAccepted() {
    for (String path : TestPath.listOfCorrectPaths()) {
      PathArgValidator.validatedPath("name", path);
    }
  }
}
