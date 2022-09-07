package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKindB.CALL;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;

/**
 * This class is immutable.
 */
public class CallCB extends OperCatB {
  public CallCB(Hash hash, TypeB evalT) {
    super(hash, "Call", CALL, evalT);
  }

  @Override
  public CallB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof CallCB);
    return new CallB(merkleRoot, bytecodeDb);
  }
}
