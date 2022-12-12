package org.smoothbuild.bytecode.type;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;
import static org.smoothbuild.bytecode.type.CategoryKindB.fromMarker;
import static org.smoothbuild.bytecode.type.CategoryKinds.ARRAY;
import static org.smoothbuild.bytecode.type.CategoryKinds.BLOB;
import static org.smoothbuild.bytecode.type.CategoryKinds.BOOL;
import static org.smoothbuild.bytecode.type.CategoryKinds.CALL;
import static org.smoothbuild.bytecode.type.CategoryKinds.CLOSURE;
import static org.smoothbuild.bytecode.type.CategoryKinds.CLOSURIZE;
import static org.smoothbuild.bytecode.type.CategoryKinds.COMBINE;
import static org.smoothbuild.bytecode.type.CategoryKinds.DEFINED_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.IF_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.INT;
import static org.smoothbuild.bytecode.type.CategoryKinds.MAP_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.NATIVE_FUNC;
import static org.smoothbuild.bytecode.type.CategoryKinds.ORDER;
import static org.smoothbuild.bytecode.type.CategoryKinds.PICK;
import static org.smoothbuild.bytecode.type.CategoryKinds.REF;
import static org.smoothbuild.bytecode.type.CategoryKinds.SELECT;
import static org.smoothbuild.bytecode.type.CategoryKinds.STRING;
import static org.smoothbuild.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.bytecode.type.Helpers.wrapCatDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatExc;
import static org.smoothbuild.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.bytecode.type.exc.DecodeFuncCatWrongFuncTypeExc.illegalIfFuncTypeExc;
import static org.smoothbuild.bytecode.type.exc.DecodeFuncCatWrongFuncTypeExc.illegalMapFuncTypeExc;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.bytecode.type.CategoryKindB.AbstFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.ArrayKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.BaseKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.ClosureKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.ClosurizeKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.DefinedFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.FuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.IfFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.MapFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.NativeFuncKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.OperKindB;
import org.smoothbuild.bytecode.type.CategoryKindB.TupleKindB;
import org.smoothbuild.bytecode.type.exc.CategoryDbExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatIllegalKindExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatRootExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatWrongNodeCatExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatWrongSeqSizeExc;
import org.smoothbuild.bytecode.type.oper.CallCB;
import org.smoothbuild.bytecode.type.oper.ClosurizeCB;
import org.smoothbuild.bytecode.type.oper.CombineCB;
import org.smoothbuild.bytecode.type.oper.OperCB;
import org.smoothbuild.bytecode.type.oper.OrderCB;
import org.smoothbuild.bytecode.type.oper.PickCB;
import org.smoothbuild.bytecode.type.oper.RefCB;
import org.smoothbuild.bytecode.type.oper.SelectCB;
import org.smoothbuild.bytecode.type.value.ArrayTB;
import org.smoothbuild.bytecode.type.value.BlobTB;
import org.smoothbuild.bytecode.type.value.BoolTB;
import org.smoothbuild.bytecode.type.value.ClosureCB;
import org.smoothbuild.bytecode.type.value.DefinedFuncCB;
import org.smoothbuild.bytecode.type.value.FuncCB;
import org.smoothbuild.bytecode.type.value.FuncTB;
import org.smoothbuild.bytecode.type.value.IfFuncCB;
import org.smoothbuild.bytecode.type.value.IntTB;
import org.smoothbuild.bytecode.type.value.MapFuncCB;
import org.smoothbuild.bytecode.type.value.NativeFuncCB;
import org.smoothbuild.bytecode.type.value.StringTB;
import org.smoothbuild.bytecode.type.value.TupleTB;
import org.smoothbuild.bytecode.type.value.TypeB;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CategoryDb {
  public static final String DATA_PATH = "data";
  private static final int DATA_IDX = 1;
  private static final int FUNC_PARAMS_IDX = 0;
  public static final String FUNC_PARAMS_PATH = DATA_PATH + "[" + FUNC_PARAMS_IDX + "]";
  private static final int FUNC_RES_IDX = 1;
  public static final String FUNC_RES_PATH = DATA_PATH + "[" + FUNC_RES_IDX + "]";

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
    } catch (HashedDbExc e) {
      throw new CategoryDbExc(e);
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

  public ClosureCB closure(TypeB res, ImmutableList<TypeB> params) {
    return funcC(CLOSURE, res, params);
  }

  public ClosureCB closure(FuncTB funcTB) {
    return funcC(CLOSURE, funcTB);
  }

  public DefinedFuncCB definedFunc(TypeB res, ImmutableList<TypeB> params) {
    return funcC(DEFINED_FUNC, res, params);
  }

  public DefinedFuncCB definedFunc(FuncTB funcTB) {
    return funcC(DEFINED_FUNC, funcTB);
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, TypeB res,
      ImmutableList<TypeB> params) {
    return funcC(funcKind, funcT(params, res));
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, FuncTB funcTB) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newFuncC(funcKind, funcTB));
  }

  public FuncTB funcT(ImmutableList<TypeB> params, TypeB res) {
    return funcT(tuple(params), res);
  }

  public FuncTB funcT(TupleTB params, TypeB res) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newFuncT(params, res));
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

  public NativeFuncCB nativeFunc(TypeB res, ImmutableList<TypeB> params) {
    return funcC(NATIVE_FUNC, res, params);
  }

  public NativeFuncCB nativeFunc(FuncTB funcTB) {
    return funcC(NATIVE_FUNC, funcTB);
  }

  public TupleTB tuple(TypeB... items) {
    return tuple(list(items));
  }

  public TupleTB tuple(ImmutableList<TypeB> items) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newTuple(items));
  }

  public StringTB string() {
    return string;
  }

  // methods for getting Expr-s types

  public CallCB call(TypeB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(CALL, evalT));
  }

  public ClosurizeCB closurize(FuncTB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newClosurize(evalT));
  }

  public CombineCB combine(TupleTB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(COMBINE, evalT));
  }

  public OrderCB order(ArrayTB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(ORDER, evalT));
  }

  public PickCB pick(TypeB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(PICK, evalT));
  }

  public RefCB ref(TypeB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(REF, evalT));
  }

  public SelectCB select(TypeB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOper(SELECT, evalT));
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
      case ArrayKindB       array       -> readArrayT(hash, rootSeq, kind);
      case BaseKindB        base        -> handleBaseT(hash, rootSeq, kind);
      case ClosurizeKindB   closurizeB  -> readClosurizeCat(hash, rootSeq, closurizeB);
      case ClosureKindB     closure     -> readFuncCat(hash, rootSeq, closure);
      case DefinedFuncKindB definedFunc -> readFuncCat(hash, rootSeq, definedFunc);
      case FuncKindB        func        -> readFuncT(hash, rootSeq);
      case IfFuncKindB      ifFunc      -> readIfFuncCat(hash, rootSeq, ifFunc);
      case MapFuncKindB     mapFunc     -> readMapFuncCat(hash, rootSeq, mapFunc);
      case NativeFuncKindB  nativeFunc  -> readFuncCat(hash, rootSeq, nativeFunc);
      case OperKindB<?>     oper        -> readOperCat(hash, rootSeq, oper);
      case TupleKindB       tuple       -> readTupleT(hash, rootSeq);
    };
    // @formatter:on
  }

  private List<Hash> readCatRootSeq(Hash hash) {
    var hashes = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readSeq(hash));
    int seqSize = hashes.size();
    if (seqSize != 1 && seqSize != 2) {
      throw new DecodeCatRootExc(hash, seqSize);
    }
    return hashes;
  }

  private CategoryKindB decodeCatMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readByte(markerHash));
    CategoryKindB kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeCatIllegalKindExc(hash, marker);
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

  private OperCB readClosurizeCat(Hash hash, List<Hash> rootSeq, ClosurizeKindB closurizeKindB) {
    var evalT = readDataAsType(hash, rootSeq, closurizeKindB, FuncTB.class);
    return newClosurize(hash, evalT);
  }

  private OperCB readOperCat(Hash hash, List<Hash> rootSeq, OperKindB<?> operKind) {
    var evalT = readDataAsType(hash, rootSeq, operKind, operKind.dataClass());
    return newOper(operKind.constructor(), hash, evalT);
  }

  private FuncTB readFuncT(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, FUNC, rootSeq, 2);
    var nodes = readDataSeqAsTypes(rootHash, FUNC, rootSeq);
    if (nodes.size() != 2) {
      throw new DecodeCatWrongSeqSizeExc(rootHash, FUNC, DATA_PATH, 2, nodes.size());
    }
    var res = nodes.get(FUNC_RES_IDX);
    var params = nodes.get(FUNC_PARAMS_IDX);
    if (params instanceof TupleTB paramsTuple) {
      return cache(new FuncTB(rootHash, paramsTuple, res));
    } else {
      throw new DecodeCatWrongNodeCatExc(
          rootHash, FUNC, FUNC_PARAMS_PATH, TupleTB.class, params.getClass());
    }
  }

  private CategoryB readIfFuncCat(Hash hash, List<Hash> rootSeq, IfFuncKindB ifFuncKind) {
    return readFuncCat(hash, rootSeq, ifFuncKind, (FuncTB funcTB) -> {
      var params = funcTB.params();
      if (params.size() != 3) {
        throw illegalIfFuncTypeExc(hash, funcTB);
      }
      var res = funcTB.res();
      boolean first = params.get(0).equals(bool);
      boolean second = params.get(1).equals(res);
      boolean third = params.get(2).equals(res);
      if (!(first && second && third)) {
        throw illegalIfFuncTypeExc(hash, funcTB);
      }
    });
  }

  private CategoryB readMapFuncCat(Hash hash, List<Hash> rootSeq, MapFuncKindB mapFuncKind) {
    return readFuncCat(hash, rootSeq, mapFuncKind, (FuncTB funcTB) -> {
      var params = funcTB.params();
      if (!(funcTB.res() instanceof ArrayTB outputArrayT)) {
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
      if (!outputArrayT.elem().equals(mappingFuncT.res())) {
        throw illegalMapFuncTypeExc(hash, funcTB);
      }
    });
  }

  private <T extends FuncCB> CategoryB readFuncCat(Hash rootHash, List<Hash> rootSeq,
      AbstFuncKindB<T> kind) {
    return readFuncCat(rootHash, rootSeq, kind, t -> {});
  }

  private <T extends FuncCB> CategoryB readFuncCat(Hash rootHash, List<Hash> rootSeq,
      AbstFuncKindB<T> kind, Consumer<FuncTB> typeVerifier) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    var dataHash = rootSeq.get(DATA_IDX);
    var typeComponent = wrapCatDbExcAsDecodeCatNodeExc(
        kind, rootHash, DATA_PATH, () -> read(dataHash));
    if (typeComponent instanceof FuncTB funcTB) {
      typeVerifier.accept(funcTB);
      return cache(kind.instantiator().apply(rootHash, funcTB));
    } else {
      throw new DecodeCatWrongNodeCatExc(
          rootHash, kind, DATA_PATH, FuncTB.class, typeComponent.getClass());
    }
  }

  private TupleTB readTupleT(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, TUPLE, rootSeq, 2);
    var items = readDataSeqAsTypes(rootHash, TUPLE, rootSeq);
    return newTuple(rootHash, items);
  }

  // helper methods for reading

  private <T extends TypeB> T readDataAsType(Hash rootHash, List<Hash> rootSeq, CategoryKindB kind,
      Class<T> typeClass) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    var hash = rootSeq.get(DATA_IDX);
    var categoryB = wrapCatDbExcAsDecodeCatNodeExc(kind, rootHash, DATA_PATH, () -> get(hash));
    if (typeClass.isAssignableFrom(categoryB.getClass())) {
      @SuppressWarnings("unchecked")
      T result = (T) categoryB;
      return result;
    } else {
      throw new DecodeCatWrongNodeCatExc(
          rootHash, kind, DATA_PATH, typeClass, categoryB.getClass());
    }
  }

  private ImmutableList<TypeB> readDataSeqAsTypes(
      Hash rootHash, CategoryKindB kind, List<Hash> rootSeq) {
    var elemHashes = wrapHashedDbExcAsDecodeCatNodeExc(
        rootHash, kind, DATA_PATH, () -> hashedDb.readSeq(rootSeq.get(DATA_IDX)));
    var builder = ImmutableList.<TypeB>builder();
    for (int i = 0; i < elemHashes.size(); i++) {
      builder.add(readDataSeqElemAsType(kind, rootHash, elemHashes.get(i), i));
    }
    return builder.build();
  }

  private TypeB readDataSeqElemAsType(CategoryKindB kind, Hash rootHash, Hash hash, int index) {
    var categoryB = wrapCatDbExcAsDecodeCatNodeExc(
        kind, rootHash, DATA_PATH, index, () -> get(hash));
    if (categoryB instanceof TypeB typeB) {
      return typeB;
    } else {
      throw new DecodeCatWrongNodeCatExc(
          rootHash, kind, DATA_PATH, index, TypeB.class, categoryB.getClass());
    }
  }

  private static void assertCatRootSeqSize(
      Hash rootHash, CategoryKindB kind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeCatRootExc(rootHash, kind, hashes.size(), expectedSize);
    }
  }

  // methods for creating java instances of CategoryB

  private ArrayTB newArray(TypeB elem) throws HashedDbExc {
    var rootHash = writeArrayRoot(elem);
    return newArray(rootHash, elem);
  }

  private ArrayTB newArray(Hash rootHash, TypeB elem) {
    return cache(new ArrayTB(rootHash, elem));
  }

  private FuncTB newFuncT(TupleTB params, TypeB res) throws HashedDbExc {
    var rootHash = writeFuncTypeRoot(params, res);
    return cache(new FuncTB(rootHash, params, res));
  }

  private <T extends FuncCB> T newFuncC(AbstFuncKindB<T> funcKind, FuncTB funcTB)
      throws HashedDbExc {
    var rootHash = writeFuncCategoryRoot(funcKind, funcTB);
    var instantiator = funcKind.instantiator();
    return cache(instantiator.apply(rootHash, funcTB));
  }

  private TupleTB newTuple(ImmutableList<TypeB> items) throws HashedDbExc {
    var hash = writeTupleRoot(items);
    return newTuple(hash, items);
  }

  private TupleTB newTuple(Hash rootHash, ImmutableList<TypeB> items) {
    return cache(new TupleTB(rootHash, items));
  }

  private ClosurizeCB newClosurize(FuncTB funcTB) throws HashedDbExc {
    var rootHash = writeRootWithData(CLOSURIZE, funcTB);
    return newClosurize(rootHash, funcTB);
  }

  private ClosurizeCB newClosurize(Hash rootHash, FuncTB funcTB) {
    return cache(new ClosurizeCB(rootHash, funcTB));
  }

  private <T extends OperCB> T newOper(OperKindB<T> kind, TypeB evalT) throws HashedDbExc {
    var rootHash = writeRootWithData(kind, evalT);
    return newOper(kind.constructor(), rootHash, evalT);
  }

  private <T extends OperCB> T newOper(BiFunction<Hash, TypeB, T> constructor, Hash rootHash,
      TypeB evalT) {
    return cache(constructor.apply(rootHash, evalT));
  }

  private <T extends CategoryB> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing category root

  private Hash writeArrayRoot(CategoryB elem) throws HashedDbExc {
    return writeRootWithData(ARRAY, elem);
  }

  private Hash writeFuncTypeRoot(TupleTB params, TypeB res) throws HashedDbExc {
    var dataHash = hashedDb.writeSeq(params.hash(), res.hash());
    return writeRootWithData(FUNC, dataHash);
  }

  private Hash writeFuncCategoryRoot(CategoryKindB kind, FuncTB funcTB) throws HashedDbExc {
    return writeRootWithData(kind, funcTB);
  }

  private Hash writeTupleRoot(ImmutableList<TypeB> items) throws HashedDbExc {
    var dataHash = hashedDb.writeSeq(Lists.map(items, CategoryB::hash));
    return writeRootWithData(TUPLE, dataHash);
  }

  // Helper methods for writing roots

  private Hash writeRootWithData(CategoryKindB kind, CategoryB categoryB) throws HashedDbExc {
    return writeRootWithData(kind, categoryB.hash());
  }

  private Hash writeRootWithData(CategoryKindB kind, Hash dataHash) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()), dataHash);
  }

  private Hash writeBaseRoot(CategoryKindB kind) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()));
  }
}
