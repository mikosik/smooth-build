package org.smoothbuild.message.listen;

import java.util.List;

import org.smoothbuild.message.message.Message;

import com.google.common.collect.Lists;

public class CollectingMessageListener implements MessageListener {
  private final List<Message> messages = Lists.newArrayList();

  @Override
  public void report(Message message) {
    messages.add(message);
  }

  public void reportCollectedMessagesTo(MessageListener listener) {
    for (Message message : messages) {
      listener.report(message);
    }
  }

  public boolean isErrorReported() {
    for (Message message : messages) {
      if (message.type() == MessageType.ERROR) {
        return true;
      }
    }
    return false;
  }
}
