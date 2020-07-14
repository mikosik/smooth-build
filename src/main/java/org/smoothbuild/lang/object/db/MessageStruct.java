package org.smoothbuild.lang.object.db;

import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.base.Struct;

public class MessageStruct {
  private static final int TEXT_INDEX = 0;
  private static final int SEVERITY_INDEX = 1;

  public static SString messageText(Struct message) {
    return (SString) message.get(TEXT_INDEX);
  }

  public static SString messageSeverity(Struct message) {
    return (SString) message.get(SEVERITY_INDEX);
  }
}
