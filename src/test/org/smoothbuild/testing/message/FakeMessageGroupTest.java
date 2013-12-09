package org.smoothbuild.testing.message;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;

public class FakeMessageGroupTest {
  FakeMessageGroup testingProblemListener = new FakeMessageGroup();
  CodeLocation location = new FakeCodeLocation();

  // assertContainsOnly()

  @Test
  public void assert_contais_only_succeeds_when_one_required_message_was_reported() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_only_fails_when_nothing_was_reported() {
    testingProblemListener.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_only_fails_when_required_message_was_reported_twice() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.report(new MyInfo());

    testingProblemListener.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_only_fails_when_info_of_different_type_was_reported() {
    testingProblemListener.report(new Message(INFO, "message"));
    testingProblemListener.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_cotais_only_info_fails_when_warning_was_reported() {
    testingProblemListener.report(new Message(WARNING, "message"));
    testingProblemListener.assertContainsOnly(MyInfo.class);
  }

  // assertContains()

  @Test
  public void assert_contais_succeeds_when_one_required_message_was_reported() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.assertContains(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_fails_when_nothing_was_reported() {
    testingProblemListener.assertContains(MyInfo.class);
  }

  public void assert_contains_succeeds_when_required_message_was_reported_twice() {
    testingProblemListener.report(new MyInfo());
    testingProblemListener.report(new MyInfo());

    testingProblemListener.assertContains(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_fails_when_info_of_different_type_was_reported() {
    testingProblemListener.report(new Message(INFO, "message"));
    testingProblemListener.assertContains(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_cotais_info_fails_when_warning_was_reported() {
    testingProblemListener.report(new Message(WARNING, "message"));
    testingProblemListener.assertContains(MyInfo.class);
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
  public void assert_no_problems_fails_when_on_error_was_reported() throws Exception {
    testingProblemListener.report(new Message(ERROR, ""));
    testingProblemListener.assertNoProblems();
  }

  @Test(expected = AssertionError.class)
  public void assert_no_problems_fails_when_on_fatal_was_reported() throws Exception {
    testingProblemListener.report(new Message(FATAL, ""));
    testingProblemListener.assertNoProblems();
  }

  private static class MyInfo extends Message {
    public MyInfo() {
      super(INFO, "message");
    }
  }
}
