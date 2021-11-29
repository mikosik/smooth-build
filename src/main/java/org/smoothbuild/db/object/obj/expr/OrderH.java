package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfComponentException;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.OrderTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class OrderH extends ExprH {
  public OrderH(MerkleRoot merkleRoot, ObjectHDb objectHDb) {
    super(merkleRoot, objectHDb);
    checkArgument(merkleRoot.spec() instanceof OrderTypeH);
  }

  @Override
  public OrderTypeH spec() {
    return (OrderTypeH) super.spec();
  }

  @Override
  public ArrayTypeH type() {
    return spec().evalType();
  }

  public ImmutableList<ObjectH> elems() {
    var elems = readSeqObjs(DATA_PATH, dataHash(), ObjectH.class);
    var expectedElementType = spec().evalType().elem();
    for (int i = 0; i < elems.size(); i++) {
      TypeH actualType = elems.get(i).type();
      if (!Objects.equals(expectedElementType, actualType)) {
        throw new DecodeExprWrongEvalTypeOfComponentException(
            hash(), spec(), "elems[" + i + "]", expectedElementType, actualType);
      }
    }
    return elems;
  }

  @Override
  public String valToString() {
    return "Order(???)";
  }
}
