package org.smoothbuild.virtualmachine.bytecode.kind;

import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainKindDbException;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.BLOB;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.BOOL;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CALL;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.COMBINE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.FUNC;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.IF;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.INT;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.LAMBDA;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.MAP;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.NATIVE_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.ORDER;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.PICK;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.REFERENCE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.SELECT;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.STRING;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.fromOrdinal;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BCombineKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFuncKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIfKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BMapKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BNativeFuncKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOperationKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BOrderKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BPickKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BReferenceKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BSelectKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
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
public class BKindDb {
  public static final String DATA_PATH = "data";
  private static final int DATA_IDX = 1;
  private static final int FUNC_PARAMS_IDX = 0;
  public static final String FUNC_PARAMS_PATH = DATA_PATH + "[" + FUNC_PARAMS_IDX + "]";
  private static final int FUNC_RESULT_IDX = 1;
  public static final String FUNC_RES_PATH = DATA_PATH + "[" + FUNC_RESULT_IDX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, BKind> cache;
  private final Function0<BBlobType, BKindDbException> blobSupplier;
  private final Function0<BBoolType, BKindDbException> boolSupplier;
  private final Function0<BIntType, BKindDbException> intSupplier;
  private final Function0<BStringType, BKindDbException> stringSupplier;

  public BKindDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();
    this.blobSupplier = createAndCacheTypeMemoizer(BBlobType::new, BLOB);
    this.boolSupplier = createAndCacheTypeMemoizer(BBoolType::new, BOOL);
    this.intSupplier = createAndCacheTypeMemoizer(BIntType::new, INT);
    this.stringSupplier = createAndCacheTypeMemoizer(BStringType::new, STRING);
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

  public BLambdaKind lambda(List<BType> params, BType result) throws BKindDbException {
    return funcC(LAMBDA, params, result, BLambdaKind::new);
  }

  public BLambdaKind lambda(BFuncType funcType) throws BKindDbException {
    return funcC(LAMBDA, funcType, BLambdaKind::new);
  }

  private <T extends BFuncKind> T funcC(
      KindId id, List<BType> params, BType result, BiFunction<Hash, BFuncType, T> factory)
      throws BKindDbException {
    return funcC(id, funcT(params, result), factory);
  }

  private <T extends BFuncKind> T funcC(
      KindId id, BFuncType funcType, BiFunction<Hash, BFuncType, T> factory)
      throws BKindDbException {
    return newFuncC(id, funcType, factory);
  }

  public BFuncType funcT(List<BType> params, BType result) throws BKindDbException {
    return funcT(tuple(params), result);
  }

  public BFuncType funcT(BTupleType params, BType result) throws BKindDbException {
    return newFuncT(params, result);
  }

  public BIfKind if_(BType type) throws BKindDbException {
    return newOperation(IF, type, BIfKind::new);
  }

  public BIntType int_() throws BKindDbException {
    return intSupplier.apply();
  }

  public BMapKind map(BType evaluationType) throws BKindDbException {
    return newOperation(MAP, evaluationType, BMapKind::new);
  }

  public BNativeFuncKind nativeFunc(List<BType> params, BType result) throws BKindDbException {
    return funcC(NATIVE_FUNC, params, result, BNativeFuncKind::new);
  }

  public BNativeFuncKind nativeFunc(BFuncType funcType) throws BKindDbException {
    return funcC(NATIVE_FUNC, funcType, BNativeFuncKind::new);
  }

  public BTupleType tuple(BType... items) throws BKindDbException {
    return tuple(list(items));
  }

  public BTupleType tuple(List<BType> items) throws BKindDbException {
    return newTuple(items);
  }

  public BStringType string() throws BKindDbException {
    return stringSupplier.apply();
  }

  // methods for getting ExprB types

  public BCallKind call(BType evaluationType) throws BKindDbException {
    return newOperation(CALL, evaluationType, BCallKind::new);
  }

  public BCombineKind combine(BTupleType evaluationType) throws BKindDbException {
    return newOperation(COMBINE, evaluationType, BCombineKind::new);
  }

  public BOrderKind order(BArrayType evaluationType) throws BKindDbException {
    return newOperation(ORDER, evaluationType, BOrderKind::new);
  }

  public BPickKind pick(BType evaluationType) throws BKindDbException {
    return newOperation(PICK, evaluationType, BPickKind::new);
  }

  public BReferenceKind reference(BType evaluationType) throws BKindDbException {
    return newOperation(REFERENCE, evaluationType, BReferenceKind::new);
  }

  public BSelectKind select(BType evaluationType) throws BKindDbException {
    return newOperation(SELECT, evaluationType, BSelectKind::new);
  }

  // methods for reading from db

  public BKind get(Hash hash) throws BKindDbException {
    return maybe(cache.get(hash)).getOrGet(() -> read(hash));
  }

  private BKind read(Hash hash) throws BKindDbException {
    List<Hash> rootChain = readKindRootChain(hash);
    var id = decodeKindId(hash, rootChain.get(0));
    return switch (id) {
      case ARRAY -> readArrayType(hash, rootChain, id);
      case BLOB -> newBaseType(hash, id, rootChain, BBlobType::new);
      case BOOL -> newBaseType(hash, id, rootChain, BBoolType::new);
      case INT -> newBaseType(hash, id, rootChain, BIntType::new);
      case STRING -> newBaseType(hash, id, rootChain, BStringType::new);
      case LAMBDA -> readFuncKind(hash, rootChain, id, BLambdaKind::new);
      case FUNC -> readFuncType(hash, rootChain);
      case IF -> readOperationKind(hash, rootChain, id, BType.class, BIfKind::new);
      case MAP -> readOperationKind(hash, rootChain, id, BArrayType.class, BMapKind::new);
      case NATIVE_FUNC -> readFuncKind(hash, rootChain, id, BNativeFuncKind::new);
      case CALL -> readOperationKind(hash, rootChain, id, BType.class, BCallKind::new);
      case COMBINE -> readOperationKind(hash, rootChain, id, BTupleType.class, BCombineKind::new);
      case ORDER -> readOperationKind(hash, rootChain, id, BArrayType.class, BOrderKind::new);
      case PICK -> readOperationKind(hash, rootChain, id, BType.class, BPickKind::new);
      case REFERENCE -> readOperationKind(hash, rootChain, id, BType.class, BReferenceKind::new);
      case SELECT -> readOperationKind(hash, rootChain, id, BType.class, BSelectKind::new);
      case TUPLE -> readTupleType(hash, rootChain);
    };
  }

  private List<Hash> readKindRootChain(Hash hash) throws DecodeKindException {
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
    var id = fromOrdinal(byteMarker);
    if (id == null) {
      throw new DecodeKindIllegalIdException(hash, byteMarker);
    }
    return id;
  }

  private BArrayType readArrayType(Hash hash, List<Hash> rootChain, KindId id)
      throws DecodeKindException {
    return newArray(hash, readDataAsType(hash, rootChain, id, BType.class));
  }

  private <T extends BType> T newBaseType(
      Hash hash, KindId id, List<Hash> rootChain, Function<Hash, T> factory)
      throws DecodeKindRootException {
    assertKindRootChainSize(hash, id, rootChain, 1);
    return cache(factory.apply(hash));
  }

  private <T extends BOperationKind> T readOperationKind(
      Hash hash,
      List<Hash> rootChain,
      KindId id,
      Class<? extends BType> dataTypeClass,
      BiFunction<Hash, BType, T> factory)
      throws DecodeKindException {
    var evaluationType = readDataAsType(hash, rootChain, id, dataTypeClass);
    return newOperation(factory, hash, evaluationType);
  }

  private BFuncType readFuncType(Hash rootHash, List<Hash> rootChain) throws DecodeKindException {
    assertKindRootChainSize(rootHash, FUNC, rootChain, 2);
    var nodes = readDataChainAsTypes(FUNC, rootHash, rootChain);
    if (nodes.size() != 2) {
      throw new DecodeKindWrongChainSizeException(rootHash, FUNC, DATA_PATH, 2, nodes.size());
    }
    var result = nodes.get(FUNC_RESULT_IDX);
    var params = nodes.get(FUNC_PARAMS_IDX);
    if (params instanceof BTupleType paramsTuple) {
      return cache(new BFuncType(rootHash, paramsTuple, result));
    } else {
      throw new DecodeKindWrongNodeKindException(
          rootHash, FUNC, FUNC_PARAMS_PATH, BTupleType.class, params.getClass());
    }
  }

  private <T extends BFuncKind> BKind readFuncKind(
      Hash rootHash, List<Hash> rootChain, KindId id, BiFunction<Hash, BFuncType, T> instantiator)
      throws BKindDbException {
    return readFuncKind(rootHash, rootChain, id, instantiator, t -> {});
  }

  private <T extends BFuncKind> BKind readFuncKind(
      Hash rootHash,
      List<Hash> rootChain,
      KindId kindId,
      BiFunction<Hash, BFuncType, T> factory,
      Consumer1<BFuncType, BKindDbException> typeVerifier)
      throws BKindDbException {
    assertKindRootChainSize(rootHash, kindId, rootChain, 2);
    var dataHash = rootChain.get(DATA_IDX);
    var typeComponent = invokeAndChainKindDbException(
        () -> read(dataHash), e -> new DecodeKindNodeException(rootHash, kindId, DATA_PATH, e));
    if (typeComponent instanceof BFuncType funcType) {
      typeVerifier.accept(funcType);
      return cache(factory.apply(rootHash, funcType));
    } else {
      throw new DecodeKindWrongNodeKindException(
          rootHash, kindId, DATA_PATH, BFuncType.class, typeComponent.getClass());
    }
  }

  private BTupleType readTupleType(Hash rootHash, List<Hash> rootChain) throws DecodeKindException {
    assertKindRootChainSize(rootHash, TUPLE, rootChain, 2);
    var items = readDataChainAsTypes(TUPLE, rootHash, rootChain);
    return newTuple(rootHash, items);
  }

  // helper methods for reading

  private <T extends BType> T readDataAsType(
      Hash rootHash, List<Hash> rootChain, KindId id, Class<T> typeClass)
      throws DecodeKindException {
    assertKindRootChainSize(rootHash, id, rootChain, 2);
    var hash = rootChain.get(DATA_IDX);
    var kindB = invokeAndChainKindDbException(
        () -> get(hash), e -> new DecodeKindNodeException(rootHash, id, DATA_PATH, e));
    if (typeClass.isAssignableFrom(kindB.getClass())) {
      @SuppressWarnings("unchecked")
      T result = (T) kindB;
      return result;
    } else {
      throw new DecodeKindWrongNodeKindException(
          rootHash, id, DATA_PATH, typeClass, kindB.getClass());
    }
  }

  private List<BType> readDataChainAsTypes(KindId id, Hash rootHash, List<Hash> rootChain)
      throws DecodeKindNodeException {
    var elemHashes = invokeAndChainHashedDbException(
        () -> hashedDb.readHashChain(rootChain.get(DATA_IDX)),
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

  private static void assertKindRootChainSize(
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

  private BFuncType newFuncT(BTupleType params, BType result) throws BKindDbException {
    var rootHash = writeFuncTypeRoot(params, result);
    return cache(new BFuncType(rootHash, params, result));
  }

  private <T extends BFuncKind> T newFuncC(
      KindId id, BFuncType funcType, BiFunction<Hash, BFuncType, T> factory)
      throws BKindDbException {
    var rootHash = writeFuncKindRoot(id, funcType);
    return cache(factory.apply(rootHash, funcType));
  }

  private BTupleType newTuple(List<BType> items) throws BKindDbException {
    var hash = writeTupleRoot(items);
    return newTuple(hash, items);
  }

  private BTupleType newTuple(Hash rootHash, List<BType> items) {
    return cache(new BTupleType(rootHash, items));
  }

  private <T extends BOperationKind> T newOperation(
      KindId id, BType evaluationType, BiFunction<Hash, BType, T> factory) throws BKindDbException {
    var rootHash = writeRoot(id, evaluationType);
    return newOperation(factory, rootHash, evaluationType);
  }

  private <T extends BOperationKind> T newOperation(
      BiFunction<Hash, BType, T> factory, Hash rootHash, BType evaluationType) {
    return cache(factory.apply(rootHash, evaluationType));
  }

  private <T extends BKind> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing kind root

  private Hash writeArrayRoot(BKind elementKind) throws BKindDbException {
    return writeRoot(ARRAY, elementKind);
  }

  private Hash writeFuncTypeRoot(BTupleType params, BType result) throws BKindDbException {
    var dataHash = writeChain(params.hash(), result.hash());
    return writeRoot(FUNC, dataHash);
  }

  private Hash writeFuncKindRoot(KindId id, BFuncType funcType) throws BKindDbException {
    return writeRoot(id, funcType);
  }

  private Hash writeTupleRoot(List<BType> items) throws BKindDbException {
    var dataHash = writeChain(items);
    return writeRoot(TUPLE, dataHash);
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
