package org.smoothbuild.testing.message;

import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.INFO;
import static org.smoothbuild.message.message.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.Message;

public class FakeUserConsoleTest {
  FakeUserConsole fakeUserConsole = new FakeUserConsole();
  CodeLocation location = new FakeCodeLocation();

  @Test(expected = AssertionError.class)
  public void problemsFoundFailsWhenNothingFound() throws Exception {
    fakeUserConsole.assertProblemsFound();
  }

  @Test(expected = AssertionError.class)
  public void problemsFoundFailsWhenInfoWasReported() throws Exception {
    fakeUserConsole.report(new Message(INFO, "message"));
    fakeUserConsole.assertProblemsFound();
  }

  @Test
  public void problemsFoundSucceedsWhenErrorWasReported() throws Exception {
    fakeUserConsole.report(new Message(ERROR, "message"));
    fakeUserConsole.assertProblemsFound();
  }

  @Test
  public void problemsFoundSucceedsWhenWarningWasReported() throws Exception {
    fakeUserConsole.report(new Message(WARNING, "message"));
    fakeUserConsole.assertProblemsFound();
  }

  @Test
  public void assertingThatOnlyOneInfoExistsSucceedsWhenOneExists() {
    fakeUserConsole.report(new MyInfo());
    fakeUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenNoOneExists() {
    fakeUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenTwoProblemsExist() {
    fakeUserConsole.report(new MyInfo());
    fakeUserConsole.report(new MyInfo());

    fakeUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneInfoExistsFailsWhenOneProblemOfWrongTypeExists() {
    fakeUserConsole.report(new Message(INFO, "message"));
    fakeUserConsole.assertOnlyInfo(MyInfo.class);
  }

  @Test
  public void assertingThatOnlyOneProblemExistsSucceedsWhenOneExists() {
    fakeUserConsole.report(new MyProblem());
    fakeUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenNoOneExists() {
    fakeUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenTwoProblemsExist() {
    fakeUserConsole.report(new MyProblem());
    fakeUserConsole.report(new MyProblem());

    fakeUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assertingThatOnlyOneProblemExistsFailsWhenOneProblemOfWrongTypeExists() {
    fakeUserConsole.report(new Message(WARNING, "message"));

    fakeUserConsole.assertOnlyProblem(MyProblem.class);
  }

  @Test
  public void assertingThatNoProblemExistsSucceedsWhenZeroExists() throws Exception {
    fakeUserConsole.assertNoProblems();
  }

  @Test(expected = AssertionError.class)
  public void assertingThatNoProblemExistsFailsWhenOneExist() throws Exception {
    fakeUserConsole.report(new MyProblem());
    fakeUserConsole.assertNoProblems();
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
