package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.Message.ERROR;
import static org.smoothbuild.lang.message.Message.INFO;
import static org.smoothbuild.lang.message.Message.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.TestingHashedDb;

public class MessagesDbTest {
  private MessagesDb messagesDb;
  private Message message;

  @Before
  public void before() {
    messagesDb = new TestingMessagesDb(new TestingHashedDb());
  }

  @Test
  public void error_severity_is_error() throws Exception {
    given(message = messagesDb.error("text"));
    when(() -> message.severity());
    thenReturned(ERROR);
  }

  @Test
  public void warning_severity_is_warning() throws Exception {
    given(message = messagesDb.warning("text"));
    when(() -> message.severity());
    thenReturned(WARNING);
  }

  @Test
  public void info_severity_is_info() throws Exception {
    given(message = messagesDb.info("text"));
    when(() -> message.severity());
    thenReturned(INFO);
  }

  @Test
  public void text_returns_text() throws Exception {
    given(message = messagesDb.error("text"));
    when(() -> message.text());
    thenReturned("text");
  }

  @Test
  public void smooth_type_of_error_is_message() throws Exception {
    given(message = messagesDb.error("text"));
    when(() -> message.value().type());
    thenReturned(messagesDb.messageType());
  }

  @Test
  public void smooth_type_of_warning_is_message() throws Exception {
    given(message = messagesDb.warning("text"));
    when(() -> message.value().type());
    thenReturned(messagesDb.messageType());
  }

  @Test
  public void smooth_type_of_info_is_message() throws Exception {
    given(message = messagesDb.info("text"));
    when(() -> message.value().type());
    thenReturned(messagesDb.messageType());
  }
}
