package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.testing.message.TestMessageListener;

public class PathArgValidatorTest {
  TestMessageListener messages = new TestMessageListener();

  @Test
  public void illegalPathsAreReported() {
    String name = "name";
    for (String path : PathTest.listOfInvalidPaths()) {
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
    for (String path : PathTest.listOfCorrectPaths()) {
      PathArgValidator.validatedPath("name", path);
    }
  }
}
