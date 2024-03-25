package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongMemberEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BIfKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBoolType;

/**
 * 'If' operation.
 * This class is thread-safe.
 */
public final class BIf extends BOper {
  public BIf(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BIfKind);
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var hashes = readDataAsHashChain(3);
    var condition = readNodeFromHashChain(hashes, 0);
    var then_ = readNodeFromHashChain(hashes, 1);
    var else_ = readNodeFromHashChain(hashes, 2);
    verifyCondition(condition);
    verifyClause(then_, "then");
    verifyClause(else_, "else");
    return new SubExprsB(condition, then_, else_);
  }

  private void verifyCondition(BExpr condition)
      throws DecodeExprWrongMemberEvaluationTypeException {
    var conditionEvaluationType = condition.evaluationType();
    if (!(conditionEvaluationType instanceof BBoolType)) {
      throw new DecodeExprWrongMemberEvaluationTypeException(
          hash(), kind(), "condition", "Bool", conditionEvaluationType);
    }
  }

  private void verifyClause(BExpr then_, String name)
      throws DecodeExprWrongMemberEvaluationTypeException {
    var thenEvaluationType = then_.evaluationType();
    if (!(thenEvaluationType.equals(evaluationType()))) {
      throw new DecodeExprWrongMemberEvaluationTypeException(
          hash(), kind(), name, evaluationType(), thenEvaluationType);
    }
  }

  private BExpr readNodeFromHashChain(List<Hash> hashes, int nodeIndex) throws BytecodeException {
    var nodePath = dataNodePath(nodeIndex);
    return readNode(nodePath, hashes.get(nodeIndex));
  }

  public static record SubExprsB(BExpr condition, BExpr then_, BExpr else_) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(condition, then_, else_);
    }
  }
}
