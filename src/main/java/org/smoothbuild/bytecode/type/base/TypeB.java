package org.smoothbuild.bytecode.type.base;

import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjIllegalPolymorphicTypeExc;
import org.smoothbuild.db.Hash;

public abstract class TypeB extends CatB {
  protected TypeB(String name, Hash hash, CatKindB kind) {
    super(name, hash, kind, false, false);
  }

  protected TypeB(
      String name, Hash hash, CatKindB kind, boolean hasOpenVars, boolean hasClosedVars) {
    super(name, hash, kind, hasOpenVars, hasClosedVars);
  }

  @Override
  public String toString() {
    return "TypeB(`" + name() + "`)";
  }

  protected static void validateNotPolymorphic(MerkleRoot merkleRoot) {
    if (merkleRoot.cat().isPolytype()) {
      throw new DecodeObjIllegalPolymorphicTypeExc(merkleRoot.hash(), merkleRoot.cat());
    }
  }
}
