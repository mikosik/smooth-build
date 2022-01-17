package org.smoothbuild.bytecode.type.base;

import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjIllegalPolymorphicTypeExc;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.TypeBBridge;

public abstract class TypeB extends CatB implements TypeBBridge {
  private final boolean hasOpenVars;
  private final boolean hasClosedVars;

  protected TypeB(Hash hash, String name, CatKindB kind) {
    this(hash, name, kind, false, false);
  }

  protected TypeB(
      Hash hash, String name, CatKindB kind, boolean hasOpenVars, boolean hasClosedVars) {
    super(hash, name, kind);
    this.hasOpenVars = hasOpenVars;
    this.hasClosedVars = hasClosedVars;
  }

  @Override
  public boolean hasOpenVars() {
    return hasOpenVars;
  }

  @Override
  public boolean hasClosedVars() {
    return hasClosedVars;
  }

  @Override
  public String toString() {
    return "TypeB(`" + name() + "`)";
  }

  protected static void validateNotPolymorphic(MerkleRoot merkleRoot) {
    if (((TypeB) merkleRoot.cat()).isPolytype()) {
      throw new DecodeObjIllegalPolymorphicTypeExc(merkleRoot.hash(), merkleRoot.cat());
    }
  }
}
