package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.PICK;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class PickCB extends OperCB {
  public PickCB(Hash hash, TypeB evalT) {
    super(hash, PICK, evalT);
  }

  @Override
  public PickB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof PickCB);
    return new PickB(merkleRoot, bytecodeDb);
  }
}
