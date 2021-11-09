package org.smoothbuild.db.object.type.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.type.base.ObjKind.FUNCTION;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.lang.base.type.help.FunctionTypeImplHelper.calculateVariables;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.FunctionV;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.lang.base.type.api.FunctionType;

import com.google.common.collect.ImmutableList;

public class FunctionTypeO extends TypeV implements FunctionType {
  private final TypeV result;
  private final TupleTypeO parametersTuple;

  public FunctionTypeO(Hash hash, TypeV result, TupleTypeO parametersTuple) {
    super(functionTypeName(result, parametersTuple.items()), hash, FUNCTION,
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
  public FunctionV newObj(MerkleRoot merkleRoot, ObjDb objDb) {
    checkArgument(this.equals(merkleRoot.type()));
    return new FunctionV(merkleRoot, objDb);
  }
}
