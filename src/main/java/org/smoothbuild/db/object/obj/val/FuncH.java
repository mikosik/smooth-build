package org.smoothbuild.db.object.obj.val;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.val.FuncTH;

/**
 * Function.
 * This class is thread-safe.
 */
public sealed abstract class FuncH extends ValH
    permits DefFuncH, IfFuncH, MapFuncH, NatFuncH {
  public FuncH(MerkleRoot merkleRoot, ObjDb objDb, CatKindH kind) {
    super(merkleRoot, objDb);
    checkType(merkleRoot, kind);
  }

  protected void checkType(MerkleRoot merkleRoot, CatKindH kind) {
    checkArgument(merkleRoot.cat() instanceof FuncTH funcTH
            && funcTH.kind().equals(kind));
  }

  @Override
  public FuncTH type() {
    return (FuncTH) super.type();
  }

  @Override
  public FuncTH cat() {
    return (FuncTH) super.cat();
  }
}
