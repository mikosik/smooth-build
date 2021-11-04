package org.smoothbuild.db.object.obj.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.type.base.TypeE;
import org.smoothbuild.db.object.type.base.TypeV;

public abstract class Expr extends Obj {
  public Expr(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.type() instanceof TypeE);
  }

  @Override
  public TypeE type() {
    return (TypeE) super.type();
  }

  public TypeV evaluationType() {
    return type().evaluationType();
  }
}
