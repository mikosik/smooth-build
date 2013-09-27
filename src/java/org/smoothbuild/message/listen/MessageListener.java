package org.smoothbuild.message.listen;

import org.smoothbuild.message.message.Message;

import com.google.inject.ImplementedBy;

@ImplementedBy(PrintingMessageListener.class)
public interface MessageListener {
  public void report(Message message);
}
