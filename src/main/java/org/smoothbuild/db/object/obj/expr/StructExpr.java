package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.obj.exc.DecodeStructExprWrongItemsSizeException;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.expr.StructExprOType;
import org.smoothbuild.db.object.type.val.StructTypeO;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class StructExpr extends Expr {
  public StructExpr(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.type() instanceof StructExprOType);
  }

  @Override
  public StructExprOType type() {
    return (StructExprOType) super.type();
  }

  @Override
  public StructTypeO evaluationType() {
    return type().evaluationType();
  }

  public ImmutableList<Expr> items() {
    NamedList<TypeV> fields = type().evaluationType().fields();
    ImmutableList<Named<TypeV>> expectedItemTypes = fields.list();
    var items = readSequenceObjs(DATA_PATH, dataHash(), Expr.class);

    allMatchOtherwise(
        expectedItemTypes,
        items,
        (s, i) -> Objects.equals(s.object(), i.evaluationType()),
        (i, j) -> {
          throw new DecodeStructExprWrongItemsSizeException(hash(), type(), j);
        },
        (i) -> {
          throw new DecodeExprWrongEvaluationTypeOfComponentException(hash(), type(),
              "items[" + i + "]", fields.getObject(i), items.get(i).evaluationType());
        }
    );
    return items;
  }

  @Override
  public String valueToString() {
    return "StructExpr(???)";
  }
}
