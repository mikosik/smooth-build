package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeConstructWrongItemsSizeException;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ConstructH extends ExprH {
  public ConstructH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.type() instanceof ConstructTypeH);
  }

  @Override
  public ConstructTypeH type() {
    return (ConstructTypeH) super.type();
  }

  @Override
  public TupleTypeH evaluationType() {
    return type().evaluationType();
  }

  public ImmutableList<ExprH> items() {
    var expectedItemTypes = type().evaluationType().items();
    var items = readSequenceObjs(DATA_PATH, dataHash(), ExprH.class);

    allMatchOtherwise(
        expectedItemTypes,
        items,
        (s, i) -> Objects.equals(s, i.evaluationType()),
        (i, j) -> {
          throw new DecodeConstructWrongItemsSizeException(hash(), type(), j);
        },
        (i) -> {
          throw new DecodeExprWrongEvaluationTypeOfComponentException(hash(), type(),
              "items[" + i + "]", expectedItemTypes.get(i), items.get(i).evaluationType());
        }
    );
    return items;
  }

  @Override
  public String valueToString() {
    return "CONSTRUCT(???)";
  }
}
