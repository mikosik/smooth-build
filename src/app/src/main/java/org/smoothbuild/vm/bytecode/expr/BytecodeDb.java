package org.smoothbuild.vm.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.util.collect.Iterables.joinWithCommaToString;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.vm.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootExc.cannotReadRootException;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.vm.bytecode.type.Validator.validateArgs;

import java.math.BigInteger;
import java.util.List;

import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprCatExc;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNoSuchExprExc;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.oper.VarB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BlobBBuilder;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.IfFuncB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.LambdaB;
import org.smoothbuild.vm.bytecode.expr.value.MapFuncB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.HashedDb;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.vm.bytecode.hashed.exc.NoSuchDataExc;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.CategoryDb;
import org.smoothbuild.vm.bytecode.type.exc.CategoryDbExc;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;
import org.smoothbuild.vm.bytecode.type.value.LambdaCB;
import org.smoothbuild.vm.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

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

  public LambdaB lambda(FuncTB type, ExprB body) {
    validateBodyEvaluationT(type, body);
    var cat = categoryDb.lambda(type);
    return wrapHashedDbExcAsBytecodeDbExc(() -> newLambda(cat, body));
  }

  public NativeFuncB nativeFunc(FuncTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    var cat = categoryDb.nativeFunc(type);
    return wrapHashedDbExcAsBytecodeDbExc(
        () -> newNativeFunc(cat, jar, classBinaryName, isPure));
  }

  public IntB int_(BigInteger value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newInt(value));
  }

  public StringB string(String value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newString(value));
  }

  public TupleB tuple(ImmutableList<ValueB> items) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newTuple(items));
  }

  // methods for creating OperB subclasses

  public CallB call(ExprB func, CombineB args) {
    var funcTB = castEvaluationTypeToFuncTB(func);
    validateArgsInCall(funcTB, args);
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCall(funcTB, func, args));
  }

  public CombineB combine(ImmutableList<ExprB> items) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCombine(items));
  }

  public IfFuncB ifFunc(TypeB t) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newIfFunc(t));
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newMapFunc(r, s));
  }

  public OrderB order(ArrayTB evaluationT, ImmutableList<ExprB> elems) {
    validateOrderElems(evaluationT.elem(), elems);
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOrder(evaluationT, elems));
  }

  public PickB pick(ExprB pickable, ExprB index) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newPick(pickable, index));
  }

  public VarB varB(TypeB evaluationT, IntB index) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newVar(evaluationT, index));
  }

  public SelectB select(ExprB selectable, IntB index) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newSelect(selectable, index));
  }

  // validators

  private static void validateBodyEvaluationT(FuncTB funcTB, ExprB body) {
    if (!body.evaluationT().equals(funcTB.result())) {
      var message = "body.evaluationT() = %s should be equal to funcTB.res() = %s."
          .formatted(body.evaluationT().q(), funcTB.result().q());
      throw new IllegalArgumentException(message);
    }
  }

  private void validateOrderElems(TypeB elemT, ImmutableList<ExprB> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElemT = elems.get(i).evaluationT();
      if (!elemT.equals(iElemT)) {
        throw new IllegalArgumentException("Illegal elem type. Expected " + elemT.q()
            + " but element at index " + i + " has type " + iElemT.q() + ".");
      }
    }
  }

  private FuncTB castEvaluationTypeToFuncTB(ExprB func) {
    if (func.evaluationT() instanceof FuncTB funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncB.");
    }
  }

  private void validateArgsInCall(FuncTB funcTB, CombineB args) {
    validateArgs(funcTB, args.evaluationT().elements(), () -> {
      throw illegalArgs(funcTB, args.evaluationT());
    });
  }

  private IllegalArgumentException illegalArgs(FuncTB funcTB, TupleTB argsT) {
    return new IllegalArgumentException(
        "Argument evaluation types %s should be equal to function parameter types %s."
            .formatted(itemTsToString(argsT), itemTsToString(funcTB.params())));
  }

  private static String itemTsToString(TupleTB argsT) {
    return "(" + joinWithCommaToString(argsT.elements()) + ")";
  }

  // generic getter

  public ExprB get(Hash rootHash) {
    var hashes = decodeRootSeq(rootHash);
    int rootSeqSize = hashes.size();
    if (rootSeqSize != 2 && rootSeqSize != 1) {
      throw wrongSizeOfRootSeqException(rootHash, rootSeqSize);
    }
    var category = getCatOrChainException(rootHash, hashes.get(0));
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

  public ArrayB newArray(ArrayTB type, List<ValueB> elems) throws HashedDbExc {
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

  private LambdaB newLambda(LambdaCB type, ExprB body) throws HashedDbExc {
    var dataHash = body.hash();
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  private IntB newInt(BigInteger value) throws HashedDbExc {
    var data = writeIntData(value);
    var root = newRoot(categoryDb.int_(), data);
    return categoryDb.int_().newExpr(root, this);
  }

  private NativeFuncB newNativeFunc(
      NativeFuncCB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    var data = writeNativeFuncData(jar, classBinaryName, isPure);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private StringB newString(String string) throws HashedDbExc {
    var data = writeStringData(string);
    var root = newRoot(categoryDb.string(), data);
    return categoryDb.string().newExpr(root, this);
  }

  private TupleB newTuple(ImmutableList<ValueB> items) throws HashedDbExc {
    var type = categoryDb.tuple(map(items, ValueB::type));
    var data = writeTupleData(items);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  // methods for creating Expr-s

  private CallB newCall(FuncTB funcTB, ExprB func, CombineB args) throws HashedDbExc {
    var callCB = categoryDb.call(funcTB.result());
    var data = writeCallData(func, args);
    var root = newRoot(callCB, data);
    return callCB.newExpr(root, this);
  }

  private CombineB newCombine(ImmutableList<ExprB> items) throws HashedDbExc {
    var evaluationT = categoryDb.tuple(map(items, ExprB::evaluationT));
    var combineCB = categoryDb.combine(evaluationT);
    var data = writeCombineData(items);
    var root = newRoot(combineCB, data);
    return combineCB.newExpr(root, this);
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

  private OrderB newOrder(ArrayTB evaluationT, ImmutableList<ExprB> elems) throws HashedDbExc {
    var orderCB = categoryDb.order(evaluationT);
    var data = writeOrderData(elems);
    var root = newRoot(orderCB, data);
    return orderCB.newExpr(root, this);
  }

  private PickB newPick(ExprB pickable, ExprB index) throws HashedDbExc {
    var evaluationT = pickEvaluationT(pickable);
    if (!(index.evaluationT() instanceof IntTB)) {
      throw new IllegalArgumentException(
          "index.evaluationT() should be IntTB but is " + index.evaluationT().q() + ".");
    }
    var data = writePickData(pickable, index);
    var category = categoryDb.pick(evaluationT);
    var root = newRoot(category, data);
    return category.newExpr(root, this);
  }

  private TypeB pickEvaluationT(ExprB pickable) {
    var evaluationT = pickable.evaluationT();
    if (evaluationT instanceof ArrayTB arrayT) {
      return arrayT.elem();
    } else {
      throw new IllegalArgumentException(
          "pickable.evaluationT() should be ArrayTB but is " + evaluationT.q() + ".");
    }
  }

  private VarB newVar(TypeB evaluationT, IntB index) throws HashedDbExc {
    var type = categoryDb.var(evaluationT);
    var root = newRoot(type, index.hash());
    return type.newExpr(root, this);
  }

  private SelectB newSelect(ExprB selectable, IntB index) throws HashedDbExc {
    var evaluationT = selectEvaluationT(selectable, index);
    var data = writeSelectData(selectable, index);
    var category = categoryDb.select(evaluationT);
    var root = newRoot(category, data);
    return category.newExpr(root, this);
  }

  private TypeB selectEvaluationT(ExprB selectable, IntB index) {
    var evaluationT = selectable.evaluationT();
    if (evaluationT instanceof TupleTB tuple) {
      int intIndex = index.toJ().intValue();
      var elements = tuple.elements();
      checkElementIndex(intIndex, elements.size());
      return elements.get(intIndex);
    } else {
      throw new IllegalArgumentException(
          "selectable.evaluationT() should be TupleTB but is " + evaluationT.q() + ".");
    }
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

  private Hash writeOrderData(ImmutableList<ExprB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writePickData(ExprB pickable, ExprB index) throws HashedDbExc {
    return hashedDb.writeSeq(pickable.hash(), index.hash());
  }

  private Hash writeSelectData(ExprB selectable, IntB index) throws HashedDbExc {
    return hashedDb.writeSeq(selectable.hash(), index.hash());
  }

  // methods for writing data of InstB-s

  private Hash writeArrayData(List<ValueB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeBoolData(boolean value) throws HashedDbExc {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeNativeFuncData(BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    return hashedDb.writeSeq(jar.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeStringData(String string) throws HashedDbExc {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(ImmutableList<ValueB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  // helpers

  private Hash writeSeq(List<? extends ExprB> exprs) throws HashedDbExc {
    var hashes = map(exprs, ExprB::hash);
    return hashedDb.writeSeq(hashes);
  }

  // visible for classes from db.object package tree until creating ExprB is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
