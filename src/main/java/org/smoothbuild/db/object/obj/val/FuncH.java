package org.smoothbuild.db.object.obj.val;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.type.base.SpecKindH;
import org.smoothbuild.db.object.type.val.FuncTypeH;

public abstract class FuncH extends ValueH {
  public FuncH(MerkleRoot merkleRoot, ObjDb objDb, SpecKindH kind) {
    super(merkleRoot, objDb);
    checkType(merkleRoot, kind);
  }

  protected void checkType(MerkleRoot merkleRoot, SpecKindH kind) {
    checkArgument(merkleRoot.spec() instanceof FuncTypeH funcTypeH
            && funcTypeH.kind().equals(kind));
  }

  @Override
  public FuncTypeH type() {
    return (FuncTypeH) super.type();
  }

  @Override
  public FuncTypeH spec() {
    return (FuncTypeH) super.spec();
  }
}
