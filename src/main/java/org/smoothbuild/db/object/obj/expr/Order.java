package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.OrderSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Order extends Expr {
  public Order(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof OrderSpec);
  }

  @Override
  public OrderSpec spec() {
    return (OrderSpec) super.spec();
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
    return "Order(???)";
  }
}
