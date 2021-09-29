package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.ArrayExprSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ArrayExpr extends Expr {
  public ArrayExpr(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof ArrayExprSpec);
  }

  @Override
  public ArrayExprSpec spec() {
    return (ArrayExprSpec) super.spec();
  }

  public ImmutableList<Expr> elements() {
    var elements = readSequenceObjs(DATA_PATH, dataHash(), Expr.class);
    var expectedElementSpec = spec().evaluationSpec().element();
    for (int i = 0; i < elements.size(); i++) {
      ValSpec actualSpec = elements.get(i).evaluationSpec();
      if (!Objects.equals(expectedElementSpec, actualSpec)) {
        throw new DecodeExprWrongEvaluationSpecOfComponentException(
            hash(), spec(), "elements[" + i + "]", expectedElementSpec, actualSpec);
      }
    }
    return elements;
  }

  @Override
  public String valueToString() {
    return "ArrayExpr(???)";
  }
}
