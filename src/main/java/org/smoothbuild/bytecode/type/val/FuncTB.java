package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.FUNC;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.base.type.api.FuncT;

import com.google.common.collect.ImmutableList;

public final class FuncTB extends TypeB implements CallableTB {
  private final TypeB res;
  private final TupleTB params;

  public FuncTB(Hash hash, TypeB res, TupleTB params) {
    super(
        hash, funcTypeName(res, params.items()),
        FUNC,
        FuncT.calculateHasOpenVars(res, params.items()),
        FuncT.calculateHasClosedVars(res, params.items()));
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
  public FuncB newObj(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    return (FuncB) super.newObj(merkleRoot, byteDb);
  }
}
