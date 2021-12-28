package org.smoothbuild.db.bytecode.type.val;

import static org.smoothbuild.db.bytecode.type.base.CatKindB.METHOD;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.val.MethodB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.hashed.Hash;

import com.google.common.collect.ImmutableList;

public final class MethodTB extends TypeB implements CallableTB {
  private final TypeB res;
  private final TupleTB params;

  public MethodTB(Hash hash, TypeB res, TupleTB params) {
    super("_" + funcTypeName(res, params.items()), hash, METHOD,
        calculateVars(concat(res, params.items())));
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
  public MethodB newObj(MerkleRoot merkleRoot, ByteDb byteDb) {
    return (MethodB) super.newObj(merkleRoot, byteDb);
  }
}
