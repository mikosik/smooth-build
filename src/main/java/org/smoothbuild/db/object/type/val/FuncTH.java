package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.FUNC;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.FuncT;

import com.google.common.collect.ImmutableList;

public final class FuncTH extends TypeH implements FuncT, CallableTH {
  private final TypeH res;
  private final TupleTH params;

  public FuncTH(Hash hash, TypeH res, TupleTH params) {
    super(funcTypeName(res, params.items()), hash, FUNC,
        calculateVars(concat(res, params.items())));
    this.res = res;
    this.params = params;
  }

  @Override
  public TypeH res() {
    return res;
  }

  @Override
  public ImmutableList<TypeH> params() {
    return params.items();
  }

  @Override
  public TupleTH paramsTuple() {
    return params;
  }

  @Override
  public FuncH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (FuncH) super.newObj(merkleRoot, objDb);
  }
}
