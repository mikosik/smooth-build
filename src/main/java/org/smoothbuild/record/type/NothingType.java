package org.smoothbuild.record.type;

import static org.smoothbuild.record.type.TypeKind.NOTHING;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.record.base.MerkleRoot;
import org.smoothbuild.record.base.SObject;
import org.smoothbuild.record.db.ObjectDb;
import org.smoothbuild.record.db.ObjectDbException;

/**
 * This class is immutable.
 */
public class NothingType extends BinaryType {
  public NothingType(MerkleRoot merkleRoot, HashedDb hashedDb, ObjectDb objectDb) {
    super(merkleRoot, NOTHING, hashedDb, objectDb);
  }

  @Override
  public SObject newJObject(MerkleRoot merkleRoot) {
    throw new ObjectDbException(merkleRoot.hash(),
        "Object type is Nothing so such object cannot exist.");
  }
}
