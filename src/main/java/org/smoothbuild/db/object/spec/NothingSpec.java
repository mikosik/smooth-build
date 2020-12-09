package org.smoothbuild.db.object.spec;

import static org.smoothbuild.db.object.spec.SpecKind.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.ObjectDbException;

/**
 * This class is immutable.
 */
public class NothingSpec extends Spec {
  public NothingSpec(Hash hash, HashedDb hashedDb, ObjectDb objectDb) {
    super(hash, NOTHING, hashedDb, objectDb);
  }

  @Override
  public Obj newObj(MerkleRoot merkleRoot) {
    throw new ObjectDbException("Cannot create java object for 'NOTHING' spec.");
  }
}
