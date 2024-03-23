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
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlobBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashingSink;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryDb;
import org.smoothbuild.virtualmachine.bytecode.type.exc.CategoryDbException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BReferenceCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

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

  public BArrayBuilder newArrayBuilder(BArrayType type) {
    return new BArrayBuilder(type, this);
  }

  public BBlobBuilder newBlobBuilder() throws BytecodeException {
    return new BBlobBuilder(this, sink());
  }

  public BBool newBool(boolean value) throws BytecodeException {
    var data = writeBoolData(value);
    var boolType = categoryDb.bool();
    var root = newRoot(boolType, data);
    return boolType.newExpr(root, this);
  }

  public BLambda newLambda(BFuncType type, BExpr body) throws BytecodeException {
    validateBodyEvaluationType(type, body);
    var cat = categoryDb.lambda(type);
    var dataHash = body.hash();
    var root = newRoot(cat, dataHash);
    return cat.newExpr(root, this);
  }

  public BNativeFunc newNativeFunc(BFuncType type, BBlob jar, BString classBinaryName, BBool isPure)
      throws BytecodeException {
    var cat = categoryDb.nativeFunc(type);
    var data = writeNativeFuncData(jar, classBinaryName, isPure);
    var root = newRoot(cat, data);
    return cat.newExpr(root, this);
  }

  public BInt newInt(BigInteger value) throws BytecodeException {
    var data = writeIntData(value);
    var intType = categoryDb.int_();
    var root = newRoot(intType, data);
    return intType.newExpr(root, this);
  }

  public BString newString(String value) throws BytecodeException {
    var data = writeStringData(value);
    var stringType = categoryDb.string();
    var root = newRoot(stringType, data);
    return stringType.newExpr(root, this);
  }

  public BTuple newTuple(List<? extends BValue> items) throws BytecodeException {
    var type = categoryDb.tuple(items.map(BValue::type));
    var data = writeTupleData(items);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  // methods for creating OperB subclasses

  public BCall newCall(BExpr func, BCombine args) throws BytecodeException {
    var funcType = castEvaluationTypeToFuncTB(func);
    validateArgsInCall(funcType, args);
    var callCategory = categoryDb.call(funcType.result());
    var data = writeCallData(func, args);
    var root = newRoot(callCategory, data);
    return callCategory.newExpr(root, this);
  }

  public BCombine newCombine(List<? extends BExpr> items) throws BytecodeException {
    var evaluationType = categoryDb.tuple(items.map(BExpr::evaluationType));
    var combineCategory = categoryDb.combine(evaluationType);
    var data = writeCombineData(items);
    var root = newRoot(combineCategory, data);
    return combineCategory.newExpr(root, this);
  }

  public BIf newIfFunc(BType t) throws BytecodeException {
    var ifCategory = categoryDb.ifFunc(t);
    var root = newRoot(ifCategory);
    return ifCategory.newExpr(root, this);
  }

  public BMap newMapFunc(BType r, BType s) throws BytecodeException {
    var mapCategory = categoryDb.mapFunc(r, s);
    var root = newRoot(mapCategory);
    return mapCategory.newExpr(root, this);
  }

  public BOrder newOrder(BArrayType evaluationType, List<? extends BExpr> elems)
      throws BytecodeException {
    validateOrderElements(evaluationType.elem(), elems);
    var orderCategory = categoryDb.order(evaluationType);
    var data = writeOrderData(elems);
    var root = newRoot(orderCategory, data);
    return orderCategory.newExpr(root, this);
  }

  public BPick newPick(BExpr pickable, BExpr index) throws BytecodeException {
    var evaluationType = pickEvaluationType(pickable);
    if (!(index.evaluationType() instanceof BIntType)) {
      throw new IllegalArgumentException("index.evaluationType() should be IntTB but is "
          + index.evaluationType().q() + ".");
    }
    var data = writePickData(pickable, index);
    var category = categoryDb.pick(evaluationType);
    var root = newRoot(category, data);
    return category.newExpr(root, this);
  }

  public BReference newReference(BType evaluationType, BInt index) throws BytecodeException {
    BReferenceCategory type = categoryDb.reference(evaluationType);
    var root = newRoot(type, index.hash());
    return type.newExpr(root, this);
  }

  public BSelect newSelect(BExpr selectable, BInt index) throws BytecodeException {
    var evaluationType = selectEvaluationType(selectable, index);
    var data = writeSelectData(selectable, index);
    var selectCategory = categoryDb.select(evaluationType);
    var root = newRoot(selectCategory, data);
    return selectCategory.newExpr(root, this);
  }

  // validators

  private static void validateBodyEvaluationType(BFuncType funcType, BExpr body) {
    if (!body.evaluationType().equals(funcType.result())) {
      var message = "body.evaluationType() = %s should be equal to funcTB.res() = %s."
          .formatted(body.evaluationType().q(), funcType.result().q());
      throw new IllegalArgumentException(message);
    }
  }

  private void validateOrderElements(BType elementType, List<? extends BExpr> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElementType = elems.get(i).evaluationType();
      if (!elementType.equals(iElementType)) {
        throw new IllegalArgumentException("Illegal elem type. Expected " + elementType.q()
            + " but element at index " + i + " has type " + iElementType.q() + ".");
      }
    }
  }

  private BFuncType castEvaluationTypeToFuncTB(BExpr func) {
    if (func.evaluationType() instanceof BFuncType funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncB.");
    }
  }

  private void validateArgsInCall(BFuncType funcType, BCombine args) {
    validateArgs(
        funcType,
        args.evaluationType().elements(),
        () -> illegalArgs(funcType, args.evaluationType()));
  }

  private IllegalArgumentException illegalArgs(BFuncType funcType, BTupleType argsT) {
    return new IllegalArgumentException(
        "Argument evaluation types %s should be equal to function parameter types %s."
            .formatted(itemTsToString(argsT), itemTsToString(funcType.params())));
  }

  private static String itemTsToString(BTupleType argsT) {
    return argsT.elements().toString("(", ",", ")");
  }

  // generic getter

  public BExpr get(Hash rootHash) throws BytecodeException {
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

  private BCategory getCatOrChainException(Hash rootHash, Hash typeHash)
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

  // methods accessed by builders

  public BArray newArray(BArrayType type, List<? extends BValue> elems) throws BytecodeException {
    var data = writeArrayData(elems);
    var root = newRoot(type, data);
    return type.newExpr(root, this);
  }

  public BBlob newBlob(Hash dataHash) throws BytecodeException {
    var blobType = categoryDb.blob();
    var root = newRoot(blobType, dataHash);
    return blobType.newExpr(root, this);
  }

  // methods for creating types

  private BType pickEvaluationType(BExpr pickable) {
    var evaluationType = pickable.evaluationType();
    if (evaluationType instanceof BArrayType arrayT) {
      return arrayT.elem();
    } else {
      throw new IllegalArgumentException(
          "pickable.evaluationType() should be ArrayTB but is " + evaluationType.q() + ".");
    }
  }

  private BType selectEvaluationType(BExpr selectable, BInt index) throws BytecodeException {
    var evaluationType = selectable.evaluationType();
    if (evaluationType instanceof BTupleType tuple) {
      int intIndex = index.toJavaBigInteger().intValue();
      var elements = tuple.elements();
      checkElementIndex(intIndex, elements.size());
      return elements.get(intIndex);
    } else {
      throw new IllegalArgumentException(
          "selectable.evaluationType() should be TupleTB but is " + evaluationType.q() + ".");
    }
  }

  private MerkleRoot newRoot(BCategory cat, Hash dataHash) throws BytecodeException {
    Hash rootHash = writeChain(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  private MerkleRoot newRoot(BCategory cat) throws BytecodeException {
    Hash rootHash = writeChain(cat.hash());
    return new MerkleRoot(rootHash, cat, null);
  }

  // methods for writing data of Operations

  private Hash writeCallData(BExpr func, BCombine args) throws BytecodeException {
    return writeChain(func.hash(), args.hash());
  }

  private Hash writeCombineData(List<? extends BExpr> items) throws BytecodeException {
    return writeChain(items);
  }

  private Hash writeOrderData(List<? extends BExpr> elems) throws BytecodeException {
    return writeChain(elems);
  }

  private Hash writePickData(BExpr pickable, BExpr index) throws BytecodeException {
    return writeChain(pickable.hash(), index.hash());
  }

  private Hash writeSelectData(BExpr selectable, BInt index) throws BytecodeException {
    return writeChain(selectable.hash(), index.hash());
  }

  // methods for writing data of Values

  private Hash writeArrayData(List<? extends BValue> elems) throws BytecodeException {
    return writeChain(elems);
  }

  private Hash writeBoolData(boolean value) throws BytecodeException {
    return writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws BytecodeException {
    return writeBigInteger(value);
  }

  private Hash writeNativeFuncData(BBlob jar, BString classBinaryName, BBool isPure)
      throws BytecodeException {
    return writeChain(jar.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeStringData(String string) throws BytecodeException {
    return writeString(string);
  }

  private Hash writeTupleData(List<? extends BValue> items) throws BytecodeException {
    return writeChain(items);
  }

  // hashedDb calls with exception chaining

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

  private Hash writeChain(List<? extends BExpr> exprs) throws ExprDbException {
    var hashes = exprs.map(BExpr::hash);
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), ExprDbException::new);
  }

  // visible for classes from db.object package tree until creating ExprB is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
