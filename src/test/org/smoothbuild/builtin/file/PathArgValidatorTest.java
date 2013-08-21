package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.builtin.file.exc.IllegalPathException;
import org.smoothbuild.plugin.PathTest;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.plugin.exc.MissingArgException;

public class PathArgValidatorTest {

  @Test
  public void missingDirArgIsReported() throws Exception {
    String name = "name";
    try {
      PathArgValidator.validatedPath(name, null);
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertThat(e.paramName()).isSameAs(name);
    }
  }

  @Test
  public void illegalPathsAreReported() throws FunctionException {
    String name = "name";
    for (String path : PathTest.listOfInvalidPaths()) {
      try {
        PathArgValidator.validatedPath(name, path);
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertThat(e.paramName()).isSameAs(name);
      }
    }
  }

  @Test
  public void validPathsAreAccepted() throws FunctionException {
    for (String path : PathTest.listOfCorrectPaths()) {
      PathArgValidator.validatedPath("name", path);
    }
  }
}
