package org.smoothbuild.message.listen;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.unmodifiableIterator;

import java.util.Iterator;
import java.util.List;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;

import com.google.common.collect.Lists;

public class MessageGroup implements MessageListener, Iterable<Message> {
  private final String name;
  private final List<Message> messages;
  private boolean isErrorReported;

  public MessageGroup(String name) {
    this.name = name;
    this.messages = Lists.newArrayList();
  }

  public String name() {
    return name;
  }

  @Override
  public Iterator<Message> iterator() {
    return unmodifiableIterator(messages.iterator());
  }

  @Override
  public void report(Message message) {
    checkNotNull(message);
    messages.add(message);
    if (message.type() == MessageType.ERROR) {
      isErrorReported = true;
    }
  }

  public boolean isErrorReported() {
    return isErrorReported;
  }
}
