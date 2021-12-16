package org.smoothbuild.db.object.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.expr.CombineCH;
import org.smoothbuild.db.object.type.val.TupleTH;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CombineH extends ExprH {
  public CombineH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
    checkArgument(merkleRoot.cat() instanceof CombineCH);
  }

  @Override
  public CombineCH cat() {
    return (CombineCH) super.cat();
  }

  @Override
  public TupleTH type() {
    return this.cat().evalT();
  }

  public ImmutableList<ObjH> items() {
    var expectedItemTs = this.cat().evalT().items();
    var items = readSeqObjs(DATA_PATH, dataHash(), ObjH.class);
    allMatchOtherwise(
        expectedItemTs,
        items,
        (type, item) -> objDb().typing().isAssignable(type, item.type()),
        (type, item) -> {
          throw new DecodeCombineWrongItemsSizeExc(hash(), this.cat(), item);
        },
        (index) -> {
          throw new DecodeObjWrongNodeTypeExc(hash(), this.cat(),
              "items", index, expectedItemTs.get(index), items.get(index).type());
        }
    );
    return items;
  }
}
