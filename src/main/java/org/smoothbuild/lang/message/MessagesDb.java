package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.Messages.ERROR;
import static org.smoothbuild.lang.message.Messages.INFO;
import static org.smoothbuild.lang.message.Messages.SEVERITY;
import static org.smoothbuild.lang.message.Messages.TEXT;
import static org.smoothbuild.lang.message.Messages.WARNING;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.value.Value;

public class MessagesDb {

  private final ValuesDb valuesDb;
  private final Types types;

  @Inject
  public MessagesDb(ValuesDb valuesDb, Types types) {
    this.valuesDb = valuesDb;
    this.types = types;
  }

  public Value error(String message) {
    return newMessage(message, ERROR);
  }

  public Value warning(String message) {
    return newMessage(message, WARNING);
  }

  public Value info(String message) {
    return newMessage(message, INFO);
  }

  private Value newMessage(String text, String severity) {
    Value textValue = valuesDb.string(text);
    Value severityValue = valuesDb.string(severity);
    return valuesDb.structBuilder(types.message())
        .set(TEXT, textValue)
        .set(SEVERITY, severityValue)
        .build();
  }
}
