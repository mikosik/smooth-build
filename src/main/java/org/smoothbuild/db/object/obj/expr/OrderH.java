package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.expr.OrderTypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class OrderH extends ExprH {
  public OrderH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof OrderTypeH);
  }

  @Override
  public OrderTypeH type() {
    return (OrderTypeH) super.type();
  }

  public ImmutableList<ExprH> elements() {
    var elements = readSequenceObjs(DATA_PATH, dataHash(), ExprH.class);
    var expectedElementType = type().evaluationType().element();
    for (int i = 0; i < elements.size(); i++) {
      TypeHV actualType = elements.get(i).evaluationType();
      if (!Objects.equals(expectedElementType, actualType)) {
        throw new DecodeExprWrongEvaluationTypeOfComponentException(
            hash(), type(), "elements[" + i + "]", expectedElementType, actualType);
      }
    }
    return elements;
  }

  @Override
  public String valueToString() {
    return "Order(???)";
  }
}
