package org.smoothbuild.db.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.type.FuncKind.ABSTRACT_KIND;
import static org.smoothbuild.db.object.type.FuncKind.DEFINED_KIND;
import static org.smoothbuild.db.object.type.FuncKind.IF_KIND;
import static org.smoothbuild.db.object.type.FuncKind.MAP_KIND;
import static org.smoothbuild.db.object.type.FuncKind.NATIVE_KIND;
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExcAsDecodeCatExc;
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.db.object.type.Helpers.wrapObjectDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.db.object.type.base.CatKindH.ANY;
import static org.smoothbuild.db.object.type.base.CatKindH.ARRAY;
import static org.smoothbuild.db.object.type.base.CatKindH.BLOB;
import static org.smoothbuild.db.object.type.base.CatKindH.BOOL;
import static org.smoothbuild.db.object.type.base.CatKindH.CALL;
import static org.smoothbuild.db.object.type.base.CatKindH.COMBINE;
import static org.smoothbuild.db.object.type.base.CatKindH.INT;
import static org.smoothbuild.db.object.type.base.CatKindH.NOTHING;
import static org.smoothbuild.db.object.type.base.CatKindH.ORDER;
import static org.smoothbuild.db.object.type.base.CatKindH.PARAM_REF;
import static org.smoothbuild.db.object.type.base.CatKindH.SELECT;
import static org.smoothbuild.db.object.type.base.CatKindH.STRING;
import static org.smoothbuild.db.object.type.base.CatKindH.TUPLE;
import static org.smoothbuild.db.object.type.base.CatKindH.VARIABLE;
import static org.smoothbuild.db.object.type.base.CatKindH.fromMarker;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.object.db.ObjDbExc;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.exc.DecodeCatIllegalKindExc;
import org.smoothbuild.db.object.type.exc.DecodeCatRootExc;
import org.smoothbuild.db.object.type.exc.DecodeVarIllegalNameExc;
import org.smoothbuild.db.object.type.exc.UnexpectedCatNodeExc;
import org.smoothbuild.db.object.type.exc.UnexpectedCatSeqExc;
import org.smoothbuild.db.object.type.expr.CallCH;
import org.smoothbuild.db.object.type.expr.CombineCH;
import org.smoothbuild.db.object.type.expr.OrderCH;
import org.smoothbuild.db.object.type.expr.ParamRefCH;
import org.smoothbuild.db.object.type.expr.SelectCH;
import org.smoothbuild.db.object.type.val.AbstFuncTH;
import org.smoothbuild.db.object.type.val.AnyTH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.BlobTH;
import org.smoothbuild.db.object.type.val.BoolTH;
import org.smoothbuild.db.object.type.val.DefFuncTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.IfFuncTH;
import org.smoothbuild.db.object.type.val.IntTH;
import org.smoothbuild.db.object.type.val.MapFuncTH;
import org.smoothbuild.db.object.type.val.NatFuncTH;
import org.smoothbuild.db.object.type.val.NothingTH;
import org.smoothbuild.db.object.type.val.StringTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.db.object.type.val.VarH;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CatDb implements TypeFactoryH {
  public static final String DATA_PATH = "data";
  private static final int DATA_IDX = 1;
  private static final int FUNC_RES_IDX = 0;
  public static final String FUNC_RES_PATH = DATA_PATH + "[" + FUNC_RES_IDX + "]";
  private static final int FUNC_PARAMS_IDX = 1;
  public static final String FUNC_PARAMS_PATH = DATA_PATH + "[" + FUNC_PARAMS_IDX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, CatH> cache;

  private final AnyTH any;
  private final BlobTH blob;
  private final BoolTH bool;
  private final IntTH int_;
  private final NothingTH nothing;
  private final StringTH string;
  private final IfFuncTH ifFunc;
  private final MapFuncTH mapFunc;
  private final Sides<TypeH> sides;

  public CatDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.any = cache(new AnyTH(writeBaseRoot(ANY)));
      this.blob = cache(new BlobTH(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolTH(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntTH(writeBaseRoot(INT)));
      this.nothing = cache(new NothingTH(writeBaseRoot(NOTHING)));
      this.string = cache(new StringTH(writeBaseRoot(STRING)));

      this.ifFunc = createIfFunc();
      this.mapFunc = createMapFunc();
    } catch (HashedDbExc e) {
      throw new ObjDbExc(e);
    }
    this.sides = new Sides<>(this.any, this.nothing);
  }

  private IfFuncTH createIfFunc() {
    VarH a = cache(var("A"));
    return cache(func(IF_KIND, a, list(bool, a, a)));
  }

  private MapFuncTH createMapFunc() {
    VarH a = cache(var("A"));
    VarH r = cache(var("B"));
    ArrayTH ar = cache(array(r));
    ArrayTH aa = cache(array(a));
    FuncTH f = cache(func(r, list(a)));
    return func(MAP_KIND, ar, list(aa, f));
  }

  @Override
  public Bounds<TypeH> unbounded() {
    return new Bounds<>(nothing(), any());
  }

  @Override
  public Bounds<TypeH> oneSideBound(Side<TypeH> side, TypeH type) {
    return switch (side) {
      case Sides.Lower l -> new Bounds<>(type, any());
      case Sides.Upper u -> new Bounds<>(nothing(), type);
    };
  }

  @Override
  public Side<TypeH> upper() {
    return sides.upper();
  }

  @Override
  public Side<TypeH> lower() {
    return sides.lower();
  }

  // methods for getting Val-s types

  public AnyTH any() {
    return any;
  }

  @Override
  public ArrayTH array(TypeH elemT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArray(elemT));
  }

  public BlobTH blob() {
    return blob;
  }

  public BoolTH bool() {
    return bool;
  }

  public DefFuncTH defFunc(TypeH res, ImmutableList<TypeH> params) {
    return func(DEFINED_KIND, res, params);
  }

  @Override
  public AbstFuncTH func(TypeH res, ImmutableList<TypeH> params) {
    return func(ABSTRACT_KIND, res, params);
  }

  private <T extends FuncTH> T func(FuncKind<T> kind, TypeH res, ImmutableList<TypeH> params) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunc(kind, res, tuple(params)));
  }

  public IfFuncTH ifFunc() {
    return ifFunc;
  }

  public IntTH int_() {
    return int_;
  }

  public MapFuncTH mapFunc() {
    return mapFunc;
  }

  public NatFuncTH natFunc(TypeH res, ImmutableList<TypeH> params) {
    return func(NATIVE_KIND, res, params);
  }

  public NothingTH nothing() {
    return nothing;
  }

  public TupleTH tuple(ImmutableList<TypeH> itemTs) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(itemTs));
  }

  public StringTH string() {
    return string;
  }

  public VarH var(String name) {
    checkArgument(isVarName(name), "Illegal type var name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVar(name));
  }

  // methods for getting Expr-s types

  public CallCH call(TypeH evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evalT));
  }

  public CombineCH combine(TupleTH evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCombine(evalT));
  }

  public OrderCH order(TypeH elemT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elemT));
  }

  public ParamRefCH ref(TypeH evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evalT));
  }

  public SelectCH select(TypeH evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evalT));
  }

  // methods for reading from db

  public CatH get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private CatH read(Hash hash) {
    List<Hash> rootSeq = readCatRootSeq(hash);
    CatKindH kind = decodeCatMarker(hash, rootSeq.get(0));
    return switch (kind) {
      case ANY, BLOB, BOOL, INT, NOTHING, STRING -> {
        assertCatRootSeqSize(hash, kind, rootSeq, 1);
        throw new RuntimeException(
            "Internal error: Category with kind " + kind + " should be found in cache.");
      }
      case ARRAY -> newArray(hash, readDataAsVal(hash, rootSeq, kind));
      case CALL -> newCall(hash, readDataAsVal(hash, rootSeq, kind));
      case COMBINE -> newCombine(hash, readDataAsTuple(hash, rootSeq, kind));
      case ABST_FUNC, DEF_FUNC, NAT_FUNC, IF_FUNC, MAP_FUNC ->
          readFunc(hash, rootSeq, kind);
      case ORDER -> newOrder(hash, readDataAsArray(hash, rootSeq, kind));
      case PARAM_REF -> newRef(hash, readDataAsVal(hash, rootSeq, kind));
      case SELECT -> newSelect(hash, readDataAsVal(hash, rootSeq, kind));
      case TUPLE -> readTuple(hash, rootSeq);
      case VARIABLE -> readVar(hash, rootSeq);
    };
  }

  private List<Hash> readCatRootSeq(Hash hash) {
    var hashes = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readSeq(hash));
    int seqSize = hashes.size();
    if (seqSize != 1 && seqSize != 2) {
      throw new DecodeCatRootExc(hash, seqSize);
    }
    return hashes;
  }

  private CatKindH decodeCatMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExcAsDecodeCatExc(hash, () -> hashedDb.readByte(markerHash));
    CatKindH kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeCatIllegalKindExc(hash, marker);
    }
    return kind;
  }

  private static void assertCatRootSeqSize(
      Hash rootHash, CatKindH kind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeCatRootExc(rootHash, kind, hashes.size(), expectedSize);
    }
  }

  private TypeH readDataAsVal(Hash rootHash, List<Hash> rootSeq, CatKindH kind) {
    return readDataAsClass(rootHash, rootSeq, kind, TypeH.class);
  }

  private ArrayTH readDataAsArray(Hash rootHash, List<Hash> rootSeq, CatKindH kind) {
    return readDataAsClass(rootHash, rootSeq, kind, ArrayTH.class);
  }

  private TupleTH readDataAsTuple(Hash rootHash, List<Hash> rootSeq, CatKindH kind) {
    return readDataAsClass(rootHash, rootSeq, kind, TupleTH.class);
  }

  private <T extends CatH> T readDataAsClass(Hash rootHash, List<Hash> rootSeq, CatKindH kind,
      Class<T> expectedCatClass) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash hash = rootSeq.get(DATA_IDX);
    return readNode(kind, rootHash, hash, DATA_PATH, expectedCatClass);
  }

  private CatH readFunc(Hash rootHash, List<Hash> rootSeq, CatKindH kind) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash dataHash = rootSeq.get(DATA_IDX);
    List<Hash> data = readSeqHashes(rootHash, dataHash, kind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedCatSeqExc(rootHash, kind, DATA_PATH, 2, data.size());
    }
    var res = readNode(kind, rootHash, data.get(FUNC_RES_IDX), FUNC_RES_PATH, TypeH.class);
    var params = readNode(kind, rootHash, data.get(FUNC_PARAMS_IDX), FUNC_PARAMS_PATH, TupleTH.class);
    return newFunc(rootHash, FuncKind.from(kind), res, params);
  }

  private TupleTH readTuple(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, TUPLE, rootSeq, 2);
    var items = readTupleItems(rootHash, rootSeq.get(DATA_IDX));
    return newTuple(rootHash, items);
  }

  private ImmutableList<TypeH> readTupleItems(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<TypeH>builder();
    var itemTypeHashes = readSeqHashes(rootHash, hash, TUPLE, DATA_PATH);
    for (int i = 0; i < itemTypeHashes.size(); i++) {
      builder.add(readNode(TUPLE, rootHash, itemTypeHashes.get(i), DATA_PATH, i));
    }
    return builder.build();
  }

  private VarH readVar(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, VARIABLE, rootSeq, 2);
    var name = wrapHashedDbExcAsDecodeCatNodeExc(
        rootHash, VARIABLE, DATA_PATH, () ->hashedDb.readString(rootSeq.get(1)));
    if (!isVarName(name)) {
      throw new DecodeVarIllegalNameExc(rootHash, name);
    }
    return newVar(rootHash, name);
  }

  private <T> T readNode(CatKindH kind, Hash outerHash, Hash hash, String path, Class<T> clazz) {
    CatH result = wrapObjectDbExcAsDecodeCatNodeExc(kind, outerHash, path, () -> get(hash));
    if (clazz.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new UnexpectedCatNodeExc(outerHash, kind, path, clazz, result.getClass());
    }
  }

  private TypeH readNode(CatKindH kind, Hash outerHash, Hash hash, String path, int index) {
    CatH result = wrapObjectDbExcAsDecodeCatNodeExc(kind, outerHash, path, index, () -> get(hash));
    if (result instanceof TypeH typeH) {
      return typeH;
    } else {
      throw new UnexpectedCatNodeExc(outerHash, kind, path, index, TypeH.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayTH newArray(TypeH elemT) throws HashedDbExc {
    var rootHash = writeArrayRoot(elemT);
    return newArray(rootHash, elemT);
  }

  private ArrayTH newArray(Hash rootHash, TypeH elemT) {
    return cache(new ArrayTH(rootHash, elemT));
  }

  private <T extends FuncTH> T newFunc(
      FuncKind<T> kind, TypeH res, TupleTH params) throws HashedDbExc {
    var rootHash = writeFuncRoot(kind, res, params);
    return newFunc(rootHash, kind, res, params);
  }

  private <T extends FuncTH> T newFunc(
      Hash rootHash, FuncKind<T> kind, TypeH res, TupleTH params) {
    return cache(kind.newInstance(rootHash, res, params));
  }

  private TupleTH newTuple(ImmutableList<TypeH> itemTs) throws HashedDbExc {
    var hash = writeTupleRoot(itemTs);
    return newTuple(hash, itemTs);
  }

  private TupleTH newTuple(Hash rootHash, ImmutableList<TypeH> itemTs) {
    return cache(new TupleTH(rootHash, itemTs));
  }

  private VarH newVar(String name) throws HashedDbExc {
    var rootHash = writeVarRoot(name);
    return newVar(rootHash, name);
  }

  private VarH newVar(Hash rootHash, String name) {
    return cache(new VarH(rootHash, name));
  }

  // methods for creating Expr types

  private CallCH newCall(TypeH evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(CALL, evalT);
    return newCall(rootHash, evalT);
  }

  private CallCH newCall(Hash rootHash, TypeH evalT) {
    return cache(new CallCH(rootHash, evalT));
  }

  private CombineCH newCombine(TupleTH evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(COMBINE, evalT);
    return newCombine(rootHash, evalT);
  }

  private CombineCH newCombine(Hash rootHash, TupleTH evalT) {
    return cache(new CombineCH(rootHash, evalT));
  }

  private OrderCH newOrder(TypeH elemT) throws HashedDbExc {
    var evalT = array(elemT);
    var rootHash = writeExprRoot(ORDER, evalT);
    return newOrder(rootHash, evalT);
  }

  private OrderCH newOrder(Hash rootHash, ArrayTH evalT) {
    return cache(new OrderCH(rootHash, evalT));
  }

  private ParamRefCH newRef(TypeH evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(PARAM_REF, evalT);
    return newRef(rootHash, evalT);
  }

  private ParamRefCH newRef(Hash rootHash, TypeH evalT) {
    return cache(new ParamRefCH(rootHash, evalT));
  }

  private SelectCH newSelect(TypeH evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(SELECT, evalT);
    return newSelect(rootHash, evalT);
  }

  private SelectCH newSelect(Hash rootHash, TypeH evalT) {
    return cache(new SelectCH(rootHash, evalT));
  }

  private <T extends CatH> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing Val type root

  private Hash writeArrayRoot(CatH elemT) throws HashedDbExc {
    return writeNonBaseRoot(ARRAY, elemT.hash());
  }

  private Hash writeFuncRoot(FuncKind<?> kind, TypeH res, TupleTH params)
      throws HashedDbExc {
    var hash = hashedDb.writeSeq(res.hash(), params.hash());
    return writeNonBaseRoot(kind.kind(), hash);
  }

  private Hash writeTupleRoot(ImmutableList<TypeH> itemTs) throws HashedDbExc {
    var itemsHash = hashedDb.writeSeq(Lists.map(itemTs, CatH::hash));
    return writeNonBaseRoot(TUPLE, itemsHash);
  }

  private Hash writeVarRoot(String name) throws HashedDbExc {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprRoot(CatKindH kind, CatH evalT) throws HashedDbExc {
    return writeNonBaseRoot(kind, evalT.hash());
  }

  private Hash writeNonBaseRoot(CatKindH kind, Hash dataHash) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()), dataHash);
  }

  private Hash writeBaseRoot(CatKindH kind) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()));
  }

  // Helper methods for reading

  private ImmutableList<Hash> readSeqHashes(
      Hash rootHash, Hash seqHash, CatKindH kind, String path) {
    return wrapHashedDbExcAsDecodeCatNodeExc(
        rootHash, kind, path, () -> hashedDb.readSeq(seqHash));
  }
}
