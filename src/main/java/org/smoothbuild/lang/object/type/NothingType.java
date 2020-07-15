package org.smoothbuild.lang.object.type;

import static org.smoothbuild.lang.object.type.TypeKind.NOTHING;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.object.base.MerkleRoot;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectDbException;

/**
 * This class is immutable.
 */
public class NothingType extends ConcreteType {
  public NothingType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, NOTHING, Nothing.class, hashedDb, objectDb);
  }

  @Override
  public SObject newJObject(MerkleRoot merkleRoot) {
    throw new ObjectDbException(merkleRoot.hash(),
        "Object type is Nothing so such object cannot exist.");
  }
}
