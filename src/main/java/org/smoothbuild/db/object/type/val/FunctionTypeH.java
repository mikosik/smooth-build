package org.smoothbuild.db.object.type.val;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.base.TypeKindH;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FunctionTypeH extends TypeHV implements FunctionType {
  private final TypeHV result;
  private final TupleTypeH paramsTuple;

  public FunctionTypeH(Hash hash, TypeKindH kind, TypeHV result, TupleTypeH paramsTuple) {
    super(functionTypeName(result, paramsTuple.items()), hash, kind,
        calculateVariables(result, paramsTuple.items()));
    this.result = result;
    this.paramsTuple = paramsTuple;
  }

  public static ImmutableSet<VariableH> calculateVariables(
      TypeHV resultType, ImmutableList<TypeHV> params) {
    return concat(resultType, params).stream()
        .map(TypeHV::variables)
        .flatMap(Collection::stream)
        .sorted(comparing(Type::name))
        .collect(toImmutableSet());
  }

  @Override
  public TypeHV result() {
    return result;
  }

  @Override
  public ImmutableList<TypeHV> params() {
    return paramsTuple.items();
  }

  public TupleTypeH paramsTuple() {
    return paramsTuple;
  }

  @Override
  public FunctionH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    return (FunctionH) super.newObj(merkleRoot, objectHDb);
  }
}
