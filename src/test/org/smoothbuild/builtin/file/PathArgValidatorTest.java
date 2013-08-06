package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.function.exc.IllegalPathException;
import org.smoothbuild.lang.function.exc.MissingArgException;
import org.smoothbuild.lang.type.PathTest;

public class PathArgValidatorTest {

  @Test
  public void missingDirArgIsReported() throws Exception {
    Param<String> param = Param.stringParam("name");
    try {
      PathArgValidator.validatedPath(param);
      Assert.fail("exception should be thrown");
    } catch (MissingArgException e) {
      // expected

      assertThat(param).isSameAs(param);
    }
  }

  @Test
  public void illegalPathsAreReported() throws FunctionException {
    for (String path : PathTest.listOfInvalidPaths()) {
      Param<String> param = Param.stringParam("name");
      param.set(path);
      try {
        PathArgValidator.validatedPath(param);
        Assert.fail("exception should be thrown");
      } catch (IllegalPathException e) {
        // expected

        assertThat(param).isSameAs(param);
      }
    }
  }

  @Test
  public void validPathsAreAccepted() throws FunctionException {
    for (String path : PathTest.listOfCorrectPaths()) {
      Param<String> param = Param.stringParam("name");
      param.set(path);
      PathArgValidator.validatedPath(param);
    }
  }
}
