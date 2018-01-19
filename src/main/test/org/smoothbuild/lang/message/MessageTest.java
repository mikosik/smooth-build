package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.Message.ERROR;
import static org.smoothbuild.lang.message.Message.INFO;
import static org.smoothbuild.lang.message.Message.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class MessageTest {
  private String text;
  private Message message;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void null_text_is_forbidden() throws Exception {
    when(() -> new Message(null, ERROR, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void text_is_returned() {
    given(message = new Message(text, ERROR, null));
    when(() -> message.text());
    thenReturned(text);
  }

  @Test
  public void null_severity_is_forbidden() throws Exception {
    when(() -> new Message("text", null, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void invalid_severity_causes_exception() {
    when(() -> new Message(text, "invalid", null));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void severity_is_returned() {
    given(message = new Message(text, ERROR, null));
    when(() -> message.severity());
    thenReturned(ERROR);
  }

  @Test
  public void error_is_error() throws Exception {
    given(message = new Message("text", ERROR, null));
    when(() -> message.isError());
    thenReturned(true);
  }

  @Test
  public void error_is_not_warning() throws Exception {
    given(message = new Message("text", ERROR, null));
    when(() -> message.isWarning());
    thenReturned(false);
  }

  @Test
  public void error_is_not_info() throws Exception {
    given(message = new Message("text", ERROR, null));
    when(() -> message.isInfo());
    thenReturned(false);
  }

  @Test
  public void warning_is_not_error() throws Exception {
    given(message = new Message("text", WARNING, null));
    when(() -> message.isError());
    thenReturned(false);
  }

  @Test
  public void warning_is_warning() throws Exception {
    given(message = new Message("text", WARNING, null));
    when(() -> message.isWarning());
    thenReturned(true);
  }

  @Test
  public void warning_is_not_info() throws Exception {
    given(message = new Message("text", WARNING, null));
    when(() -> message.isInfo());
    thenReturned(false);
  }

  @Test
  public void info_is_not_error() throws Exception {
    given(message = new Message("text", INFO, null));
    when(() -> message.isError());
    thenReturned(false);
  }

  @Test
  public void info_is_not_warning() throws Exception {
    given(message = new Message("text", INFO, null));
    when(() -> message.isWarning());
    thenReturned(false);
  }

  @Test
  public void info_is_info() throws Exception {
    given(message = new Message("text", INFO, null));
    when(() -> message.isInfo());
    thenReturned(true);
  }

  @Test
  public void to_string() throws Exception {
    given(message = new Message("my-message", ERROR, null));
    when(message.toString());
    thenReturned("ERROR: my-message");
  }

  @Test
  public void equals_and_hash_code() throws Exception {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new Message("equal", WARNING, null),
        new Message("equal", WARNING, null));
    tester.addEqualityGroup(new Message("equal", ERROR, null));
    tester.addEqualityGroup(new Message("not equal", ERROR, null));
    tester.addEqualityGroup(new Message("equal", INFO, null));
    tester.addEqualityGroup(new Message("not equal", INFO, null));
    tester.testEquals();
  }
}
