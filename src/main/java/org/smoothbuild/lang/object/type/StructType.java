package org.smoothbuild.lang.object.type;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectDb;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class StructType extends ConcreteType {
  private final ImmutableList<ConcreteType> fieldTypes;

  public StructType(MerkleRoot merkleRoot, String name, Iterable<? extends ConcreteType> fieldTypes,
      HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, name, Struct.class, hashedDb, objectDb);
    this.fieldTypes = ImmutableList.copyOf(fieldTypes);
  }

  @Override
  public Struct newObject(MerkleRoot merkleRoot) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Struct(merkleRoot, objectDb, hashedDb);
  }

  public ImmutableList<ConcreteType> fieldTypes() {
    return fieldTypes;
  }
}
