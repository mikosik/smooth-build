package org.smoothbuild.virtualmachine.bytecode.kind;

import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainKindDbException;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.ARRAY_GET;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.BLOB;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.BOOL;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CALL;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CONSTRUCT_ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CONSTRUCT_TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CONSTRUCT_VARIANT;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.FOLD;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.IF;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.INT;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.INVOKE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.LAMBDA;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.MAP;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.REF;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.STRING;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.SWITCH;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.TUPLE_GET;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.VARIANT;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.fromOrdinal;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayGetKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BConstructArrayKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BConstructTupleKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BConstructVariantKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFoldKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIfKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BInvokeKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOperationKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BRefKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSwitchKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleGetKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BVariantType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.KindId;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.BKindDbException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindIllegalIdException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindNodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindRootException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindWrongNodeKindException;

/**
 * This class is thread-safe.
 */
@PerCommand
public class BKindDb {
  public static final String DATA_PATH = "data";
  private static final int DATA_IDX = 1;
  private static final int LAMBDA_PARAMS_IDX = 0;
  public static final String LAMBDA_PARAMS_PATH = DATA_PATH + "[" + LAMBDA_PARAMS_IDX + "]";
  private static final int LAMBDA_RESULT_IDX = 1;
  public static final String LAMBDA_RES_PATH = DATA_PATH + "[" + LAMBDA_RESULT_IDX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, BKind> cache;
  private final Function0<BBlobType, BKindDbException> blobSupplier;
  private final Function0<BBoolType, BKindDbException> boolSupplier;
  private final Function0<BIntType, BKindDbException> intSupplier;
  private final Function0<BStringType, BKindDbException> stringSupplier;
  private final Function0<BTupleType, BKindDbException> methodSupplier;

  @Inject
  public BKindDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();
    this.blobSupplier = createAndCacheTypeMemoizer(BBlobType::new, BLOB);
    this.boolSupplier = createAndCacheTypeMemoizer(BBoolType::new, BOOL);
    this.intSupplier = createAndCacheTypeMemoizer(BIntType::new, INT);
    this.stringSupplier = createAndCacheTypeMemoizer(BStringType::new, STRING);
    this.methodSupplier = memoizer(() -> cache(tuple(blob(), string(), string())));
  }

  private <A extends BType> Function0<A, BKindDbException> createAndCacheTypeMemoizer(
      Function<Hash, A> factory, KindId id) {
    return memoizer(() -> cache(factory.apply(writeRoot(id))));
  }

  // methods for getting ValueB types

  public BArrayType array(BType elemT) throws BKindDbException {
    return newArray(elemT);
  }

  public BBlobType blob() throws BKindDbException {
    return blobSupplier.apply();
  }

  public BBoolType bool() throws BKindDbException {
    return boolSupplier.apply();
  }

  public BVariantType variant(BType... alternatives) throws BKindDbException {
    return variant(list(alternatives));
  }

  public BVariantType variant(List<BType> alternatives) throws BKindDbException {
    return newVariant(alternatives);
  }

  public BFoldKind fold(BType evaluationType) throws BKindDbException {
    return newOperation(FOLD, evaluationType, BFoldKind::new);
  }

  public BLambdaType lambda(List<BType> params, BType result) throws BKindDbException {
    return lambda(tuple(params), result);
  }

  public BLambdaType lambda(BTupleType params, BType result) throws BKindDbException {
    return newLambda(params, result);
  }

  public BIfKind if_(BType type) throws BKindDbException {
    return newOperation(IF, type, BIfKind::new);
  }

  public BIntType int_() throws BKindDbException {
    return intSupplier.apply();
  }

  public BInvokeKind invoke(BType evaluationType) throws BKindDbException {
    return newOperation(INVOKE, evaluationType, BInvokeKind::new);
  }

  public BMapKind map(BArrayType evaluationType) throws BKindDbException {
    return newOperation(MAP, evaluationType, BMapKind::new);
  }

  public BTupleType method() throws BKindDbException {
    return methodSupplier.apply();
  }

  public BTupleType tuple(BType... items) throws BKindDbException {
    return tuple(list(items));
  }

  public BTupleType tuple(List<? extends BType> items) throws BKindDbException {
    return newTuple(items);
  }

  public BStringType string() throws BKindDbException {
    return stringSupplier.apply();
  }

  // methods for getting ExprB types

  public BCallKind call(BType evaluationType) throws BKindDbException {
    return newOperation(CALL, evaluationType, BCallKind::new);
  }

  public BConstructVariantKind constructVariant(BVariantType evaluationType)
      throws BKindDbException {
    return newOperation(CONSTRUCT_VARIANT, evaluationType, BConstructVariantKind::new);
  }

  public BSwitchKind switch_(BType evaluationType) throws BKindDbException {
    return newOperation(SWITCH, evaluationType, BSwitchKind::new);
  }

  public BConstructTupleKind constructTuple(BTupleType evaluationType) throws BKindDbException {
    return newOperation(CONSTRUCT_TUPLE, evaluationType, BConstructTupleKind::new);
  }

  public BConstructArrayKind constructArray(BArrayType evaluationType) throws BKindDbException {
    return newOperation(CONSTRUCT_ARRAY, evaluationType, BConstructArrayKind::new);
  }

  public BArrayGetKind arrayGet(BType evaluationType) throws BKindDbException {
    return newOperation(ARRAY_GET, evaluationType, BArrayGetKind::new);
  }

  public BRefKind ref(BType evaluationType) throws BKindDbException {
    return newOperation(REF, evaluationType, BRefKind::new);
  }

  public BTupleGetKind tupleGet(BType evaluationType) throws BKindDbException {
    return newOperation(TUPLE_GET, evaluationType, BTupleGetKind::new);
  }

  // methods for reading from db

  public BKind get(Hash hash) throws BKindDbException {
    return maybe(cache.get(hash)).getOrGet(() -> read(hash));
  }

  private BKind read(Hash hash) throws BKindDbException {
    List<Hash> children = readKindRootChildren(hash);
    var id = decodeKindId(hash, children.get(0));
    return switch (id) {
      case ARRAY -> readArrayType(hash, children, id);
      case BLOB -> newBaseType(hash, id, children, BBlobType::new);
      case BOOL -> newBaseType(hash, id, children, BBoolType::new);
      case INT -> newBaseType(hash, id, children, BIntType::new);
      case STRING -> newBaseType(hash, id, children, BStringType::new);
      case LAMBDA -> readLambdaType(hash, children);
      case VARIANT -> readVariantType(hash, children);
      case IF -> readOperationKind(hash, children, id, BType.class, BIfKind::new);
      case MAP -> readOperationKind(hash, children, id, BArrayType.class, BMapKind::new);
      case INVOKE -> readOperationKind(hash, children, id, BType.class, BInvokeKind::new);
      case CALL -> readOperationKind(hash, children, id, BType.class, BCallKind::new);
      case CONSTRUCT_VARIANT ->
        readOperationKind(hash, children, id, BVariantType.class, BConstructVariantKind::new);
      case CONSTRUCT_TUPLE ->
        readOperationKind(hash, children, id, BTupleType.class, BConstructTupleKind::new);
      case FOLD -> readOperationKind(hash, children, id, BType.class, BFoldKind::new);
      case CONSTRUCT_ARRAY ->
        readOperationKind(hash, children, id, BArrayType.class, BConstructArrayKind::new);
      case ARRAY_GET -> readOperationKind(hash, children, id, BType.class, BArrayGetKind::new);
      case REF -> readOperationKind(hash, children, id, BType.class, BRefKind::new);
      case TUPLE_GET -> readOperationKind(hash, children, id, BType.class, BTupleGetKind::new);
      case SWITCH -> readOperationKind(hash, children, id, BType.class, BSwitchKind::new);
      case TUPLE -> readTupleType(hash, children);
    };
  }

  private List<Hash> readKindRootChildren(Hash hash) throws DecodeKindException {
    var hashes = invokeAndChainHashedDbException(
        () -> hashedDb.readHashChain(hash), e -> new DecodeKindException(hash, e));
    int chainSize = hashes.size();
    if (chainSize != 1 && chainSize != 2) {
      throw new DecodeKindRootException(hash, chainSize);
    }
    return hashes;
  }

  private KindId decodeKindId(Hash hash, Hash markerHash) throws DecodeKindException {
    byte byteMarker = invokeAndChainHashedDbException(
        () -> hashedDb.readByte(markerHash), e -> new DecodeKindException(hash, e));
    return fromOrdinal(byteMarker)
        .getOrThrow(() -> new DecodeKindIllegalIdException(hash, byteMarker));
  }

  private BArrayType readArrayType(Hash hash, List<Hash> rootChildren, KindId id)
      throws DecodeKindException {
    return newArray(hash, readDataAsType(hash, rootChildren, id, BType.class));
  }

  private <T extends BType> T newBaseType(
      Hash hash, KindId id, List<Hash> rootChildren, Function<Hash, T> factory)
      throws DecodeKindRootException {
    assertKindRootChildrenSize(hash, id, rootChildren, 1);
    return cache(factory.apply(hash));
  }

  private <K extends BOperationKind, T extends BType> K readOperationKind(
      Hash hash,
      List<Hash> rootChildren,
      KindId id,
      Class<T> expectedEvaluationTypeClass,
      BiFunction<Hash, T, K> factory)
      throws DecodeKindException {
    var evaluationType = readDataAsType(hash, rootChildren, id, expectedEvaluationTypeClass);
    return newOperation(factory, hash, evaluationType);
  }

  private BLambdaType readLambdaType(Hash rootHash, List<Hash> rootChildren)
      throws DecodeKindException {
    assertKindRootChildrenSize(rootHash, LAMBDA, rootChildren, 2);
    var nodes = readDataChainAsTypes(LAMBDA, rootHash, rootChildren);
    if (nodes.size() != 2) {
      throw new DecodeKindWrongChainSizeException(rootHash, LAMBDA, DATA_PATH, 2, nodes.size());
    }
    var result = nodes.get(LAMBDA_RESULT_IDX);
    var params = nodes.get(LAMBDA_PARAMS_IDX);
    if (params instanceof BTupleType paramsTuple) {
      return cache(new BLambdaType(rootHash, paramsTuple, result));
    } else {
      throw new DecodeKindWrongNodeKindException(
          rootHash, LAMBDA, LAMBDA_PARAMS_PATH, BTupleType.class, params.getClass());
    }
  }

  private BVariantType readVariantType(Hash rootHash, List<Hash> rootChildren)
      throws DecodeKindException {
    assertKindRootChildrenSize(rootHash, VARIANT, rootChildren, 2);
    var items = readDataChainAsTypes(VARIANT, rootHash, rootChildren);
    return newVariant(rootHash, items);
  }

  private BTupleType readTupleType(Hash rootHash, List<Hash> rootChildren)
      throws DecodeKindException {
    assertKindRootChildrenSize(rootHash, TUPLE, rootChildren, 2);
    var items = readDataChainAsTypes(TUPLE, rootHash, rootChildren);
    return newTuple(rootHash, items);
  }

  // helper methods for reading

  private <T extends BType> T readDataAsType(
      Hash rootHash, List<Hash> rootChildren, KindId id, Class<T> expectedTypeClass)
      throws DecodeKindException {
    assertKindRootChildrenSize(rootHash, id, rootChildren, 2);
    var dataHash = rootChildren.get(DATA_IDX);
    BKind kind = invokeAndChainKindDbException(
        () -> get(dataHash), e -> new DecodeKindNodeException(rootHash, id, DATA_PATH, e));
    if (expectedTypeClass.isAssignableFrom(kind.getClass())) {
      @SuppressWarnings("unchecked")
      T result = (T) kind;
      return result;
    } else {
      throw new DecodeKindWrongNodeKindException(
          rootHash, id, DATA_PATH, expectedTypeClass, kind.getClass());
    }
  }

  private List<BType> readDataChainAsTypes(KindId id, Hash rootHash, List<Hash> rootChildren)
      throws DecodeKindNodeException {
    var elemHashes = invokeAndChainHashedDbException(
        () -> hashedDb.readHashChain(rootChildren.get(DATA_IDX)),
        e -> new DecodeKindNodeException(rootHash, id, DATA_PATH, e));
    var builder = new ArrayList<BType>();
    for (int i = 0; i < elemHashes.size(); i++) {
      builder.add(readDataChainElementAsType(id, rootHash, elemHashes.get(i), i));
    }
    return listOfAll(builder);
  }

  private BType readDataChainElementAsType(KindId id, Hash rootHash, Hash hash, int index)
      throws DecodeKindNodeException {
    var kind = invokeAndChainKindDbException(
        () -> get(hash), e -> new DecodeKindNodeException(rootHash, id, DATA_PATH, index, e));
    if (kind instanceof BType type) {
      return type;
    } else {
      throw new DecodeKindWrongNodeKindException(
          rootHash, id, DATA_PATH, index, BType.class, kind.getClass());
    }
  }

  private static void assertKindRootChildrenSize(
      Hash rootHash, KindId id, List<Hash> hashes, int expectedSize)
      throws DecodeKindRootException {
    if (hashes.size() != expectedSize) {
      throw new DecodeKindRootException(rootHash, id, hashes.size(), expectedSize);
    }
  }

  // methods for creating java instances of KindB

  private BArrayType newArray(BType element) throws BKindDbException {
    var rootHash = writeArrayRoot(element);
    return newArray(rootHash, element);
  }

  private BArrayType newArray(Hash rootHash, BType element) {
    return cache(new BArrayType(rootHash, element));
  }

  private BLambdaType newLambda(BTupleType params, BType result) throws BKindDbException {
    var rootHash = writeLambdaTypeRoot(params, result);
    return cache(new BLambdaType(rootHash, params, result));
  }

  private BVariantType newVariant(List<BType> alternatives) throws BKindDbException {
    var hash = writeRootWithElements(VARIANT, alternatives);
    return newVariant(hash, alternatives);
  }

  private BVariantType newVariant(Hash rootHash, List<BType> alternatives) {
    return cache(new BVariantType(rootHash, alternatives));
  }

  private BTupleType newTuple(List<? extends BType> items) throws BKindDbException {
    var hash = writeRootWithElements(TUPLE, items);
    return newTuple(hash, items);
  }

  private BTupleType newTuple(Hash rootHash, List<? extends BType> items) {
    return cache(new BTupleType(rootHash, items));
  }

  private <K extends BOperationKind, T extends BType> K newOperation(
      KindId id, T evaluationType, BiFunction<Hash, T, K> factory) throws BKindDbException {
    var rootHash = writeRoot(id, evaluationType);
    return newOperation(factory, rootHash, evaluationType);
  }

  private <K extends BOperationKind, T extends BType> K newOperation(
      BiFunction<Hash, T, K> factory, Hash rootHash, T evaluationType) {
    return cache(factory.apply(rootHash, evaluationType));
  }

  private <T extends BKind> T cache(T kind) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(kind.hash(), kind), kind);
    return result;
  }

  // Methods for writing kind root

  private Hash writeArrayRoot(BKind elementKind) throws BKindDbException {
    return writeRoot(ARRAY, elementKind);
  }

  private Hash writeLambdaTypeRoot(BTupleType params, BType result) throws BKindDbException {
    var dataHash = writeChain(params.hash(), result.hash());
    return writeRoot(LAMBDA, dataHash);
  }

  private Hash writeRootWithElements(KindId kindId, List<? extends BType> elements)
      throws BKindDbException {
    var dataHash = writeChain(elements);
    return writeRoot(kindId, dataHash);
  }

  // Helper methods for writing roots

  private Hash writeRoot(KindId id, BKind kind) throws BKindDbException {
    return writeRoot(id, kind.hash());
  }

  private Hash writeRoot(KindId id, Hash dataHash) throws BKindDbException {
    return writeChain(writeByte(id.byteMarker()), dataHash);
  }

  private Hash writeRoot(KindId id) throws BKindDbException {
    return writeChain(writeByte(id.byteMarker()));
  }

  // hashedDb calls with exception translation

  private Hash writeByte(byte value) throws BKindDbException {
    return invokeAndChainHashedDbException(() -> hashedDb.writeByte(value), BKindDbException::new);
  }

  private Hash writeChain(Hash... hashes) throws BKindDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), BKindDbException::new);
  }

  private Hash writeChain(List<? extends BType> types) throws BKindDbException {
    var hashes = types.map(BType::hash);
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), BKindDbException::new);
  }
}
