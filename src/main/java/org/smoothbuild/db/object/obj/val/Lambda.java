package org.smoothbuild.db.object.obj.val;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;

import com.google.common.collect.ImmutableList;

public abstract class Lambda extends Val {
  protected static final int DATA_SEQUENCE_SIZE = 2;
  protected static final int BODY_INDEX = 0;
  private static final int DEFAULT_ARGUMENTS_INDEX = 1;
  protected static final String BODY_PATH = "data[" + BODY_INDEX + "]";
  private static final String DEFAULT_ARGUMENTS_PATH = "data[" + DEFAULT_ARGUMENTS_INDEX + "]";

  public Lambda(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
  }

  @Override
  public LambdaSpec spec() {
    return (LambdaSpec) super.spec();
  }

  protected ImmutableList<Expr> defaultArguments() {
    var hash = readSequenceElementHash(
        DATA_PATH, dataHash(), DEFAULT_ARGUMENTS_INDEX, DATA_SEQUENCE_SIZE);
    var arguments = readSequenceObjs(
        DEFAULT_ARGUMENTS_PATH, hash, spec().parameters().items().size(), Expr.class);
    for (int i = 0; i < arguments.size(); i++) {
      ValSpec expected = spec().parameters().items().get(i);
      ValSpec actual = arguments.get(i).evaluationSpec();
      if (!Objects.equals(expected, actual)) {
        throw new DecodeExprWrongEvaluationSpecOfComponentException(
            hash(), spec(), DEFAULT_ARGUMENTS_PATH + "[" + i + "]", expected, actual);
      }
    }
    return arguments;
  }
}
