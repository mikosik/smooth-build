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
  public ConcreteType newObject(MerkleRoot merkleRoot) {
    return objectsDb.getType(merkleRoot.hash());
  }
}
