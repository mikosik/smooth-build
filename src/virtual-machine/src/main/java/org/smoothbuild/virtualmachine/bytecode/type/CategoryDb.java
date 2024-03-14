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
import org.smoothbuild.virtualmachine.bytecode.type.oper.CallCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OperCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OrderCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.PickCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.ReferenceCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BlobTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.BoolTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IfFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.IntTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.LambdaCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.MapFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.StringTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

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
  private final ConcurrentHashMap<Hash, CategoryB> cache;
  private final Function0<BlobTB, CategoryDbException> blobSupplier;
  private final Function0<BoolTB, CategoryDbException> boolSupplier;
  private final Function0<IntTB, CategoryDbException> intSupplier;
  private final Function0<StringTB, CategoryDbException> stringSupplier;

  public CategoryDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();
    this.blobSupplier = createAndCacheTypeMemoizer(BlobTB::new, BLOB);
    this.boolSupplier = createAndCacheTypeMemoizer(BoolTB::new, BOOL);
    this.intSupplier = createAndCacheTypeMemoizer(IntTB::new, INT);
    this.stringSupplier = createAndCacheTypeMemoizer(StringTB::new, STRING);
  }

  private <A extends TypeB> Function0<A, CategoryDbException> createAndCacheTypeMemoizer(
      Function<Hash, A> factory, CategoryId id) {
    return memoizer(() -> cache(factory.apply(writeRoot(id))));
  }

  // methods for getting ValueB types

  public ArrayTB array(TypeB elemT) throws CategoryDbException {
    return newArray(elemT);
  }

  public BlobTB blob() throws CategoryDbException {
    return blobSupplier.apply();
  }

  public BoolTB bool() throws CategoryDbException {
    return boolSupplier.apply();
  }

  public LambdaCB lambda(List<TypeB> params, TypeB result) throws CategoryDbException {
    return funcC(LAMBDA, params, result, LambdaCB::new);
  }

  public LambdaCB lambda(FuncTB funcTB) throws CategoryDbException {
    return funcC(LAMBDA, funcTB, LambdaCB::new);
  }

  private <T extends FuncCB> T funcC(
      CategoryId id, List<TypeB> params, TypeB result, BiFunction<Hash, FuncTB, T> factory)
      throws CategoryDbException {
    return funcC(id, funcT(params, result), factory);
  }

  private <T extends FuncCB> T funcC(
      CategoryId id, FuncTB funcTB, BiFunction<Hash, FuncTB, T> factory)
      throws CategoryDbException {
    return newFuncC(id, funcTB, factory);
  }

  public FuncTB funcT(List<TypeB> params, TypeB result) throws CategoryDbException {
    return funcT(tuple(params), result);
  }

  public FuncTB funcT(TupleTB params, TypeB result) throws CategoryDbException {
    return newFuncT(params, result);
  }

  public IfFuncCB ifFunc(TypeB type) throws CategoryDbException {
    var funcT = funcT(list(bool(), type, type), type);
    return funcC(IF_FUNC, funcT, IfFuncCB::new);
  }

  public IntTB int_() throws CategoryDbException {
    return intSupplier.apply();
  }

  public MapFuncCB mapFunc(TypeB r, TypeB s) throws CategoryDbException {
    var funcT = funcT(list(array(s), funcT(list(s), r)), array(r));
    return funcC(MAP_FUNC, funcT, MapFuncCB::new);
  }

  public NativeFuncCB nativeFunc(List<TypeB> params, TypeB result) throws CategoryDbException {
    return funcC(NATIVE_FUNC, params, result, NativeFuncCB::new);
  }

  public NativeFuncCB nativeFunc(FuncTB funcTB) throws CategoryDbException {
    return funcC(NATIVE_FUNC, funcTB, NativeFuncCB::new);
  }

  public TupleTB tuple(TypeB... items) throws CategoryDbException {
    return tuple(list(items));
  }

  public TupleTB tuple(List<TypeB> items) throws CategoryDbException {
    return newTuple(items);
  }

  public StringTB string() throws CategoryDbException {
    return stringSupplier.apply();
  }

  // methods for getting ExprB types

  public CallCB call(TypeB evaluationType) throws CategoryDbException {
    return newOper(CALL, evaluationType, CallCB::new);
  }

  public CombineCB combine(TupleTB evaluationType) throws CategoryDbException {
    return newOper(COMBINE, evaluationType, CombineCB::new);
  }

  public OrderCB order(ArrayTB evaluationType) throws CategoryDbException {
    return newOper(ORDER, evaluationType, OrderCB::new);
  }

  public PickCB pick(TypeB evaluationType) throws CategoryDbException {
    return newOper(PICK, evaluationType, PickCB::new);
  }

  public ReferenceCB reference(TypeB evaluationType) throws CategoryDbException {
    return newOper(REFERENCE, evaluationType, ReferenceCB::new);
  }

  public SelectCB select(TypeB evaluationType) throws CategoryDbException {
    return newOper(SELECT, evaluationType, SelectCB::new);
  }

  // methods for reading from db

  public CategoryB get(Hash hash) throws CategoryDbException {
    return maybe(cache.get(hash)).getOrGet(() -> read(hash));
  }

  private CategoryB read(Hash hash) throws CategoryDbException {
    List<Hash> rootChain = readCategoryRootChain(hash);
    var id = decodeCategoryId(hash, rootChain.get(0));
    return switch (id) {
      case ARRAY -> readArrayType(hash, rootChain, id);
      case BLOB -> newBaseType(hash, id, rootChain, BlobTB::new);
      case BOOL -> newBaseType(hash, id, rootChain, BoolTB::new);
      case INT -> newBaseType(hash, id, rootChain, IntTB::new);
      case STRING -> newBaseType(hash, id, rootChain, StringTB::new);
      case LAMBDA -> readFuncCategory(hash, rootChain, id, LambdaCB::new);
      case FUNC -> readFuncType(hash, rootChain);
      case IF_FUNC -> readIfCategory(hash, rootChain, id);
      case MAP_FUNC -> readMapCategory(hash, rootChain, id);
      case NATIVE_FUNC -> readFuncCategory(hash, rootChain, id, NativeFuncCB::new);
      case CALL -> readOperCategory(hash, rootChain, id, TypeB.class, CallCB::new);
      case COMBINE -> readOperCategory(hash, rootChain, id, TupleTB.class, CombineCB::new);
      case ORDER -> readOperCategory(hash, rootChain, id, ArrayTB.class, OrderCB::new);
      case PICK -> readOperCategory(hash, rootChain, id, TypeB.class, PickCB::new);
      case REFERENCE -> readOperCategory(hash, rootChain, id, TypeB.class, ReferenceCB::new);
      case SELECT -> readOperCategory(hash, rootChain, id, TypeB.class, SelectCB::new);
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

  private ArrayTB readArrayType(Hash hash, List<Hash> rootChain, CategoryId id)
      throws DecodeCatException {
    return newArray(hash, readDataAsType(hash, rootChain, id, TypeB.class));
  }

  private <T extends TypeB> T newBaseType(
      Hash hash, CategoryId id, List<Hash> rootChain, Function<Hash, T> factory)
      throws DecodeCatRootException {
    assertCatRootChainSize(hash, id, rootChain, 1);
    return cache(factory.apply(hash));
  }

  private <T extends OperCB> T readOperCategory(
      Hash hash,
      List<Hash> rootChain,
      CategoryId id,
      Class<? extends TypeB> dataTypeClass,
      BiFunction<Hash, TypeB, T> factory)
      throws DecodeCatException {
    var evaluationType = readDataAsType(hash, rootChain, id, dataTypeClass);
    return newOper(factory, hash, evaluationType);
  }

  private FuncTB readFuncType(Hash rootHash, List<Hash> rootChain) throws DecodeCatException {
    assertCatRootChainSize(rootHash, FUNC, rootChain, 2);
    var nodes = readDataChainAsTypes(FUNC, rootHash, rootChain);
    if (nodes.size() != 2) {
      throw new DecodeCatWrongChainSizeException(rootHash, FUNC, DATA_PATH, 2, nodes.size());
    }
    var result = nodes.get(FUNC_RESULT_IDX);
    var params = nodes.get(FUNC_PARAMS_IDX);
    if (params instanceof TupleTB paramsTuple) {
      return cache(new FuncTB(rootHash, paramsTuple, result));
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, FUNC, FUNC_PARAMS_PATH, TupleTB.class, params.getClass());
    }
  }

  private CategoryB readIfCategory(Hash hash, List<Hash> rootChain, CategoryId id)
      throws CategoryDbException {
    return readFuncCategory(hash, rootChain, id, IfFuncCB::new, (FuncTB funcTB) -> {
      var params = funcTB.params();
      if (params.size() != 3) {
        throw illegalIfFuncTypeExc(hash, funcTB);
      }
      var result = funcTB.result();
      boolean first = params.get(0).equals(bool());
      boolean second = params.get(1).equals(result);
      boolean third = params.get(2).equals(result);
      if (!(first && second && third)) {
        throw illegalIfFuncTypeExc(hash, funcTB);
      }
    });
  }

  private CategoryB readMapCategory(Hash hash, List<Hash> rootChain, CategoryId id)
      throws CategoryDbException {
    return readFuncCategory(hash, rootChain, id, MapFuncCB::new, (FuncTB funcTB) -> {
      var params = funcTB.params();
      if (!(funcTB.result() instanceof ArrayTB outputArrayT)) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
      if (params.size() != 2) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
      if (!(params.get(0) instanceof ArrayTB inputArrayT)) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
      if (!(params.get(1) instanceof FuncTB mappingFuncT)) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
      if (mappingFuncT.params().size() != 1) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
      if (!inputArrayT.elem().equals(mappingFuncT.params().get(0))) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
      if (!outputArrayT.elem().equals(mappingFuncT.result())) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
    });
  }

  private <T extends FuncCB> CategoryB readFuncCategory(
      Hash rootHash, List<Hash> rootChain, CategoryId id, BiFunction<Hash, FuncTB, T> instantiator)
      throws CategoryDbException {
    return readFuncCategory(rootHash, rootChain, id, instantiator, t -> {});
  }

  private <T extends FuncCB> CategoryB readFuncCategory(
      Hash rootHash,
      List<Hash> rootChain,
      CategoryId categoryId,
      BiFunction<Hash, FuncTB, T> factory,
      Consumer1<FuncTB, CategoryDbException> typeVerifier)
      throws CategoryDbException {
    assertCatRootChainSize(rootHash, categoryId, rootChain, 2);
    var dataHash = rootChain.get(DATA_IDX);
    var typeComponent = invokeAndChainCategoryDbException(
        () -> read(dataHash), e -> new DecodeCatNodeException(rootHash, categoryId, DATA_PATH, e));
    if (typeComponent instanceof FuncTB funcTB) {
      typeVerifier.accept(funcTB);
      return cache(factory.apply(rootHash, funcTB));
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, categoryId, DATA_PATH, FuncTB.class, typeComponent.getClass());
    }
  }

  private TupleTB readTupleType(Hash rootHash, List<Hash> rootChain) throws DecodeCatException {
    assertCatRootChainSize(rootHash, TUPLE, rootChain, 2);
    var items = readDataChainAsTypes(TUPLE, rootHash, rootChain);
    return newTuple(rootHash, items);
  }

  // helper methods for reading

  private <T extends TypeB> T readDataAsType(
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

  private List<TypeB> readDataChainAsTypes(CategoryId id, Hash rootHash, List<Hash> rootChain)
      throws DecodeCatNodeException {
    var elemHashes = invokeAndChainHashedDbException(
        () -> hashedDb.readHashChain(rootChain.get(DATA_IDX)),
        e -> new DecodeCatNodeException(rootHash, id, DATA_PATH, e));
    var builder = new ArrayList<TypeB>();
    for (int i = 0; i < elemHashes.size(); i++) {
      builder.add(readDataChainElementAsType(id, rootHash, elemHashes.get(i), i));
    }
    return listOfAll(builder);
  }

  private TypeB readDataChainElementAsType(CategoryId id, Hash rootHash, Hash hash, int index)
      throws DecodeCatNodeException {
    var categoryB = invokeAndChainCategoryDbException(
        () -> get(hash), e -> new DecodeCatNodeException(rootHash, id, DATA_PATH, index, e));
    if (categoryB instanceof TypeB typeB) {
      return typeB;
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, id, DATA_PATH, index, TypeB.class, categoryB.getClass());
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

  private ArrayTB newArray(TypeB element) throws CategoryDbException {
    var rootHash = writeArrayRoot(element);
    return newArray(rootHash, element);
  }

  private ArrayTB newArray(Hash rootHash, TypeB element) {
    return cache(new ArrayTB(rootHash, element));
  }

  private FuncTB newFuncT(TupleTB params, TypeB result) throws CategoryDbException {
    var rootHash = writeFuncTypeRoot(params, result);
    return cache(new FuncTB(rootHash, params, result));
  }

  private <T extends FuncCB> T newFuncC(
      CategoryId id, FuncTB funcTB, BiFunction<Hash, FuncTB, T> factory)
      throws CategoryDbException {
    var rootHash = writeFuncCategoryRoot(id, funcTB);
    return cache(factory.apply(rootHash, funcTB));
  }

  private TupleTB newTuple(List<TypeB> items) throws CategoryDbException {
    var hash = writeTupleRoot(items);
    return newTuple(hash, items);
  }

  private TupleTB newTuple(Hash rootHash, List<TypeB> items) {
    return cache(new TupleTB(rootHash, items));
  }

  private <T extends OperCB> T newOper(
      CategoryId id, TypeB evaluationType, BiFunction<Hash, TypeB, T> factory)
      throws CategoryDbException {
    var rootHash = writeRoot(id, evaluationType);
    return newOper(factory, rootHash, evaluationType);
  }

  private <T extends OperCB> T newOper(
      BiFunction<Hash, TypeB, T> factory, Hash rootHash, TypeB evaluationType) {
    return cache(factory.apply(rootHash, evaluationType));
  }

  private <T extends CategoryB> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing category root

  private Hash writeArrayRoot(CategoryB elementCategory) throws CategoryDbException {
    return writeRoot(ARRAY, elementCategory);
  }

  private Hash writeFuncTypeRoot(TupleTB params, TypeB result) throws CategoryDbException {
    var dataHash = writeChain(params.hash(), result.hash());
    return writeRoot(FUNC, dataHash);
  }

  private Hash writeFuncCategoryRoot(CategoryId id, FuncTB funcTB) throws CategoryDbException {
    return writeRoot(id, funcTB);
  }

  private Hash writeTupleRoot(List<TypeB> items) throws CategoryDbException {
    var dataHash = writeChain(items);
    return writeRoot(TUPLE, dataHash);
  }

  // Helper methods for writing roots

  private Hash writeRoot(CategoryId id, CategoryB categoryB) throws CategoryDbException {
    return writeRoot(id, categoryB.hash());
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

  private Hash writeChain(List<? extends TypeB> types) throws CategoryDbException {
    var hashes = types.map(TypeB::hash);
    return invokeAndChainHashedDbException(
        () -> hashedDb.writeHashChain(hashes), CategoryDbException::new);
  }
}
