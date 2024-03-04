package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.wrongSizeOfRootChainException;
import static org.smoothbuild.virtualmachine.bytecode.type.Validator.validateArgs;

import java.math.BigInteger;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprCatException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNoSuchExprException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ExprDbException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CallB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.ReferenceB;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.SelectB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobBBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BoolB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IfFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.LambdaB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.MapFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.StringB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashingSink;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;
import org.smoothbuild.virtualmachine.bytecode.type.exc.CategoryDbException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.ReferenceCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IntTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.LambdaCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

/**
 * This class is thread-safe.
 */
public class ExprDb {
  private final HashedDb hashedDb;
  private final CategoryDb categoryDb;

  public ExprDb(HashedDb hashedDb, CategoryDb categoryDb) {
    this.hashedDb = hashedDb;
    this.categoryDb = categoryDb;
  }

  // methods for creating InstB subclasses

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return new ArrayBBuilder(type, this);
  }

  public BlobBBuilder blobBuilder() throws BytecodeException {
    return new BlobBBuilder(this, sink());
  }

  public BoolB bool(boolean value) throws BytecodeException {
    return newBool(value);
  }

  public LambdaB lambda(FuncTB type, ExprB body) throws BytecodeException {
    validateBodyEvaluationType(type, body);
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

  public OrderB order(ArrayTB evaluationType, List<ExprB> elems) throws BytecodeException {
    validateOrderElements(evaluationType.elem(), elems);
    return newOrder(evaluationType, elems);
  }

  public PickB pick(ExprB pickable, ExprB index) throws BytecodeException {
    return newPick(pickable, index);
  }

  public ReferenceB referenceB(TypeB evaluationType, IntB index) throws BytecodeException {
    return newReference(evaluationType, index);
  }

  public SelectB select(ExprB selectable, IntB index) throws BytecodeException {
    return newSelect(selectable, index);
  }

  // validators

  private static void validateBodyEvaluationType(FuncTB funcTB, ExprB body) {
    if (!body.evaluationType().equals(funcTB.result())) {
      var message = "body.evaluationType() = %s should be equal to funcTB.res() = %s."
          .formatted(body.evaluationType().q(), funcTB.result().q());
      throw new IllegalArgumentException(message);
    }
  }

  private void validateOrderElements(TypeB elemT, List<ExprB> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElemT = elems.get(i).evaluationType();
      if (!elemT.equals(iElemT)) {
        throw new IllegalArgumentException("Illegal elem type. Expected " + elemT.q()
            + " but element at index " + i + " has type " + iElemT.q() + ".");
      }
    }
  }

  private FuncTB castEvaluationTypeToFuncTB(ExprB func) {
    if (func.evaluationType() instanceof FuncTB funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncB.");
    }
  }

  private void validateArgsInCall(FuncTB funcTB, CombineB args) {
    validateArgs(
        funcTB, args.evaluationType().elements(), () -> illegalArgs(funcTB, args.evaluationType()));
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
    var hashes = decodeRootChain(rootHash);
    int rootChainSize = hashes.size();
    if (rootChainSize != 2 && rootChainSize != 1)
      throw wrongSizeOfRootChainException(rootHash, rootChainSize);
    var category = getCatOrChainException(rootHash, hashes.get(0));
    if (category.containsData()) {
      if (rootChainSize != 2) {
        throw wrongSizeOfRootChainException(rootHash, category, rootChainSize);
      }
      var dataHash = hashes.get(1);
      return category.newExpr(new MerkleRoot(rootHash, category, dataHash), this);
    } else {
      if (rootChainSize != 1) {
        throw wrongSizeOfRootChainException(rootHash, category, rootChainSize);
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

  private List<Hash> decodeRootChain(Hash rootHash) throws BytecodeException {
    return readRootChain(rootHash);
  }

  // methods for creating ValueBs

  public ArrayB newArray(ArrayTB type, List<ValueB> elems) throws BytecodeException {
    var data = writeArrayData(elems);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  public BlobB newBlob(Hash dataHash) throws BytecodeException {
    var blobTB = categoryDb.blob();
    var root = newRoot(blobTB, dataHash);
    return blobTB.newExpr(root, this);
  }

  private BoolB newBool(boolean value) throws BytecodeException {
    var data = writeBoolData(value);
    var boolTB = categoryDb.bool();
    var root = newRoot(boolTB, data);
    return boolTB.newExpr(root, this);
  }

  private LambdaB newLambda(LambdaCB type, ExprB body) throws BytecodeException {
    var dataHash = body.hash();
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  private IntB newInt(BigInteger value) throws BytecodeException {
    var data = writeIntData(value);
    var intTB = categoryDb.int_();
    var root = newRoot(intTB, data);
    return intTB.newExpr(root, this);
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
    var stringTB = categoryDb.string();
    var root = newRoot(stringTB, data);
    return stringTB.newExpr(root, this);
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
    var evaluationType = categoryDb.tuple(items.map(ExprB::evaluationType));
    var combineCB = categoryDb.combine(evaluationType);
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

  private OrderB newOrder(ArrayTB evaluationType, List<ExprB> elems) throws BytecodeException {
    var orderCB = categoryDb.order(evaluationType);
    var data = writeOrderData(elems);
    var root = newRoot(orderCB, data);
    return orderCB.newExpr(root, this);
  }

  private PickB newPick(ExprB pickable, ExprB index) throws BytecodeException {
    var evaluationType = pickEvaluationType(pickable);
    if (!(index.evaluationType() instanceof IntTB)) {
      throw new IllegalArgumentException("index.evaluationType() should be IntTB but is "
          + index.evaluationType().q() + ".");
    }
    var data = writePickData(pickable, index);
    var category = categoryDb.pick(evaluationType);
    var root = newRoot(category, data);
    return category.newExpr(root, this);
  }

  private TypeB pickEvaluationType(ExprB pickable) {
    var evaluationType = pickable.evaluationType();
    if (evaluationType instanceof ArrayTB arrayT) {
      return arrayT.elem();
    } else {
      throw new IllegalArgumentException(
          "pickable.evaluationType() should be ArrayTB but is " + evaluationType.q() + ".");
    }
  }

  private ReferenceB newReference(TypeB evaluationType, IntB index) throws BytecodeException {
    ReferenceCB type = categoryDb.reference(evaluationType);
    var root = newRoot(type, index.hash());
    return type.newExpr(root, this);
  }

  private SelectB newSelect(ExprB selectable, IntB index) throws BytecodeException {
    var evaluationType = selectEvaluationType(selectable, index);
    var data = writeSelectData(selectable, index);
    var category = categoryDb.select(evaluationType);
    var root = newRoot(category, data);
    return category.newExpr(root, this);
  }

  private TypeB selectEvaluationType(ExprB selectable, IntB index) throws BytecodeException {
    var evaluationType = selectable.evaluationType();
    if (evaluationType instanceof TupleTB tuple) {
      int intIndex = index.toJavaBigInteger().intValue();
      var elements = tuple.elements();
      checkElementIndex(intIndex, elements.size());
      return elements.get(intIndex);
    } else {
      throw new IllegalArgumentException(
          "selectable.evaluationType() should be TupleTB but is " + evaluationType.q() + ".");
    }
  }

  private MerkleRoot newRoot(CategoryB cat, Hash dataHash) throws BytecodeException {
    Hash rootHash = writeChain(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  private MerkleRoot newRoot(CategoryB cat) throws BytecodeException {
    Hash rootHash = writeChain(cat.hash());
    return new MerkleRoot(rootHash, cat, null);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ExprB func, CombineB args) throws BytecodeException {
    return writeChain(func.hash(), args.hash());
  }

  private Hash writeCombineData(List<ExprB> items) throws BytecodeException {
    return writeChain(items);
  }

  private Hash writeOrderData(List<ExprB> elems) throws BytecodeException {
    return writeChain(elems);
  }

  private Hash writePickData(ExprB pickable, ExprB index) throws BytecodeException {
    return writeChain(pickable.hash(), index.hash());
  }

  private Hash writeSelectData(ExprB selectable, IntB index) throws BytecodeException {
    return writeChain(selectable.hash(), index.hash());
  }

  // methods for writing data of InstB-s

  private Hash writeArrayData(List<ValueB> elems) throws BytecodeException {
    return writeChain(elems);
  }

  private Hash writeBoolData(boolean value) throws BytecodeException {
    return writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws BytecodeException {
    return writeBigInteger(value);
  }

  private Hash writeNativeFuncData(BlobB jar, StringB classBinaryName, BoolB isPure)
      throws BytecodeException {
    return writeChain(jar.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeStringData(String string) throws BytecodeException {
    return writeString(string);
  }

  private Hash writeTupleData(List<ValueB> items) throws BytecodeException {
    return writeChain(items);
  }

  // hashedDb calls with exception translation

  private List<Hash> readRootChain(Hash rootHash) throws ExprDbException {
    try {
      return hashedDb.readHashChain(rootHash);
    } catch (NoSuchDataException e) {
      throw new DecodeExprNoSuchExprException(rootHash, e);
    } catch (HashedDbException e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  private HashingSink sink() throws ExprDbException {
    return invokeAndChainHashedDbException(hashedDb::sink, ExprDbException::new);
  }

  private Hash writeBoolean(boolean value) throws ExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeBoolean(value), ExprDbException::new);
  }

  private Hash writeBigInteger(BigInteger value) throws ExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeBigInteger(value), ExprDbException::new);
  }

  private Hash writeString(String string) throws ExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeString(string), ExprDbException::new);
  }

  private Hash writeChain(Hash... hashes) throws ExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), ExprDbException::new);
  }

  private Hash writeChain(List<? extends ExprB> exprs) throws ExprDbException {
    var hashes = exprs.map(ExprB::hash);
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), ExprDbException::new);
  }

  // visible for classes from db.object package tree until creating ExprB is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
