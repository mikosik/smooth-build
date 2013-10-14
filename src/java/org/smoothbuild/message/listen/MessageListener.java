package org.smoothbuild.message.listen;

import org.smoothbuild.message.message.Message;

public interface MessageListener {
  public void report(Message message);
}
