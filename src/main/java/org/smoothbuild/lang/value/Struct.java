package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Unmarshaller;
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
      try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(hash())) {
        Builder<String, Value> builder = ImmutableMap.builder();
        for (Map.Entry<String, Type> entry : type().fields().entrySet()) {
          builder.put(entry.getKey(), entry.getValue().newValue(unmarshaller.readHash()));
        }
        fields = builder.build();
      }
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
