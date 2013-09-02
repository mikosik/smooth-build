package org.smoothbuild.testing.problem;

import static org.smoothbuild.problem.ProblemType.ERROR;

import org.junit.Test;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.Warning;

public class TestingProblemsListenerTest {
  TestingProblemsListener testingProblemListener = new TestingProblemsListener();

  @Test(expected = AssertionError.class)
  public void problemsFoundFailesWhenNothingFound() throws Exception {
    testingProblemListener.assertProblemsFound();
  }

  @Test
  public void problemsFoundSucceedsWhenProblemsWereReported() throws Exception {
    testingProblemListener.report(new MyProblem());
    testingProblemListener.assertProblemsFound();
  }

  @Test
  public void assertingThatOnlyOneProblemExistsSucceedsWhenOneExists() {
    testingProblemListener.report(new MyProblem());
    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenNoOneExists() {
    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenTwoProblemsExist() {
    testingProblemListener.report(new MyProblem());
    testingProblemListener.report(new MyProblem());

    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenOneProblemOfWrongTypeExists() {
    testingProblemListener.report(new Warning("message"));

    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test
  public void assertingThatNoProblemExistsSucceedsWhenZeroExists() throws Exception {
    testingProblemListener.assertNoProblems();
  }

  @Test(expected = AssertionError.class)
  public void assertingThatNoProblemExistsFailsWhenOneExist() throws Exception {
    testingProblemListener.report(new MyProblem());
    testingProblemListener.assertNoProblems();
  }

  private static class MyProblem extends Problem {
    public MyProblem() {
      super(ERROR, "message");
    }
  }
}
