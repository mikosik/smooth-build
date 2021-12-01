package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * This class is immutable.
 */
public sealed abstract class ValH extends ObjH
    permits ArrayH, BlobH, BoolH, FuncH, IntH, StringH, TupleH {
  public ValH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public TypeH spec() {
    return (TypeH) super.spec();
  }

  @Override
  public TypeH type() {
    return spec();
  }
}
