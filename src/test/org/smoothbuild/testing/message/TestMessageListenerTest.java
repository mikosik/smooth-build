package org.smoothbuild.testing.message;

import static org.smoothbuild.message.listen.MessageType.ERROR;
import static org.smoothbuild.message.listen.MessageType.INFO;
import static org.smoothbuild.message.listen.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.message.Info;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.Warning;

public class TestMessageListenerTest {
  TestMessageListener testingProblemListener = new TestMessageListener();

  @Test(expected = AssertionError.class)
  public void problemsFoundFailsWhenNothingFound() throws Exception {
    testingProblemListener.assertProblemsFound();
  }

  @Test(expected = AssertionError.class)
  public void problemsFoundFailsWhenInfoWasReported() throws Exception {
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
  public void assertingThatOnlyOneInfoExistsSucceedsWhenOneExists() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenNoOneExists() {
    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenTwoProblemsExist() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.report(new MyInfo());

    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenOneProblemOfWrongTypeExists() {
    testingProblemListener.report(new Info("message"));
    testingProblemListener.assertOnlyInfo(MyInfo.class);
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

  private static class MyInfo extends Message {
    public MyInfo() {
      super(INFO, "message");
    }
  }
}
