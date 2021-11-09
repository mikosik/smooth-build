package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.ObjKind.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.type.base.TypeV;

/**
 * This class is immutable.
 */
public class AnyTypeO extends TypeV {
  public AnyTypeO(Hash hash) {
    super("Any", hash, ANY);
  }

  @Override
  public Blob newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    throw new UnsupportedOperationException("Cannot create object for " + ANY + " type.");
  }
}
