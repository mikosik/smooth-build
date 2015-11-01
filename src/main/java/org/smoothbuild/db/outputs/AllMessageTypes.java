package org.smoothbuild.db.outputs;

import org.smoothbuild.db.hashed.EnumValues;
import org.smoothbuild.lang.message.MessageType;

public class AllMessageTypes extends EnumValues<MessageType> {
  public static final AllMessageTypes INSTANCE = new AllMessageTypes();

  private AllMessageTypes() {
    super(MessageType.values());
  }
}
