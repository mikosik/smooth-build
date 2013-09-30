package org.smoothbuild.message.message;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.message.listen.MessageType.WARNING;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.message.listen.MessageType;

public class WrappedCodeMessageTest {
  String messageString = "message string";
  MessageType type = WARNING;
  Message message = new Message(type, messageString);
  CodeLocation codeLocation = codeLocation(1, 2, 4);
  WrappedCodeMessage wrappedCodeMessage;

  @Test
  public void messageReturnsMessageOfWrappedMessage() throws Exception {
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
