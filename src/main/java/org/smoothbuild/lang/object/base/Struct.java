package org.smoothbuild.lang.object.base;

import static java.util.Objects.checkIndex;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectDbException;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.StructType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Struct extends SObjectImpl {
  private ImmutableList<SObject> fields;
  private final ObjectDb objectDb;

  public Struct(MerkleRoot merkleRoot, ObjectDb objectDb, HashedDb hashedDb) {
    super(merkleRoot, hashedDb);
    this.objectDb = objectDb;
  }

  @Override
  public StructType type() {
    return (StructType) super.type();
  }

  public SObject get(int index) {
    ImmutableList<SObject> fields = fields();
    checkIndex(index, fields.size());
    return fields.get(index);
  }

  public SObject superObject() {
    ImmutableList<SObject> fields = fields();
    return fields.size() == 0 ? null : fields.iterator().next();
  }

  private ImmutableList<SObject> fields() {
    if (fields == null) {
      var fieldTypes = type().fieldTypes();
      var fieldHashes = readFieldHashes(fieldTypes);
      if (fieldTypes.size() != fieldHashes.size()) {
        throw new ObjectDbException(hash(), "Its type (Struct) specifies " + fieldTypes.size()
            + " fields but its data points to" + fieldHashes.size() + "  fields.");
      }
      var builder = ImmutableList.<SObject>builder();
      for (int i = 0; i < fieldTypes.size(); i++) {
        SObject object = objectDb.get(fieldHashes.get(i));
        ConcreteType type = fieldTypes.get(i);
        if (type.equals(object.type())) {
          builder.add(object);
        } else {
          throw new ObjectDbException(hash(), "Its type (Struct) specifies field at index " + i
              + " with type " + type + " but its data has object of type " + object.type()
              + " at that index.");
        }
      }
      fields = builder.build();
    }
    return fields;
  }

  private List<Hash> readFieldHashes(final ImmutableList<ConcreteType> fieldTypes) {
    try {
      return hashedDb.readHashes(dataHash(), fieldTypes.size());
    } catch (HashedDbException e) {
      throw new ObjectDbException(hash(), "Error reading field hashes.", e);
    }
  }
}
