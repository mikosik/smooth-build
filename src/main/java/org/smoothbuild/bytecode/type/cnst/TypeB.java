package org.smoothbuild.bytecode.type.cnst;

import static org.smoothbuild.bytecode.type.cnst.VarSetB.toVarSetB;
import static org.smoothbuild.bytecode.type.cnst.VarSetB.varSetB;

import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjIllegalPolymorphicTypeExc;
import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.CatKindB;
import org.smoothbuild.db.Hash;

import com.google.common.collect.ImmutableList;

public abstract class TypeB extends CatB {
  private final VarSetB vars;

  protected TypeB(Hash hash, String name, CatKindB kind) {
    this(hash, name, kind, varSetB());
  }

  protected TypeB(Hash hash, String name, CatKindB kind, VarSetB vars) {
    super(hash, name, kind);
    this.vars = vars;
  }

  public static VarSetB calculateVars(ImmutableList<TypeB> types) {
    return types.stream()
        .map(TypeB::vars)
        .flatMap(VarSetB::stream)
        .collect(toVarSetB());
  }

  public VarSetB vars() {
    return vars;
  }

  @Override
  public String toString() {
    return "TypeB(`" + name() + "`)";
  }

  protected static void validateNotPolymorphic(MerkleRoot merkleRoot) {
    if (!((TypeB) merkleRoot.cat()).vars().isEmpty()) {
      throw new DecodeObjIllegalPolymorphicTypeExc(merkleRoot.hash(), merkleRoot.cat());
    }
  }
}