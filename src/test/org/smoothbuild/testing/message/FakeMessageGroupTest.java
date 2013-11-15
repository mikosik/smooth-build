package org.smoothbuild.testing.message;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.SUGGESTION;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;

public class FakeMessageGroupTest {
  FakeMessageGroup testingProblemListener = new FakeMessageGroup();
  CodeLocation location = new FakeCodeLocation();

  // assertProblemsFound()

  @Test(expected = AssertionError.class)
  public void assert_problems_found_fails_when_nothing_was_reported() throws Exception {
    testingProblemListener.assertProblemsFound();
  }

  @Test(expected = AssertionError.class)
  public void assert_problems_found_fails_when_info_was_reported() throws Exception {
    testingProblemListener.report(new Message(INFO, "message"));
    testingProblemListener.assertProblemsFound();
  }

  @Test(expected = AssertionError.class)
  public void assert_problems_found_fails_when_suggestion_was_reported() throws Exception {
    testingProblemListener.report(new Message(SUGGESTION, "message"));
    testingProblemListener.assertProblemsFound();
  }

  @Test(expected = AssertionError.class)
  public void assert_problems_found_fails_when_warning_was_reported() throws Exception {
    testingProblemListener.report(new Message(WARNING, "message"));
    testingProblemListener.assertProblemsFound();
  }

  @Test
  public void assert_problems_found_succeeds_when_error_was_reported() throws Exception {
    testingProblemListener.report(new Message(ERROR, "message"));
    testingProblemListener.assertProblemsFound();
  }

  @Test
  public void assert_problems_found_succeeds_when_fatal_was_reported() throws Exception {
    testingProblemListener.report(new Message(FATAL, "message"));
    testingProblemListener.assertProblemsFound();
  }

  // assertOnlyInfo()

  @Test
  public void assert_only_info_succeeds_when_one_info_was_reported() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_info_fails_when_nothing_was_reported() {
    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_info_fails_when_two_infos_were_reported() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.report(new MyInfo());

    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_info_fails_when_info_of_different_type_was_reported() {
    testingProblemListener.report(new Message(INFO, "message"));
    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_info_fails_when_warning_was_reported() {
    testingProblemListener.report(new Message(WARNING, "message"));
    testingProblemListener.assertOnlyInfo(MyInfo.class);
  }

  // assertOnlyProblem()

  @Test
  public void assert_only_problem_succeeds_when_one_problem_was_reported() {
    testingProblemListener.report(new MyProblem());
    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_problem_fails_when_nothing_was_reported() {
    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_problem_fails_when_two_problems_were_reported() {
    testingProblemListener.report(new MyProblem());
    testingProblemListener.report(new MyProblem());

    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_problem_fails_when_problem_of_different_type_was_reported() {
    testingProblemListener.report(new Message(ERROR, "message"));
    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_only_problem_fails_when_info_was_reported() {
    testingProblemListener.report(new Message(INFO, "message"));
    testingProblemListener.assertOnlyProblem(MyProblem.class);
  }

  // assertNoProblems()

  @Test
  public void assert_no_problems_succeeds_when_nothing_was_reported() throws Exception {
    testingProblemListener.assertNoProblems();
  }

  @Test
  public void assert_no_problems_succeeds_when_warning_was_reported() throws Exception {
    testingProblemListener.report(new Message(WARNING, "message"));
    testingProblemListener.assertNoProblems();
  }

  @Test(expected = AssertionError.class)
  public void assert_no_problems_fails_when_on_problem_was_reported() throws Exception {
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
