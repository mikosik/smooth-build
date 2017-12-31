package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.lang.type.Instantiator;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class Struct extends Value {
  private ImmutableMap<String, Value> fields;
  private final Instantiator instantiator;

  public Struct(HashCode dataHash, StructType type, Instantiator instantiator, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    this.instantiator = instantiator;
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
      List<HashCode> hashes = hashedDb.readHashes(dataHash());
      ImmutableMap<String, Type> fieldTypes = type().fields();
      if (hashes.size() != fieldTypes.size()) {
        throw new CorruptedValueException(hash(), "Its type is " + type() + " with "
            + fieldTypes.size() + " fields but its data hash merkle tree contains "
            + hashes.size() + " children.");
      }
      int i = 0;
      Builder<String, Value> builder = ImmutableMap.builder();
      for (Map.Entry<String, Type> entry : fieldTypes.entrySet()) {
        Value value = instantiator.instantiate(hashes.get(i));
        if (!entry.getValue().equals(value.type())) {
          throw new CorruptedValueException(hash(),
              "Its type specifies field '" + entry.getKey() + "' with type " + entry.getValue()
                  + " but its data has value of type " + value.type() + " assigned to that field.");
        }
        builder.put(entry.getKey(), value);
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
