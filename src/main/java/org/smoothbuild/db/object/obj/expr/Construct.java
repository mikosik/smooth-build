package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeConstructWrongItemsSizeException;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.type.expr.ConstructOType;
import org.smoothbuild.db.object.type.val.TupleOType;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Construct extends Expr {
  public Construct(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.type() instanceof ConstructOType);
  }

  @Override
  public ConstructOType type() {
    return (ConstructOType) super.type();
  }

  @Override
  public TupleOType evaluationType() {
    return type().evaluationType();
  }

  public ImmutableList<Expr> items() {
    var expectedItemTypes = type().evaluationType().items();
    var items = readSequenceObjs(DATA_PATH, dataHash(), Expr.class);

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
