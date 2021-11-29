package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
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
    checkArgument(merkleRoot.spec() instanceof ConstructTypeH);
  }

  @Override
  public ConstructTypeH spec() {
    return (ConstructTypeH) super.spec();
  }

  @Override
  public TupleTypeH type() {
    return spec().evalType();
  }

  public ImmutableList<ObjectH> items() {
    var expectedItemTypes = spec().evalType().items();
    var items = readSequenceObjs(DATA_PATH, dataHash(), ObjectH.class);
    allMatchOtherwise(
        expectedItemTypes,
        items,
        (s, i) -> Objects.equals(s, i.type()),
        (i, j) -> {
          throw new DecodeConstructWrongItemsSizeException(hash(), spec(), j);
        },
        (i) -> {
          throw new DecodeExprWrongEvaluationTypeOfComponentException(hash(), spec(),
              "items[" + i + "]", expectedItemTypes.get(i), items.get(i).type());
        }
    );
    return items;
  }

  @Override
  public String valueToString() {
    return "CONSTRUCT(???)";
  }
}
