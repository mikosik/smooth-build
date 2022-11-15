package org.smoothbuild.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.bytecode.expr.Helpers.wrapBytecodeDbExcAsDecodeExprNodeException;
import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsDecodeExprNodeException;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Objects;

import org.smoothbuild.bytecode.expr.Helpers.HashedDbCallable;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNodeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongSeqSizeExc;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.type.CategoryB;
import org.smoothbuild.bytecode.type.inst.TypeB;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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

  public abstract TypeB evalT();

  public abstract String exprToString();

  protected <T> T readData(HashedDbCallable<T> reader) {
    return wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), DATA_PATH, reader);
  }

  protected ExprB readDataAsExpr() {
    return wrapBytecodeDbExcAsDecodeExprNodeException(
        hash(), category(), DATA_PATH, () -> bytecodeDb.get(dataHash()));
  }

  protected long readDataSeqSize() {
    return wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), DATA_PATH,
        () -> bytecodeDb.hashedDb().readSeqSize(dataHash()));
  }

  protected ImmutableList<ValueB> readDataSeqElems(int expectedSize) {
    var seqHashes = readDataSeqHashes(expectedSize);
    var exprs = readDataSeqElems(seqHashes);
    return castDataSeqElem(exprs, ValueB.class);
  }

  protected <T extends ExprB> ImmutableList<T> readDataSeqElems(Class<T> clazz) {
    var exprs = readDataSeqElems();
    return castDataSeqElem(exprs, clazz);
  }

  protected ImmutableList<ExprB> readDataSeqElems() {
    var seqHashes = readDataSeqHashes();
    return readDataSeqElems(seqHashes);
  }

  private ImmutableList<ExprB> readDataSeqElems(ImmutableList<Hash> seq) {
    Builder<ExprB> builder = ImmutableList.builder();
    for (int i = 0; i < seq.size(); i++) {
      var expr = readDataSeqElem(i, seq.get(i));
      builder.add(expr);
    }
    return builder.build();
  }

  private ImmutableList<Hash> readDataSeqHashes(int expectedSize) {
    ImmutableList<Hash> data = readDataSeqHashes();
    if (data.size() != expectedSize) {
      throw new DecodeExprWrongSeqSizeExc(hash(), category(), DATA_PATH, expectedSize, data.size());
    }
    return data;
  }

  private ImmutableList<Hash> readDataSeqHashes() {
    return wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), DATA_PATH,
        () -> bytecodeDb.hashedDb().readSeq(dataHash()));
  }

  protected <T> T readDataSeqElem(int i, int expectedSize, Class<T> clazz) {
    var expr = readDataSeqElem(i, expectedSize);
    return castDataSeqElem(expr, i, clazz);
  }

  private ExprB readDataSeqElem(int i, int expectedSize) {
    var elemHash = readDataSeqElemHash(i, expectedSize);
    return readDataSeqElem(i, elemHash);
  }

  private ExprB readDataSeqElem(int i, Hash elemHash) {
    return wrapBytecodeDbExcAsDecodeExprNodeException(
        hash(), category(), DATA_PATH, i, () -> bytecodeDb.get(elemHash));
  }

  protected Hash readDataSeqElemHash(int i, int expectedSize) {
    checkElementIndex(i, expectedSize);
    return readDataSeqHashes(expectedSize).get(i);
  }

  protected static String exprsToString(ImmutableList<? extends ExprB> exprs) {
    return toCommaSeparatedString(exprs, ExprB::valToStringSafe);
  }

  private <T> T castDataSeqElem(ExprB expr, int index, Class<T> clazz) {
    if (clazz.isInstance(expr)) {
      @SuppressWarnings("unchecked")
      T result = (T) expr;
      return result;
    } else {
      throw new DecodeExprWrongNodeClassExc(
          hash(), category(), DATA_PATH, index, clazz, expr.getClass());
    }
  }

  private <T> ImmutableList<T> castDataSeqElem(ImmutableList<ExprB> elems, Class<T> clazz) {
    for (int i = 0; i < elems.size(); i++) {
      ExprB elem = elems.get(i);
      if (!clazz.isInstance(elem)) {
        throw new DecodeExprWrongNodeClassExc(
            hash(), category(), DATA_PATH, i, clazz, elem.getClass());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
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
