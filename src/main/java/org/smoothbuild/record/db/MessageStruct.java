package org.smoothbuild.record.db;

import org.smoothbuild.record.base.RString;
import org.smoothbuild.record.base.Tuple;

public class MessageStruct {
  private static final int TEXT_INDEX = 0;
  private static final int SEVERITY_INDEX = 1;

  public static RString messageText(Tuple message) {
    return (RString) message.get(TEXT_INDEX);
  }

  public static RString messageSeverity(Tuple message) {
    return (RString) message.get(SEVERITY_INDEX);
  }
}
