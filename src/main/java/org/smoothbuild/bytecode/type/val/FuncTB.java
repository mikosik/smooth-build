package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.FUNC;
import static org.smoothbuild.lang.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;

import com.google.common.collect.ImmutableList;

public final class FuncTB extends TypeB implements CallableTB {
  private final VarSetB tParams;
  private final TypeB res;
  private final TupleTB params;

  public FuncTB(Hash hash, VarSetB tParams, TypeB res, TupleTB params) {
    super(
        hash, funcTypeName(tParams, res, params.items()),
        FUNC,
        calculateFuncVars(res, params.items()));
    this.tParams = tParams;
    this.res = res;
    this.params = params;
  }

  public static VarSetB calculateFuncVars(TypeB resT, ImmutableList<TypeB> paramTs) {
    return calculateVars(concat(resT, paramTs));
  }

  public VarSetB tParams() {
    return tParams;
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
}
