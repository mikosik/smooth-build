package org.smoothbuild.db.object.spec.val;

import static org.smoothbuild.db.object.spec.base.SpecKind.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.spec.base.ValSpec;

/**
 * This class is immutable.
 */
public class AnySpec extends ValSpec {
  public AnySpec(Hash hash) {
    super(hash, ANY);
  }

  @Override
  public Blob newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    throw new UnsupportedOperationException("Cannot create object for " + ANY + " spec.");
  }
}
