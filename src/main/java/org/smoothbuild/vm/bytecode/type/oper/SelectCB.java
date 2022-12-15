package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.SELECT;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class SelectCB extends OperCB {
  public SelectCB(Hash hash, TypeB evalT) {
    super(hash, SELECT, evalT);
  }

  @Override
  public SelectB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof SelectCB);
    return new SelectB(merkleRoot, bytecodeDb);
  }
}
