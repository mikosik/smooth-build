package org.smoothbuild.bytecode.type;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;
import static org.smoothbuild.bytecode.type.CatKindB.fromMarker;
import static org.smoothbuild.bytecode.type.CatKinds.ARRAY;
import static org.smoothbuild.bytecode.type.CatKinds.BLOB;
import static org.smoothbuild.bytecode.type.CatKinds.BOOL;
import static org.smoothbuild.bytecode.type.CatKinds.CALL;
import static org.smoothbuild.bytecode.type.CatKinds.COMBINE;
import static org.smoothbuild.bytecode.type.CatKinds.DEF_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.IF_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.INT;
import static org.smoothbuild.bytecode.type.CatKinds.MAP_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.NAT_FUNC;
import static org.smoothbuild.bytecode.type.CatKinds.ORDER;
import static org.smoothbuild.bytecode.type.CatKinds.PARAM_REF;
import static org.smoothbuild.bytecode.type.CatKinds.SELECT;
import static org.smoothbuild.bytecode.type.CatKinds.STRING;
import static org.smoothbuild.bytecode.type.CatKinds.TUPLE;
import static org.smoothbuild.bytecode.type.Helpers.wrapCatDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatExc;
import static org.smoothbuild.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.bytecode.type.exc.DecodeFuncCatWrongFuncTypeExc.illegalIfFuncTypeExc;
import static org.smoothbuild.bytecode.type.exc.DecodeFuncCatWrongFuncTypeExc.illegalMapFuncTypeExc;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.bytecode.type.CatKindB.AbstFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.ArrayKindB;
import org.smoothbuild.bytecode.type.CatKindB.BaseKindB;
import org.smoothbuild.bytecode.type.CatKindB.CallKindB;
import org.smoothbuild.bytecode.type.CatKindB.CombineKindB;
import org.smoothbuild.bytecode.type.CatKindB.DefFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.FuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.IfFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.MapFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.NatFuncKindB;
import org.smoothbuild.bytecode.type.CatKindB.OrderKindB;
import org.smoothbuild.bytecode.type.CatKindB.ParamRefKindB;
import org.smoothbuild.bytecode.type.CatKindB.SelectKindB;
import org.smoothbuild.bytecode.type.CatKindB.TupleKindB;
import org.smoothbuild.bytecode.type.exc.CatDbExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatIllegalKindExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatRootExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatWrongNodeCatExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatWrongSeqSizeExc;
import org.smoothbuild.bytecode.type.oper.CallCB;
import org.smoothbuild.bytecode.type.oper.CombineCB;
import org.smoothbuild.bytecode.type.oper.OrderCB;
import org.smoothbuild.bytecode.type.oper.ParamRefCB;
import org.smoothbuild.bytecode.type.oper.SelectCB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BlobTB;
import org.smoothbuild.bytecode.type.val.BoolTB;
import org.smoothbuild.bytecode.type.val.DefFuncCB;
import org.smoothbuild.bytecode.type.val.FuncCB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IfFuncCB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.MapFuncCB;
import org.smoothbuild.bytecode.type.val.NatFuncCB;
import org.smoothbuild.bytecode.type.val.StringTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CatDb {
  public static final String DATA_PATH = "data";
  private static final int DATA_IDX = 1;
  private static final int FUNC_RES_IDX = 0;
  public static final String FUNC_RES_PATH = DATA_PATH + "[" + FUNC_RES_IDX + "]";
  private static final int FUNC_PARAMS_IDX = 1;
  public static final String FUNC_PARAMS_PATH = DATA_PATH + "[" + FUNC_PARAMS_IDX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, CatB> cache;

  private final BlobTB blob;
  private final BoolTB bool;
  private final IntTB int_;
  private final StringTB string;

  public CatDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.blob = cache(new BlobTB(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolTB(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntTB(writeBaseRoot(INT)));
      this.string = cache(new StringTB(writeBaseRoot(STRING)));
    } catch (HashedDbExc e) {
      throw new CatDbExc(e);
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

  public DefFuncCB defFunc(TypeB res, ImmutableList<TypeB> params) {
    return funcC(DEF_FUNC, res, params);
  }

  public DefFuncCB defFunc(FuncTB funcTB) {
    return funcC(DEF_FUNC, funcTB);
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, TypeB res,
      ImmutableList<TypeB> params) {
    return funcC(funcKind, funcT(res, params));
  }

  private <T extends FuncCB> T funcC(AbstFuncKindB<T> funcKind, FuncTB funcTB) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newFuncC(funcKind, funcTB));
  }

  public FuncTB funcT(TypeB res, ImmutableList<TypeB> params) {
    return funcT(res, tuple(params));
  }

  public FuncTB funcT(TypeB res, TupleTB params) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newFuncT(res, params));
  }

  public IfFuncCB ifFunc(TypeB t) {
    var funcT = funcT(t, list(bool(), t, t));
    return wrapHashedDbExcAsBytecodeDbExc(() -> funcC(IF_FUNC, funcT));
  }

  public IntTB int_() {
    return int_;
  }

  public MapFuncCB mapFunc(TypeB r, TypeB s) {
    var funcT = funcT(array(r), list(array(s), funcT(r, list(s))));
    return wrapHashedDbExcAsBytecodeDbExc(() -> funcC(MAP_FUNC, funcT));
  }

  public NatFuncCB natFunc(TypeB res, ImmutableList<TypeB> params) {
    return funcC(NAT_FUNC, res, params);
  }

  public NatFuncCB natFunc(FuncTB funcTB) {
    return funcC(NAT_FUNC, funcTB);
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
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCall(evalT));
  }

  public CombineCB combine(TupleTB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCombine(evalT));
  }

  public OrderCB order(ArrayTB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOrder(evalT));
  }

  public ParamRefCB paramRef(TypeB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newParamRef(evalT));
  }

  public SelectCB select(TypeB evalT) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newSelect(evalT));
  }

  // methods for reading from db

  public CatB get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private CatB read(Hash hash) {
    List<Hash> rootSeq = readCatRootSeq(hash);
    CatKindB kind = decodeCatMarker(hash, rootSeq.get(0));
    return switch (kind) {
      case BaseKindB base -> handleBaseType(hash, rootSeq, kind);
      case ArrayKindB array -> newArray(hash, readDataAsValT(hash, rootSeq, kind));
      case CallKindB call -> newCall(hash, readDataAsEvalT(hash, rootSeq, kind));
      case CombineKindB combine -> newCombine(hash, readDataAsTupleT(hash, rootSeq, kind));
      case DefFuncKindB defFunc -> readFuncCat(hash, rootSeq, defFunc);
      case FuncKindB func -> readFuncT(hash, rootSeq);
      case IfFuncKindB ifFunc -> readIfFuncCat(hash, rootSeq, ifFunc);
      case MapFuncKindB mapFunc -> readMapFuncCat(hash, rootSeq, mapFunc);
      case NatFuncKindB natFunc -> readFuncCat(hash, rootSeq, natFunc);
      case OrderKindB order -> newOrder(hash, readDataAsArrayT(hash, rootSeq, kind));
      case ParamRefKindB paramRef -> newParamRef(hash, readDataAsEvalT(hash, rootSeq, kind));
      case SelectKindB select -> newSelect(hash, readDataAsEvalT(hash, rootSeq, kind));
      case TupleKindB tuple -> readTuple(hash, rootSeq);
    };
  }

  private static CatB handleBaseType(Hash hash, List<Hash> rootSeq, CatKindB kind) {
    assertCatRootSeqSize(hash, kind, rootSeq, 1);
    throw new RuntimeException(
        "Internal error: Category with kind " + kind + " should be found in cache.");
  }

  private List<Hash> readCatRootSeq(Hash hash) {
    var hashes = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readSeq(hash));
    int seqSize = hashes.size();
    if (seqSize != 1 && seqSize != 2) {
      throw new DecodeCatRootExc(hash, seqSize);
    }
    return hashes;
  }

  private CatKindB decodeCatMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readByte(markerHash));
    CatKindB kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeCatIllegalKindExc(hash, marker);
    }
    return kind;
  }

  private static void assertCatRootSeqSize(
      Hash rootHash, CatKindB kind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeCatRootExc(rootHash, kind, hashes.size(), expectedSize);
    }
  }

  private TypeB readDataAsEvalT(Hash rootHash, List<Hash> rootSeq, CatKindB kind) {
    return readDataAsClass(rootHash, rootSeq, kind, TypeB.class);
  }

  private TypeB readDataAsValT(Hash rootHash, List<Hash> rootSeq, CatKindB kind) {
    return readDataAsClass(rootHash, rootSeq, kind, TypeB.class);
  }

  private ArrayTB readDataAsArrayT(Hash rootHash, List<Hash> rootSeq, CatKindB kind) {
    return readDataAsClass(rootHash, rootSeq, kind, ArrayTB.class);
  }

  private TupleTB readDataAsTupleT(Hash rootHash, List<Hash> rootSeq, CatKindB kind) {
    return readDataAsClass(rootHash, rootSeq, kind, TupleTB.class);
  }

  private <T extends CatB> T readDataAsClass(Hash rootHash, List<Hash> rootSeq, CatKindB kind,
      Class<T> expectedCatClass) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash hash = rootSeq.get(DATA_IDX);
    return readNode(kind, rootHash, hash, DATA_PATH, expectedCatClass);
  }

  private FuncTB readFuncT(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, FUNC, rootSeq, 2);
    var nodes = readNodes(rootHash, FUNC, rootSeq.get(DATA_IDX));
    if (nodes.size() != 2) {
      throw new DecodeCatWrongSeqSizeExc(rootHash, FUNC, DATA_PATH, 2, nodes.size());
    }
    var res = nodes.get(FUNC_RES_IDX);
    var params = nodes.get(FUNC_PARAMS_IDX);
    if (params instanceof TupleTB paramsTuple) {
      return cache(new FuncTB(rootHash, res, paramsTuple));
    } else {
      throw new DecodeCatWrongNodeCatExc(
          rootHash, FUNC, FUNC_PARAMS_PATH, TupleTB.class, params.getClass());
    }
  }

  private CatB readIfFuncCat(Hash hash, List<Hash> rootSeq, IfFuncKindB ifFuncKind) {
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

  private CatB readMapFuncCat(Hash hash, List<Hash> rootSeq, MapFuncKindB mapFuncKind) {
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

  private <T extends FuncCB> CatB readFuncCat(Hash rootHash, List<Hash> rootSeq,
      AbstFuncKindB<T> kind) {
    return readFuncCat(rootHash, rootSeq, kind, t -> {});
  }

  private <T extends FuncCB> CatB readFuncCat(Hash rootHash, List<Hash> rootSeq,
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

  private TupleTB readTuple(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, TUPLE, rootSeq, 2);
    var items = readNodes(rootHash, TUPLE, rootSeq.get(DATA_IDX));
    return newTuple(rootHash, items);
  }

  private ImmutableList<TypeB> readNodes(Hash rootHash, CatKindB kind, Hash hash) {
    var builder = ImmutableList.<TypeB>builder();
    var itemTypeHashes = readSeqHashes(rootHash, hash, kind, DATA_PATH);
    for (int i = 0; i < itemTypeHashes.size(); i++) {
      builder.add(readNode(kind, rootHash, itemTypeHashes.get(i), DATA_PATH, i));
    }
    return builder.build();
  }

  private <T> T readNode(CatKindB kind, Hash rootHash, Hash hash, String path, Class<T> clazz) {
    CatB result = wrapCatDbExcAsDecodeCatNodeExc(kind, rootHash, path, () -> get(hash));
    if (clazz.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new DecodeCatWrongNodeCatExc(rootHash, kind, path, clazz, result.getClass());
    }
  }

  private TypeB readNode(CatKindB kind, Hash outerHash, Hash hash, String path, int index) {
    CatB result = wrapCatDbExcAsDecodeCatNodeExc(kind, outerHash, path, index, () -> get(hash));
    if (result instanceof TypeB typeB) {
      return typeB;
    } else {
      throw new DecodeCatWrongNodeCatExc(
          outerHash, kind, path, index, TypeB.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayTB newArray(TypeB elem) throws HashedDbExc {
    var rootHash = writeArrayRoot(elem);
    return newArray(rootHash, elem);
  }

  private ArrayTB newArray(Hash rootHash, TypeB elem) {
    return cache(new ArrayTB(rootHash, elem));
  }

  private FuncTB newFuncT(TypeB res, TupleTB params) throws HashedDbExc {
    var rootHash = writeFuncTypeRoot(res, params);
    return cache(new FuncTB(rootHash, res, params));
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

  // methods for creating Oper types

  private CallCB newCall(TypeB evalT) throws HashedDbExc {
    var rootHash = writeOperRoot(CALL, evalT);
    return newCall(rootHash, evalT);
  }

  private CallCB newCall(Hash rootHash, TypeB evalT) {
    return cache(new CallCB(rootHash, evalT));
  }

  private CombineCB newCombine(TupleTB evalT) throws HashedDbExc {
    var rootHash = writeOperRoot(COMBINE, evalT);
    return newCombine(rootHash, evalT);
  }

  private CombineCB newCombine(Hash rootHash, TupleTB evalT) {
    return cache(new CombineCB(rootHash, evalT));
  }

  private OrderCB newOrder(ArrayTB evalT) throws HashedDbExc {
    var rootHash = writeOperRoot(ORDER, evalT);
    return newOrder(rootHash, evalT);
  }

  private OrderCB newOrder(Hash rootHash, ArrayTB evalT) {
    return cache(new OrderCB(rootHash, evalT));
  }

  private ParamRefCB newParamRef(TypeB evalT) throws HashedDbExc {
    var rootHash = writeOperRoot(PARAM_REF, evalT);
    return newParamRef(rootHash, evalT);
  }

  private ParamRefCB newParamRef(Hash rootHash, TypeB evalT) {
    return cache(new ParamRefCB(rootHash, evalT));
  }

  private SelectCB newSelect(TypeB evalT) throws HashedDbExc {
    var rootHash = writeOperRoot(SELECT, evalT);
    return newSelect(rootHash, evalT);
  }

  private SelectCB newSelect(Hash rootHash, TypeB evalT) {
    return cache(new SelectCB(rootHash, evalT));
  }

  private <T extends CatB> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing Val type root

  private Hash writeArrayRoot(CatB elem) throws HashedDbExc {
    return writeNonBaseRoot(ARRAY, elem.hash());
  }

  private Hash writeFuncTypeRoot(TypeB res, TupleTB params) throws HashedDbExc {
    var dataHash = hashedDb.writeSeq(res.hash(), params.hash());
    return writeNonBaseRoot(FUNC, dataHash);
  }

  private Hash writeFuncCategoryRoot(CatKindB kind, FuncTB funcTB) throws HashedDbExc {
    var dataHash = funcTB.hash();
    return writeNonBaseRoot(kind, dataHash);
  }

  private Hash writeTupleRoot(ImmutableList<TypeB> items) throws HashedDbExc {
    var dataHash = hashedDb.writeSeq(Lists.map(items, CatB::hash));
    return writeNonBaseRoot(TUPLE, dataHash);
  }

  // Helper methods for writing roots

  private Hash writeOperRoot(CatKindB kind, CatB evalT) throws HashedDbExc {
    return writeNonBaseRoot(kind, evalT.hash());
  }

  private Hash writeNonBaseRoot(CatKindB kind, Hash dataHash) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()), dataHash);
  }

  private Hash writeBaseRoot(CatKindB kind) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()));
  }

  // Helper methods for reading

  private ImmutableList<Hash> readSeqHashes(
      Hash rootHash, Hash seqHash, CatKindB kind, String path) {
    return wrapHashedDbExcAsDecodeCatNodeExc(
        rootHash, kind, path, () -> hashedDb.readSeq(seqHash));
  }
}
