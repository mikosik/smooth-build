package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.wrongSizeOfRootChainException;

import java.math.BigInteger;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlobBuilder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprKindException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNoSuchExprException;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashingSink;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.kind.BKindDb;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BReferenceKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.BKindDbException;

/**
 * This class is thread-safe.
 */
public class BExprDb {
  private final HashedDb hashedDb;
  private final BKindDb kindDb;

  public BExprDb(HashedDb hashedDb, BKindDb kindDb) {
    this.hashedDb = hashedDb;
    this.kindDb = kindDb;
  }

  // methods for creating InstB subclasses

  public BArrayBuilder newArrayBuilder(BArrayType type) {
    return new BArrayBuilder(type, this);
  }

  public BBlobBuilder newBlobBuilder() throws BytecodeException {
    return new BBlobBuilder(this, sink());
  }

  public BBool newBool(boolean value) throws BytecodeException {
    var type = kindDb.bool();
    var dataHash = writeBoolean(value);
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  public BLambda newLambda(BLambdaType type, BExpr body) throws BytecodeException {
    validateBodyEvaluationType(type, body);
    var dataHash = body.hash();
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  public BInvoke newInvoke(
      BType evaluationType, BExpr jar, BExpr classBinaryName, BExpr isPure, BExpr arguments)
      throws BytecodeException {
    var jarEvaluationType = jar.evaluationType();
    if (!jarEvaluationType.equals(kindDb.blob())) {
      throw illegalEvaluationType("jar", kindDb.blob(), jarEvaluationType);
    }
    var classBinaryNameEvaluationType = classBinaryName.evaluationType();
    if (!classBinaryNameEvaluationType.equals(kindDb.string())) {
      throw illegalEvaluationType(
          "classBinaryName", kindDb.string(), classBinaryNameEvaluationType);
    }
    var isPureEvaluationType = isPure.evaluationType();
    if (!isPureEvaluationType.equals(kindDb.bool())) {
      throw illegalEvaluationType("isPure", kindDb.bool(), isPureEvaluationType);
    }
    var argumentsEvaluationType = arguments.evaluationType();
    if (!(argumentsEvaluationType instanceof BTupleType)) {
      throw illegalEvaluationType("arguments", BTupleType.class, argumentsEvaluationType);
    }

    var kind = kindDb.invoke(evaluationType);
    var dataHash = writeChain(jar.hash(), classBinaryName.hash(), isPure.hash(), arguments.hash());
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  public BInt newInt(BigInteger value) throws BytecodeException {
    var type = kindDb.int_();
    var dataHash = writeBigInteger(value);
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  public BString newString(String value) throws BytecodeException {
    var type = kindDb.string();
    var dataHash = writeString(value);
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  public BTuple newTuple(List<? extends BValue> items) throws BytecodeException {
    var type = kindDb.tuple(items.map(BValue::type));
    var dataHash = writeChain(items);
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  // methods for creating OperB subclasses

  public BCall newCall(BExpr lambda, BExpr args) throws BytecodeException {
    var lambdaType = validateFunctionType(lambda, args);
    var kind = kindDb.call(lambdaType.result());
    var dataHash = writeChain(lambda.hash(), args.hash());
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  private static BLambdaType validateFunctionType(BExpr lambda, BExpr arguments) {
    var lambdaEvaluationType = lambda.evaluationType();
    if (!(lambdaEvaluationType instanceof BLambdaType lambdaType)) {
      throw illegalEvaluationType("lambda", BLambdaType.class, lambdaEvaluationType);
    }
    var argumentsEvaluationType = arguments.evaluationType();
    if (!lambdaType.params().equals(argumentsEvaluationType)) {
      throw illegalEvaluationType("arguments", lambdaType.params(), argumentsEvaluationType);
    }
    return lambdaType;
  }

  public BCombine newCombine(List<? extends BExpr> items) throws BytecodeException {
    var evaluationType = kindDb.tuple(items.map(BExpr::evaluationType));
    var kind = kindDb.combine(evaluationType);
    var dataHash = writeChain(items);
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  public BIf newIf(BExpr condition, BExpr then_, BExpr else_) throws BytecodeException {
    var conditionEvaluationType = condition.evaluationType();
    if (!conditionEvaluationType.equals(kindDb.bool())) {
      throw illegalEvaluationType("condition", kindDb.bool(), conditionEvaluationType);
    }
    var thenType = then_.evaluationType();
    var elseType = else_.evaluationType();
    if (!thenType.equals(elseType)) {
      throw illegalEvaluationType("then", elseType, thenType);
    }
    var kind = kindDb.if_(thenType);
    var data = writeChain(condition.hash(), then_.hash(), else_.hash());
    var root = newRoot(kind, data);
    return kind.newExpr(root, this);
  }

  public BMap newMap(BExpr array, BExpr mapper) throws BytecodeException {
    var mapperArgumentType = validateMapSubExprsAndGetMapperResultType(array, mapper);
    var kind = kindDb.map(kindDb.array(mapperArgumentType));

    var data = writeChain(array.hash(), mapper.hash());
    var root = newRoot(kind, data);
    return kind.newExpr(root, this);
  }

  private BType validateMapSubExprsAndGetMapperResultType(BExpr array, BExpr mapper)
      throws BKindDbException {
    var type = array.evaluationType();
    if (!(type instanceof BArrayType arrayType)) {
      throw illegalEvaluationType("array", BArrayType.class, type);
    }
    var elementType = arrayType.element();
    var mapperType = mapper.evaluationType();
    if (!(mapperType instanceof BLambdaType lambdaType)) {
      throw illegalEvaluationType("mapper", BLambdaType.class, mapperType);
    }
    var params = lambdaType.params().elements();
    if (!(params.size() == 1 && params.get(0).equals(elementType))) {
      throw illegalEvaluationType(
          "mapper.arguments", expectedMapperArgumentsType(elementType), lambdaType.params());
    }
    return lambdaType.result();
  }

  private BTupleType expectedMapperArgumentsType(BType elementType) throws BKindDbException {
    return kindDb.tuple(elementType);
  }

  public BOrder newOrder(BArrayType evaluationType, List<? extends BExpr> elements)
      throws BytecodeException {
    validateOrderElements(evaluationType.element(), elements);
    var kind = kindDb.order(evaluationType);
    var dataHash = writeChain(elements);
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  public BPick newPick(BExpr pickable, BExpr index) throws BytecodeException {
    var evaluationType = pickEvaluationType(pickable);
    var indexEvaluationType = index.evaluationType();
    if (!(indexEvaluationType instanceof BIntType)) {
      throw illegalEvaluationType("index", BIntType.class, indexEvaluationType);
    }
    var kind = kindDb.pick(evaluationType);
    var dataHash = writeChain(pickable.hash(), index.hash());
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  public BReference newReference(BType evaluationType, BInt index) throws BytecodeException {
    BReferenceKind type = kindDb.reference(evaluationType);
    var root = newRoot(type, index.hash());
    return type.newExpr(root, this);
  }

  public BSelect newSelect(BExpr selectable, BInt index) throws BytecodeException {
    var evaluationType = selectEvaluationType(selectable, index);
    var kind = kindDb.select(evaluationType);
    var dataHash = writeChain(selectable.hash(), index.hash());
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  // validators

  private static void validateBodyEvaluationType(BLambdaType lambdaType, BExpr body) {
    var bodyEvaluationType = body.evaluationType();
    var lambdaEvaluationType = lambdaType.result();
    if (!bodyEvaluationType.equals(lambdaEvaluationType)) {
      throw illegalEvaluationType("body", lambdaEvaluationType, bodyEvaluationType);
    }
  }

  private void validateOrderElements(BType elementType, List<? extends BExpr> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElementType = elems.get(i).evaluationType();
      if (!elementType.equals(iElementType)) {
        throw illegalEvaluationType("element" + i, elementType, iElementType);
      }
    }
  }

  private static IllegalArgumentException illegalEvaluationType(
      String name, Class<?> expected, BType actual) {
    return illegalEvaluationType(
        name, expected.getSimpleName(), actual.getClass().getSimpleName());
  }

  private static IllegalArgumentException illegalEvaluationType(
      String name, BType expected, BType actual) {
    return illegalEvaluationType(name, expected.name(), actual.name());
  }

  private static IllegalArgumentException illegalEvaluationType(
      String name, String expected, String actual) {
    return new IllegalArgumentException(
        "`%s.evaluationType()` should be `%s` but is `%s`.".formatted(name, expected, actual));
  }

  // generic getter

  public BExpr get(Hash rootHash) throws BytecodeException {
    var hashes = decodeRootChain(rootHash);
    int rootChainSize = hashes.size();
    if (rootChainSize != 2 && rootChainSize != 1) {
      throw wrongSizeOfRootChainException(rootHash, rootChainSize);
    }
    var kind = getKindOrChainException(rootHash, hashes.get(0));
    if (rootChainSize != 2) {
      throw wrongSizeOfRootChainException(rootHash, kind, rootChainSize);
    }
    var dataHash = hashes.get(1);
    return kind.newExpr(new MerkleRoot(rootHash, kind, dataHash), this);
  }

  private BKind getKindOrChainException(Hash rootHash, Hash typeHash)
      throws DecodeExprKindException {
    try {
      return kindDb.get(typeHash);
    } catch (BKindDbException e) {
      throw new DecodeExprKindException(rootHash, e);
    }
  }

  private List<Hash> decodeRootChain(Hash rootHash) throws BytecodeException {
    return readRootChain(rootHash);
  }

  // methods accessed by builders

  public BArray newArray(BArrayType type, List<? extends BValue> elems) throws BytecodeException {
    var dataHash = writeChain(elems);
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  public BBlob newBlob(Hash dataHash) throws BytecodeException {
    var type = kindDb.blob();
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  // methods for creating types

  private BType pickEvaluationType(BExpr pickable) {
    var pickableEvaluationType = pickable.evaluationType();
    if (pickableEvaluationType instanceof BArrayType arrayType) {
      return arrayType.element();
    } else {
      throw illegalEvaluationType("pickable", BArrayType.class, pickableEvaluationType);
    }
  }

  private BType selectEvaluationType(BExpr selectable, BInt index) throws BytecodeException {
    var selectableEvaluationType = selectable.evaluationType();
    if (selectableEvaluationType instanceof BTupleType tuple) {
      int intIndex = index.toJavaBigInteger().intValue();
      var elements = tuple.elements();
      checkElementIndex(intIndex, elements.size());
      return elements.get(intIndex);
    } else {
      throw illegalEvaluationType("selectable", BTupleType.class, selectableEvaluationType);
    }
  }

  private MerkleRoot newRoot(BKind kind, Hash dataHash) throws BytecodeException {
    Hash rootHash = writeChain(kind.hash(), dataHash);
    return new MerkleRoot(rootHash, kind, dataHash);
  }

  private MerkleRoot newRoot(BKind kind) throws BytecodeException {
    Hash rootHash = writeChain(kind.hash());
    return new MerkleRoot(rootHash, kind, null);
  }

  // hashedDb calls with exception chaining

  private List<Hash> readRootChain(Hash rootHash) throws BExprDbException {
    try {
      return hashedDb.readHashChain(rootHash);
    } catch (NoSuchDataException e) {
      throw new DecodeExprNoSuchExprException(rootHash, e);
    } catch (HashedDbException e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  private HashingSink sink() throws BExprDbException {
    return invokeAndChainHashedDbException(hashedDb::sink, BExprDbException::new);
  }

  private Hash writeBoolean(boolean value) throws BExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeBoolean(value), BExprDbException::new);
  }

  private Hash writeBigInteger(BigInteger value) throws BExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeBigInteger(value), BExprDbException::new);
  }

  private Hash writeString(String string) throws BExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeString(string), BExprDbException::new);
  }

  private Hash writeChain(Hash... hashes) throws BExprDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), BExprDbException::new);
  }

  private Hash writeChain(List<? extends BExpr> exprs) throws BExprDbException {
    var hashes = exprs.map(BExpr::hash);
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), BExprDbException::new);
  }

  public BKindDb kindDb() {
    return kindDb;
  }

  // visible for classes from db.object package tree until creating ExprB is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
