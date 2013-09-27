package org.smoothbuild.message.listen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.smoothbuild.message.message.Message;

public class ForwardingMessageListenerTest {

  @Test
  public void messagesAreForwarded() {
    MessageListener wrapped = mock(MessageListener.class);
    Message message = new Message(MessageType.ERROR, "message");

    MyForwardingMessageListener listener = new MyForwardingMessageListener(wrapped);
    listener.report(message);

    verify(wrapped).report(message);
    assertThat(listener.getOnForwardMessage()).isSameAs(message);
  }

  public static class MyForwardingMessageListener extends ForwardingMessageListener {
    private Message message;

    public MyForwardingMessageListener(MessageListener wrapped) {
      super(wrapped);
    }

    @Override
    protected void onForward(Message message) {
      this.message = message;
    }

    public Message getOnForwardMessage() {
      return message;
    }
  }
}
