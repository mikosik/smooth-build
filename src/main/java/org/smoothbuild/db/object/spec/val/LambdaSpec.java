package org.smoothbuild.db.object.spec.val;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.object.spec.base.SpecKind.LAMBDA;
import static org.smoothbuild.lang.base.type.api.TypeNames.functionTypeName;
import static org.smoothbuild.lang.base.type.help.FunctionTypeImplHelper.calculateVariables;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.lang.base.type.api.FunctionType;

import com.google.common.collect.ImmutableList;

public class LambdaSpec extends ValSpec implements FunctionType {
  private final ValSpec result;
  private final TupleSpec parametersTuple;

  public LambdaSpec(Hash hash, ValSpec result, TupleSpec parametersTuple) {
    super(functionTypeName(result, parametersTuple.items()), hash, LAMBDA,
        calculateVariables(result, parametersTuple.items()));
    this.result = result;
    this.parametersTuple = parametersTuple;
  }

  @Override
  public ValSpec result() {
    return result;
  }

  @Override
  public ImmutableList<ValSpec> parameters() {
    return parametersTuple.items();
  }

  public TupleSpec parametersTuple() {
    return parametersTuple;
  }

  @Override
  public Lambda newObj(MerkleRoot merkleRoot, ObjectDb objectDb) {
    checkArgument(this.equals(merkleRoot.spec()));
    return new Lambda(merkleRoot, objectDb);
  }
}
