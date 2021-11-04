package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.expr.OrderOType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Order extends Expr {
  public Order(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.type() instanceof OrderOType);
  }

  @Override
  public OrderOType type() {
    return (OrderOType) super.type();
  }

  public ImmutableList<Expr> elements() {
    var elements = readSequenceObjs(DATA_PATH, dataHash(), Expr.class);
    var expectedElementType = type().evaluationType().element();
    for (int i = 0; i < elements.size(); i++) {
      TypeV actualType = elements.get(i).evaluationType();
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
