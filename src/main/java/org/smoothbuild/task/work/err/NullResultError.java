package org.smoothbuild.task.work.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class NullResultError extends Message {
  public NullResultError() {
    super(ERROR, "Faulty function implementation : it returned 'null' but logged no error.");
  }
}
