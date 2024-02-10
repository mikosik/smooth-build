package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.type.oper.CombineCB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

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
  public CombineSubExprsB subExprs() throws BytecodeException {
    return new CombineSubExprsB(items());
  }

  public List<ExprB> items() throws BytecodeException {
    List<TypeB> expectedTypes = category().evaluationT().elements();
    List<ExprB> items = readDataSeqElems(ExprB.class);
    if (items.size() != expectedTypes.size()) {
      throw new DecodeCombineWrongElementsSizeException(hash(), category(), items.size());
    }
    for (int i = 0; i < items.size(); i++) {
      ExprB item = items.get(i);
      TypeB type = expectedTypes.get(i);
      if (!type.equals(item.evaluationT())) {
        throw new DecodeExprWrongNodeTypeException(
            hash(),
            category(),
            "elements",
            i,
            expectedTypes.get(i),
            items.get(i).evaluationT());
      }
    }
    return items;
  }
}
