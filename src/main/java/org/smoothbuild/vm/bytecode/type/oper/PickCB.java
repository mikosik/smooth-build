package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.PICK;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class PickCB extends OperCB {
  public PickCB(Hash hash, TypeB evaluationT) {
    super(hash, PICK, evaluationT);
  }

  @Override
  public PickB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof PickCB);
    return new PickB(merkleRoot, bytecodeDb);
  }
}
