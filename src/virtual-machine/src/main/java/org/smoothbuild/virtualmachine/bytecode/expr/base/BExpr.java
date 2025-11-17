package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainBytecodeException;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;

import java.util.Objects;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MembersSizeIsWrongException;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

/**
 * Bytecode expression.
 * This class is thread-safe.
 */
public abstract sealed class BExpr permits BOperation, BValue {
  public static final String DATA_PATH = "data";

  private final MerkleRoot merkleRoot;
  private final BExprDb exprDb;

  public BExpr(MerkleRoot merkleRoot, BExprDb exprDb) {
    this.merkleRoot = merkleRoot;
    this.exprDb = exprDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected BExprDb exprDb() {
    return exprDb;
  }

  protected BKindDb kindDb() {
    return exprDb.kindDb();
  }

  protected HashedDb hashedDb() {
    return exprDb.hashedDb();
  }

  public Hash hash() {
    return merkleRoot.hash();
  }

  public Hash dataHash() {
    return merkleRoot.dataHash();
  }

  public BKind kind() {
    return merkleRoot.kind();
  }

  public abstract BType evaluationType();

  public abstract String exprToString() throws BytecodeException;

  protected List<Member> members(String... names) throws BytecodeException {
    List<Hash> chain = readDataAsHashChain();
    if (chain.size() != names.length) {
      throw new MembersSizeIsWrongException(hash(), kind(), DATA_PATH, names.length, chain.size());
    }
    Member[] array = new Member[names.length];
    for (var i = 0; i < array.length; i++) {
      array[i] = member(names[i], chain.get(i));
    }
    return list(array);
  }

  protected Member loneMember(String name) throws BytecodeException {
    return member(name, dataHash());
  }

  private Member member(String name, Hash hash) throws BytecodeException {
    return new Member(this, readNode(name, hash), name);
  }

  protected ElementsMember loneElementsMember(String name) throws BytecodeException {
    return loneElementsMember(name, none());
  }

  protected ElementsMember loneElementsMember(String name, int expectedCount)
      throws BytecodeException {
    return this.loneElementsMember(name, some(expectedCount));
  }

  private ElementsMember loneElementsMember(String name, Maybe<Integer> expectedCount)
      throws BytecodeException {
    var chain = readDataAsHashChain();
    expectedCount.ifPresent(expected -> {
      if (chain.size() != expected) {
        throw new MembersSizeIsWrongException(hash(), kind(), name, expected, chain.size());
      }
    });
    var exprs = readDataAsExprChain(chain, name);
    return new ElementsMember(this, exprs, name);
  }

  protected <T> T readData(Function0<T, HashedDbException> reader) throws BytecodeException {
    return invokeAndChainHashedDbException(
        reader, e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  protected long hashCoundInDataNode() throws BytecodeException {
    return invokeAndChainHashedDbException(
        () -> exprDb.hashedDb().readHashChainSize(dataHash()),
        e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  private List<BExpr> readDataAsExprChain(List<Hash> chain, String name) throws BytecodeException {
    return chain
        .zipWithIndex()
        .map(tuple -> readNode(name + "[" + tuple.element2() + "]", chain.get(tuple.element2())));
  }

  private List<Hash> readDataAsHashChain() throws BExprDbException {
    return invokeAndChainHashedDbException(
        () -> exprDb.hashedDb().readHashChain(dataHash()),
        e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  private BExpr readNode(String nodePath, Hash nodeHash) throws BytecodeException {
    return invokeAndChainBytecodeException(
        () -> exprDb.get(nodeHash), e -> new DecodeExprNodeException(hash(), kind(), nodePath, e));
  }

  protected void checkMemberEvaluationType(String name, BType actual, BType expected)
      throws MemberHasWrongEvaluationTypeException {
    if (!actual.equals(expected)) {
      throw new MemberHasWrongEvaluationTypeException(this, name, expected, actual);
    }
  }

  protected void checkMemberEvaluationType(String name, BType actual, Class<?> expected)
      throws MemberHasWrongEvaluationTypeException {
    if (!expected.isInstance(actual)) {
      throw new MemberHasWrongEvaluationTypeException(this, name, expected, actual);
    }
  }

  protected <T> T castMember(BExpr member, String name, Class<T> clazz) throws BExprDbException {
    if (clazz.isInstance(member)) {
      @SuppressWarnings("unchecked")
      T result = (T) member;
      return result;
    } else {
      throw new MemberHasWrongTypeException(hash(), kind(), name, clazz, member.getClass());
    }
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof BExpr that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    try {
      return exprToString();
    } catch (BytecodeException e) {
      return "!Exception!@" + hash();
    }
  }
}
