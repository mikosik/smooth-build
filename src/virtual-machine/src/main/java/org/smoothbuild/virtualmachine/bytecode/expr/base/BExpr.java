package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainBytecodeException;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;

import java.util.Objects;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NodeChainSizeIsWrongException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NodeClassIsWrongException;
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

  protected <T> T readData(Function0<T, HashedDbException> reader) throws BytecodeException {
    return invokeAndChainHashedDbException(
        reader, e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  protected <T extends BExpr> T readData(Class<T> clazz) throws BytecodeException {
    var exprB = readData();
    return castNode(DATA_PATH, exprB, clazz);
  }

  protected BExpr readData(String memberName, BType expectedEvaluationType)
      throws BytecodeException {
    var expr = readNode(DATA_PATH, dataHash());
    validateEvaluationType(memberName, expectedEvaluationType, expr.evaluationType());
    return expr;
  }

  protected BExpr readData() throws BytecodeException {
    return readNode(DATA_PATH, dataHash());
  }

  protected long readDataAsHashChainSize() throws BytecodeException {
    return invokeAndChainHashedDbException(
        () -> exprDb.hashedDb().readHashChainSize(dataHash()),
        e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  protected List<BValue> readDataAsValueChain(int expectedSize) throws BytecodeException {
    var chainHashes = readDataAsHashChain(expectedSize);
    var exprs = readDataAsExprChain(chainHashes);
    return castDataChainElements(exprs, BValue.class);
  }

  protected <T extends BExpr> List<T> readDataAsExprChain(Class<T> clazz) throws BytecodeException {
    var exprs = readDataAsExprChain();
    return castDataChainElements(exprs, clazz);
  }

  protected List<BExpr> readDataAsExprChain() throws BytecodeException {
    var chainHashes = readDataAsHashChain();
    return readDataAsExprChain(chainHashes);
  }

  private List<BExpr> readDataAsExprChain(List<Hash> chain) throws BytecodeException {
    return chain
        .zipWithIndex()
        .map(tuple -> readNode(dataNodePath(tuple.element2()), chain.get(tuple.element2())));
  }

  protected List<Hash> readDataAsHashChain(int expectedSize) throws BExprDbException {
    List<Hash> data = readDataAsHashChain();
    if (data.size() != expectedSize) {
      throw new NodeChainSizeIsWrongException(hash(), kind(), DATA_PATH, expectedSize, data.size());
    }
    return data;
  }

  private List<Hash> readDataAsHashChain() throws BExprDbException {
    return invokeAndChainHashedDbException(
        () -> exprDb.hashedDb().readHashChain(dataHash()),
        e -> new DecodeExprNodeException(hash(), kind(), DATA_PATH, e));
  }

  protected <T> T readElementFromDataAsInstanceChain(int i, int expectedSize, Class<T> clazz)
      throws BytecodeException {
    var expr = readElementFromDataAsExprChain(i, expectedSize);
    return castNode(dataNodePath(i), expr, clazz);
  }

  private BExpr readElementFromDataAsExprChain(int i, int expectedSize) throws BytecodeException {
    var elemHash = readHashFromDataAsHashChain(i, expectedSize);
    return readNode(dataNodePath(i), elemHash);
  }

  protected BExpr readMemberFromHashChain(
      List<Hash> hashes, int index, String nodeName, BType expectedEvaluationType)
      throws BytecodeException {
    var expr = readMemberFromHashChain(hashes, index);
    validateEvaluationType(nodeName, expectedEvaluationType, expr.evaluationType());
    return expr;
  }

  protected void validateEvaluationType(
      String nodeName, BType expectedEvaluationType, BType evaluationType)
      throws MemberHasWrongEvaluationTypeException {
    if (!(evaluationType.equals(expectedEvaluationType))) {
      throw new MemberHasWrongEvaluationTypeException(
          hash(), kind(), nodeName, expectedEvaluationType, evaluationType);
    }
  }

  protected <T extends BExpr> T readAndCastMemberFromHashChain(
      List<Hash> hashes, int index, String name, Class<T> clazz) throws BytecodeException {
    return castMember(readMemberFromHashChain(hashes, index), name, clazz);
  }

  protected BExpr readMemberFromHashChain(List<Hash> hashes, int nodeIndex)
      throws BytecodeException {
    var nodePath = dataNodePath(nodeIndex);
    return readNode(nodePath, hashes.get(nodeIndex));
  }

  protected BExpr readNode(String nodePath, Hash nodeHash) throws BytecodeException {
    return invokeAndChainBytecodeException(
        () -> exprDb.get(nodeHash), e -> new DecodeExprNodeException(hash(), kind(), nodePath, e));
  }

  private Hash readHashFromDataAsHashChain(int i, int expectedSize) throws BExprDbException {
    checkElementIndex(i, expectedSize);
    return readDataAsHashChain(expectedSize).get(i);
  }

  private <T> List<T> castDataChainElements(List<BExpr> elements, Class<T> clazz)
      throws BExprDbException {
    for (int i = 0; i < elements.size(); i++) {
      castNode(dataNodePath(i), elements.get(i), clazz);
    }
    @SuppressWarnings("unchecked")
    List<T> result = (List<T>) elements;
    return result;
  }

  protected <T> T castMember(BExpr nodeExpr, String name, Class<T> clazz) throws BExprDbException {
    if (clazz.isInstance(nodeExpr)) {
      @SuppressWarnings("unchecked")
      T result = (T) nodeExpr;
      return result;
    } else {
      throw new MemberHasWrongTypeException(hash(), kind(), name, clazz, nodeExpr.getClass());
    }
  }

  protected <T> T castNode(String nodePath, BExpr nodeExpr, Class<T> clazz)
      throws BExprDbException {
    if (clazz.isInstance(nodeExpr)) {
      @SuppressWarnings("unchecked")
      T result = (T) nodeExpr;
      return result;
    } else {
      throw new NodeClassIsWrongException(hash(), kind(), nodePath, clazz, nodeExpr.getClass());
    }
  }

  protected static String dataNodePath(int i) {
    return DATA_PATH + "[" + i + "]";
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
