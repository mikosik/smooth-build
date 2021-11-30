package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfCompExc;
import org.smoothbuild.db.object.type.expr.CombineTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class CombineH extends ExprH {
  public CombineH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.spec() instanceof CombineTypeH);
  }

  @Override
  public CombineTypeH spec() {
    return (CombineTypeH) super.spec();
  }

  @Override
  public TupleTypeH type() {
    return spec().evalType();
  }

  public ImmutableList<ObjH> items() {
    var expectedItemTypes = spec().evalType().items();
    var items = readSeqObjs(DATA_PATH, dataHash(), ObjH.class);
    allMatchOtherwise(
        expectedItemTypes,
        items,
        (s, i) -> Objects.equals(s, i.type()),
        (i, j) -> {
          throw new DecodeCombineWrongItemsSizeExc(hash(), spec(), j);
        },
        (i) -> {
          throw new DecodeExprWrongEvalTypeOfCompExc(hash(), spec(),
              "items[" + i + "]", expectedItemTypes.get(i), items.get(i).type());
        }
    );
    return items;
  }

  @Override
  public String valToString() {
    return "CONSTRUCT(???)";
  }
}
