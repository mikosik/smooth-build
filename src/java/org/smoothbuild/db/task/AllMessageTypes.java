package org.smoothbuild.db.task;

import org.smoothbuild.db.hash.EnumValues;
import org.smoothbuild.message.message.MessageType;

public class AllMessageTypes extends EnumValues<MessageType> {
  public static final AllMessageTypes INSTANCE = new AllMessageTypes();

  private AllMessageTypes() {
    super(MessageType.values());
  }
}
