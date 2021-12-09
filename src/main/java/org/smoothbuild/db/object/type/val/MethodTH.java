package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.METHOD;
import static org.smoothbuild.db.object.type.val.CallableTH.calculateVars;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.type.base.TypeH;

import com.google.common.collect.ImmutableList;

public class MethodTH extends TypeH implements CallableTH{
  private final TypeH res;
  private final TupleTH params;

  public MethodTH(Hash hash, TypeH res, TupleTH params) {
    super("_" + funcTypeName(res, params.items()), hash, METHOD,
        calculateVars(res, params.items()));
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
  public MethodH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (MethodH) super.newObj(merkleRoot, objDb);
  }
}
