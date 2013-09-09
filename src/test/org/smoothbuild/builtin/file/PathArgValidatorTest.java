package org.smoothbuild.builtin.file;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.MissingRequiredArgError;
import org.smoothbuild.plugin.api.PathTest;
import org.smoothbuild.testing.problem.TestProblemsListener;

public class PathArgValidatorTest {
  TestProblemsListener problems = new TestProblemsListener();

  @Test
  public void missingDirArgIsReported() throws Exception {
    String name = "name";
    PathArgValidator.validatedPath(name, null, problems);
    problems.assertOnlyProblem(MissingRequiredArgError.class);
  }

  @Test
  public void illegalPathsAreReported() {
    String name = "name";
    for (String path : PathTest.listOfInvalidPaths()) {
      TestProblemsListener problems = new TestProblemsListener();
      PathArgValidator.validatedPath(name, path, problems);
      problems.assertOnlyProblem(IllegalPathError.class);
    }
  }

  @Test
  public void validPathsAreAccepted() {
    for (String path : PathTest.listOfCorrectPaths()) {
      TestProblemsListener problems = new TestProblemsListener();
      PathArgValidator.validatedPath("name", path, problems);
      problems.assertNoProblems();
    }
  }
}
