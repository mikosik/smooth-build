package org.smoothbuild.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKindB.INVOKE;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.oper.InvokeB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;

public class InvokeCB extends OperCatB {
  public InvokeCB(Hash hash, TypeB evalT) {
    super(hash, "Invoke", INVOKE, evalT);
  }

  @Override
  public InvokeB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof InvokeCB);
    return new InvokeB(merkleRoot, bytecodeDb);
  }
}
