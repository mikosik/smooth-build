package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.Message.ERROR;
import static org.smoothbuild.lang.message.Message.WARNING;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;

public class MessagesDb {
  public static final String SEVERITY = "severity";
  public static final String TEXT = "text";
  private final ValuesDb valuesDb;
  private final Types types;

  public MessagesDb() {
    this(new HashedDb());
  }

  public MessagesDb(HashedDb hashedDb) {
    this(new ValuesDb(hashedDb), new RuntimeTypes(new TypesDb(hashedDb)));
  }

  public MessagesDb(ValuesDb valuesDb, RuntimeTypes types) {
    this(valuesDb, (Types) types);
    types.struct("Message", ImmutableMap.of(TEXT, types.string(), SEVERITY, types.string()));
  }

  @Inject
  public MessagesDb(ValuesDb valuesDb, Types types) {
    this.valuesDb = valuesDb;
    this.types = types;
  }

  public StructType messageType() {
    return (StructType) types.getType("Message");
  }

  public Message error(String message) {
    return newMessage(message, ERROR);
  }

  public Message warning(String message) {
    return newMessage(message, WARNING);
  }

  public Message info(String message) {
    return newMessage(message, Message.INFO);
  }

  private Message newMessage(String text, String severity) {
    Value textValue = valuesDb.string(text);
    Value severityValue = valuesDb.string(severity);
    Value message = valuesDb.structBuilder(messageType())
        .set(TEXT, textValue)
        .set(SEVERITY, severityValue)
        .build();
    return new Message(text, severity, message);
  }
}
