package org.smoothbuild.lang.object.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectDbException;

public class NothingType extends ConcreteType {
  public NothingType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, null, "Nothing", Nothing.class, hashedDb, objectDb);
  }

  @Override
  public SObject newObject(MerkleRoot merkleRoot) {
    throw new ObjectDbException(merkleRoot.hash(),
        "Object type is Nothing so such object cannot exist.");
  }
}
