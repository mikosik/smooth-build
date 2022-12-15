package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.COMBINE;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

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
