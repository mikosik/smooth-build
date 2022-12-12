package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CategoryKinds.CALL;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.value.TypeB;

/**
 * This class is immutable.
 */
public class CallCB extends OperCB {
  public CallCB(Hash hash, TypeB evalT) {
    super(hash, CALL, evalT);
  }

  @Override
  public CallB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof CallCB);
    return new CallB(merkleRoot, bytecodeDb);
  }
}
