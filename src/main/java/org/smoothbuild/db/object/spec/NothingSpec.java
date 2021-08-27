package org.smoothbuild.db.object.spec;

import static org.smoothbuild.db.object.spec.SpecKind.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.MerkleRoot;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.ObjectDbException;

/**
 * This class is immutable.
 */
public class NothingSpec extends ValSpec {
  public NothingSpec(Hash hash, ObjectDb objectDb) {
    super(hash, NOTHING, objectDb);
  }

  @Override
  public Obj newObj(MerkleRoot merkleRoot) {
    throw new ObjectDbException("Cannot create java object for 'NOTHING' spec.");
  }
}
