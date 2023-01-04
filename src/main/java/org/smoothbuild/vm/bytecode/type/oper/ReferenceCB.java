package org.smoothbuild.vm.bytecode.type.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.REFERENCE;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class ReferenceCB extends OperCB {
  public ReferenceCB(Hash hash, TypeB evalT) {
    super(hash, REFERENCE, evalT);
  }

  @Override
  public ReferenceB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.category() instanceof ReferenceCB);
    return new ReferenceB(merkleRoot, bytecodeDb);
  }
}
