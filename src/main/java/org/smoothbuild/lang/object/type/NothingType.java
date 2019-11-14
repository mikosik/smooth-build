package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectsDb;

public class NothingType extends ConcreteType {
  public NothingType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectsDb objectsDb) {
    super(merkleRoot, null, "Nothing", Nothing.class, hashedDb, objectsDb);
  }

  @Override
  public SObject newSObject(MerkleRoot merkleRoot) {
    throw new RuntimeException("Cannot create instance of type 'Nothing'.");
  }
}
