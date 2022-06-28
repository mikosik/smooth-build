package org.smoothbuild.bytecode.type.cnst;

import static org.smoothbuild.bytecode.type.CatKindB.FUNC;
import static org.smoothbuild.bytecode.type.cnst.TNamesB.funcTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.cnst.FuncB;
import org.smoothbuild.db.Hash;

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
  public FuncB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (FuncB) super.newObj(merkleRoot, objDb);
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
