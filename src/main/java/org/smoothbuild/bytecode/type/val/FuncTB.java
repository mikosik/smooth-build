package org.smoothbuild.bytecode.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.CatKindB.FUNC;
import static org.smoothbuild.bytecode.type.val.TNamesB.funcTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.hashed.Hash;

public final class FuncTB extends TypeB implements CallableTB {
  private final TypeB res;
  private final TupleTB params;

  public FuncTB(Hash hash, TypeB res, TupleTB params) {
    super(hash, funcTypeName(res, params.items()), FUNC);
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
  public FuncB newExpr(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    checkArgument(merkleRoot.cat() instanceof FuncTB);
    return new FuncB(merkleRoot, bytecodeDb);
  }
}
