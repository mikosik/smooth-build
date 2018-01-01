package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.StructType;

import com.google.common.hash.HashCode;

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
    List<String> names = fieldNames();
    List<String> unspecified = names
        .stream()
        .filter(e -> !fields.containsKey(e))
        .collect(toImmutableList());
    if (0 < unspecified.size()) {
      throw new IllegalStateException("Field " + unspecified.get(0) + " hasn't been specified.");
    }
    HashCode[] hashes = names
        .stream()
        .map(name -> fields.get(name).hash())
        .toArray(HashCode[]::new);
    HashCode hash = hashedDb.writeHashes(hashes);
    return new Struct(hash, type, hashedDb);
  }

  private List<String> fieldNames() {
    return type
        .fields()
        .entrySet()
        .stream()
        .map(e -> e.getKey())
        .collect(toImmutableList());
  }
}
