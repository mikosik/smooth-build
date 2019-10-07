package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.type.Instantiator;
import org.smoothbuild.lang.type.StructType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class Struct extends AbstractValue {
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

  public Value superValue() {
    ImmutableMap<String, Value> fields = fields();
    return fields.size() == 0 ? null : fields.values().iterator().next();
  }

  private ImmutableMap<String, Value> fields() {
    if (fields == null) {
      List<HashCode> hashes = readHashes();
      ImmutableMap<String, Field> fieldTypes = type().fields();
      if (hashes.size() != fieldTypes.size()) {
        throw corruptedValueException(hash(), "Its type is " + type() + " with "
            + fieldTypes.size() + " fields but its data hash Merkle tree contains "
            + hashes.size() + " children.");
      }
      int i = 0;
      Builder<String, Value> builder = ImmutableMap.builder();
      for (Map.Entry<String, Field> entry : fieldTypes.entrySet()) {
        Value value = instantiator.instantiate(hashes.get(i));
        if (!entry.getValue().type().equals(value.type())) {
          throw corruptedValueException(hash(), "Its type specifies field '" + entry.getKey()
              + "' with type " + entry.getValue().type() + " but its data has value of type "
              + value.type() + " assigned to that field.");
        }
        builder.put(entry.getKey(), value);
        i++;
      }
      fields = builder.build();
    }
    return fields;
  }

  private List<HashCode> readHashes() {
    try {
      return hashedDb.readHashes(dataHash());
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }
}
