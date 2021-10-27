package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.exc.DecodeStructExprWrongItemsSizeException;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.StructExprSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class StructExpr extends Expr {
  public StructExpr(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof StructExprSpec);
  }

  @Override
  public StructExprSpec spec() {
    return (StructExprSpec) super.spec();
  }

  @Override
  public StructSpec evaluationSpec() {
    return spec().evaluationSpec();
  }

  public ImmutableList<Expr> items() {
    NamedList<ValSpec> fields = spec().evaluationSpec().fields();
    ImmutableList<Named<ValSpec>> expectedItemSpecs = fields.list();
    var items = readSequenceObjs(DATA_PATH, dataHash(), Expr.class);

    allMatchOtherwise(
        expectedItemSpecs,
        items,
        (s, i) -> Objects.equals(s.object(), i.evaluationSpec()),
        (i, j) -> {
          throw new DecodeStructExprWrongItemsSizeException(hash(), spec(), j);
        },
        (i) -> {
          throw new DecodeExprWrongEvaluationSpecOfComponentException(hash(), spec(),
              "items[" + i + "]", fields.getObject(i), items.get(i).evaluationSpec());
        }
    );
    return items;
  }

  @Override
  public String valueToString() {
    return "StructExpr(???)";
  }
}
