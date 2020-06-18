package org.smoothbuild.lang.object.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectDbException;
import org.smoothbuild.lang.object.type.Field;
import org.smoothbuild.lang.object.type.StructType;

import com.google.common.collect.ImmutableMap;

/**
 * This class is immutable.
 */
public class Struct extends SObjectImpl {
  private ImmutableMap<String, SObject> fields;
  private final ObjectDb objectDb;

  public Struct(MerkleRoot merkleRoot, ObjectDb objectDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.objectDb = objectDb;
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
      try {
        ImmutableMap<String, Field> fieldTypes = type().fields();
        List<Hash> hashes = hashedDb.readHashes(dataHash(), fieldTypes.size());
        int i = 0;
        ImmutableMap.Builder<String, SObject> builder = ImmutableMap.builder();
        for (Map.Entry<String, Field> entry : fieldTypes.entrySet()) {
          SObject object = objectDb.get(hashes.get(i));
          if (!entry.getValue().type().equals(object.type())) {
            throw new ObjectDbException(hash(), "It" +
                "s type specifies field '" + entry.getKey()
                + "' with type " + entry.getValue().type() + " but its data has object of type "
                + object.type() + " assigned to that field.");
          }
          builder.put(entry.getKey(), object);
          i++;
        }
        fields = builder.build();
      } catch (HashedDbException e) {
        throw new ObjectDbException(hash(), e);
      }
    }
    return fields;
  }
}
