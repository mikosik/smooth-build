package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.lang.object.db.ObjectsDbException.corruptedObjectException;
import static org.smoothbuild.lang.object.db.ObjectsDbException.objectsDbException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.StructType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Struct extends SObjectImpl {
  private ImmutableMap<String, SObject> fields;
  private final ObjectsDb objectsDb;

  public Struct(Hash dataHash, StructType type, ObjectsDb objectsDb, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    this.objectsDb = objectsDb;
  }

  @Override
  public StructType type() {
    return (StructType) super.type();
  }

  public SObject get(String name) {
    ImmutableMap<String, SObject> fields = fields();
    checkArgument(fields.containsKey(name), name);
    return fields.get(name);
  }

  public SObject superObject() {
    ImmutableMap<String, SObject> fields = fields();
    return fields.size() == 0 ? null : fields.values().iterator().next();
  }

  private ImmutableMap<String, SObject> fields() {
    if (fields == null) {
      List<Hash> hashes = readHashes();
      ImmutableMap<String, Field> fieldTypes = type().fields();
      if (hashes.size() != fieldTypes.size()) {
        throw corruptedObjectException(hash(), "Its type is " + type() + " with "
            + fieldTypes.size() + " fields but its data hash Merkle tree contains "
            + hashes.size() + " children.");
      }
      int i = 0;
      Builder<String, SObject> builder = ImmutableMap.builder();
      for (Map.Entry<String, Field> entry : fieldTypes.entrySet()) {
        SObject object = objectsDb.get(hashes.get(i));
        if (!entry.getValue().type().equals(object.type())) {
          throw corruptedObjectException(hash(), "Its type specifies field '" + entry.getKey()
              + "' with type " + entry.getValue().type() + " but its data has object of type "
              + object.type() + " assigned to that field.");
        }
        builder.put(entry.getKey(), object);
        i++;
      }
      fields = builder.build();
    }
    return fields;
  }

  private List<Hash> readHashes() {
    try {
      return hashedDb.readHashes(dataHash());
    } catch (IOException e) {
      throw objectsDbException(e);
    }
  }
}
