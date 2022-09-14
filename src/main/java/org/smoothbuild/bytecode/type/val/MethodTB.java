package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKindB.METHOD;
import static org.smoothbuild.bytecode.type.val.TNamesB.funcTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.MethodB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class MethodTB extends TypeB implements CallableTB {
  private final TypeB res;
  private final TupleTB params;

  public MethodTB(Hash hash, TypeB res, TupleTB params) {
    super(hash, "_" + funcTypeName(res, params.items()), METHOD);
    this.res = res;
    this.params = params;
  }

  @Override
  public TypeB res() {
    return res;
  }

  @Override
  public TupleTB params() {
    return params;
  }

  @Override
  public MethodB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof MethodTB);
    return new MethodB(merkleRoot, bytecodeDb);
  }
}
