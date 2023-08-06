package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainBytecodeException;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;

import java.util.Objects;
import org.smoothbuild.common.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ExprDbException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * Bytecode expression.
 * This class is thread-safe.
 */
public abstract class ExprB {
  public static final String DATA_PATH = "data";

  private final MerkleRoot merkleRoot;
  private final ExprDb exprDb;

  public ExprB(MerkleRoot merkleRoot, ExprDb exprDb) {
    this.merkleRoot = merkleRoot;
    this.exprDb = exprDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected ExprDb exprDb() {
    return exprDb;
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

  public CategoryB category() {
    return merkleRoot.category();
  }

  public abstract TypeB evaluationT();

  public abstract String exprToString() throws BytecodeException;

  protected <T> T readData(Function0<T, HashedDbException> reader) throws BytecodeException {
    return invokeAndChainHashedDbException(
        reader, e -> new DecodeExprNodeException(hash(), category(), DATA_PATH, e));
  }

  protected <T extends ExprB> T readData(Class<T> clazz) throws BytecodeException {
    var exprB = readData();
    return castNode(DATA_PATH, exprB, clazz);
  }

  protected ExprB readData() throws BytecodeException {
    return readNode(DATA_PATH, dataHash());
  }

  protected long readDataSeqSize() throws BytecodeException {
    return invokeAndChainHashedDbException(
        () -> exprDb.hashedDb().readHashChainSize(dataHash()),
        e -> new DecodeExprNodeException(hash(), category(), DATA_PATH, e));
  }

  protected List<ValueB> readDataSeqElems(int expectedSize) throws BytecodeException {
    var seqHashes = readDataSeqHashes(expectedSize);
    var exprs = readDataSeqElems(seqHashes);
    return castDataSeqElements(exprs, ValueB.class);
  }

  protected <T extends ExprB> List<T> readDataSeqElems(Class<T> clazz) throws BytecodeException {
    var exprs = readDataSeqElems();
    return castDataSeqElements(exprs, clazz);
  }

  protected List<ExprB> readDataSeqElems() throws BytecodeException {
    var seqHashes = readDataSeqHashes();
    return readDataSeqElems(seqHashes);
  }

  private List<ExprB> readDataSeqElems(List<Hash> seq) throws BytecodeException {
    return seq.zipWithIndex()
        .map(tuple -> readNode(dataNodePath(tuple.element2()), seq.get(tuple.element2())));
  }

  private List<Hash> readDataSeqHashes(int expectedSize) throws ExprDbException {
    List<Hash> data = readDataSeqHashes();
    if (data.size() != expectedSize) {
      throw new DecodeExprWrongChainSizeException(
          hash(), category(), DATA_PATH, expectedSize, data.size());
    }
    return data;
  }

  private List<Hash> readDataSeqHashes() throws ExprDbException {
    return invokeAndChainHashedDbException(
        () -> exprDb.hashedDb().readHashChain(dataHash()),
        e -> new DecodeExprNodeException(hash(), category(), DATA_PATH, e));
  }

  protected <T> T readDataSeqElem(int i, int expectedSize, Class<T> clazz)
      throws BytecodeException {
    var expr = readDataSeqElem(i, expectedSize);
    return castNode(dataNodePath(i), expr, clazz);
  }

  private ExprB readDataSeqElem(int i, int expectedSize) throws BytecodeException {
    var elemHash = readDataSeqElemHash(i, expectedSize);
    return readNode(dataNodePath(i), elemHash);
  }

  private ExprB readNode(String nodePath, Hash nodeHash) throws BytecodeException {
    return invokeAndChainBytecodeException(
        () -> exprDb.get(nodeHash),
        e -> new DecodeExprNodeException(hash(), category(), nodePath, e));
  }

  private Hash readDataSeqElemHash(int i, int expectedSize) throws ExprDbException {
    checkElementIndex(i, expectedSize);
    return readDataSeqHashes(expectedSize).get(i);
  }

  protected static String exprsToString(List<? extends ExprB> exprs) {
    return exprs.map(ExprB::valToStringSafe).toString(",");
  }

  private <T> List<T> castDataSeqElements(List<ExprB> elems, Class<T> clazz)
      throws ExprDbException {
    for (int i = 0; i < elems.size(); i++) {
      castNode(dataNodePath(i), elems.get(i), clazz);
    }
    @SuppressWarnings("unchecked")
    List<T> result = (List<T>) elems;
    return result;
  }

  private <T> T castNode(String nodePath, ExprB nodeExpr, Class<T> clazz) throws ExprDbException {
    if (clazz.isInstance(nodeExpr)) {
      @SuppressWarnings("unchecked")
      T result = (T) nodeExpr;
      return result;
    } else {
      throw new DecodeExprWrongNodeClassException(
          hash(), category(), nodePath, clazz, nodeExpr.getClass());
    }
  }

  private static String dataNodePath(int i) {
    return DATA_PATH + "[" + i + "]";
  }

  @Override
  public boolean equals(Object object) {
    return (object instanceof ExprB that) && Objects.equals(hash(), that.hash());
  }

  @Override
  public int hashCode() {
    return hash().hashCode();
  }

  @Override
  public String toString() {
    return valToStringSafe() + "@" + hash();
  }

  private String valToStringSafe() {
    try {
      return exprToString();
    } catch (BytecodeException e) {
      return "!Exception!@" + hash();
    }
  }
}
