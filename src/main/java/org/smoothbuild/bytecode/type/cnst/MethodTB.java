package org.smoothbuild.bytecode.type.cnst;

import static org.smoothbuild.bytecode.type.CatKindB.METHOD;
import static org.smoothbuild.bytecode.type.cnst.FuncTB.calculateFuncVars;
import static org.smoothbuild.bytecode.type.cnst.TNamesB.funcTypeName;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.cnst.MethodB;
import org.smoothbuild.db.Hash;

import com.google.common.collect.ImmutableList;

public final class MethodTB extends TypeB implements CallableTB {
  private final TypeB res;
  private final TupleTB params;

  public MethodTB(Hash hash, TypeB res, TupleTB params) {
    super(
        hash, "_" + funcTypeName(res, params.items()),
        METHOD,
        calculateFuncVars(res, params.items()));
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
  public MethodB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (MethodB) super.newObj(merkleRoot, objDb);
  }
}
