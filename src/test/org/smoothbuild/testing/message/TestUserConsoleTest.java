package org.smoothbuild.testing.message;

import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.WrappedCodeMessage;

public class TestUserConsoleTest {
  TestUserConsole testUserConsole = new TestUserConsole();
  CodeLocation location = codeLocation(1, 2, 4);

  @Test(expected = AssertionError.class)
  public void problemsFoundFailsWhenNothingFound() throws Exception {
    testUserConsole.assertProblemsFound();
  }

  @Test(expected = AssertionError.class)
  public void problemsFoundFailsWhenInfoWasReported() throws Exception {
    testUserConsole.report(new Message(INFO, "message"));
    testUserConsole.assertProblemsFound();
  }

  @Test
  public void problemsFoundSucceedsWhenErrorWasReported() throws Exception {
    testUserConsole.report(new Message(ERROR, "message"));
    testUserConsole.assertProblemsFound();
  }

  @Test
  public void problemsFoundSucceedsWhenWarningWasReported() throws Exception {
    testUserConsole.report(new Message(WARNING, "message"));
    testUserConsole.assertProblemsFound();
  }

  @Test
  public void assertingThatOnlyOneInfoExistsSucceedsWhenOneExists() {
    testUserConsole.report(new MyInfo());
    testUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenNoOneExists() {
    testUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenTwoProblemsExist() {
    testUserConsole.report(new MyInfo());
    testUserConsole.report(new MyInfo());

    testUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenOneProblemOfWrongTypeExists() {
    testUserConsole.report(new Message(INFO, "message"));
    testUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test
  public void assertingThatOnlyOneProblemExistsSucceedsWhenOneExists() {
    testUserConsole.report(new MyProblem());
    testUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenNoOneExists() {
    testUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenTwoProblemsExist() {
    testUserConsole.report(new MyProblem());
    testUserConsole.report(new MyProblem());

    testUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenOneProblemOfWrongTypeExists() {
    testUserConsole.report(new Message(WARNING, "message"));

    testUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test
  public void assertingThatOnlyOneProblemExistsSucceedsWhenWrappedOneExists() {
    testUserConsole.report(new WrappedCodeMessage(new MyProblem(), location));
    testUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenTwoWrappedProblemsExist() {
    testUserConsole.report(new WrappedCodeMessage(new MyProblem(), location));
    testUserConsole.report(new WrappedCodeMessage(new MyProblem(), location));

    testUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenOneWrappedProblemOfWrongTypeExists() {
    testUserConsole.report(new WrappedCodeMessage(new Message(WARNING, ""), location));

    testUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test
  public void assertingThatNoProblemExistsSucceedsWhenZeroExists() throws Exception {
    testUserConsole.assertNoProblems();
  }

  @Test(expected = AssertionError.class)
  public void assertingThatNoProblemExistsFailsWhenOneExist() throws Exception {
    testUserConsole.report(new MyProblem());
    testUserConsole.assertNoProblems();
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
