package org.smoothbuild.testing.message;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;

import java.util.List;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.util.LineBuilder;

import com.google.common.collect.Lists;

public class FakeMessageGroup extends MessageGroup {
  private final List<Message> messages = Lists.newArrayList();

  public FakeMessageGroup() {
    super("TestMessageGroup");
  }

  @Override
  public void report(Message message) {
    messages.add(message);
    super.report(message);
  }

  public void assertContainsOnly(Class<? extends Message> messageClass) {
    if (messages.size() != 1) {
      throw new AssertionError("Expected one message ,\nbut got:\n" + messagesToString());
    }
    assertContains(messageClass);
  }

  public void assertContains(Class<? extends Message> messageClass) {
    for (Message message : messages) {
      if (messageClass.isInstance(message)) {
        return;
      }
    }
    throw new AssertionError("Expected messages to contain " + messageClass.getSimpleName() + "\n"
        + "but got:\n" + messagesToString());
  }

  public void assertNoProblems() {
    for (Message message : messages) {
      MessageType type = message.type();
      if (type == FATAL || type == ERROR) {
        throw new AssertionError("Expected zero problems,\nbut got:\n" + messagesToString());
      }
    }
  }

  private String messagesToString() {
    LineBuilder builder = new LineBuilder();
    for (Message message : messages) {
      builder.addLine(message.getClass().getSimpleName());
      builder.addLine(message.toString());
    }
    return builder.build();
  }
}
