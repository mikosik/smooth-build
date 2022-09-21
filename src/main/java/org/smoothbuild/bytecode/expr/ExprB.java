package org.smoothbuild.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.bytecode.expr.Helpers.wrapBytecodeDbExcAsDecodeExprNodeException;
import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsDecodeExprNodeException;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Objects;

import org.smoothbuild.bytecode.expr.Helpers.HashedDbCallable;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNodeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongSeqSizeExc;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.type.CategoryB;
import org.smoothbuild.bytecode.type.val.TypeB;

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

  public abstract TypeB type();

  public abstract String exprToString();

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

  protected <T> T readData(HashedDbCallable<T> reader) {
    return wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), DATA_PATH, reader);
  }

  protected <T> T readExpr(String path, Hash hash, Class<T> clazz) {
    var expr = Helpers.wrapBytecodeDbExcAsDecodeExprNodeException(
        hash(), category(), path, () -> bytecodeDb().get(hash));
    return castExpr(expr, path, clazz);
  }

  protected <T> T readSeqElemExpr(String path, Hash hash, int i, int expectedSize, Class<T> clazz) {
    var expr = readSeqElemExpr(path, hash, i, expectedSize);
    return castExpr(expr, path, i, clazz);
  }

  protected ExprB readSeqElemExprWithType(
      String path, Hash hash, int i, int expectedSize, TypeB type) {
    var expr = readSeqElemExpr(path, hash, i, expectedSize);
    return validateType(expr, DATA_PATH, i, type);
  }

  private ExprB readSeqElemExpr(String path, Hash hash, int i, int expectedSize) {
    var elemHash = readSeqElemHash(path, hash, i, expectedSize);
    var expr = wrapBytecodeDbExcAsDecodeExprNodeException(
        hash(), category(), path, i, () -> bytecodeDb().get(elemHash));
    return expr;
  }

  protected Hash readSeqElemHash(String path, Hash hash, int i, int expectedSize) {
    checkElementIndex(i, expectedSize);
    return readSeqHashes(path, hash, expectedSize)
        .get(i);
  }

  protected <T> ImmutableList<T> readSeqExprs(
      String path, Hash hash, int expectedSize, Class<T> clazz) {
    var seqHashes = readSeqHashes(path, hash, expectedSize);
    var exprs = readSeqExprs(path, seqHashes);
    return castSeq(exprs, path, clazz);
  }

  protected <T> ImmutableList<T> readSeqExprs(String path, Hash hash, Class<T> clazz) {
    var exprs = readSeqExprs(path, hash);
    return castSeq(exprs, path, clazz);
  }

  protected ImmutableList<ExprB> readSeqExprs(String path, Hash hash) {
    var seqHashes = readSeqHashes(path, hash);
    return readSeqExprs(path, seqHashes);
  }

  private ImmutableList<ExprB> readSeqExprs(String path, ImmutableList<Hash> seq) {
    Builder<ExprB> builder = ImmutableList.builder();
    for (int i = 0; i < seq.size(); i++) {
      int index = i;
      var expr = wrapBytecodeDbExcAsDecodeExprNodeException(hash(), category(), path, index,
          () -> bytecodeDb.get(seq.get(index)));
      builder.add(expr);
    }
    return builder.build();
  }

  private ImmutableList<Hash> readSeqHashes(String path, Hash hash, int expectedSize) {
    ImmutableList<Hash> data = readSeqHashes(path, hash);
    if (data.size() != expectedSize) {
      throw new DecodeExprWrongSeqSizeExc(hash(), category(), path, expectedSize, data.size());
    }
    return data;
  }

  private ImmutableList<Hash> readSeqHashes(String path, Hash hash) {
    return wrapHashedDbExcAsDecodeExprNodeException(hash(), category(), path,
        () -> bytecodeDb.hashedDb().readSeq(hash));
  }

  protected static String exprsToString(ImmutableList<? extends ExprB> exprs) {
    return toCommaSeparatedString(exprs, ExprB::valToStringSafe);
  }

  private <T> T castExpr(ExprB expr, String path, Class<T> clazz) {
    if (clazz.isInstance(expr)) {
      @SuppressWarnings("unchecked")
      T result = (T) expr;
      return result;
    } else {
      throw new DecodeExprWrongNodeClassExc(hash(), category(), path, clazz, expr.getClass());
    }
  }

  private <T> T castExpr(ExprB expr, String path, int index, Class<T> clazz) {
    if (clazz.isInstance(expr)) {
      @SuppressWarnings("unchecked")
      T result = (T) expr;
      return result;
    } else {
      throw new DecodeExprWrongNodeClassExc(hash(), category(), path, index, clazz, expr.getClass());
    }
  }

  private <T> ImmutableList<T> castSeq(ImmutableList<ExprB> elems, String path, Class<T> clazz) {
    for (int i = 0; i < elems.size(); i++) {
      ExprB elem = elems.get(i);
      if (!clazz.isInstance(elem)) {
        throw new DecodeExprWrongNodeClassExc(hash(), category(), path, i, clazz, elem.getClass());
      }
    }
    @SuppressWarnings("unchecked")
    ImmutableList<T> result = (ImmutableList<T>) elems;
    return result;
  }

  protected ExprB validateType(ExprB expr, String path, int index, TypeB expectedT) {
    var exprT = expr.type();
    if (!expectedT.equals(exprT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), category(), path, index, expectedT, exprT);
    }
    return expr;
  }

  private String valToStringSafe() {
    try {
      return exprToString();
    } catch (DecodeExprNodeExc e) {
      return "?Exception?@" + hash();
    }
  }
}
