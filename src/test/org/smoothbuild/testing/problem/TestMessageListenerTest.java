package org.smoothbuild.testing.problem;

import static org.smoothbuild.problem.MessageType.ERROR;
import static org.smoothbuild.problem.MessageType.INFO;
import static org.smoothbuild.problem.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.problem.Message;
import org.smoothbuild.problem.Warning;

public class TestMessageListenerTest {
  TestMessageListener testingProblemListener = new TestMessageListener();

  @Test(expected = AssertionError.class)
  public void problemsFoundFailesWhenNothingFound() throws Exception {
    testingProblemListener.assertProblemsFound();
  }

  @Test(expected = AssertionError.class)
  public void problemsFoundFailesWhenInfoWasReported() throws Exception {
    testingProblemListener.report(new Message(INFO, "message"));
    testingProblemListener.assertProblemsFound();
  }

  @Test
  public void problemsFoundSucceedsWhenErrorWasReported() throws Exception {
    testingProblemListener.report(new Message(ERROR, "message"));
    testingProblemListener.assertProblemsFound();
  }

  @Test
  public void problemsFoundSucceedsWhenWarningWasReported() throws Exception {
    testingProblemListener.report(new Message(WARNING, "message"));
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

  private static class MyProblem extends Message {
    public MyProblem() {
      super(ERROR, "message");
    }
  }
}
