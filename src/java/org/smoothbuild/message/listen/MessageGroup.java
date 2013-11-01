package org.smoothbuild.message.listen;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.unmodifiableIterator;
import static org.smoothbuild.message.message.MessageType.ERROR;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageStats;

import com.google.common.collect.Lists;

public class MessageGroup implements Iterable<Message> {
  private final String name;
  private final List<Message> messages;
  private final MessageStats messageStats;

  public MessageGroup(String name) {
    this.name = checkNotNull(name);
    this.messages = Lists.newArrayList();
    this.messageStats = new MessageStats();
  }

  public String name() {
    return name;
  }

  @Override
  public Iterator<Message> iterator() {
    return unmodifiableIterator(messages.iterator());
  }

  public void report(Message message) {
    checkNotNull(message);
    messages.add(message);
    messageStats.incCount(message.type());
  }

  public boolean containsMessages() {
    return !messages.isEmpty();
  }

  public boolean containsErrors() {
    return 0 < messageStats.getCount(ERROR);
  }

  public void failIfContainsErrors() {
    if (containsErrors()) {
      throw new PhaseFailedException();
    }
  }

  public MessageStats messageStats() {
    return messageStats;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Message message : messages) {
      builder.append(message.toString());
      builder.append("\n");
    }
    return builder.toString();
  }
}
