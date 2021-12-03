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
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExceptionAsDecodeTypeException;
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExceptionAsDecodeTypeNodeException;
import static org.smoothbuild.db.object.type.Helpers.wrapObjectDbExceptionAsDecodeTypeNodeException;
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
import org.smoothbuild.db.object.type.exc.DecodeTypeIllegalKindExc;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootExc;
import org.smoothbuild.db.object.type.exc.DecodeVarIllegalNameExc;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeNodeExc;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeSeqExc;
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
  private static final int DATA_INDEX = 1;
  private static final int FUNCTION_RES_INDEX = 0;
  public static final String FUNCTION_RES_PATH = DATA_PATH + "[" + FUNCTION_RES_INDEX + "]";
  private static final int FUNCTION_PARAMS_INDEX = 1;
  public static final String FUNCTION_PARAMS_PATH = DATA_PATH + "[" + FUNCTION_PARAMS_INDEX + "]";

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
  public ArrayTH array(TypeH elemType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArray(elemType));
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

  public TupleTH tuple(ImmutableList<TypeH> itemTypes) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(itemTypes));
  }

  public StringTH string() {
    return string;
  }

  public VarH var(String name) {
    checkArgument(isVarName(name), "Illegal type var name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVar(name));
  }

  // methods for getting Expr-s types

  public CallCH call(TypeH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evalType));
  }

  public CombineCH combine(TupleTH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCombine(evalType));
  }

  public OrderCH order(TypeH elemType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elemType));
  }

  public ParamRefCH ref(TypeH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evalType));
  }

  public SelectCH select(TypeH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evalType));
  }

  // methods for reading from db

  public CatH get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private CatH read(Hash hash) {
    List<Hash> rootSeq = readTypeRootSeq(hash);
    CatKindH kind = decodeTypeMarker(hash, rootSeq.get(0));
    return switch (kind) {
      case ANY, BLOB, BOOL, INT, NOTHING, STRING -> {
        assertTypeRootSeqSize(hash, kind, rootSeq, 1);
        throw new RuntimeException(
            "Internal error: Type with kind " + kind + " should be found in cache.");
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

  private List<Hash> readTypeRootSeq(Hash hash) {
    List<Hash> hashes = wrapHashedDbExceptionAsDecodeTypeException(
        hash, () -> hashedDb.readSeq(hash));
    int seqSize = hashes.size();
    if (seqSize != 1 && seqSize != 2) {
      throw new DecodeTypeRootExc(hash, seqSize);
    }
    return hashes;
  }

  private CatKindH decodeTypeMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExceptionAsDecodeTypeException(
        hash, () -> hashedDb.readByte(markerHash));
    CatKindH kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeTypeIllegalKindExc(hash, marker);
    }
    return kind;
  }

  private static void assertTypeRootSeqSize(
      Hash rootHash, CatKindH kind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeTypeRootExc(rootHash, kind, hashes.size(), expectedSize);
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

  private <T extends CatH> T readDataAsClass(Hash rootHash, List<Hash> rootSeq,
      CatKindH kind, Class<T> expectedTypeClass) {
    assertTypeRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash hash = rootSeq.get(DATA_INDEX);
    return readNode(kind, rootHash, hash, DATA_PATH, expectedTypeClass);
  }

  private CatH readFunc(Hash rootHash, List<Hash> rootSeq, CatKindH kind) {
    assertTypeRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash dataHash = rootSeq.get(DATA_INDEX);
    List<Hash> data = readSeqHashes(rootHash, dataHash, kind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedTypeSeqExc(rootHash, kind, DATA_PATH, 2, data.size());
    }
    TypeH result = readNode(kind, rootHash, data.get(FUNCTION_RES_INDEX), FUNCTION_RES_PATH, TypeH.class);
    TupleTH params = readNode(kind, rootHash, data.get(FUNCTION_PARAMS_INDEX),
        FUNCTION_PARAMS_PATH, TupleTH.class);
    return newFunc(rootHash, FuncKind.from(kind), result, params);
  }

  private TupleTH readTuple(Hash rootHash, List<Hash> rootSeq) {
    assertTypeRootSeqSize(rootHash, TUPLE, rootSeq, 2);
    var items = readTupleItems(rootHash, rootSeq.get(DATA_INDEX));
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
    assertTypeRootSeqSize(rootHash, VARIABLE, rootSeq, 2);
    String name = wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, VARIABLE, DATA_PATH, () ->hashedDb.readString(rootSeq.get(1)));
    if (!isVarName(name)) {
      throw new DecodeVarIllegalNameExc(rootHash, name);
    }
    return newVar(rootHash, name);
  }

  private <T> T readNode(CatKindH kind, Hash outerHash, Hash hash, String path, Class<T> clazz) {
    CatH result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        kind, outerHash, path, () -> get(hash));
    if (clazz.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new UnexpectedTypeNodeExc(outerHash, kind, path, clazz, result.getClass());
    }
  }

  private TypeH readNode(CatKindH kind, Hash outerHash, Hash hash, String path, int index) {
    CatH result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        kind, outerHash, path, index, () -> get(hash));
    if (result instanceof TypeH typeH) {
      return typeH;
    } else {
      throw new UnexpectedTypeNodeExc(outerHash, kind, path, index, TypeH.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayTH newArray(TypeH elemType) throws HashedDbExc {
    var rootHash = writeArrayRoot(elemType);
    return newArray(rootHash, elemType);
  }

  private ArrayTH newArray(Hash rootHash, TypeH elemType) {
    return cache(new ArrayTH(rootHash, elemType));
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

  private TupleTH newTuple(ImmutableList<TypeH> itemTypes) throws HashedDbExc {
    var hash = writeTupleRoot(itemTypes);
    return newTuple(hash, itemTypes);
  }

  private TupleTH newTuple(Hash rootHash, ImmutableList<TypeH> itemTypes) {
    return cache(new TupleTH(rootHash, itemTypes));
  }

  private VarH newVar(String name) throws HashedDbExc {
    var rootHash = writeVarRoot(name);
    return newVar(rootHash, name);
  }

  private VarH newVar(Hash rootHash, String name) {
    return cache(new VarH(rootHash, name));
  }

  // methods for creating Expr types

  private CallCH newCall(TypeH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(CALL, evalType);
    return newCall(rootHash, evalType);
  }

  private CallCH newCall(Hash rootHash, TypeH evalType) {
    return cache(new CallCH(rootHash, evalType));
  }

  private CombineCH newCombine(TupleTH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(COMBINE, evalType);
    return newCombine(rootHash, evalType);
  }

  private CombineCH newCombine(Hash rootHash, TupleTH evalType) {
    return cache(new CombineCH(rootHash, evalType));
  }

  private OrderCH newOrder(TypeH elemType) throws HashedDbExc {
    var evalType = array(elemType);
    var rootHash = writeExprRoot(ORDER, evalType);
    return newOrder(rootHash, evalType);
  }

  private OrderCH newOrder(Hash rootHash, ArrayTH evalType) {
    return cache(new OrderCH(rootHash, evalType));
  }

  private ParamRefCH newRef(TypeH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(PARAM_REF, evalType);
    return newRef(rootHash, evalType);
  }

  private ParamRefCH newRef(Hash rootHash, TypeH evalType) {
    return cache(new ParamRefCH(rootHash, evalType));
  }

  private SelectCH newSelect(TypeH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(SELECT, evalType);
    return newSelect(rootHash, evalType);
  }

  private SelectCH newSelect(Hash rootHash, TypeH evalType) {
    return cache(new SelectCH(rootHash, evalType));
  }

  private <T extends CatH> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing Val type root

  private Hash writeArrayRoot(CatH elemType) throws HashedDbExc {
    return writeNonBaseRoot(ARRAY, elemType.hash());
  }

  private Hash writeFuncRoot(FuncKind<?> kind, TypeH res, TupleTH params)
      throws HashedDbExc {
    var hash = hashedDb.writeSeq(res.hash(), params.hash());
    return writeNonBaseRoot(kind.kind(), hash);
  }

  private Hash writeTupleRoot(ImmutableList<TypeH> itemTypes) throws HashedDbExc {
    var itemsHash = hashedDb.writeSeq(Lists.map(itemTypes, CatH::hash));
    return writeNonBaseRoot(TUPLE, itemsHash);
  }

  private Hash writeVarRoot(String name) throws HashedDbExc {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprRoot(CatKindH kind, CatH evalType) throws HashedDbExc {
    return writeNonBaseRoot(kind, evalType.hash());
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
    return wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, kind, path, () -> hashedDb.readSeq(seqHash));
  }
}
