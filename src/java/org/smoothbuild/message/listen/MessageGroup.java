package org.smoothbuild.message.listen;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.unmodifiableIterator;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageStats;
import org.smoothbuild.util.LineBuilder;

import com.google.common.collect.Lists;

public class MessageGroup implements Iterable<Message> {
  private final List<Message> messages;
  private final MessageStats stats;

  public MessageGroup() {
    this.messages = Lists.newArrayList();
    this.stats = new MessageStats();
  }

  @Override
  public Iterator<Message> iterator() {
    return unmodifiableIterator(messages.iterator());
  }

  public void report(Message message) {
    checkNotNull(message);
    messages.add(message);
    stats.incCount(message.type());
  }

  public boolean isEmpty() {
    return messages.isEmpty();
  }

  public boolean containsProblems() {
    return stats.containsProblems();
  }

  public void failIfContainsProblems() {
    if (containsProblems()) {
      throw new PhaseFailedException();
    }
  }

  public MessageStats stats() {
    return stats;
  }

  @Override
  public String toString() {
    LineBuilder builder = new LineBuilder();
    for (Message message : messages) {
      builder.addLine(message.toString());
    }
    return builder.build();
  }
}
