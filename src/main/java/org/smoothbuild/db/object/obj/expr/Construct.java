package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeConstructWrongItemsSizeException;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.spec.expr.ConstructSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Construct extends Expr {
  public Construct(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof ConstructSpec);
  }

  @Override
  public ConstructSpec spec() {
    return (ConstructSpec) super.spec();
  }

  @Override
  public TupleSpec evaluationSpec() {
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
          throw new DecodeConstructWrongItemsSizeException(hash(), spec(), j);
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
    return "CONSTRUCT(???)";
  }
}
