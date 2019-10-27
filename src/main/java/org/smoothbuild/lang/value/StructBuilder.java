package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.StructType;

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
    checkArgument(type.fields().get(name).type().equals(value.type()));
    fields.put(name, value);
    return this;
  }

  public Struct build() {
    Set<String> fieldNames = type.fields().keySet();
    List<String> unspecifiedNames = fieldNames
        .stream()
        .filter(e -> !fields.containsKey(e))
        .collect(toImmutableList());
    if (0 < unspecifiedNames.size()) {
      throw new IllegalStateException(
          "Field " + unspecifiedNames.get(0) + " hasn't been specified.");
    }
    Hash[] fieldValueHashes = fieldNames
        .stream()
        .map(name -> fields.get(name).hash())
        .toArray(Hash[]::new);
    Hash dataHash = writeHashes(fieldValueHashes);
    return type.newValue(dataHash);
  }

  private Hash writeHashes(Hash[] hashes) {
    try {
      return hashedDb.writeHashes(hashes);
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }
}
