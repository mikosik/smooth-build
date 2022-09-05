package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.CatKindB.FUNC;
import static org.smoothbuild.bytecode.type.val.TNamesB.funcTypeName;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.hashed.Hash;

import com.google.common.collect.ImmutableList;

public final class FuncTB extends TypeB implements CallableTB, ComposedTB {
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
  public ImmutableList<TypeB> params() {
    return params.items();
  }

  @Override
  public TupleTB paramsTuple() {
    return params;
  }

  @Override
  public FuncB newObj(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    return (FuncB) super.newObj(merkleRoot, bytecodeDb);
  }

  @Override
  public ImmutableList<TypeB> covars() {
    return ImmutableList.of(res());
  }

  @Override
  public ImmutableList<TypeB> contravars() {
    return params();
  }
}
