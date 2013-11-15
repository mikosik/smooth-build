package org.smoothbuild.lang.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.smoothbuild.lang.builtin.file.err.IllegalPathError;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.io.fs.base.PathTesting;
import org.smoothbuild.testing.lang.function.value.FakeString;

public class PathArgValidatorTest {
  @Test
  public void illegalPathsAreReported() {
    String name = "name";
    for (String path : PathTesting.listOfInvalidPaths()) {
      try {
        PathArgValidator.validatedPath(name, new FakeString(path));
        fail("exception should be thrown");
      } catch (ErrorMessageException e) {
        // expected
        assertThat(e.errorMessage()).isInstanceOf(IllegalPathError.class);
      }
    }
  }

  @Test
  public void validPathsAreAccepted() {
    for (String path : PathTesting.listOfCorrectPaths()) {
      PathArgValidator.validatedPath("name", new FakeString(path));
    }
  }
}
