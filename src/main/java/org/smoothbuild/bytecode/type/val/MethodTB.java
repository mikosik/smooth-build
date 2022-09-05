package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.METHOD;
import static org.smoothbuild.bytecode.type.val.TNamesB.funcTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.MethodB;
import org.smoothbuild.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableList;

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
  public ImmutableList<TypeB> params() {
    return params.items();
  }

  @Override
  public TupleTB paramsTuple() {
    return params;
  }

  @Override
  public MethodB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (MethodB) super.newObj(merkleRoot, bytecodeDb);
  }
}
