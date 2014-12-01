package org.smoothbuild.testing.message;

import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.message.base.MessageType.INFO;
import static org.smoothbuild.message.base.MessageType.WARNING;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;

public class FakeLoggedMessagesTest {
  CodeLocation location = codeLocation(1);
  FakeLoggedMessages fakeLoggedMessages = new FakeLoggedMessages();

  // assertContainsOnly()

  @Test
  public void assert_contais_only_succeeds_when_one_required_message_was_logged() {
    fakeLoggedMessages.log(new MyInfo());
    fakeLoggedMessages.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_only_fails_when_nothing_was_logged() {
    fakeLoggedMessages.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_only_fails_when_required_message_was_logged_twice() {
    fakeLoggedMessages.log(new MyInfo());
    fakeLoggedMessages.log(new MyInfo());

    fakeLoggedMessages.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_only_fails_when_info_of_different_type_was_logged() {
    fakeLoggedMessages.log(new Message(INFO, "message"));
    fakeLoggedMessages.assertContainsOnly(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_cotais_only_info_fails_when_warning_was_logged() {
    fakeLoggedMessages.log(new Message(WARNING, "message"));
    fakeLoggedMessages.assertContainsOnly(MyInfo.class);
  }

  // assertContains()

  @Test
  public void assert_contais_succeeds_when_one_required_message_was_logged() {
    fakeLoggedMessages.log(new MyInfo());
    fakeLoggedMessages.assertContains(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_fails_when_nothing_was_logged() {
    fakeLoggedMessages.assertContains(MyInfo.class);
  }

  public void assert_contains_succeeds_when_required_message_was_logged_twice() {
    fakeLoggedMessages.log(new MyInfo());
    fakeLoggedMessages.log(new MyInfo());

    fakeLoggedMessages.assertContains(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_contains_fails_when_info_of_different_type_was_logged() {
    fakeLoggedMessages.log(new Message(INFO, "message"));
    fakeLoggedMessages.assertContains(MyInfo.class);
  }

  @Test(expected = AssertionError.class)
  public void assert_cotais_info_fails_when_warning_was_logged() {
    fakeLoggedMessages.log(new Message(WARNING, "message"));
    fakeLoggedMessages.assertContains(MyInfo.class);
  }

  // assertNoProblems()

  @Test
  public void assert_no_problems_succeeds_when_nothing_was_logged() throws Exception {
    fakeLoggedMessages.assertNoProblems();
  }

  @Test
  public void assert_no_problems_succeeds_when_warning_was_logged() throws Exception {
    fakeLoggedMessages.log(new Message(WARNING, "message"));
    fakeLoggedMessages.assertNoProblems();
  }

  @Test(expected = AssertionError.class)
  public void assert_no_problems_fails_when_on_error_was_logged() throws Exception {
    fakeLoggedMessages.log(new Message(ERROR, ""));
    fakeLoggedMessages.assertNoProblems();
  }

  @Test(expected = AssertionError.class)
  public void assert_no_problems_fails_when_on_fatal_was_logged() throws Exception {
    fakeLoggedMessages.log(new Message(FATAL, ""));
    fakeLoggedMessages.assertNoProblems();
  }

  private static class MyInfo extends Message {
    public MyInfo() {
      super(INFO, "message");
    }
  }
}
