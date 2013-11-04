package org.smoothbuild.testing.message;

import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.Message;

public class FakeUserConsoleTest {
  FakeUserConsole fakeUserConsole = new FakeUserConsole();
  CodeLocation location = new FakeCodeLocation();

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
}
