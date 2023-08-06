package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is thread-safe.
 */
public class CombineB extends OperB {
  public CombineB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
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
  public SubExprsB subExprs() throws BytecodeException {
    return new SubExprsB(items());
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

  public static record SubExprsB(List<ExprB> items) implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return items;
    }
  }
}
