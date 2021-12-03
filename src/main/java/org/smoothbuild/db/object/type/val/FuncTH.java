package org.smoothbuild.db.object.type.val;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FuncTH extends TypeH implements FuncT {
  private final TypeH res;
  private final TupleTH paramsTuple;

  public FuncTH(Hash hash, CatKindH kind, TypeH res, TupleTH paramsTuple) {
    super(funcTypeName(res, paramsTuple.items()), hash, kind,
        calculateVars(res, paramsTuple.items()));
    this.res = res;
    this.paramsTuple = paramsTuple;
  }

  public static ImmutableSet<VarH> calculateVars(
      TypeH resultType, ImmutableList<TypeH> params) {
    return concat(resultType, params).stream()
        .map(TypeH::vars)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  @Override
  public TypeH res() {
    return res;
  }

  @Override
  public ImmutableList<TypeH> params() {
    return paramsTuple.items();
  }

  public TupleTH paramsTuple() {
    return paramsTuple;
  }

  @Override
  public FuncH newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    return (FuncH) super.newObj(merkleRoot, objDb);
  }
}
