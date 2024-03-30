package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainSizeIsWrongException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainSizeIsWrongException.wrongSizeOfRootChainException;

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
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NoSuchExprException;
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
    validateMemberEvaluationType("body", body, type.result());
    var dataHash = body.hash();
    var root = newRoot(type, dataHash);
    return type.newExpr(root, this);
  }

  public BInvoke newInvoke(BType evaluationType, BExpr method, BExpr isPure, BExpr arguments)
      throws BytecodeException {
    validateMemberEvaluationType("method", method, kindDb.method());
    validateMemberEvaluationType("isPure", isPure, kindDb.bool());
    validateMemberEvaluationTypeClass("arguments", arguments, BTupleType.class);
    var kind = kindDb.invoke(evaluationType);
    var dataHash = writeChain(method.hash(), isPure.hash(), arguments.hash());
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
    var lambdaType = validateMemberEvaluationTypeClass("lambda", lambda, BLambdaType.class);
    validateMemberEvaluationType("arguments", args, lambdaType.params());
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
    validateMemberEvaluationType("condition", condition, kindDb.bool());
    validateMemberEvaluationType("then", then_, else_.evaluationType());
    var kind = kindDb.if_(then_.evaluationType());
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
    var lambdaType = validateMemberEvaluationTypeClass("mapper", mapper, BLambdaType.class);
    var arrayType = validateMemberEvaluationTypeClass("array", array, BArrayType.class);
    var params = lambdaType.params().elements();
    var elementType = arrayType.element();
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
    validateMemberEvaluationTypeClass("index", index, BIntType.class);
    var kind = kindDb.pick(pickEvaluationType(pickable));
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

  private void validateOrderElements(BType elementType, List<? extends BExpr> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElementType = elems.get(i).evaluationType();
      if (!elementType.equals(iElementType)) {
        throw illegalEvaluationType("element" + i, elementType, iElementType);
      }
    }
  }

  private static <T extends BType> T validateMemberEvaluationTypeClass(
      String memberName, BExpr member, Class<T> clazz) {
    var lambdaEvaluationType = member.evaluationType();
    if (!(lambdaEvaluationType.getClass().equals(clazz))) {
      throw illegalEvaluationType(memberName, clazz, lambdaEvaluationType);
    }
    @SuppressWarnings("unchecked")
    var cast = (T) lambdaEvaluationType;
    return cast;
  }

  private static void validateMemberEvaluationType(
      String memberName, BExpr method, BType expectedType) {
    var methodEvaluationType = method.evaluationType();
    if (!methodEvaluationType.equals(expectedType)) {
      throw illegalEvaluationType(memberName, expectedType, methodEvaluationType);
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
    var arrayType = validateMemberEvaluationTypeClass("pickable", pickable, BArrayType.class);
    return arrayType.element();
  }

  private BType selectEvaluationType(BExpr selectable, BInt index) throws BytecodeException {
    var tuple = validateMemberEvaluationTypeClass("selectable", selectable, BTupleType.class);
    int intIndex = index.toJavaBigInteger().intValue();
    var elements = tuple.elements();
    checkElementIndex(intIndex, elements.size());
    return elements.get(intIndex);
  }

  private MerkleRoot newRoot(BKind kind, Hash dataHash) throws BytecodeException {
    Hash rootHash = writeChain(kind.hash(), dataHash);
    return new MerkleRoot(rootHash, kind, dataHash);
  }

  // hashedDb calls with exception chaining

  private List<Hash> readRootChain(Hash rootHash) throws BExprDbException {
    try {
      return hashedDb.readHashChain(rootHash);
    } catch (NoSuchDataException e) {
      throw new NoSuchExprException(rootHash, e);
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
