package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.Lists.allMatchOtherwise;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.type.oper.CombineCB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import io.vavr.collection.Array;

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
  public TupleTB evaluationT() {
    return category().evaluationT();
  }

  @Override
  public CombineSubExprsB subExprs() {
    return new CombineSubExprsB(items());
  }

  public Array<ExprB> items() {
    Array<TypeB> expectedElementsTs = category().evaluationT().elements();
    Array<ExprB> items = readDataSeqElems(ExprB.class);
    allMatchOtherwise(
        expectedElementsTs,
        items,
        (type, item) -> type.equals(item.evaluationT()),
        (type, item) -> {
          throw new DecodeCombineWrongElementsSizeException(hash(), category(), item);
        },
        (index) -> {
          throw new DecodeExprWrongNodeTypeException(hash(), category(), "elements", index,
              expectedElementsTs.get(index), items.get(index).evaluationT());
        }
    );
    return items;
  }
}
