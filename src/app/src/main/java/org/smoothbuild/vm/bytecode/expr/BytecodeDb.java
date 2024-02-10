package org.smoothbuild.vm.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.vm.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootException.cannotReadRootException;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootException.wrongSizeOfRootSeqException;
import static org.smoothbuild.vm.bytecode.type.Validator.validateArgs;

import java.math.BigInteger;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.exc.BytecodeDbException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprCatException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNoSuchExprException;
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
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.CategoryDb;
import org.smoothbuild.vm.bytecode.type.exc.CategoryDbException;
import org.smoothbuild.vm.bytecode.type.oper.VarCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;
import org.smoothbuild.vm.bytecode.type.value.LambdaCB;
import org.smoothbuild.vm.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

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

  public BlobBBuilder blobBuilder() throws BytecodeException {
    return wrapHashedDbExcAsBytecodeDbExc(() -> new BlobBBuilder(this, hashedDb.sink()));
  }

  public BoolB bool(boolean value) throws BytecodeException {
    return newBool(value);
  }

  public LambdaB lambda(FuncTB type, ExprB body) throws BytecodeException {
    validateBodyEvaluationT(type, body);
    var cat = categoryDb.lambda(type);
    return newLambda(cat, body);
  }

  public NativeFuncB nativeFunc(FuncTB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    var cat = categoryDb.nativeFunc(type);
    return newNativeFunc(cat, jar, classBinaryName, isPure);
  }

  public IntB int_(BigInteger value) throws BytecodeException {
    return newInt(value);
  }

  public StringB string(String value) throws BytecodeException {
    return newString(value);
  }

  public TupleB tuple(List<ValueB> items) throws BytecodeException {
    return newTuple(items);
  }

  // methods for creating OperB subclasses

  public CallB call(ExprB func, CombineB args) throws BytecodeException {
    var funcTB = castEvaluationTypeToFuncTB(func);
    validateArgsInCall(funcTB, args);
    return newCall(funcTB, func, args);
  }

  public CombineB combine(List<ExprB> items) throws BytecodeException {
    return newCombine(items);
  }

  public IfFuncB ifFunc(TypeB t) throws BytecodeException {
    return newIfFunc(t);
  }

  public MapFuncB mapFunc(TypeB r, TypeB s) throws BytecodeException {
    return newMapFunc(r, s);
  }

  public OrderB order(ArrayTB evaluationT, List<ExprB> elems) throws BytecodeException {
    validateOrderElems(evaluationT.elem(), elems);
    return newOrder(evaluationT, elems);
  }

  public PickB pick(ExprB pickable, ExprB index) throws BytecodeException {
    return newPick(pickable, index);
  }

  public VarB varB(TypeB evaluationT, IntB index) throws BytecodeException {
    return newVar(evaluationT, index);
  }

  public SelectB select(ExprB selectable, IntB index) throws BytecodeException {
    return newSelect(selectable, index);
  }

  // validators

  private static void validateBodyEvaluationT(FuncTB funcTB, ExprB body) {
    if (!body.evaluationT().equals(funcTB.result())) {
      var message = "body.evaluationT() = %s should be equal to funcTB.res() = %s."
          .formatted(body.evaluationT().q(), funcTB.result().q());
      throw new IllegalArgumentException(message);
    }
  }

  private void validateOrderElems(TypeB elemT, List<ExprB> elems) {
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
    validateArgs(
        funcTB, args.evaluationT().elements(), () -> illegalArgs(funcTB, args.evaluationT()));
  }

  private IllegalArgumentException illegalArgs(FuncTB funcTB, TupleTB argsT) {
    return new IllegalArgumentException(
        "Argument evaluation types %s should be equal to function parameter types %s."
            .formatted(itemTsToString(argsT), itemTsToString(funcTB.params())));
  }

  private static String itemTsToString(TupleTB argsT) {
    return argsT.elements().toString("(", ",", ")");
  }

  // generic getter

  public ExprB get(Hash rootHash) throws BytecodeException {
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

  private CategoryB getCatOrChainException(Hash rootHash, Hash typeHash)
      throws DecodeExprCatException {
    try {
      return categoryDb.get(typeHash);
    } catch (CategoryDbException e) {
      throw new DecodeExprCatException(rootHash, e);
    }
  }

  private List<Hash> decodeRootSeq(Hash rootHash) throws BytecodeException {
    return readRootSeq(rootHash);
  }

  // methods for creating ValueBs

  public ArrayB newArray(ArrayTB type, List<ValueB> elems) throws BytecodeException {
    var data = writeArrayData(elems);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  public BlobB newBlob(Hash dataHash) throws BytecodeException {
    var root = newRoot(categoryDb.blob(), dataHash);
    return categoryDb.blob().newExpr(root, this);
  }

  private BoolB newBool(boolean value) throws BytecodeException {
    var data = writeBoolData(value);
    var root = newRoot(categoryDb.bool(), data);
    return categoryDb.bool().newExpr(root, this);
  }

  private LambdaB newLambda(LambdaCB type, ExprB body) throws BytecodeException {
    var dataHash = body.hash();
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  private IntB newInt(BigInteger value) throws BytecodeException {
    var data = writeIntData(value);
    var root = newRoot(categoryDb.int_(), data);
    return categoryDb.int_().newExpr(root, this);
  }

  private NativeFuncB newNativeFunc(
      NativeFuncCB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    var data = writeNativeFuncData(jar, classBinaryName, isPure);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  private StringB newString(String string) throws BytecodeException {
    var data = writeStringData(string);
    var root = newRoot(categoryDb.string(), data);
    return categoryDb.string().newExpr(root, this);
  }

  private TupleB newTuple(List<ValueB> items) throws BytecodeException {
    var type = categoryDb.tuple(items.map(ValueB::type));
    var data = writeTupleData(items);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  // methods for creating OperBs

  private CallB newCall(FuncTB funcTB, ExprB func, CombineB args) throws BytecodeException {
    var callCB = categoryDb.call(funcTB.result());
    var data = writeCallData(func, args);
    var root = newRoot(callCB, data);
    return callCB.newExpr(root, this);
  }

  private CombineB newCombine(List<ExprB> items) throws BytecodeException {
    var evaluationT = categoryDb.tuple(items.map(ExprB::evaluationT));
    var combineCB = categoryDb.combine(evaluationT);
    var data = writeCombineData(items);
    var root = newRoot(combineCB, data);
    return combineCB.newExpr(root, this);
  }

  private IfFuncB newIfFunc(TypeB t) throws BytecodeException {
    var ifFuncCB = categoryDb.ifFunc(t);
    var root = newRoot(ifFuncCB);
    return ifFuncCB.newExpr(root, this);
  }

  private MapFuncB newMapFunc(TypeB r, TypeB s) throws BytecodeException {
    var mapFuncCB = categoryDb.mapFunc(r, s);
    var root = newRoot(mapFuncCB);
    return mapFuncCB.newExpr(root, this);
  }

  private OrderB newOrder(ArrayTB evaluationT, List<ExprB> elems) throws BytecodeException {
    var orderCB = categoryDb.order(evaluationT);
    var data = writeOrderData(elems);
    var root = newRoot(orderCB, data);
    return orderCB.newExpr(root, this);
  }

  private PickB newPick(ExprB pickable, ExprB index) throws BytecodeException {
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

  private VarB newVar(TypeB evaluationT, IntB index) throws BytecodeException {
    VarCB type = categoryDb.var(evaluationT);
    var root = newRoot(type, index.hash());
    return type.newExpr(root, this);
  }

  private SelectB newSelect(ExprB selectable, IntB index) throws BytecodeException {
    var evaluationT = selectEvaluationT(selectable, index);
    var data = writeSelectData(selectable, index);
    var category = categoryDb.select(evaluationT);
    var root = newRoot(category, data);
    return category.newExpr(root, this);
  }

  private TypeB selectEvaluationT(ExprB selectable, IntB index) throws BytecodeException {
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

  private MerkleRoot newRoot(CategoryB cat, Hash dataHash) throws BytecodeException {
    Hash rootHash = writeSeq(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  private MerkleRoot newRoot(CategoryB cat) throws BytecodeException {
    Hash rootHash = writeSeq(cat.hash());
    return new MerkleRoot(rootHash, cat, null);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ExprB func, CombineB args) throws BytecodeException {
    return writeSeq(func.hash(), args.hash());
  }

  private Hash writeCombineData(List<ExprB> items) throws BytecodeException {
    return writeSeq(items);
  }

  private Hash writeOrderData(List<ExprB> elems) throws BytecodeException {
    return writeSeq(elems);
  }

  private Hash writePickData(ExprB pickable, ExprB index) throws BytecodeException {
    return writeSeq(pickable.hash(), index.hash());
  }

  private Hash writeSelectData(ExprB selectable, IntB index) throws BytecodeException {
    return writeSeq(selectable.hash(), index.hash());
  }

  // methods for writing data of InstB-s

  private Hash writeArrayData(List<ValueB> elems) throws BytecodeException {
    return writeSeq(elems);
  }

  private Hash writeBoolData(boolean value) throws BytecodeException {
    return writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws BytecodeException {
    return writeBigInteger(value);
  }

  private Hash writeNativeFuncData(BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    return writeSeq(jar.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeStringData(String string) throws BytecodeException {
    return writeString(string);
  }

  private Hash writeTupleData(List<ValueB> items) throws BytecodeException {
    return writeSeq(items);
  }

  // hashedDb calls with exception translation

  private List<Hash> readRootSeq(Hash rootHash) throws BytecodeDbException {
    try {
      return hashedDb.readSeq(rootHash);
    } catch (NoSuchDataException e) {
      throw new DecodeExprNoSuchExprException(rootHash, e);
    } catch (HashedDbException e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  private Hash writeBoolean(boolean value) throws BytecodeDbException {
    return wrapHashedDbExcAsBytecodeDbExc(() -> hashedDb.writeBoolean(value));
  }

  private Hash writeBigInteger(BigInteger value) throws BytecodeDbException {
    return wrapHashedDbExcAsBytecodeDbExc(() -> hashedDb.writeBigInteger(value));
  }

  private Hash writeString(String string) throws BytecodeDbException {
    return wrapHashedDbExcAsBytecodeDbExc(() -> hashedDb.writeString(string));
  }

  private Hash writeSeq(Hash... hashes) throws BytecodeDbException {
    return wrapHashedDbExcAsBytecodeDbExc(() -> hashedDb.writeSeq(hashes));
  }

  private Hash writeSeq(List<? extends ExprB> exprs) throws BytecodeDbException {
    var hashes = exprs.map(ExprB::hash);
    return wrapHashedDbExcAsBytecodeDbExc(() -> hashedDb.writeSeq(hashes));
  }

  // visible for classes from db.object package tree until creating ExprB is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
