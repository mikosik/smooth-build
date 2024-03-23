package org.smoothbuild.virtualmachine.bytecode.type;

import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainCategoryDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.BLOB;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.BOOL;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.CALL;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.COMBINE;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.IF_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.INT;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.LAMBDA;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.MAP_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.NATIVE_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.ORDER;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.PICK;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.REFERENCE;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.SELECT;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.STRING;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.fromOrdinal;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalIfFuncTypeExc;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalMapFuncTypeExc;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.type.exc.CategoryDbException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatIllegalIdException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatNodeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatRootException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatWrongNodeCatException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BCallCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BCombineCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BOperCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BOrderCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BPickCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BReferenceCategory;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BSelectCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBlobType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BBoolType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIfCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BLambdaCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BMapCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BNativeFuncCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BStringType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

/**
 * This class is thread-safe.
 */
public class CategoryDb {
  public static final String DATA_PATH = "data";
  private static final int DATA_IDX = 1;
  private static final int FUNC_PARAMS_IDX = 0;
  public static final String FUNC_PARAMS_PATH = DATA_PATH + "[" + FUNC_PARAMS_IDX + "]";
  private static final int FUNC_RESULT_IDX = 1;
  public static final String FUNC_RES_PATH = DATA_PATH + "[" + FUNC_RESULT_IDX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, BCategory> cache;
  private final Function0<BBlobType, CategoryDbException> blobSupplier;
  private final Function0<BBoolType, CategoryDbException> boolSupplier;
  private final Function0<BIntType, CategoryDbException> intSupplier;
  private final Function0<BStringType, CategoryDbException> stringSupplier;

  public CategoryDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();
    this.blobSupplier = createAndCacheTypeMemoizer(BBlobType::new, BLOB);
    this.boolSupplier = createAndCacheTypeMemoizer(BBoolType::new, BOOL);
    this.intSupplier = createAndCacheTypeMemoizer(BIntType::new, INT);
    this.stringSupplier = createAndCacheTypeMemoizer(BStringType::new, STRING);
  }

  private <A extends BType> Function0<A, CategoryDbException> createAndCacheTypeMemoizer(
      Function<Hash, A> factory, CategoryId id) {
    return memoizer(() -> cache(factory.apply(writeRoot(id))));
  }

  // methods for getting ValueB types

  public BArrayType array(BType elemT) throws CategoryDbException {
    return newArray(elemT);
  }

  public BBlobType blob() throws CategoryDbException {
    return blobSupplier.apply();
  }

  public BBoolType bool() throws CategoryDbException {
    return boolSupplier.apply();
  }

  public BLambdaCategory lambda(List<BType> params, BType result) throws CategoryDbException {
    return funcC(LAMBDA, params, result, BLambdaCategory::new);
  }

  public BLambdaCategory lambda(BFuncType funcType) throws CategoryDbException {
    return funcC(LAMBDA, funcType, BLambdaCategory::new);
  }

  private <T extends BFuncCategory> T funcC(
      CategoryId id, List<BType> params, BType result, BiFunction<Hash, BFuncType, T> factory)
      throws CategoryDbException {
    return funcC(id, funcT(params, result), factory);
  }

  private <T extends BFuncCategory> T funcC(
      CategoryId id, BFuncType funcType, BiFunction<Hash, BFuncType, T> factory)
      throws CategoryDbException {
    return newFuncC(id, funcType, factory);
  }

  public BFuncType funcT(List<BType> params, BType result) throws CategoryDbException {
    return funcT(tuple(params), result);
  }

  public BFuncType funcT(BTupleType params, BType result) throws CategoryDbException {
    return newFuncT(params, result);
  }

  public BIfCategory ifFunc(BType type) throws CategoryDbException {
    var funcT = funcT(list(bool(), type, type), type);
    return funcC(IF_FUNC, funcT, BIfCategory::new);
  }

  public BIntType int_() throws CategoryDbException {
    return intSupplier.apply();
  }

  public BMapCategory mapFunc(BType r, BType s) throws CategoryDbException {
    var funcT = funcT(list(array(s), funcT(list(s), r)), array(r));
    return funcC(MAP_FUNC, funcT, BMapCategory::new);
  }

  public BNativeFuncCategory nativeFunc(List<BType> params, BType result)
      throws CategoryDbException {
    return funcC(NATIVE_FUNC, params, result, BNativeFuncCategory::new);
  }

  public BNativeFuncCategory nativeFunc(BFuncType funcType) throws CategoryDbException {
    return funcC(NATIVE_FUNC, funcType, BNativeFuncCategory::new);
  }

  public BTupleType tuple(BType... items) throws CategoryDbException {
    return tuple(list(items));
  }

  public BTupleType tuple(List<BType> items) throws CategoryDbException {
    return newTuple(items);
  }

  public BStringType string() throws CategoryDbException {
    return stringSupplier.apply();
  }

  // methods for getting ExprB types

  public BCallCategory call(BType evaluationType) throws CategoryDbException {
    return newOper(CALL, evaluationType, BCallCategory::new);
  }

  public BCombineCategory combine(BTupleType evaluationType) throws CategoryDbException {
    return newOper(COMBINE, evaluationType, BCombineCategory::new);
  }

  public BOrderCategory order(BArrayType evaluationType) throws CategoryDbException {
    return newOper(ORDER, evaluationType, BOrderCategory::new);
  }

  public BPickCategory pick(BType evaluationType) throws CategoryDbException {
    return newOper(PICK, evaluationType, BPickCategory::new);
  }

  public BReferenceCategory reference(BType evaluationType) throws CategoryDbException {
    return newOper(REFERENCE, evaluationType, BReferenceCategory::new);
  }

  public BSelectCategory select(BType evaluationType) throws CategoryDbException {
    return newOper(SELECT, evaluationType, BSelectCategory::new);
  }

  // methods for reading from db

  public BCategory get(Hash hash) throws CategoryDbException {
    return maybe(cache.get(hash)).getOrGet(() -> read(hash));
  }

  private BCategory read(Hash hash) throws CategoryDbException {
    List<Hash> rootChain = readCategoryRootChain(hash);
    var id = decodeCategoryId(hash, rootChain.get(0));
    return switch (id) {
      case ARRAY -> readArrayType(hash, rootChain, id);
      case BLOB -> newBaseType(hash, id, rootChain, BBlobType::new);
      case BOOL -> newBaseType(hash, id, rootChain, BBoolType::new);
      case INT -> newBaseType(hash, id, rootChain, BIntType::new);
      case STRING -> newBaseType(hash, id, rootChain, BStringType::new);
      case LAMBDA -> readFuncCategory(hash, rootChain, id, BLambdaCategory::new);
      case FUNC -> readFuncType(hash, rootChain);
      case IF_FUNC -> readIfCategory(hash, rootChain, id);
      case MAP_FUNC -> readMapCategory(hash, rootChain, id);
      case NATIVE_FUNC -> readFuncCategory(hash, rootChain, id, BNativeFuncCategory::new);
      case CALL -> readOperCategory(hash, rootChain, id, BType.class, BCallCategory::new);
      case COMBINE -> readOperCategory(
          hash, rootChain, id, BTupleType.class, BCombineCategory::new);
      case ORDER -> readOperCategory(hash, rootChain, id, BArrayType.class, BOrderCategory::new);
      case PICK -> readOperCategory(hash, rootChain, id, BType.class, BPickCategory::new);
      case REFERENCE -> readOperCategory(hash, rootChain, id, BType.class, BReferenceCategory::new);
      case SELECT -> readOperCategory(hash, rootChain, id, BType.class, BSelectCategory::new);
      case TUPLE -> readTupleType(hash, rootChain);
    };
  }

  private List<Hash> readCategoryRootChain(Hash hash) throws DecodeCatException {
    var hashes = invokeAndChainHashedDbException(
        () -> hashedDb.readHashChain(hash), e -> new DecodeCatException(hash, e));
    int chainSize = hashes.size();
    if (chainSize != 1 && chainSize != 2) {
      throw new DecodeCatRootException(hash, chainSize);
    }
    return hashes;
  }

  private CategoryId decodeCategoryId(Hash hash, Hash markerHash) throws DecodeCatException {
    byte byteMarker = invokeAndChainHashedDbException(
        () -> hashedDb.readByte(markerHash), e -> new DecodeCatException(hash, e));
    var id = fromOrdinal(byteMarker);
    if (id == null) {
      throw new DecodeCatIllegalIdException(hash, byteMarker);
    }
    return id;
  }

  private BArrayType readArrayType(Hash hash, List<Hash> rootChain, CategoryId id)
      throws DecodeCatException {
    return newArray(hash, readDataAsType(hash, rootChain, id, BType.class));
  }

  private <T extends BType> T newBaseType(
      Hash hash, CategoryId id, List<Hash> rootChain, Function<Hash, T> factory)
      throws DecodeCatRootException {
    assertCatRootChainSize(hash, id, rootChain, 1);
    return cache(factory.apply(hash));
  }

  private <T extends BOperCategory> T readOperCategory(
      Hash hash,
      List<Hash> rootChain,
      CategoryId id,
      Class<? extends BType> dataTypeClass,
      BiFunction<Hash, BType, T> factory)
      throws DecodeCatException {
    var evaluationType = readDataAsType(hash, rootChain, id, dataTypeClass);
    return newOper(factory, hash, evaluationType);
  }

  private BFuncType readFuncType(Hash rootHash, List<Hash> rootChain) throws DecodeCatException {
    assertCatRootChainSize(rootHash, FUNC, rootChain, 2);
    var nodes = readDataChainAsTypes(FUNC, rootHash, rootChain);
    if (nodes.size() != 2) {
      throw new DecodeCatWrongChainSizeException(rootHash, FUNC, DATA_PATH, 2, nodes.size());
    }
    var result = nodes.get(FUNC_RESULT_IDX);
    var params = nodes.get(FUNC_PARAMS_IDX);
    if (params instanceof BTupleType paramsTuple) {
      return cache(new BFuncType(rootHash, paramsTuple, result));
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, FUNC, FUNC_PARAMS_PATH, BTupleType.class, params.getClass());
    }
  }

  private BCategory readIfCategory(Hash hash, List<Hash> rootChain, CategoryId id)
      throws CategoryDbException {
    return readFuncCategory(hash, rootChain, id, BIfCategory::new, (BFuncType funcType) -> {
      var params = funcType.params();
      if (params.size() != 3) {
        throw illegalIfFuncTypeExc(hash, funcType);
      }
      var result = funcType.result();
      boolean first = params.get(0).equals(bool());
      boolean second = params.get(1).equals(result);
      boolean third = params.get(2).equals(result);
      if (!(first && second && third)) {
        throw illegalIfFuncTypeExc(hash, funcType);
      }
    });
  }

  private BCategory readMapCategory(Hash hash, List<Hash> rootChain, CategoryId id)
      throws CategoryDbException {
    return readFuncCategory(hash, rootChain, id, BMapCategory::new, (BFuncType funcType) -> {
      var params = funcType.params();
      if (!(funcType.result() instanceof BArrayType outputArrayT)) {
        throw illegalMapFuncTypeExc(hash, funcType);
      }
      if (params.size() != 2) {
        throw illegalMapFuncTypeExc(hash, funcType);
      }
      if (!(params.get(0) instanceof BArrayType inputArrayT)) {
        throw illegalMapFuncTypeExc(hash, funcType);
      }
      if (!(params.get(1) instanceof BFuncType mappingFuncT)) {
        throw illegalMapFuncTypeExc(hash, funcType);
      }
      if (mappingFuncT.params().size() != 1) {
        throw illegalMapFuncTypeExc(hash, funcType);
      }
      if (!inputArrayT.elem().equals(mappingFuncT.params().get(0))) {
        throw illegalMapFuncTypeExc(hash, funcType);
      }
      if (!outputArrayT.elem().equals(mappingFuncT.result())) {
        throw illegalMapFuncTypeExc(hash, funcType);
      }
    });
  }

  private <T extends BFuncCategory> BCategory readFuncCategory(
      Hash rootHash,
      List<Hash> rootChain,
      CategoryId id,
      BiFunction<Hash, BFuncType, T> instantiator)
      throws CategoryDbException {
    return readFuncCategory(rootHash, rootChain, id, instantiator, t -> {});
  }

  private <T extends BFuncCategory> BCategory readFuncCategory(
      Hash rootHash,
      List<Hash> rootChain,
      CategoryId categoryId,
      BiFunction<Hash, BFuncType, T> factory,
      Consumer1<BFuncType, CategoryDbException> typeVerifier)
      throws CategoryDbException {
    assertCatRootChainSize(rootHash, categoryId, rootChain, 2);
    var dataHash = rootChain.get(DATA_IDX);
    var typeComponent = invokeAndChainCategoryDbException(
        () -> read(dataHash), e -> new DecodeCatNodeException(rootHash, categoryId, DATA_PATH, e));
    if (typeComponent instanceof BFuncType funcType) {
      typeVerifier.accept(funcType);
      return cache(factory.apply(rootHash, funcType));
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, categoryId, DATA_PATH, BFuncType.class, typeComponent.getClass());
    }
  }

  private BTupleType readTupleType(Hash rootHash, List<Hash> rootChain) throws DecodeCatException {
    assertCatRootChainSize(rootHash, TUPLE, rootChain, 2);
    var items = readDataChainAsTypes(TUPLE, rootHash, rootChain);
    return newTuple(rootHash, items);
  }

  // helper methods for reading

  private <T extends BType> T readDataAsType(
      Hash rootHash, List<Hash> rootChain, CategoryId id, Class<T> typeClass)
      throws DecodeCatException {
    assertCatRootChainSize(rootHash, id, rootChain, 2);
    var hash = rootChain.get(DATA_IDX);
    var categoryB = invokeAndChainCategoryDbException(
        () -> get(hash), e -> new DecodeCatNodeException(rootHash, id, DATA_PATH, e));
    if (typeClass.isAssignableFrom(categoryB.getClass())) {
      @SuppressWarnings("unchecked")
      T result = (T) categoryB;
      return result;
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, id, DATA_PATH, typeClass, categoryB.getClass());
    }
  }

  private List<BType> readDataChainAsTypes(CategoryId id, Hash rootHash, List<Hash> rootChain)
      throws DecodeCatNodeException {
    var elemHashes = invokeAndChainHashedDbException(
        () -> hashedDb.readHashChain(rootChain.get(DATA_IDX)),
        e -> new DecodeCatNodeException(rootHash, id, DATA_PATH, e));
    var builder = new ArrayList<BType>();
    for (int i = 0; i < elemHashes.size(); i++) {
      builder.add(readDataChainElementAsType(id, rootHash, elemHashes.get(i), i));
    }
    return listOfAll(builder);
  }

  private BType readDataChainElementAsType(CategoryId id, Hash rootHash, Hash hash, int index)
      throws DecodeCatNodeException {
    var category = invokeAndChainCategoryDbException(
        () -> get(hash), e -> new DecodeCatNodeException(rootHash, id, DATA_PATH, index, e));
    if (category instanceof BType type) {
      return type;
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, id, DATA_PATH, index, BType.class, category.getClass());
    }
  }

  private static void assertCatRootChainSize(
      Hash rootHash, CategoryId id, List<Hash> hashes, int expectedSize)
      throws DecodeCatRootException {
    if (hashes.size() != expectedSize) {
      throw new DecodeCatRootException(rootHash, id, hashes.size(), expectedSize);
    }
  }

  // methods for creating java instances of CategoryB

  private BArrayType newArray(BType element) throws CategoryDbException {
    var rootHash = writeArrayRoot(element);
    return newArray(rootHash, element);
  }

  private BArrayType newArray(Hash rootHash, BType element) {
    return cache(new BArrayType(rootHash, element));
  }

  private BFuncType newFuncT(BTupleType params, BType result) throws CategoryDbException {
    var rootHash = writeFuncTypeRoot(params, result);
    return cache(new BFuncType(rootHash, params, result));
  }

  private <T extends BFuncCategory> T newFuncC(
      CategoryId id, BFuncType funcType, BiFunction<Hash, BFuncType, T> factory)
      throws CategoryDbException {
    var rootHash = writeFuncCategoryRoot(id, funcType);
    return cache(factory.apply(rootHash, funcType));
  }

  private BTupleType newTuple(List<BType> items) throws CategoryDbException {
    var hash = writeTupleRoot(items);
    return newTuple(hash, items);
  }

  private BTupleType newTuple(Hash rootHash, List<BType> items) {
    return cache(new BTupleType(rootHash, items));
  }

  private <T extends BOperCategory> T newOper(
      CategoryId id, BType evaluationType, BiFunction<Hash, BType, T> factory)
      throws CategoryDbException {
    var rootHash = writeRoot(id, evaluationType);
    return newOper(factory, rootHash, evaluationType);
  }

  private <T extends BOperCategory> T newOper(
      BiFunction<Hash, BType, T> factory, Hash rootHash, BType evaluationType) {
    return cache(factory.apply(rootHash, evaluationType));
  }

  private <T extends BCategory> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing category root

  private Hash writeArrayRoot(BCategory elementCategory) throws CategoryDbException {
    return writeRoot(ARRAY, elementCategory);
  }

  private Hash writeFuncTypeRoot(BTupleType params, BType result) throws CategoryDbException {
    var dataHash = writeChain(params.hash(), result.hash());
    return writeRoot(FUNC, dataHash);
  }

  private Hash writeFuncCategoryRoot(CategoryId id, BFuncType funcType) throws CategoryDbException {
    return writeRoot(id, funcType);
  }

  private Hash writeTupleRoot(List<BType> items) throws CategoryDbException {
    var dataHash = writeChain(items);
    return writeRoot(TUPLE, dataHash);
  }

  // Helper methods for writing roots

  private Hash writeRoot(CategoryId id, BCategory category) throws CategoryDbException {
    return writeRoot(id, category.hash());
  }

  private Hash writeRoot(CategoryId id, Hash dataHash) throws CategoryDbException {
    return writeChain(writeByte(id.byteMarker()), dataHash);
  }

  private Hash writeRoot(CategoryId id) throws CategoryDbException {
    return writeChain(writeByte(id.byteMarker()));
  }

  // hashedDb calls with exception translation

  private Hash writeByte(byte value) throws CategoryDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeByte(value), CategoryDbException::new);
  }

  private Hash writeChain(Hash... hashes) throws CategoryDbException {
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), CategoryDbException::new);
  }

  private Hash writeChain(List<? extends BType> types) throws CategoryDbException {
    var hashes = types.map(BType::hash);
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), CategoryDbException::new);
  }
}
