package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeKind.TYPE;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.db.ObjectDb;

/**
 * This class is immutable.
 */
public class TypeType extends ConcreteType {
  public TypeType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, TYPE, hashedDb, objectDb);
  }

  @Override
  public ConcreteType type() {
    return this;
  }

  @Override
  public ConcreteType newJObject(MerkleRoot merkleRoot) {
    return objectDb.getType(merkleRoot);
  }
}
