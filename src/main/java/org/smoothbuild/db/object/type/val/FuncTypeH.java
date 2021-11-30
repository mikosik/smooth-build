package org.smoothbuild.db.object.type.val;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.api.TypeNames.funcTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.type.base.SpecKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.FuncType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FuncTypeH extends TypeH implements FuncType {
  private final TypeH res;
  private final TupleTypeH paramsTuple;

  public FuncTypeH(Hash hash, SpecKindH kind, TypeH res, TupleTypeH paramsTuple) {
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

  public TupleTypeH paramsTuple() {
    return paramsTuple;
  }

  @Override
  public FuncH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (FuncH) super.newObj(merkleRoot, objectHDb);
  }
}
