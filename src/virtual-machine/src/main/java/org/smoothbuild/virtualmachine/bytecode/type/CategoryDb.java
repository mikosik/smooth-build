package org.smoothbuild.virtualmachine.bytecode.type;

import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.function.Function0.memoizer;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainCategoryDbException;
import static org.smoothbuild.virtualmachine.bytecode.expr.Helpers.invokeAndChainHashedDbException;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.fromMarker;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.BLOB;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.BOOL;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.CALL;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.COMBINE;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.IF_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.INT;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.LAMBDA;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.MAP_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.NATIVE_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.ORDER;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.PICK;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.SELECT;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.STRING;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryKinds.VAR;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalIfFuncTypeExc;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalMapFuncTypeExc;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.smoothbuild.common.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.AbstFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.BaseKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.BlobKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.BoolKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.IfFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.IntKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.LambdaKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.MapFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.NativeFuncKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.OperKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.StringKindB;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryKindB.TupleKindB;
import org.smoothbuild.virtualmachine.bytecode.type.exc.CategoryDbException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatIllegalKindException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatNodeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatRootException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatWrongNodeCatException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CallCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CombineCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OperCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.OrderCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.PickCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.SelectCB;
import org.smoothbuild.virtualmachine.bytecode.type.oper.VarCB;
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
      Function<Hash, A> factory, CategoryKindB kind) {
    return memoizer(() -> cache(factory.apply(writeRoot(kind))));
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
    return funcC(LAMBDA, params, result);
  }

  public LambdaCB lambda(FuncTB funcTB) throws CategoryDbException {
    return funcC(LAMBDA, funcTB);
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, List<TypeB> params, TypeB result)
      throws CategoryDbException {
    return funcC(funcKind, funcT(params, result));
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, FuncTB funcTB)
      throws CategoryDbException {
    return newFuncC(funcKind, funcTB);
  }

  public FuncTB funcT(List<TypeB> params, TypeB result) throws CategoryDbException {
    return funcT(tuple(params), result);
  }

  public FuncTB funcT(TupleTB params, TypeB result) throws CategoryDbException {
    return newFuncT(params, result);
  }

  public IfFuncCB ifFunc(TypeB t) throws CategoryDbException {
    var funcT = funcT(list(bool(), t, t), t);
    return funcC(IF_FUNC, funcT);
  }

  public IntTB int_() throws CategoryDbException {
    return intSupplier.apply();
  }

  public MapFuncCB mapFunc(TypeB r, TypeB s) throws CategoryDbException {
    var funcT = funcT(list(array(s), funcT(list(s), r)), array(r));
    return funcC(MAP_FUNC, funcT);
  }

  public NativeFuncCB nativeFunc(List<TypeB> params, TypeB result) throws CategoryDbException {
    return funcC(NATIVE_FUNC, params, result);
  }

  public NativeFuncCB nativeFunc(FuncTB funcTB) throws CategoryDbException {
    return funcC(NATIVE_FUNC, funcTB);
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

  public CallCB call(TypeB evaluationT) throws CategoryDbException {
    return newOper(CALL, evaluationT);
  }

  public CombineCB combine(TupleTB evaluationT) throws CategoryDbException {
    return newOper(COMBINE, evaluationT);
  }

  public OrderCB order(ArrayTB evaluationT) throws CategoryDbException {
    return newOper(ORDER, evaluationT);
  }

  public PickCB pick(TypeB evaluationT) throws CategoryDbException {
    return newOper(PICK, evaluationT);
  }

  public VarCB var(TypeB evaluationT) throws CategoryDbException {
    return newOper(VAR, evaluationT);
  }

  public SelectCB select(TypeB evaluationT) throws CategoryDbException {
    return newOper(SELECT, evaluationT);
  }

  // methods for reading from db

  public CategoryB get(Hash hash) throws CategoryDbException {
    return maybe(cache.get(hash)).getOrGet(() -> read(hash));
  }

  private CategoryB read(Hash hash) throws CategoryDbException {
    List<Hash> rootChain = readCategoryRootChain(hash);
    CategoryKindB kind = decodeCatMarker(hash, rootChain.get(0));
    return switch (kind) {
      case ArrayKindB array -> readArrayT(hash, rootChain, kind);
      case BaseKindB base -> readBaseT(hash, rootChain, base);
      case LambdaKindB lambda -> readFuncCat(hash, rootChain, lambda);
      case FuncKindB func -> readFuncT(hash, rootChain);
      case IfFuncKindB ifFunc -> readIfFuncCat(hash, rootChain, ifFunc);
      case MapFuncKindB mapFunc -> readMapFuncCat(hash, rootChain, mapFunc);
      case NativeFuncKindB nativeFunc -> readFuncCat(hash, rootChain, nativeFunc);
      case OperKindB<?> oper -> readOperCat(hash, rootChain, oper);
      case TupleKindB tuple -> readTupleT(hash, rootChain);
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

  private CategoryKindB decodeCatMarker(Hash hash, Hash markerHash) throws DecodeCatException {
    byte marker = invokeAndChainHashedDbException(
        () -> hashedDb.readByte(markerHash), e -> new DecodeCatException(hash, e));
    CategoryKindB kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeCatIllegalKindException(hash, marker);
    }
    return kind;
  }

  private ArrayTB readArrayT(Hash hash, List<Hash> rootChain, CategoryKindB kind)
      throws DecodeCatException {
    return newArray(hash, readDataAsType(hash, rootChain, kind, TypeB.class));
  }

  private TypeB readBaseT(Hash rootHash, List<Hash> rootChain, BaseKindB kind)
      throws DecodeCatRootException {
    assertCatRootChainSize(rootHash, kind, rootChain, 1);
    return newBaseT(rootHash, kind);
  }

  private OperCB readOperCat(Hash hash, List<Hash> rootChain, OperKindB<?> operKind)
      throws DecodeCatException {
    var evaluationT = readDataAsType(hash, rootChain, operKind, operKind.dataClass());
    return newOper(operKind.constructor(), hash, evaluationT);
  }

  private FuncTB readFuncT(Hash rootHash, List<Hash> rootChain) throws DecodeCatException {
    assertCatRootChainSize(rootHash, FUNC, rootChain, 2);
    var nodes = readDataChainAsTypes(rootHash, FUNC, rootChain);
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

  private CategoryB readIfFuncCat(Hash hash, List<Hash> rootChain, IfFuncKindB ifFuncKind)
      throws CategoryDbException {
    return readFuncCat(hash, rootChain, ifFuncKind, (FuncTB funcTB) -> {
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

  private CategoryB readMapFuncCat(Hash hash, List<Hash> rootChain, MapFuncKindB mapFuncKind)
      throws CategoryDbException {
    return readFuncCat(hash, rootChain, mapFuncKind, (FuncTB funcTB) -> {
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

  private <T extends FuncCB> CategoryB readFuncCat(
      Hash rootHash, List<Hash> rootChain, AbstFuncKindB<T> kind) throws CategoryDbException {
    return readFuncCat(rootHash, rootChain, kind, t -> {});
  }

  private <T extends FuncCB> CategoryB readFuncCat(
      Hash rootHash,
      List<Hash> rootChain,
      AbstFuncKindB<T> kind,
      Consumer1<FuncTB, CategoryDbException> typeVerifier)
      throws CategoryDbException {
    assertCatRootChainSize(rootHash, kind, rootChain, 2);
    var dataHash = rootChain.get(DATA_IDX);
    var typeComponent = invokeAndChainCategoryDbException(
        () -> read(dataHash), e -> new DecodeCatNodeException(rootHash, kind, DATA_PATH, e));
    if (typeComponent instanceof FuncTB funcTB) {
      typeVerifier.accept(funcTB);
      return cache(kind.instantiator().apply(rootHash, funcTB));
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, kind, DATA_PATH, FuncTB.class, typeComponent.getClass());
    }
  }

  private TupleTB readTupleT(Hash rootHash, List<Hash> rootChain) throws DecodeCatException {
    assertCatRootChainSize(rootHash, TUPLE, rootChain, 2);
    var items = readDataChainAsTypes(rootHash, TUPLE, rootChain);
    return newTuple(rootHash, items);
  }

  // helper methods for reading

  private <T extends TypeB> T readDataAsType(
      Hash rootHash, List<Hash> rootChain, CategoryKindB kind, Class<T> typeClass)
      throws DecodeCatException {
    assertCatRootChainSize(rootHash, kind, rootChain, 2);
    var hash = rootChain.get(DATA_IDX);
    var categoryB = invokeAndChainCategoryDbException(
        () -> get(hash), e -> new DecodeCatNodeException(rootHash, kind, DATA_PATH, e));
    if (typeClass.isAssignableFrom(categoryB.getClass())) {
      @SuppressWarnings("unchecked")
      T result = (T) categoryB;
      return result;
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, kind, DATA_PATH, typeClass, categoryB.getClass());
    }
  }

  private List<TypeB> readDataChainAsTypes(Hash rootHash, CategoryKindB kind, List<Hash> rootChain)
      throws DecodeCatNodeException {
    var elemHashes = invokeAndChainHashedDbException(
        () -> hashedDb.readHashChain(rootChain.get(DATA_IDX)),
        e -> new DecodeCatNodeException(rootHash, kind, DATA_PATH, e));
    var builder = new ArrayList<TypeB>();
    for (int i = 0; i < elemHashes.size(); i++) {
      builder.add(readDataChainElementAsType(kind, rootHash, elemHashes.get(i), i));
    }
    return listOfAll(builder);
  }

  private TypeB readDataChainElementAsType(CategoryKindB kind, Hash rootHash, Hash hash, int index)
      throws DecodeCatNodeException {
    var categoryB = invokeAndChainCategoryDbException(
        () -> get(hash), e -> new DecodeCatNodeException(rootHash, kind, DATA_PATH, index, e));
    if (categoryB instanceof TypeB typeB) {
      return typeB;
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, kind, DATA_PATH, index, TypeB.class, categoryB.getClass());
    }
  }

  private static void assertCatRootChainSize(
      Hash rootHash, CategoryKindB kind, List<Hash> hashes, int expectedSize)
      throws DecodeCatRootException {
    if (hashes.size() != expectedSize) {
      throw new DecodeCatRootException(rootHash, kind, hashes.size(), expectedSize);
    }
  }

  // methods for creating java instances of CategoryB

  private ArrayTB newArray(TypeB elem) throws CategoryDbException {
    var rootHash = writeArrayRoot(elem);
    return newArray(rootHash, elem);
  }

  private ArrayTB newArray(Hash rootHash, TypeB elem) {
    return cache(new ArrayTB(rootHash, elem));
  }

  private TypeB newBaseT(Hash rootHash, BaseKindB kind) {
    return cache(
        switch (kind) {
          case BlobKindB blobKind -> new BlobTB(rootHash);
          case BoolKindB boolKind -> new BoolTB(rootHash);
          case IntKindB intKind -> new IntTB(rootHash);
          case StringKindB stringKind -> new StringTB(rootHash);
        });
  }

  private FuncTB newFuncT(TupleTB params, TypeB result) throws CategoryDbException {
    var rootHash = writeFuncTypeRoot(params, result);
    return cache(new FuncTB(rootHash, params, result));
  }

  private <T extends FuncCB> T newFuncC(AbstFuncKindB<T> funcKind, FuncTB funcTB)
      throws CategoryDbException {
    var rootHash = writeFuncCategoryRoot(funcKind, funcTB);
    var instantiator = funcKind.instantiator();
    return cache(instantiator.apply(rootHash, funcTB));
  }

  private TupleTB newTuple(List<TypeB> items) throws CategoryDbException {
    var hash = writeTupleRoot(items);
    return newTuple(hash, items);
  }

  private TupleTB newTuple(Hash rootHash, List<TypeB> items) {
    return cache(new TupleTB(rootHash, items));
  }

  private <T extends OperCB> T newOper(OperKindB<T> kind, TypeB evaluationT)
      throws CategoryDbException {
    var rootHash = writeRoot(kind, evaluationT);
    return newOper(kind.constructor(), rootHash, evaluationT);
  }

  private <T extends OperCB> T newOper(
      BiFunction<Hash, TypeB, T> constructor, Hash rootHash, TypeB evaluationT) {
    return cache(constructor.apply(rootHash, evaluationT));
  }

  private <T extends CategoryB> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing category root

  private Hash writeArrayRoot(CategoryB elem) throws CategoryDbException {
    return writeRoot(ARRAY, elem);
  }

  private Hash writeFuncTypeRoot(TupleTB params, TypeB result) throws CategoryDbException {
    var dataHash = writeChain(params.hash(), result.hash());
    return writeRoot(FUNC, dataHash);
  }

  private Hash writeFuncCategoryRoot(CategoryKindB kind, FuncTB funcTB) throws CategoryDbException {
    return writeRoot(kind, funcTB);
  }

  private Hash writeTupleRoot(List<TypeB> items) throws CategoryDbException {
    var dataHash = writeChain(items);
    return writeRoot(TUPLE, dataHash);
  }

  // Helper methods for writing roots

  private Hash writeRoot(CategoryKindB kind, CategoryB categoryB) throws CategoryDbException {
    return writeRoot(kind, categoryB.hash());
  }

  private Hash writeRoot(CategoryKindB kind, Hash dataHash) throws CategoryDbException {
    return writeChain(writeByte(kind.marker()), dataHash);
  }

  private Hash writeRoot(CategoryKindB kind) throws CategoryDbException {
    return writeChain(writeByte(kind.marker()));
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
