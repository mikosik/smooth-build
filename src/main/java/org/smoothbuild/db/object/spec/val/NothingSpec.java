package org.smoothbuild.db.object.spec.val;

import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.NothingType;

/**
 * This class is immutable.
 */
public class NothingSpec extends ValSpec implements NothingType {
  public NothingSpec(Hash hash) {
    super(hash, NOTHING);
  }

  @Override
  public Obj newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    throw new UnsupportedOperationException("Cannot create object for " + NOTHING + " spec.");
  }
}
