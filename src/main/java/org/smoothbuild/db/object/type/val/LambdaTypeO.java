package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.LAMBDA;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.lang.base.type.help.FunctionTypeImplHelper.calculateVariables;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.lang.base.type.api.FunctionType;

import com.google.common.collect.ImmutableList;

public class LambdaTypeO extends TypeV implements FunctionType {
  private final TypeV result;
  private final TupleTypeO parametersTuple;

  public LambdaTypeO(Hash hash, TypeV result, TupleTypeO parametersTuple) {
    super(functionTypeName(result, parametersTuple.items()), hash, LAMBDA,
        calculateVariables(result, parametersTuple.items()));
    this.result = result;
    this.parametersTuple = parametersTuple;
  }

  @Override
  public TypeV result() {
    return result;
  }

  @Override
  public ImmutableList<TypeV> parameters() {
    return parametersTuple.items();
  }

  public TupleTypeO parametersTuple() {
    return parametersTuple;
  }

  @Override
  public Lambda newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new Lambda(merkleRoot, objDb);
  }
}
