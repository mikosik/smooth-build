package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;

public class StructBuilder {
  private final StructType type;
  private final HashedDb hashedDb;
  private final Map<String, Value> fields;

  public StructBuilder(StructType type, HashedDb hashedDb) {
    this.type = type;
    this.hashedDb = hashedDb;
    this.fields = new HashMap<>();
  }

  public StructBuilder set(String name, Value value) {
    checkArgument(type.fields().containsKey(name), name);
    checkArgument(type.fields().get(name).equals(value.type()));
    fields.put(name, value);
    return this;
  }

  public Struct build() {
    Marshaller marshaller = hashedDb.newMarshaller();
    for (Map.Entry<String, Type> entry : type.fields().entrySet()) {
      String name = entry.getKey();
      if (fields.containsKey(name)) {
        marshaller.writeHash(fields.get(name).hash());
      } else {
        throw new IllegalStateException("Field " + name + " hasn't been specified.");
      }
    }
    marshaller.close();
    return new Struct(type, marshaller.hash(), hashedDb);
  }
}
