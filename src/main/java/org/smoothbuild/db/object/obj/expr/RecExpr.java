package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.exc.DecodeRecExprWrongItemsSizeException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.spec.expr.RecExprSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class RecExpr extends Expr {
  public RecExpr(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof RecExprSpec);
  }

  @Override
  public RecExprSpec spec() {
    return (RecExprSpec) super.spec();
  }

  @Override
  public RecSpec evaluationSpec() {
    return spec().evaluationSpec();
  }

  public ImmutableList<Expr> items() {
    var expectedItemSpecs = spec().evaluationSpec().items();
    var items = readSequenceObjs(DATA_PATH, dataHash(), Expr.class);

    allMatchOtherwise(
        expectedItemSpecs,
        items,
        (s, i) -> Objects.equals(s, i.evaluationSpec()),
        (i, j) -> {
          throw new DecodeRecExprWrongItemsSizeException(hash(), spec(), j);
        },
        (i) -> {
          throw new DecodeExprWrongEvaluationSpecOfComponentException(hash(), spec(),
              "items[" + i + "]", expectedItemSpecs.get(i), items.get(i).evaluationSpec());
        }
    );
    return items;
  }

  @Override
  public String valueToString() {
    return "RecExpr(???)";
  }
}
