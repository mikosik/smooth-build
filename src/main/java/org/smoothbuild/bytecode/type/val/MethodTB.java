package org.smoothbuild.bytecode.type.val;

import static org.smoothbuild.bytecode.type.base.CatKindB.METHOD;
import static org.smoothbuild.lang.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.val.MethodB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.lang.type.api.FuncT;

import com.google.common.collect.ImmutableList;

public final class MethodTB extends TypeB implements CallableTB {
  private final TypeB res;
  private final TupleTB params;

  public MethodTB(Hash hash, TypeB res, TupleTB params) {
    super(
        hash, "_" + funcTypeName(res, params.items()),
        METHOD,
        calculateOpenVars(concat(res, params.items())),
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
  public MethodB newObj(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    return (MethodB) super.newObj(merkleRoot, objDb);
  }
}
