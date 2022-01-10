package org.smoothbuild.bytecode.obj.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.ExprB;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.expr.CombineCB;
import org.smoothbuild.bytecode.type.val.TupleTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CombineB extends ExprB {
  public CombineB(MerkleRoot merkleRoot, ObjDbImpl byteDb) {
    super(merkleRoot, byteDb);
    checkArgument(merkleRoot.cat() instanceof CombineCB);
  }

  @Override
  public CombineCB cat() {
    return (CombineCB) super.cat();
  }

  @Override
  public TupleTB type() {
    return this.cat().evalT();
  }

  public ImmutableList<ObjB> items() {
    var expectedItemTs = this.cat().evalT().items();
    var items = readSeqObjs(DATA_PATH, dataHash(), ObjB.class);
    allMatchOtherwise(
        expectedItemTs,
        items,
        (type, item) -> byteDb().typing().isAssignable(type, item.type()),
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