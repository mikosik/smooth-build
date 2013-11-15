package org.smoothbuild.io.cache.task;

import org.smoothbuild.io.cache.hash.EnumValues;
import org.smoothbuild.message.base.MessageType;

public class AllMessageTypes extends EnumValues<MessageType> {
  public static final AllMessageTypes INSTANCE = new AllMessageTypes();

  private AllMessageTypes() {
    super(MessageType.values());
  }
}
