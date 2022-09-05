package org.smoothbuild.bytecode.type.oper;

import static org.smoothbuild.bytecode.type.CatKindB.PARAM_REF;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;

public class ParamRefCB extends OperCatB {
  public ParamRefCB(Hash hash, TypeB evalT) {
    super(hash, "ParamRef", PARAM_REF, evalT);
  }

  @Override
  public ParamRefB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (ParamRefB) super.newObj(merkleRoot, bytecodeDb);
  }
}
