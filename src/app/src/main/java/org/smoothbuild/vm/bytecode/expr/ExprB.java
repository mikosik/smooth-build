package org.smoothbuild.vm.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;

import java.util.Objects;

import org.smoothbuild.vm.bytecode.expr.Helpers.HashedDbCallable;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNodeExc;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongSeqSizeExc;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.HashedDb;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import io.vavr.collection.Array;

/**
 * Bytecode expression.
 * This class is thread-safe.
 */
public abstract class ExprB {
  public static final String DATA_PATH = "data";

  private final MerkleRoot merkleRoot;
  private final BytecodeDb bytecodeDb;

  public ExprB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    this.merkleRoot = merkleRoot;
    this.bytecodeDb = bytecodeDb;
  }

  protected MerkleRoot merkleRoot() {
    return merkleRoot;
  }

  protected BytecodeDb bytecodeDb() {
    return bytecodeDb;
  }

  protected HashedDb hashedDb() {
    return bytecodeDb.hashedDb();
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

  public abstract String exprToString();

  protected <T> T readData(HashedDbCallable<T> reader) {
    return Helpers.wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), DATA_PATH, reader);
  }

  protected <T extends ExprB> T readData(Class<T> clazz) {
    var exprB = readData();
    return castNode(DATA_PATH, exprB, clazz);
  }

  protected ExprB readData() {
    return readNode(DATA_PATH, dataHash());
  }

  protected long readDataSeqSize() {
    return Helpers.wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), DATA_PATH,
        () -> bytecodeDb.hashedDb().readSeqSize(dataHash()));
  }

  protected Array<ValueB> readDataSeqElems(int expectedSize) {
    var seqHashes = readDataSeqHashes(expectedSize);
    var exprs = readDataSeqElems(seqHashes);
    return castDataSeqElements(exprs, ValueB.class);
  }

  protected <T extends ExprB> Array<T> readDataSeqElems(Class<T> clazz) {
    var exprs = readDataSeqElems();
    return castDataSeqElements(exprs, clazz);
  }

  protected Array<ExprB> readDataSeqElems() {
    var seqHashes = readDataSeqHashes();
    return readDataSeqElems(seqHashes);
  }

  private Array<ExprB> readDataSeqElems(Array<Hash> seq) {
    return seq.zipWithIndex((hash, i) -> readNode(indexOfDataNode(i), seq.get(i)));
  }

  private Array<Hash> readDataSeqHashes(int expectedSize) {
    Array<Hash> data = readDataSeqHashes();
    if (data.size() != expectedSize) {
      throw new DecodeExprWrongSeqSizeExc(hash(), category(), DATA_PATH, expectedSize, data.size());
    }
    return data;
  }

  private Array<Hash> readDataSeqHashes() {
    return Helpers.wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), DATA_PATH,
        () -> bytecodeDb.hashedDb().readSeq(dataHash()));
  }

  protected <T> T readDataSeqElem(int i, int expectedSize, Class<T> clazz) {
    var expr = readDataSeqElem(i, expectedSize);
    return castNode(indexOfDataNode(i), expr, clazz);
  }

  private ExprB readDataSeqElem(int i, int expectedSize) {
    var elemHash = readDataSeqElemHash(i, expectedSize);
    return readNode(indexOfDataNode(i), elemHash);
  }

  private ExprB readNode(String nodePath, Hash nodeHash) {
    return Helpers.wrapBytecodeDbExcAsDecodeExprNodeException(
        hash(), category(), nodePath, () -> bytecodeDb.get(nodeHash));
  }

  protected Hash readDataSeqElemHash(int i, int expectedSize) {
    checkElementIndex(i, expectedSize);
    return readDataSeqHashes(expectedSize).get(i);
  }

  protected static String exprsToString(Array<? extends ExprB> exprs) {
    return exprs.map(ExprB::valToStringSafe).mkString(",");
  }

  private <T> Array<T> castDataSeqElements(Array<ExprB> elems, Class<T> clazz) {
    for (int i = 0; i < elems.size(); i++) {
      castNode(indexOfDataNode(i), elems.get(i), clazz);
    }
    @SuppressWarnings("unchecked")
    Array<T> result = (Array<T>) elems;
    return result;
  }

  private <T> T castNode(String nodePath, ExprB nodeExpr, Class<T> clazz) {
    if (clazz.isInstance(nodeExpr)) {
      @SuppressWarnings("unchecked")
      T result = (T) nodeExpr;
      return result;
    } else {
      throw new DecodeExprWrongNodeClassExc(
          hash(), category(), nodePath, clazz, nodeExpr.getClass());
    }
  }

  private static String indexOfDataNode(int i) {
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
    } catch (DecodeExprNodeExc e) {
      return "!Exception!@" + hash();
    }
  }
}
