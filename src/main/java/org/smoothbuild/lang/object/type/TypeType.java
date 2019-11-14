package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class TypeType extends ConcreteType {
  public TypeType(MerkleRoot merkleRoot, ObjectsDb objectsDb, HashedDb hashedDb) {
    super(merkleRoot, null, "Type", ConcreteType.class, hashedDb, objectsDb);
  }

  @Override
  public ConcreteType type() {
    return this;
  }

  @Override
  public ConcreteType newSObject(MerkleRoot merkleRoot) {
    throw new UnsupportedOperationException("This method in this subclass is never called as " +
        "ObjectsDb treats it as corner case so it could properly cache returned type and have its" +
        " hash when reporting error in case of problems.");
  }
}
