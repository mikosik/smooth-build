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
import static org.smoothbuild.db.object.type.base.SpecKindH.ANY;
import static org.smoothbuild.db.object.type.base.SpecKindH.ARRAY;
import static org.smoothbuild.db.object.type.base.SpecKindH.BLOB;
import static org.smoothbuild.db.object.type.base.SpecKindH.BOOL;
import static org.smoothbuild.db.object.type.base.SpecKindH.CALL;
import static org.smoothbuild.db.object.type.base.SpecKindH.COMBINE;
import static org.smoothbuild.db.object.type.base.SpecKindH.INT;
import static org.smoothbuild.db.object.type.base.SpecKindH.NOTHING;
import static org.smoothbuild.db.object.type.base.SpecKindH.ORDER;
import static org.smoothbuild.db.object.type.base.SpecKindH.PARAM_REF;
import static org.smoothbuild.db.object.type.base.SpecKindH.SELECT;
import static org.smoothbuild.db.object.type.base.SpecKindH.STRING;
import static org.smoothbuild.db.object.type.base.SpecKindH.TUPLE;
import static org.smoothbuild.db.object.type.base.SpecKindH.VARIABLE;
import static org.smoothbuild.db.object.type.base.SpecKindH.fromMarker;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.object.db.ObjDbExc;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.SpecKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.exc.DecodeTypeIllegalKindExc;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootExc;
import org.smoothbuild.db.object.type.exc.DecodeVarIllegalNameExc;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeNodeExc;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeSeqExc;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.CombineTypeH;
import org.smoothbuild.db.object.type.expr.OrderTypeH;
import org.smoothbuild.db.object.type.expr.ParamRefTypeH;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.AbstFuncTypeH;
import org.smoothbuild.db.object.type.val.AnyTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.DefFuncTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.IfFuncTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.MapFuncTypeH;
import org.smoothbuild.db.object.type.val.NatFuncTypeH;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.db.object.type.val.VarH;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class TypeDb implements TypeFactoryH {
  public static final String DATA_PATH = "data";
  private static final int DATA_INDEX = 1;
  private static final int FUNCTION_RES_INDEX = 0;
  public static final String FUNCTION_RES_PATH = DATA_PATH + "[" + FUNCTION_RES_INDEX + "]";
  private static final int FUNCTION_PARAMS_INDEX = 1;
  public static final String FUNCTION_PARAMS_PATH = DATA_PATH + "[" + FUNCTION_PARAMS_INDEX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, SpecH> cache;

  private final AnyTypeH any;
  private final BlobTypeH blob;
  private final BoolTypeH bool;
  private final IntTypeH int_;
  private final NothingTypeH nothing;
  private final StringTypeH string;
  private final IfFuncTypeH ifFunc;
  private final MapFuncTypeH mapFunc;
  private final Sides<TypeH> sides;

  public TypeDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.any = cache(new AnyTypeH(writeBaseRoot(ANY)));
      this.blob = cache(new BlobTypeH(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolTypeH(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntTypeH(writeBaseRoot(INT)));
      this.nothing = cache(new NothingTypeH(writeBaseRoot(NOTHING)));
      this.string = cache(new StringTypeH(writeBaseRoot(STRING)));

      this.ifFunc = createIfFunc();
      this.mapFunc = createMapFunc();
    } catch (HashedDbExc e) {
      throw new ObjDbExc(e);
    }
    this.sides = new Sides<>(this.any, this.nothing);
  }

  private IfFuncTypeH createIfFunc() {
    VarH a = cache(var("A"));
    return cache(func(IF_KIND, a, list(bool, a, a)));
  }

  private MapFuncTypeH createMapFunc() {
    VarH a = cache(var("A"));
    VarH r = cache(var("B"));
    ArrayTypeH ar = cache(array(r));
    ArrayTypeH aa = cache(array(a));
    FuncTypeH f = cache(func(r, list(a)));
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

  public AnyTypeH any() {
    return any;
  }

  @Override
  public ArrayTypeH array(TypeH elemType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArray(elemType));
  }

  public BlobTypeH blob() {
    return blob;
  }

  public BoolTypeH bool() {
    return bool;
  }

  public DefFuncTypeH defFunc(TypeH res, ImmutableList<TypeH> params) {
    return func(DEFINED_KIND, res, params);
  }

  @Override
  public AbstFuncTypeH func(TypeH res, ImmutableList<TypeH> params) {
    return func(ABSTRACT_KIND, res, params);
  }

  private <T extends FuncTypeH> T func(FuncKind<T> kind, TypeH res, ImmutableList<TypeH> params) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunc(kind, res, tuple(params)));
  }

  public IfFuncTypeH ifFunc() {
    return ifFunc;
  }

  public IntTypeH int_() {
    return int_;
  }

  public MapFuncTypeH mapFunc() {
    return mapFunc;
  }

  public NatFuncTypeH natFunc(TypeH res, ImmutableList<TypeH> params) {
    return func(NATIVE_KIND, res, params);
  }

  public NothingTypeH nothing() {
    return nothing;
  }

  public TupleTypeH tuple(ImmutableList<TypeH> itemTypes) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(itemTypes));
  }

  public StringTypeH string() {
    return string;
  }

  public VarH var(String name) {
    checkArgument(isVarName(name), "Illegal type var name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVar(name));
  }

  // methods for getting Expr-s types

  public CallTypeH call(TypeH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evalType));
  }

  public CombineTypeH combine(TupleTypeH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCombine(evalType));
  }

  public OrderTypeH order(TypeH elemType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elemType));
  }

  public ParamRefTypeH ref(TypeH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evalType));
  }

  public SelectTypeH select(TypeH evalType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evalType));
  }

  // methods for reading from db

  public SpecH get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private SpecH read(Hash hash) {
    List<Hash> rootSeq = readTypeRootSeq(hash);
    SpecKindH kind = decodeTypeMarker(hash, rootSeq.get(0));
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

  private SpecKindH decodeTypeMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExceptionAsDecodeTypeException(
        hash, () -> hashedDb.readByte(markerHash));
    SpecKindH kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeTypeIllegalKindExc(hash, marker);
    }
    return kind;
  }

  private static void assertTypeRootSeqSize(
      Hash rootHash, SpecKindH kind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeTypeRootExc(rootHash, kind, hashes.size(), expectedSize);
    }
  }

  private TypeH readDataAsVal(Hash rootHash, List<Hash> rootSeq, SpecKindH kind) {
    return readDataAsClass(rootHash, rootSeq, kind, TypeH.class);
  }

  private ArrayTypeH readDataAsArray(Hash rootHash, List<Hash> rootSeq, SpecKindH kind) {
    return readDataAsClass(rootHash, rootSeq, kind, ArrayTypeH.class);
  }

  private TupleTypeH readDataAsTuple(Hash rootHash, List<Hash> rootSeq, SpecKindH kind) {
    return readDataAsClass(rootHash, rootSeq, kind, TupleTypeH.class);
  }

  private <T extends SpecH> T readDataAsClass(Hash rootHash, List<Hash> rootSeq,
      SpecKindH kind, Class<T> expectedTypeClass) {
    assertTypeRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash hash = rootSeq.get(DATA_INDEX);
    return readNode(kind, rootHash, hash, DATA_PATH, expectedTypeClass);
  }

  private SpecH readFunc(Hash rootHash, List<Hash> rootSeq, SpecKindH kind) {
    assertTypeRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash dataHash = rootSeq.get(DATA_INDEX);
    List<Hash> data = readSeqHashes(rootHash, dataHash, kind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedTypeSeqExc(rootHash, kind, DATA_PATH, 2, data.size());
    }
    TypeH result = readNode(kind, rootHash, data.get(FUNCTION_RES_INDEX), FUNCTION_RES_PATH, TypeH.class);
    TupleTypeH params = readNode(kind, rootHash, data.get(FUNCTION_PARAMS_INDEX),
        FUNCTION_PARAMS_PATH, TupleTypeH.class);
    return newFunc(rootHash, FuncKind.from(kind), result, params);
  }

  private TupleTypeH readTuple(Hash rootHash, List<Hash> rootSeq) {
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

  private <T> T readNode(SpecKindH kind, Hash outerHash, Hash hash, String path, Class<T> clazz) {
    SpecH result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        kind, outerHash, path, () -> get(hash));
    if (clazz.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new UnexpectedTypeNodeExc(outerHash, kind, path, clazz, result.getClass());
    }
  }

  private TypeH readNode(SpecKindH kind, Hash outerHash, Hash hash, String path, int index) {
    SpecH result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        kind, outerHash, path, index, () -> get(hash));
    if (result instanceof TypeH typeH) {
      return typeH;
    } else {
      throw new UnexpectedTypeNodeExc(outerHash, kind, path, index, TypeH.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayTypeH newArray(TypeH elemType) throws HashedDbExc {
    var rootHash = writeArrayRoot(elemType);
    return newArray(rootHash, elemType);
  }

  private ArrayTypeH newArray(Hash rootHash, TypeH elemType) {
    return cache(new ArrayTypeH(rootHash, elemType));
  }

  private <T extends FuncTypeH> T newFunc(
      FuncKind<T> kind, TypeH res, TupleTypeH params) throws HashedDbExc {
    var rootHash = writeFuncRoot(kind, res, params);
    return newFunc(rootHash, kind, res, params);
  }

  private <T extends FuncTypeH> T newFunc(
      Hash rootHash, FuncKind<T> kind, TypeH res, TupleTypeH params) {
    return cache(kind.newInstance(rootHash, res, params));
  }

  private TupleTypeH newTuple(ImmutableList<TypeH> itemTypes) throws HashedDbExc {
    var hash = writeTupleRoot(itemTypes);
    return newTuple(hash, itemTypes);
  }

  private TupleTypeH newTuple(Hash rootHash, ImmutableList<TypeH> itemTypes) {
    return cache(new TupleTypeH(rootHash, itemTypes));
  }

  private VarH newVar(String name) throws HashedDbExc {
    var rootHash = writeVarRoot(name);
    return newVar(rootHash, name);
  }

  private VarH newVar(Hash rootHash, String name) {
    return cache(new VarH(rootHash, name));
  }

  // methods for creating Expr types

  private CallTypeH newCall(TypeH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(CALL, evalType);
    return newCall(rootHash, evalType);
  }

  private CallTypeH newCall(Hash rootHash, TypeH evalType) {
    return cache(new CallTypeH(rootHash, evalType));
  }

  private CombineTypeH newCombine(TupleTypeH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(COMBINE, evalType);
    return newCombine(rootHash, evalType);
  }

  private CombineTypeH newCombine(Hash rootHash, TupleTypeH evalType) {
    return cache(new CombineTypeH(rootHash, evalType));
  }

  private OrderTypeH newOrder(TypeH elemType) throws HashedDbExc {
    var evalType = array(elemType);
    var rootHash = writeExprRoot(ORDER, evalType);
    return newOrder(rootHash, evalType);
  }

  private OrderTypeH newOrder(Hash rootHash, ArrayTypeH evalType) {
    return cache(new OrderTypeH(rootHash, evalType));
  }

  private ParamRefTypeH newRef(TypeH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(PARAM_REF, evalType);
    return newRef(rootHash, evalType);
  }

  private ParamRefTypeH newRef(Hash rootHash, TypeH evalType) {
    return cache(new ParamRefTypeH(rootHash, evalType));
  }

  private SelectTypeH newSelect(TypeH evalType) throws HashedDbExc {
    var rootHash = writeExprRoot(SELECT, evalType);
    return newSelect(rootHash, evalType);
  }

  private SelectTypeH newSelect(Hash rootHash, TypeH evalType) {
    return cache(new SelectTypeH(rootHash, evalType));
  }

  private <T extends SpecH> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing Val type root

  private Hash writeArrayRoot(SpecH elemType) throws HashedDbExc {
    return writeNonBaseRoot(ARRAY, elemType.hash());
  }

  private Hash writeFuncRoot(FuncKind<?> kind, TypeH res, TupleTypeH params)
      throws HashedDbExc {
    var hash = hashedDb.writeSeq(res.hash(), params.hash());
    return writeNonBaseRoot(kind.kind(), hash);
  }

  private Hash writeTupleRoot(ImmutableList<TypeH> itemTypes) throws HashedDbExc {
    var itemsHash = hashedDb.writeSeq(Lists.map(itemTypes, SpecH::hash));
    return writeNonBaseRoot(TUPLE, itemsHash);
  }

  private Hash writeVarRoot(String name) throws HashedDbExc {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprRoot(SpecKindH kind, SpecH evalType) throws HashedDbExc {
    return writeNonBaseRoot(kind, evalType.hash());
  }

  private Hash writeNonBaseRoot(SpecKindH kind, Hash dataHash) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()), dataHash);
  }

  private Hash writeBaseRoot(SpecKindH kind) throws HashedDbExc {
    return hashedDb.writeSeq(hashedDb.writeByte(kind.marker()));
  }

  // Helper methods for reading

  private ImmutableList<Hash> readSeqHashes(
      Hash rootHash, Hash seqHash, SpecKindH kind, String path) {
    return wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, kind, path, () -> hashedDb.readSeq(seqHash));
  }
}
