package org.smoothbuild.db.object.obj.val;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.type.base.TypeH;

/**
 * Value.
 * This class is thread-safe.
 */
public sealed abstract class ValH extends ObjH
    permits ArrayH, BlobH, BoolH, FuncH, IntH, MethodH, StringH, TupleH {
  public ValH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  @Override
  public TypeH cat() {
    return (TypeH) super.cat();
  }

  @Override
  public TypeH type() {
    return cat();
  }
}
