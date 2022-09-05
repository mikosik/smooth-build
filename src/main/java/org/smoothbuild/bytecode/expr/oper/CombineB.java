package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.oper.CombineCB;
import org.smoothbuild.bytecode.type.val.TupleTB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CombineB extends OperB {
  public CombineB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.cat() instanceof CombineCB);
  }

  @Override
  public CombineCB cat() {
    return (CombineCB) super.cat();
  }

  @Override
  public TupleTB type() {
    return cat().evalT();
  }

  public ImmutableList<ExprB> items() {
    var expectedItemTs = cat().evalT().items();
    var items = readSeqExprs(DATA_PATH, dataHash(), ExprB.class);
    allMatchOtherwise(
        expectedItemTs,
        items,
        (type, item) -> type.equals(item.type()),
        (type, item) -> { throw new DecodeCombineWrongItemsSizeExc(hash(), cat(), item); },
        (index) -> {
          throw new DecodeExprWrongNodeTypeExc(
              hash(), cat(), "items", index, expectedItemTs.get(index), items.get(index).type());
        }
    );
    return items;
  }
}
