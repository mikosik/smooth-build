package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.wrongSizeOfRootChainException;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.Validator.validateArgs;

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
      throw new IllegalArgumentException("`jar.evaluationType()` should be "
          + kindDb.blob().q() + " but is " + jarEvaluationType.q() + ".");
    }
    var classBinaryNameEvaluationType = classBinaryName.evaluationType();
    if (!classBinaryNameEvaluationType.equals(kindDb.string())) {
      throw new IllegalArgumentException("`classBinaryName.evaluationType()` should be "
          + kindDb.string().q() + " but is " + classBinaryNameEvaluationType.q() + ".");
    }
    var isPureEvaluationType = isPure.evaluationType();
    if (!isPureEvaluationType.equals(kindDb.bool())) {
      throw new IllegalArgumentException("`isPure.evaluationType()` should be "
          + kindDb.bool().q() + " but is " + isPureEvaluationType.q() + ".");
    }
    var argumentsEvaluationType = arguments.evaluationType();
    if (!(argumentsEvaluationType instanceof BTupleType)) {
      throw new IllegalArgumentException("`arguments.evaluationType()` should be `BTupleType` but "
          + "is " + q(argumentsEvaluationType.getClass().getSimpleName()) + ".");
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

  public BCall newCall(BExpr lambda, BCombine args) throws BytecodeException {
    var lambdaType = castEvaluationTypeToLambdaType(lambda);
    validateArgsInCall(lambdaType, args);
    var kind = kindDb.call(lambdaType.result());
    var dataHash = writeChain(lambda.hash(), args.hash());
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  public BCombine newCombine(List<? extends BExpr> items) throws BytecodeException {
    var evaluationType = kindDb.tuple(items.map(BExpr::evaluationType));
    var kind = kindDb.combine(evaluationType);
    var dataHash = writeChain(items);
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  public BIf newIf(BExpr condition, BExpr then_, BExpr else_) throws BytecodeException {
    var conditionType = condition.evaluationType();
    if (!conditionType.equals(kindDb.bool())) {
      throw new IllegalArgumentException(
          "`condition.evaluationType()` should be `Bool` but is " + conditionType.q() + ".");
    }
    var thenType = then_.evaluationType();
    var elseType = else_.evaluationType();
    if (!thenType.equals(elseType)) {
      throw new IllegalArgumentException("`then.evaluationType()` (which is " + thenType.q()
          + ") should be equal to `else.evaluationType()` (which is " + elseType.q() + ").");
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

  private static BType validateMapSubExprsAndGetMapperResultType(BExpr array, BExpr mapper) {
    var type = array.evaluationType();
    if (!(type instanceof BArrayType arrayType)) {
      throw new IllegalArgumentException(
          "`array.evaluationType()` must be array type but is " + type.q() + ".");
    }
    var elementType = arrayType.elem();
    var mapperType = mapper.evaluationType();
    if (!(mapperType instanceof BLambdaType lambdaType)) {
      throw new IllegalArgumentException("`mapper.evaluationType()` must be "
          + expectedMapperType(elementType) + " but is " + mapperType.q() + ".");
    }
    var params = lambdaType.params().elements();
    if (!(params.size() == 1 && params.get(0).equals(elementType))) {
      throw new IllegalArgumentException(
          "`mapper.evaluationType()` must be " + expectedMapperType(elementType) + " but is `"
              + params.toString("(", ", ", ")") + "->?`.");
    }
    return lambdaType.result();
  }

  private static String expectedMapperType(BType elementType) {
    return "`(" + elementType.name() + ")->?`";
  }

  public BOrder newOrder(BArrayType evaluationType, List<? extends BExpr> elems)
      throws BytecodeException {
    validateOrderElements(evaluationType.elem(), elems);
    var kind = kindDb.order(evaluationType);
    var dataHash = writeChain(elems);
    var root = newRoot(kind, dataHash);
    return kind.newExpr(root, this);
  }

  public BPick newPick(BExpr pickable, BExpr index) throws BytecodeException {
    var evaluationType = pickEvaluationType(pickable);
    if (!(index.evaluationType() instanceof BIntType)) {
      throw new IllegalArgumentException("index.evaluationType() should be `Int` but is "
          + index.evaluationType().q() + ".");
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
    if (!body.evaluationType().equals(lambdaType.result())) {
      var message = "body.evaluationType() = %s should be equal to lambdaType.result() = %s."
          .formatted(body.evaluationType().q(), lambdaType.result().q());
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

  private BLambdaType castEvaluationTypeToLambdaType(BExpr lambda) {
    if (lambda.evaluationType() instanceof BLambdaType lambdaType) {
      return lambdaType;
    } else {
      throw new IllegalArgumentException("`lambda` component doesn't evaluate to BLambda.");
    }
  }

  private void validateArgsInCall(BLambdaType lambdaType, BCombine arguments) {
    validateArgs(
        lambdaType,
        arguments.evaluationType().elements(),
        () -> illegalArgs(lambdaType, arguments.evaluationType()));
  }

  private IllegalArgumentException illegalArgs(BLambdaType lambdaType, BTupleType argumentTypes) {
    return new IllegalArgumentException(
        "Argument evaluation types %s should be equal to lambda parameter types %s."
            .formatted(itemTsToString(argumentTypes), itemTsToString(lambdaType.params())));
  }

  private static String itemTsToString(BTupleType argumentTypes) {
    return argumentTypes.elements().toString("(", ",", ")");
  }

  // generic getter

  public BExpr get(Hash rootHash) throws BytecodeException {
    var hashes = decodeRootChain(rootHash);
    int rootChainSize = hashes.size();
    if (rootChainSize != 2 && rootChainSize != 1) {
      throw wrongSizeOfRootChainException(rootHash, rootChainSize);
    }
    var kind = getKindOrChainException(rootHash, hashes.get(0));
    if (kind.containsData()) {
      if (rootChainSize != 2) {
        throw wrongSizeOfRootChainException(rootHash, kind, rootChainSize);
      }
      var dataHash = hashes.get(1);
      return kind.newExpr(new MerkleRoot(rootHash, kind, dataHash), this);
    } else {
      if (rootChainSize != 1) {
        throw wrongSizeOfRootChainException(rootHash, kind, rootChainSize);
      }
      return kind.newExpr(new MerkleRoot(rootHash, kind, null), this);
    }
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
    var evaluationType = pickable.evaluationType();
    if (evaluationType instanceof BArrayType arrayT) {
      return arrayT.elem();
    } else {
      throw new IllegalArgumentException(
          "pickable.evaluationType() should be `Array` but is " + evaluationType.q() + ".");
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
          "selectable.evaluationType() should be `Tuple` but is " + evaluationType.q() + ".");
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
