package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.COMBINE;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;

/**
 * This class is immutable.
 */
public class CombineCB extends OperCB {
  public CombineCB(Hash hash, TypeB evalT) {
    super(hash, COMBINE, evalT);
    checkArgument(evalT instanceof TupleTB);
  }

  @Override
  public TupleTB evalT() {
    return (TupleTB) super.evalT();
  }

  @Override
  public CombineB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof CombineCB);
    return new CombineB(merkleRoot, bytecodeDb);
  }
}
