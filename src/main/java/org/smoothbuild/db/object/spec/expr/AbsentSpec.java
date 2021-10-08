package org.smoothbuild.db.object.spec.expr;

import static org.smoothbuild.db.object.spec.base.SpecKind.ABSENT;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.spec.base.ValSpec;

/**
 * This class is immutable.
 */
public class AbsentSpec extends ValSpec {
  public AbsentSpec(Hash hash) {
    super(hash, ABSENT);
  }

  @Override
  public Null newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    throw new UnsupportedOperationException("Cannot create object for " + ABSENT + " spec.");
  }
}
