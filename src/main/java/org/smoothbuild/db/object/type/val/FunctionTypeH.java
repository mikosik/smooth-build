package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Comparator.comparing;
import static org.smoothbuild.db.object.type.base.TypeKindH.FUNCTION;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.util.collect.Lists.concat;

import java.util.Collection;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FunctionTypeH extends TypeHV implements FunctionType {
  private final TypeHV result;
  private final TupleTypeH parametersTuple;

  public FunctionTypeH(Hash hash, TypeHV result, TupleTypeH parametersTuple) {
    super(functionTypeName(result, parametersTuple.items()), hash, FUNCTION,
        calculateVariables(result, parametersTuple.items()));
    this.result = result;
    this.parametersTuple = parametersTuple;
  }

  public static ImmutableSet<VariableH> calculateVariables(
      TypeHV resultType, ImmutableList<TypeHV> parameters) {
    return concat(resultType, parameters).stream()
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
  public ImmutableList<TypeHV> parameters() {
    return parametersTuple.items();
  }

  public TupleTypeH parametersTuple() {
    return parametersTuple;
  }

  @Override
  public FunctionH newObj(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new FunctionH(merkleRoot, objectHDb);
  }
}
