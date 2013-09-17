package org.smoothbuild.builtin.file;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.testing.problem.TestMessageListener;

public class PathArgValidatorTest {
  TestMessageListener messages = new TestMessageListener();

  @Test
  public void illegalPathsAreReported() {
    String name = "name";
    for (String path : PathTest.listOfInvalidPaths()) {
      TestMessageListener messages = new TestMessageListener();
      PathArgValidator.validatedPath(name, path, messages);
      messages.assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void validPathsAreAccepted() {
    for (String path : PathTest.listOfCorrectPaths()) {
      TestMessageListener messages = new TestMessageListener();
      PathArgValidator.validatedPath("name", path, messages);
      messages.assertNoProblems();
    }
  }
}
