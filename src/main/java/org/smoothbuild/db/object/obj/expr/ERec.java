package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.db.object.exc.DecodeERecWrongItemsSizeException;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.ERecSpec;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class ERec extends Expr {
  public ERec(MerkleRoot merkleRoot, ObjectDb objectDb) {
    super(merkleRoot, objectDb);
    checkArgument(merkleRoot.spec() instanceof ERecSpec);
  }

  @Override
  public ERecSpec spec() {
    return (ERecSpec) super.spec();
  }

  public ImmutableList<Expr> items() {
    var items = readSequenceObjs(DATA_PATH, dataHash(), Expr.class);
    var expectedItemSpecs = spec().evaluationSpec().items();

    if (expectedItemSpecs.size() != items.size()) {
      throw new DecodeERecWrongItemsSizeException(hash(), spec(), items.size());
    }
    for (int i = 0; i < items.size(); i++) {
      ValSpec expectedSpec = expectedItemSpecs.get(i);
      ValSpec actualSpec = items.get(i).evaluationSpec();
      if (!Objects.equals(expectedSpec, actualSpec)) {
        throw new DecodeExprWrongEvaluationSpecOfComponentException(
            hash(), spec(), "items[" + i + "]", expectedSpec, actualSpec);
      }
    }
    return items;
  }

  @Override
  public String valueToString() {
    return "ERec(???)";
  }
}
