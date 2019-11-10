package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.lang.object.type.StructType;

public class StructBuilder {
  private final StructType type;
  private final HashedDb hashedDb;
  private final Map<String, SObject> fields;

  public StructBuilder(StructType type, HashedDb hashedDb) {
    this.type = type;
    this.hashedDb = hashedDb;
    this.fields = new HashMap<>();
  }

  public StructBuilder set(String name, SObject object) {
    checkArgument(type.fields().containsKey(name), name);
    checkArgument(type.fields().get(name).type().equals(object.type()));
    fields.put(name, object);
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
    Hash[] fieldObjectHashes = fieldNames
        .stream()
        .map(name -> fields.get(name).hash())
        .toArray(Hash[]::new);
    Hash dataHash = writeHashes(fieldObjectHashes);
    return type.newSObject(dataHash);
  }

  private Hash writeHashes(Hash[] hashes) {
    try {
      return hashedDb.writeHashes(hashes);
    } catch (HashedDbException e) {
      throw new ObjectsDbException(e);
    }
  }
}
