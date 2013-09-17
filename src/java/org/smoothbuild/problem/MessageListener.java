package org.smoothbuild.problem;

import com.google.inject.ImplementedBy;

@ImplementedBy(PrintingMessageListener.class)
public interface MessageListener {
  public void report(Message message);
}
