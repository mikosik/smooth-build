package org.smoothbuild.task.base.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class NullResultError extends Message {
  public NullResultError() {
    super(ERROR, "Faulty function implementation : it returned 'null' but logged no error.");
  }
}
