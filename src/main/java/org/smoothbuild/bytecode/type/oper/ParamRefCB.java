package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.PARAM_REF;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;

public class ParamRefCB extends OperCB {
  public ParamRefCB(Hash hash, TypeB evalT) {
    super(hash, "ParamRef", PARAM_REF, evalT);
  }

  @Override
  public ParamRefB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof ParamRefCB);
    return new ParamRefB(merkleRoot, bytecodeDb);
  }
}
