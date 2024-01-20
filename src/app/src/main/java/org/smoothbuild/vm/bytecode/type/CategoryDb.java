package org.smoothbuild.vm.bytecode.type;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.vm.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;
import static org.smoothbuild.vm.bytecode.type.CategoryKindB.fromMarker;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.ARRAY;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.BLOB;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.BOOL;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.CALL;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.COMBINE;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.IF_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.INT;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.LAMBDA;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.MAP_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.NATIVE_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.ORDER;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.PICK;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.SELECT;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.STRING;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.VAR;
import static org.smoothbuild.vm.bytecode.type.Helpers.wrapCatDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.vm.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatExc;
import static org.smoothbuild.vm.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.vm.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalIfFuncTypeExc;
import static org.smoothbuild.vm.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalMapFuncTypeExc;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.HashedDb;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.AbstFuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.BaseKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.IfFuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.LambdaKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.MapFuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.NativeFuncKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.OperKindB;
import org.smoothbuild.vm.bytecode.type.CategoryKindB.TupleKindB;
import org.smoothbuild.vm.bytecode.type.exc.CategoryDbException;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatIllegalKindException;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatRootException;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatWrongNodeCatException;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatWrongSeqSizeException;
import org.smoothbuild.vm.bytecode.type.oper.CallCB;
import org.smoothbuild.vm.bytecode.type.oper.CombineCB;
import org.smoothbuild.vm.bytecode.type.oper.OperCB;
import org.smoothbuild.vm.bytecode.type.oper.OrderCB;
import org.smoothbuild.vm.bytecode.type.oper.PickCB;
import org.smoothbuild.vm.bytecode.type.oper.SelectCB;
import org.smoothbuild.vm.bytecode.type.oper.VarCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.BlobTB;
import org.smoothbuild.vm.bytecode.type.value.BoolTB;
import org.smoothbuild.vm.bytecode.type.value.FuncCB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.IfFuncCB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;
import org.smoothbuild.vm.bytecode.type.value.LambdaCB;
import org.smoothbuild.vm.bytecode.type.value.MapFuncCB;
import org.smoothbuild.vm.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.vm.bytecode.type.value.StringTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

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

  private final BlobTB blob;
  private final BoolTB bool;
  private final IntTB int_;
  private final StringTB string;

  public CategoryDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.blob = cache(new BlobTB(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolTB(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntTB(writeBaseRoot(INT)));
      this.string = cache(new StringTB(writeBaseRoot(STRING)));
    } catch (HashedDbException e) {
      throw new CategoryDbException(e);
    }
  }

  // methods for getting Val-s types

  public ArrayTB array(TypeB elemT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newArray(elemT));
  }

  public BlobTB blob() {
    return blob;
  }

  public BoolTB bool() {
    return bool;
  }

  public LambdaCB lambda(List<TypeB> params, TypeB result) {
    return funcC(LAMBDA, params, result);
  }

  public LambdaCB lambda(FuncTB funcTB) {
    return funcC(LAMBDA, funcTB);
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, List<TypeB> params, TypeB result) {
    return funcC(funcKind, funcT(params, result));
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, FuncTB funcTB) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newFuncC(funcKind, funcTB));
  }

  public FuncTB funcT(List<TypeB> params, TypeB result) {
    return funcT(tuple(params), result);
  }

  public FuncTB funcT(TupleTB params, TypeB result) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newFuncT(params, result));
  }

  public IfFuncCB ifFunc(TypeB t) {
    var funcT = funcT(list(bool(), t, t), t);
    return wrapHashedDbExcAsBytecodeDbExc(() -> funcC(IF_FUNC, funcT));
  }

  public IntTB int_() {
    return int_;
  }

  public MapFuncCB mapFunc(TypeB r, TypeB s) {
    var funcT = funcT(list(array(s), funcT(list(s), r)), array(r));
    return wrapHashedDbExcAsBytecodeDbExc(() -> funcC(MAP_FUNC, funcT));
  }

  public NativeFuncCB nativeFunc(List<TypeB> params, TypeB result) {
    return funcC(NATIVE_FUNC, params, result);
  }

  public NativeFuncCB nativeFunc(FuncTB funcTB) {
    return funcC(NATIVE_FUNC, funcTB);
  }

  public TupleTB tuple(TypeB... items) {
    return tuple(list(items));
  }

  public TupleTB tuple(List<TypeB> items) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newTuple(items));
  }

  public StringTB string() {
    return string;
  }

  // methods for getting Expr-s types

  public CallCB call(TypeB evaluationT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(CALL, evaluationT));
  }

  public CombineCB combine(TupleTB evaluationT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(COMBINE, evaluationT));
  }

  public OrderCB order(ArrayTB evaluationT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(ORDER, evaluationT));
  }

  public PickCB pick(TypeB evaluationT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(PICK, evaluationT));
  }

  public VarCB var(TypeB evaluationT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(VAR, evaluationT));
  }

  public SelectCB select(TypeB evaluationT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(SELECT, evaluationT));
  }

  // methods for reading from db

  public CategoryB get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private CategoryB read(Hash hash) {
    List<Hash> rootSeq = readCatRootSeq(hash);
    CategoryKindB kind = decodeCatMarker(hash, rootSeq.get(0));
    // @formatter:off
    return switch (kind) {
      case ArrayKindB array -> readArrayT(hash, rootSeq, kind);
      case BaseKindB base -> handleBaseT(hash, rootSeq, kind);
      case LambdaKindB lambda -> readFuncCat(hash, rootSeq, lambda);
      case FuncKindB func -> readFuncT(hash, rootSeq);
      case IfFuncKindB ifFunc -> readIfFuncCat(hash, rootSeq, ifFunc);
      case MapFuncKindB mapFunc -> readMapFuncCat(hash, rootSeq, mapFunc);
      case NativeFuncKindB nativeFunc -> readFuncCat(hash, rootSeq, nativeFunc);
      case OperKindB<?> oper -> readOperCat(hash, rootSeq, oper);
      case TupleKindB tuple -> readTupleT(hash, rootSeq);
    };
  }

  private List<Hash> readCatRootSeq(Hash hash) {
    var hashes = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readSeq(hash));
    int seqSize = hashes.size();
    if (seqSize != 1 && seqSize != 2) {
      throw new DecodeCatRootException(hash, seqSize);
    }
    return hashes;
  }

  private CategoryKindB decodeCatMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readByte(markerHash));
    CategoryKindB kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeCatIllegalKindException(hash, marker);
    }
    return kind;
  }

  private ArrayTB readArrayT(Hash hash, List<Hash> rootSeq, CategoryKindB kind) {
    return newArray(hash, readDataAsType(hash, rootSeq, kind, TypeB.class));
  }

  private static CategoryB handleBaseT(Hash hash, List<Hash> rootSeq, CategoryKindB kind) {
    assertCatRootSeqSize(hash, kind, rootSeq, 1);
    throw new RuntimeException(
        "Internal error: Category with kind " + kind + " should be found in cache.");
  }

  private OperCB readOperCat(Hash hash, List<Hash> rootSeq, OperKindB<?> operKind) {
    var evaluationT = readDataAsType(hash, rootSeq, operKind, operKind.dataClass());
    return newOper(operKind.constructor(), hash, evaluationT);
  }

  private FuncTB readFuncT(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, FUNC, rootSeq, 2);
    var nodes = readDataSeqAsTypes(rootHash, FUNC, rootSeq);
    if (nodes.size() != 2) {
      throw new DecodeCatWrongSeqSizeException(rootHash, FUNC, DATA_PATH, 2, nodes.size());
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

  private CategoryB readIfFuncCat(Hash hash, List<Hash> rootSeq, IfFuncKindB ifFuncKind) {
    return readFuncCat(hash, rootSeq, ifFuncKind, (FuncTB funcTB) -> {
      var params = funcTB.params();
      if (params.size() != 3) {
        throw illegalIfFuncTypeExc(hash, funcTB);
      }
      var result = funcTB.result();
      boolean first = params.get(0).equals(bool);
      boolean second = params.get(1).equals(result);
      boolean third = params.get(2).equals(result);
      if (!(first && second && third)) {
        throw illegalIfFuncTypeExc(hash, funcTB);
      }
    });
  }

  private CategoryB readMapFuncCat(Hash hash, List<Hash> rootSeq, MapFuncKindB mapFuncKind) {
    return readFuncCat(hash, rootSeq, mapFuncKind, (FuncTB funcTB) -> {
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
      Hash rootHash, List<Hash> rootSeq, AbstFuncKindB<T> kind) {
    return readFuncCat(rootHash, rootSeq, kind, t -> {});
  }

  private <T extends FuncCB> CategoryB readFuncCat(
      Hash rootHash, List<Hash> rootSeq, AbstFuncKindB<T> kind, Consumer<FuncTB> typeVerifier) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    var dataHash = rootSeq.get(DATA_IDX);
    var typeComponent =
        wrapCatDbExcAsDecodeCatNodeExc(kind, rootHash, DATA_PATH, () -> read(dataHash));
    if (typeComponent instanceof FuncTB funcTB) {
      typeVerifier.accept(funcTB);
      return cache(kind.instantiator().apply(rootHash, funcTB));
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, kind, DATA_PATH, FuncTB.class, typeComponent.getClass());
    }
  }

  private TupleTB readTupleT(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, TUPLE, rootSeq, 2);
    var items = readDataSeqAsTypes(rootHash, TUPLE, rootSeq);
    return newTuple(rootHash, items);
  }

  // helper methods for reading

  private <T extends TypeB> T readDataAsType(
      Hash rootHash, List<Hash> rootSeq, CategoryKindB kind, Class<T> typeClass) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    var hash = rootSeq.get(DATA_IDX);
    var categoryB = wrapCatDbExcAsDecodeCatNodeExc(kind, rootHash, DATA_PATH, () -> get(hash));
    if (typeClass.isAssignableFrom(categoryB.getClass())) {
      @SuppressWarnings("unchecked")
      T result = (T) categoryB;
      return result;
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, kind, DATA_PATH, typeClass, categoryB.getClass());
    }
  }

  private List<TypeB> readDataSeqAsTypes(Hash rootHash, CategoryKindB kind, List<Hash> rootSeq) {
    var elemHashes = wrapHashedDbExcAsDecodeCatNodeExc(
        rootHash, kind, DATA_PATH, () -> hashedDb.readSeq(rootSeq.get(DATA_IDX)));
    var builder = new ArrayList<TypeB>();
    for (int i = 0; i < elemHashes.size(); i++) {
      builder.add(readDataSeqElemAsType(kind, rootHash, elemHashes.get(i), i));
    }
    return listOfAll(builder);
  }

  private TypeB readDataSeqElemAsType(CategoryKindB kind, Hash rootHash, Hash hash, int index) {
    var categoryB =
        wrapCatDbExcAsDecodeCatNodeExc(kind, rootHash, DATA_PATH, index, () -> get(hash));
    if (categoryB instanceof TypeB typeB) {
      return typeB;
    } else {
      throw new DecodeCatWrongNodeCatException(
          rootHash, kind, DATA_PATH, index, TypeB.class, categoryB.getClass());
    }
  }

  private static void assertCatRootSeqSize(
      Hash rootHash, CategoryKindB kind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeCatRootException(rootHash, kind, hashes.size(), expectedSize);
    }
  }

  // methods for creating java instances of CategoryB

  private ArrayTB newArray(TypeB elem) throws HashedDbException {
    var rootHash = writeArrayRoot(elem);
    return newArray(rootHash, elem);
  }

  private ArrayTB newArray(Hash rootHash, TypeB elem) {
    return cache(new ArrayTB(rootHash, elem));
  }

  private FuncTB newFuncT(TupleTB params, TypeB result) throws HashedDbException {
    var rootHash = writeFuncTypeRoot(params, result);
    return cache(new FuncTB(rootHash, params, result));
  }

  private <T extends FuncCB> T newFuncC(AbstFuncKindB<T> funcKind, FuncTB funcTB)
      throws HashedDbException {
    var rootHash = writeFuncCategoryRoot(funcKind, funcTB);
    var instantiator = funcKind.instantiator();
    return cache(instantiator.apply(rootHash, funcTB));
  }

  private TupleTB newTuple(List<TypeB> items) throws HashedDbException {
    var hash = writeTupleRoot(items);
    return newTuple(hash, items);
  }

  private TupleTB newTuple(Hash rootHash, List<TypeB> items) {
    return cache(new TupleTB(rootHash, items));
  }

  private <T extends OperCB> T newOper(OperKindB<T> kind, TypeB evaluationT)
      throws HashedDbException {
    var rootHash = writeRootWithData(kind, evaluationT);
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

  private Hash writeArrayRoot(CategoryB elem) throws HashedDbException {
    return writeRootWithData(ARRAY, elem);
  }

  private Hash writeFuncTypeRoot(TupleTB params, TypeB result) throws HashedDbException {
    var dataHash = hashedDb.writeSeq(params.hash(), result.hash());
    return writeRootWithData(FUNC, dataHash);
  }

  private Hash writeFuncCategoryRoot(CategoryKindB kind, FuncTB funcTB) throws HashedDbException {
    return writeRootWithData(kind, funcTB);
  }

  private Hash writeTupleRoot(List<TypeB> items) throws HashedDbException {
    var dataHash = hashedDb.writeSeq(items.map(CategoryB::hash));
    return writeRootWithData(TUPLE, dataHash);
  }

  // Helper methods for writing roots

  private Hash writeRootWithData(CategoryKindB kind, CategoryB categoryB) throws HashedDbException {
    return writeRootWithData(kind, categoryB.hash());
  }

  private Hash writeRootWithData(CategoryKindB kind, Hash dataHash) throws HashedDbException {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()), dataHash);
  }

  private Hash writeBaseRoot(CategoryKindB kind) throws HashedDbException {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()));
  }
}
