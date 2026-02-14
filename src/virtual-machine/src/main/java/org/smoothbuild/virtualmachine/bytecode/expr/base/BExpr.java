package org.smoothbuild.virtualmachine.bytecode.expr.base;

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
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprsCountIsWrongException;
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

  protected List<BSubExpr> createSubExprList(List<String> list) throws BytecodeException {
    var hashChain = readDataAsHashChain(list.size());
    return list.zip(hashChain, this::subExpr);
  }

  private List<Hash> readDataAsHashChain(int count) throws BExprDbException {
    List<Hash> chain = readDataAsHashChain();
    if (count != -1 && chain.size() != count) {
      throw new SubExprsCountIsWrongException(hash(), kind(), DATA_PATH, count, chain.size());
    }
    return chain;
  }

  protected BSubExpr createLoneSubExpr(String name) throws BytecodeException {
    return subExpr(name, dataHash());
  }

  private BSubExpr subExpr(String name, Hash hash) throws BytecodeException {
    return new BSubExprImpl(this, readNode(name, hash), name);
  }

  protected BElements createLoneElements(String name) throws BytecodeException {
    return createLoneElements(name, none());
  }

  protected BElements createLoneElements(String name, int expectedCount) throws BytecodeException {
    return this.createLoneElements(name, some(expectedCount));
  }

  private BElements createLoneElements(String name, Maybe<Integer> expectedCount)
      throws BytecodeException {
    var chain = readDataAsHashChain();
    expectedCount.ifPresent(expected -> {
      if (chain.size() != expected) {
        throw new SubExprsCountIsWrongException(hash(), kind(), name, expected, chain.size());
      }
    });
    var exprs = readDataAsExprChain(chain, name);
    return new BElements(this, exprs, name);
  }

  protected <T> T readData(Function0<T, HashedDbException> reader) throws BytecodeException {
    return invokeAndChainHashedDbException(
        reader, e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  protected long hashCountInDataNode() throws BytecodeException {
    return invokeAndChainHashedDbException(
        () -> exprDb.hashedDb().readHashChainSize(dataHash()),
        e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  protected List<BExpr> readDataAsExprChain(int count) throws BytecodeException {
    var hashes = readDataAsHashChain(count);
    return readDataAsExprChain(hashes, DATA_PATH);
  }

  private List<BExpr> readDataAsExprChain(List<Hash> chain, String name) throws BytecodeException {
    return chain
        .zipWithIndex()
        .map(tuple -> readNode(name + "[" + tuple.element2() + "]", tuple.element1()));
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

  protected void checkSubExprEvaluationType(String name, BType actual, BType expected)
      throws SubExprHasWrongEvaluationTypeException {
    if (!actual.equals(expected)) {
      throw new SubExprHasWrongEvaluationTypeException(this, name, expected, actual);
    }
  }

  protected void checkSubExprEvaluationType(String name, BType actual, Class<?> expected)
      throws SubExprHasWrongEvaluationTypeException {
    if (!expected.isInstance(actual)) {
      throw new SubExprHasWrongEvaluationTypeException(this, name, expected, actual);
    }
  }

  protected <T> T castSubExpr(BExpr subExpr, String name, Class<T> clazz) throws BExprDbException {
    if (clazz.isInstance(subExpr)) {
      @SuppressWarnings("unchecked")
      T result = (T) subExpr;
      return result;
    } else {
      throw new SubExprHasWrongTypeException(hash(), kind(), name, clazz, subExpr.getClass());
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
