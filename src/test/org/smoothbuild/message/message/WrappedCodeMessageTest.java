package org.smoothbuild.message.message;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.message.message.MessageType.WARNING;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

public class WrappedCodeMessageTest {
  String messageString;
  MessageType type = WARNING;
  Message message;
  CodeLocation codeLocation;
  WrappedCodeMessage wrappedCodeMessage;

  @Before
  public void before() {
    givenTest(this);
    message = new Message(type, messageString);
  }

  @Test
  public void messageReturnsMessageOfWrappedMessage() throws Exception {
    given(message = new Message(type, messageString));
    given(wrappedCodeMessage = new WrappedCodeMessage(message, codeLocation));
    when(wrappedCodeMessage).message();
    thenReturned(messageString);
  }

  @Test
  public void codeLocationReturnsCodeLocationPassedToConstructor() throws Exception {
    given(wrappedCodeMessage = new WrappedCodeMessage(message, codeLocation));
    when(wrappedCodeMessage).codeLocation();
    thenReturned(codeLocation);
  }

  @Test
  public void typeReturnsTypeOfWrappedMessage() throws Exception {
    given(message = new Message(type, messageString));
    given(wrappedCodeMessage = new WrappedCodeMessage(message, codeLocation));
    when(wrappedCodeMessage).type();
    thenReturned(type);
  }

  @Test
  public void wrappedMessageReturnsWrappedMessage() throws Exception {
    given(wrappedCodeMessage = new WrappedCodeMessage(message, codeLocation));
    when(wrappedCodeMessage).wrappedMessage();
    thenReturned(sameInstance(message));
  }
}
