package org.smoothbuild.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;
import static org.smoothbuild.bytecode.expr.exc.DecodeExprRootExc.cannotReadRootException;
import static org.smoothbuild.bytecode.expr.exc.DecodeExprRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.bytecode.type.Validator.validateArgs;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.smoothbuild.bytecode.expr.exc.DecodeExprCatExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNoSuchExprExc;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.RefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BlobBBuilder;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.DefFuncB;
import org.smoothbuild.bytecode.expr.val.IfFuncB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.MapFuncB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.bytecode.hashed.exc.NoSuchDataExc;
import org.smoothbuild.bytecode.type.CategoryB;
import org.smoothbuild.bytecode.type.CategoryDb;
import org.smoothbuild.bytecode.type.exc.CategoryDbExc;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.DefFuncCB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.NatFuncCB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class BytecodeDb {
  private final HashedDb hashedDb;
  private final CategoryDb categoryDb;

  public BytecodeDb(HashedDb hashedDb, CategoryDb categoryDb) {
    this.hashedDb = hashedDb;
    this.categoryDb = categoryDb;
  }

  // methods for creating InstB subclasses

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return new ArrayBBuilder(type, this);
  }

  public BlobBBuilder blobBuilder() {
    return wrapHashedDbExcAsBytecodeDbExc(() -> new BlobBBuilder(this, hashedDb.sink()));
  }

  public BoolB bool(boolean value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newBool(value));
  }

  public NatFuncB natFunc(FuncTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    var cat = categoryDb.natFunc(type);
    return wrapHashedDbExcAsBytecodeDbExc(
        () -> newNatFunc(cat, jar, classBinaryName, isPure));
  }

  public DefFuncB defFunc(FuncTB type, ExprB body) {
    checkBodyTypeAssignableToFuncResT(type, body);
    var cat = categoryDb.defFunc(type);
    return wrapHashedDbExcAsBytecodeDbExc(() -> newDefFunc(cat, body));
  }

  private void checkBodyTypeAssignableToFuncResT(FuncTB type, ExprB body) {
    if (!type.res().equals(body.type())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.res().q()
          + " but body.type() is " + body.type().q() + ".");
    }
  }

  public IntB int_(BigInteger value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newInt(value));
  }

  public StringB string(String value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newString(value));
  }

  public TupleB tuple(TupleTB tupleT, ImmutableList<InstB> items) {
    var itemTs = tupleT.items();
    allMatchOtherwise(itemTs, items, (s, i) -> Objects.equals(s, i.category()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + itemTs.get(i).name() + " but provided item has type "
              + items.get(i).category().name() + " at that index.");
        }
    );

    return wrapHashedDbExcAsBytecodeDbExc(() -> newTuple(tupleT, items));
  }

  // methods for creating OperB subclasses

  public CallB call(TypeB evalT, ExprB func, CombineB args) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCall(evalT, func, args));
  }

  public CombineB combine(TupleTB evalT, ImmutableList<ExprB> items) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCombine(evalT, items));
  }

  public IfFuncB ifFunc(TypeB t) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newIfFunc(t));
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newMapFunc(r, s));
  }

  public OrderB order(ArrayTB evalT, ImmutableList<ExprB> elems) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOrder(evalT, elems));
  }

  public RefB ref(TypeB evalT, BigInteger value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newRef(evalT, value));
  }

  public SelectB select(TypeB evalT, ExprB selectable, IntB index) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newSelect(evalT, selectable, index));
  }

  // generic getter

  public ExprB get(Hash rootHash) {
    List<Hash> hashes = decodeRootSeq(rootHash);
    int rootSeqSize = hashes.size();
    if (rootSeqSize != 2 && rootSeqSize != 1) {
      throw wrongSizeOfRootSeqException(rootHash, rootSeqSize);
    }
    CategoryB category = getCatOrChainException(rootHash, hashes.get(0));
    if (category.containsData()) {
      if (rootSeqSize != 2) {
        throw wrongSizeOfRootSeqException(rootHash, category, rootSeqSize);
      }
      var dataHash = hashes.get(1);
      return category.newExpr(new MerkleRoot(rootHash, category, dataHash), this);
    } else {
      if (rootSeqSize != 1) {
        throw wrongSizeOfRootSeqException(rootHash, category, rootSeqSize);
      }
      return category.newExpr(new MerkleRoot(rootHash, category, null), this);
    }
  }

  private CategoryB getCatOrChainException(Hash rootHash, Hash typeHash) {
    try {
      return categoryDb.get(typeHash);
    } catch (CategoryDbExc e) {
      throw new DecodeExprCatExc(rootHash, e);
    }
  }

  private List<Hash> decodeRootSeq(Hash rootHash) {
    try {
      return hashedDb.readSeq(rootHash);
    } catch (NoSuchDataExc e) {
      throw new DecodeExprNoSuchExprExc(rootHash, e);
    } catch (HashedDbExc e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  // methods for creating InstBs

  public ArrayB newArray(ArrayTB type, List<InstB> elems) throws HashedDbExc {
    var data = writeArrayData(elems);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  public BlobB newBlob(Hash dataHash) throws HashedDbExc {
    var root = newRoot(categoryDb.blob(), dataHash);
    return categoryDb.blob().newExpr(root, this);
  }

  private BoolB newBool(boolean value) throws HashedDbExc {
    var data = writeBoolData(value);
    var root = newRoot(categoryDb.bool(), data);
    return categoryDb.bool().newExpr(root, this);
  }

  private DefFuncB newDefFunc(DefFuncCB type, ExprB body) throws HashedDbExc {
    var data = writeDefFuncData(body);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private IntB newInt(BigInteger value) throws HashedDbExc {
    var data = writeIntData(value);
    var root = newRoot(categoryDb.int_(), data);
    return categoryDb.int_().newExpr(root, this);
  }

  private NatFuncB newNatFunc(NatFuncCB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    var data = writeNatFuncData(jar, classBinaryName, isPure);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private StringB newString(String string) throws HashedDbExc {
    var data = writeStringData(string);
    var root = newRoot(categoryDb.string(), data);
    return categoryDb.string().newExpr(root, this);
  }

  private TupleB newTuple(TupleTB type, ImmutableList<InstB> items) throws HashedDbExc {
    var data = writeTupleData(items);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  // methods for creating Expr-s

  private CallB newCall(TypeB evalT, ExprB func, CombineB args) throws HashedDbExc {
    var funcTB = castTypeToFuncTB(func);
    validateArgsInCall(funcTB, args);
    var resT = funcTB.res();
    if (!evalT.equals(resT)) {
      throw new IllegalArgumentException(
          "Call's result type " + resT.q() + " cannot be assigned to evalT " + evalT.q() + ".");
    }

    var type = categoryDb.call(evalT);
    var data = writeCallData(func, args);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private FuncTB castTypeToFuncTB(ExprB callable) {
    if (callable.type() instanceof FuncTB funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncB.");
    }
  }

  private void validateArgsInCall(FuncTB funcTB, CombineB args) {
    validateArgs(funcTB, args.type().items(), () -> {
      throw illegalArgs(funcTB, args.type());
    });
  }

  private IllegalArgumentException illegalArgs(FuncTB funcTB, TupleTB argsT) {
    return new IllegalArgumentException(
        "Argument evaluation types %s should be equal to function parameter types %s."
            .formatted(itemTsToString(argsT), itemTsToString(funcTB.params())));
  }

  private static String itemTsToString(TupleTB argsT) {
    return "(" + toCommaSeparatedString(argsT.items()) + ")";
  }

  private OrderB newOrder(ArrayTB evalT, ImmutableList<ExprB> elems) throws HashedDbExc {
    validateOrderElems(evalT.elem(), elems);
    var type = categoryDb.order(evalT);
    var data = writeOrderData(elems);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private void validateOrderElems(TypeB elemT, ImmutableList<ExprB> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElemT = elems.get(i).type();
      if (!elemT.equals(iElemT)) {
        throw new IllegalArgumentException("Illegal elem type. Expected " + elemT.q()
            + " but element at index " + i + " has type " + iElemT.q() + ".");
      }
    }
  }

  private CombineB newCombine(TupleTB evalT, ImmutableList<ExprB> items) throws HashedDbExc {
    validateCombineItems(evalT, items);
    var type = categoryDb.combine(evalT);
    var data = writeCombineData(items);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private void validateCombineItems(TupleTB evalT, ImmutableList<ExprB> items) {
    var expectedItemTs = evalT.items();
    if (expectedItemTs.size() != items.size()) {
      throw new IllegalArgumentException(
          "Expected " + expectedItemTs.size() + " items, got " + items.size() + ".");
    }
    for (int i = 0; i < items.size(); i++) {
      var expectedItemT = expectedItemTs.get(i);
      var actualItemT = items.get(i).type();
      if (!expectedItemT.equals(actualItemT)) {
        throw new IllegalArgumentException("Illegal item type. Expected " + expectedItemT.q()
            + " at index " + i + " but item type is " + actualItemT.q() + ".");
      }
    }
  }

  private IfFuncB newIfFunc(TypeB t) throws HashedDbExc {
    var ifFuncCB = categoryDb.ifFunc(t);
    var root = newRoot(ifFuncCB);
    return ifFuncCB.newExpr(root, this);
  }

  private MapFuncB newMapFunc(TypeB r, TypeB s) throws HashedDbExc {
    var mapFuncCB = categoryDb.mapFunc(r, s);
    var root = newRoot(mapFuncCB);
    return mapFuncCB.newExpr(root, this);
  }

  private SelectB newSelect(TypeB evalT, ExprB selectable, IntB index) throws HashedDbExc {
    var inferredEvalT = selectEvalT(selectable, index);
    if (!evalT.equals(inferredEvalT)) {
      throw new IllegalArgumentException("Selected item type " + inferredEvalT.q()
          + " cannot be assigned to evalT " + evalT.q() + ".");
    }
    var data = writeSelectData(selectable, index);
    var cat = categoryDb.select(evalT);
    var root = newRoot(cat, data);
    return cat.newExpr(root, this);
  }

  private TypeB selectEvalT(ExprB selectable, IntB index) {
    var evalT = selectable.type();
    if (evalT instanceof TupleTB tuple) {
      int intIndex = index.toJ().intValue();
      var items = tuple.items();
      checkElementIndex(intIndex, items.size());
      return items.get(intIndex);
    } else {
      throw new IllegalArgumentException(
          "Selectable.type() should be instance of TupleTB but is " + evalT.q());
    }
  }

  private RefB newRef(TypeB evalT, BigInteger index) throws HashedDbExc {
    var data = writeRefData(index);
    var type = categoryDb.ref(evalT);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private MerkleRoot newRoot(CategoryB cat, Hash dataHash) throws HashedDbExc {
    Hash rootHash = hashedDb.writeSeq(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  private MerkleRoot newRoot(CategoryB cat) throws HashedDbExc {
    Hash rootHash = hashedDb.writeSeq(cat.hash());
    return new MerkleRoot(rootHash, cat, null);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ExprB func, CombineB args) throws HashedDbExc {
    return hashedDb.writeSeq(func.hash(), args.hash());
  }

  private Hash writeCombineData(ImmutableList<ExprB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  private Hash writeIfData(ExprB condition, ExprB then, ExprB else_) throws HashedDbExc {
    return hashedDb.writeSeq(condition.hash(), then.hash(), else_.hash());
  }

  private Hash writeMapData(ExprB array, ExprB func) throws HashedDbExc {
    return hashedDb.writeSeq(array.hash(), func.hash());
  }

  private Hash writeOrderData(ImmutableList<ExprB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeRefData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(ExprB selectable, IntB index) throws HashedDbExc {
    return hashedDb.writeSeq(selectable.hash(), index.hash());
  }

  // methods for writing data of InstB-s

  private Hash writeArrayData(List<InstB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeBoolData(boolean value) throws HashedDbExc {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeDefFuncData(ExprB body) {
    return body.hash();
  }

  private Hash writeIntData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeNatFuncData(BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    return hashedDb.writeSeq(jar.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeStringData(String string) throws HashedDbExc {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(ImmutableList<InstB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  // helpers

  private Hash writeSeq(List<? extends ExprB> exprs) throws HashedDbExc {
    var hashes = Lists.map(exprs, ExprB::hash);
    return hashedDb.writeSeq(hashes);
  }

  // visible for classes from db.object package tree until creating ExprB is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
