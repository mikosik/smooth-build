package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class Struct extends Value {
  private ImmutableMap<String, Value> fields;

  public Struct(HashCode hash, StructType type, HashedDb hashedDb) {
    super(hash, type, hashedDb);
  }

  @Override
  public StructType type() {
    return (StructType) super.type();
  }

  public Value get(String name) {
    ImmutableMap<String, Value> fields = fields();
    checkArgument(fields.containsKey(name), name);
    return fields.get(name);
  }

  private ImmutableMap<String, Value> fields() {
    if (fields == null) {
      List<HashCode> hashes = hashedDb.readHashes(hash());
      ImmutableMap<String, Type> fieldTypes = type().fields();
      if (hashes.size() != fieldTypes.size()) {
        throw new HashedDbException("Corrupted struct data");
      }
      int i = 0;
      Builder<String, Value> builder = ImmutableMap.builder();
      for (Map.Entry<String, Type> entry : fieldTypes.entrySet()) {
        builder.put(entry.getKey(), entry.getValue().newValue(hashes.get(i)));
        i++;
      }
      fields = builder.build();
    }
    return fields;
  }

  @Override
  public String toString() {
    return type().name()
        + "("
        + fields()
            .entrySet()
            .stream()
            .map(f -> f.getKey() + "=" + f.getValue().toString())
            .collect(joining(", "))
        + ")";
  }
}
