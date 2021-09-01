package org.smoothbuild.db.object.spec.val;

import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.ValSpec;

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
