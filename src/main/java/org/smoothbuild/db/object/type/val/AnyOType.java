package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.ObjKind.ANY;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.lang.base.type.api.AnyType;

/**
 * This class is immutable.
 */
public class AnyOType extends TypeV implements AnyType {
  public AnyOType(Hash hash) {
    super("Any", hash, ANY);
  }

  @Override
  public Blob newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    throw new UnsupportedOperationException("Cannot create object for " + ANY + " type.");
  }
}
