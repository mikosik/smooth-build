package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.oper.CombineCB;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CombineB extends OperB {
  public CombineB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof CombineCB);
  }

  @Override
  public CombineCB category() {
    return (CombineCB) super.category();
  }

  @Override
  public TupleTB evalT() {
    return category().evalT();
  }

  public ImmutableList<ExprB> items() {
    return dataSeq();
  }

  @Override
  public ImmutableList<ExprB> dataSeq() {
    var expectedItemTs = category().evalT().items();
    var items = readDataSeqElems(ExprB.class);
    allMatchOtherwise(
        expectedItemTs,
        items,
        (type, item) -> type.equals(item.evalT()),
        (type, item) -> { throw new DecodeCombineWrongItemsSizeExc(hash(), category(), item); },
        (index) -> {
          throw new DecodeExprWrongNodeTypeExc(hash(), category(), "items", index,
              expectedItemTs.get(index), items.get(index).evalT());
        }
    );
    return items;
  }
}
