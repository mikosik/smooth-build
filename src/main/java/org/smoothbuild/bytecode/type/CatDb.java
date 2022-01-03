package org.smoothbuild.bytecode.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.bytecode.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.bytecode.type.Helpers.wrapCatDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatExc;
import static org.smoothbuild.bytecode.type.Helpers.wrapHashedDbExcAsDecodeCatNodeExc;
import static org.smoothbuild.bytecode.type.base.CatKindB.ANY;
import static org.smoothbuild.bytecode.type.base.CatKindB.ARRAY;
import static org.smoothbuild.bytecode.type.base.CatKindB.BLOB;
import static org.smoothbuild.bytecode.type.base.CatKindB.BOOL;
import static org.smoothbuild.bytecode.type.base.CatKindB.CALL;
import static org.smoothbuild.bytecode.type.base.CatKindB.COMBINE;
import static org.smoothbuild.bytecode.type.base.CatKindB.FUNC;
import static org.smoothbuild.bytecode.type.base.CatKindB.IF;
import static org.smoothbuild.bytecode.type.base.CatKindB.INT;
import static org.smoothbuild.bytecode.type.base.CatKindB.INVOKE;
import static org.smoothbuild.bytecode.type.base.CatKindB.MAP;
import static org.smoothbuild.bytecode.type.base.CatKindB.METHOD;
import static org.smoothbuild.bytecode.type.base.CatKindB.NOTHING;
import static org.smoothbuild.bytecode.type.base.CatKindB.ORDER;
import static org.smoothbuild.bytecode.type.base.CatKindB.PARAM_REF;
import static org.smoothbuild.bytecode.type.base.CatKindB.SELECT;
import static org.smoothbuild.bytecode.type.base.CatKindB.STRING;
import static org.smoothbuild.bytecode.type.base.CatKindB.TUPLE;
import static org.smoothbuild.bytecode.type.base.CatKindB.VARIABLE;
import static org.smoothbuild.bytecode.type.base.CatKindB.fromMarker;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVarName;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.bytecode.type.base.CatKindB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.exc.CatDbExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatIllegalKindExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatRootExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatWrongNodeCatExc;
import org.smoothbuild.bytecode.type.exc.DecodeCatWrongSeqSizeExc;
import org.smoothbuild.bytecode.type.exc.DecodeVarIllegalNameExc;
import org.smoothbuild.bytecode.type.expr.CallCB;
import org.smoothbuild.bytecode.type.expr.CombineCB;
import org.smoothbuild.bytecode.type.expr.IfCB;
import org.smoothbuild.bytecode.type.expr.InvokeCB;
import org.smoothbuild.bytecode.type.expr.MapCB;
import org.smoothbuild.bytecode.type.expr.OrderCB;
import org.smoothbuild.bytecode.type.expr.ParamRefCB;
import org.smoothbuild.bytecode.type.expr.SelectCB;
import org.smoothbuild.bytecode.type.val.AnyTB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BlobTB;
import org.smoothbuild.bytecode.type.val.BoolTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.StringTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarTB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.db.HashedDb;
import org.smoothbuild.db.exc.HashedDbExc;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.util.TriFunction;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class CatDb implements TypeFactoryB {
  public static final String DATA_PATH = "data";
  private static final int DATA_IDX = 1;
  private static final int CALLABLE_RES_IDX = 0;
  public static final String CALLABLE_RES_PATH = DATA_PATH + "[" + CALLABLE_RES_IDX + "]";
  private static final int CALLABLE_PARAMS_IDX = 1;
  public static final String CALLABLE_PARAMS_PATH = DATA_PATH + "[" + CALLABLE_PARAMS_IDX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, CatB> cache;

  private final AnyTB any;
  private final BlobTB blob;
  private final BoolTB bool;
  private final IntTB int_;
  private final NothingTB nothing;
  private final StringTB string;
  private final Sides<TypeB> sides;

  public CatDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.any = cache(new AnyTB(writeBaseRoot(ANY)));
      this.blob = cache(new BlobTB(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolTB(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntTB(writeBaseRoot(INT)));
      this.nothing = cache(new NothingTB(writeBaseRoot(NOTHING)));
      this.string = cache(new StringTB(writeBaseRoot(STRING)));
    } catch (HashedDbExc e) {
      throw new CatDbExc(e);
    }
    this.sides = new Sides<>(this.any, this.nothing);
  }

  public ImmutableList<TypeB> baseTs() {
    return ImmutableList.of(any, blob, bool, int_, nothing, string);
  }

  @Override
  public Bounds<TypeB> unbounded() {
    return new Bounds<>(nothing(), any());
  }

  @Override
  public Bounds<TypeB> oneSideBound(Side<TypeB> side, TypeB type) {
    return switch (side) {
      case Sides.Lower l -> new Bounds<>(type, any());
      case Sides.Upper u -> new Bounds<>(nothing(), type);
    };
  }

  @Override
  public Side<TypeB> upper() {
    return sides.upper();
  }

  @Override
  public Side<TypeB> lower() {
    return sides.lower();
  }

  // methods for getting Val-s types

  public AnyTB any() {
    return any;
  }

  @Override
  public ArrayTB array(TypeB elemT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArray(elemT));
  }

  public BlobTB blob() {
    return blob;
  }

  public BoolTB bool() {
    return bool;
  }

  @Override
  public FuncTB func(TypeB res, ImmutableList<TypeB> params) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunc(res, tuple(params)));
  }

  public IntTB int_() {
    return int_;
  }

  public MethodTB method(TypeB res, ImmutableList<TypeB> params) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newMethod(res, tuple(params)));
  }

  public NothingTB nothing() {
    return nothing;
  }

  @Override
  public TupleTB tuple(ImmutableList<TypeB> itemTs) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(itemTs));
  }

  public StringTB string() {
    return string;
  }

  public VarTB var(String name) {
    checkArgument(isVarName(name), "Illegal type var name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVar(name));
  }

  // methods for getting Expr-s types

  public CallCB call(TypeB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evalT));
  }

  public CombineCB combine(TupleTB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCombine(evalT));
  }

  public IfCB if_(TypeB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIf(evalT));
  }

  public InvokeCB invoke(TypeB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvoke(evalT));
  }

  public MapCB map(ArrayTB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newMap(evalT));
  }

  public OrderCB order(TypeB elemT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elemT));
  }

  public ParamRefCB ref(TypeB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evalT));
  }

  public SelectCB select(TypeB evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evalT));
  }

  // methods for reading from db

  public CatB get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private CatB read(Hash hash) {
    List<Hash> rootSeq = readCatRootSeq(hash);
    CatKindB kind = decodeCatMarker(hash, rootSeq.get(0));
    return switch (kind) {
      case ANY, BLOB, BOOL, INT, NOTHING, STRING -> {
        assertCatRootSeqSize(hash, kind, rootSeq, 1);
        throw new RuntimeException(
            "Internal error: Category with kind " + kind + " should be found in cache.");
      }
      case ARRAY -> newArray(hash, readDataAsValT(hash, rootSeq, kind));
      case CALL -> newCall(hash, readDataAsValT(hash, rootSeq, kind));
      case COMBINE -> newCombine(hash, readDataAsTupleT(hash, rootSeq, kind));
      case FUNC -> readFunc(hash, rootSeq, kind);
      case IF -> newIf(hash, readDataAsValT(hash, rootSeq, kind));
      case INVOKE -> newInvoke(hash, readDataAsValT(hash, rootSeq, kind));
      case MAP -> newMap(hash, readDataAsArrayT(hash, rootSeq, kind));
      case METHOD -> readMethod(hash, rootSeq, kind);
      case ORDER -> newOrder(hash, readDataAsArrayT(hash, rootSeq, kind));
      case PARAM_REF -> newRef(hash, readDataAsValT(hash, rootSeq, kind));
      case SELECT -> newSelect(hash, readDataAsValT(hash, rootSeq, kind));
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

  private CatB readFunc(Hash rootHash, List<Hash> rootSeq, CatKindB kind) {
    return readCallable(rootHash, rootSeq, kind, this::newFunc);
  }

  private CatB readMethod(Hash rootHash, List<Hash> rootSeq, CatKindB kind) {
    return readCallable(rootHash, rootSeq, kind, this::newMethod);
  }

  private CatB readCallable(Hash rootHash, List<Hash> rootSeq, CatKindB kind,
      TriFunction<Hash, TypeB, TupleTB, CatB> instantiator) {
    assertCatRootSeqSize(rootHash, kind, rootSeq, 2);
    Hash dataHash = rootSeq.get(DATA_IDX);
    List<Hash> data = readSeqHashes(rootHash, dataHash, kind, DATA_PATH);
    if (data.size() != 2) {
      throw new DecodeCatWrongSeqSizeExc(rootHash, kind, DATA_PATH, 2, data.size());
    }
    var res = readNode(kind, rootHash, data.get(CALLABLE_RES_IDX), CALLABLE_RES_PATH, TypeB.class);
    var params = readNode(kind, rootHash, data.get(CALLABLE_PARAMS_IDX), CALLABLE_PARAMS_PATH, TupleTB.class);
    return instantiator.apply(rootHash, res, params);
  }

  private TupleTB readTuple(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, TUPLE, rootSeq, 2);
    var items = readTupleItems(rootHash, rootSeq.get(DATA_IDX));
    return newTuple(rootHash, items);
  }

  private ImmutableList<TypeB> readTupleItems(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<TypeB>builder();
    var itemTypeHashes = readSeqHashes(rootHash, hash, TUPLE, DATA_PATH);
    for (int i = 0; i < itemTypeHashes.size(); i++) {
      builder.add(readNode(TUPLE, rootHash, itemTypeHashes.get(i), DATA_PATH, i));
    }
    return builder.build();
  }

  private VarTB readVar(Hash rootHash, List<Hash> rootSeq) {
    assertCatRootSeqSize(rootHash, VARIABLE, rootSeq, 2);
    var name = wrapHashedDbExcAsDecodeCatNodeExc(
        rootHash, VARIABLE, DATA_PATH, () ->hashedDb.readString(rootSeq.get(1)));
    if (!isVarName(name)) {
      throw new DecodeVarIllegalNameExc(rootHash, name);
    }
    return newVar(rootHash, name);
  }

  private <T> T readNode(CatKindB kind, Hash outerHash, Hash hash, String path, Class<T> clazz) {
    CatB result = wrapCatDbExcAsDecodeCatNodeExc(kind, outerHash, path, () -> get(hash));
    if (clazz.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new DecodeCatWrongNodeCatExc(outerHash, kind, path, clazz, result.getClass());
    }
  }

  private TypeB readNode(CatKindB kind, Hash outerHash, Hash hash, String path, int index) {
    CatB result = wrapCatDbExcAsDecodeCatNodeExc(kind, outerHash, path, index, () -> get(hash));
    if (result instanceof TypeB typeB) {
      return typeB;
    } else {
      throw new DecodeCatWrongNodeCatExc(outerHash, kind, path, index, TypeB.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayTB newArray(TypeB elemT) throws HashedDbExc {
    var rootHash = writeArrayRoot(elemT);
    return newArray(rootHash, elemT);
  }

  private ArrayTB newArray(Hash rootHash, TypeB elemT) {
    return cache(new ArrayTB(rootHash, elemT));
  }

  private FuncTB newFunc(TypeB res, TupleTB params) throws HashedDbExc {
    var rootHash = writeFuncLikeRoot(res, params, FUNC);
    return newFunc(rootHash, res, params);
  }

  private FuncTB newFunc(Hash rootHash, TypeB res, TupleTB params) {
    return cache(new FuncTB(rootHash, res, params));
  }

  private MethodTB newMethod(TypeB res, TupleTB params) throws HashedDbExc {
    var rootHash = writeFuncLikeRoot(res, params, METHOD);
    return newMethod(rootHash, res, params);
  }

  private MethodTB newMethod(Hash rootHash, TypeB res, TupleTB params) {
    return cache(new MethodTB(rootHash, res, params));
  }

  private TupleTB newTuple(ImmutableList<TypeB> itemTs) throws HashedDbExc {
    var hash = writeTupleRoot(itemTs);
    return newTuple(hash, itemTs);
  }

  private TupleTB newTuple(Hash rootHash, ImmutableList<TypeB> itemTs) {
    return cache(new TupleTB(rootHash, itemTs));
  }

  private VarTB newVar(String name) throws HashedDbExc {
    var rootHash = writeVarRoot(name);
    return newVar(rootHash, name);
  }

  private VarTB newVar(Hash rootHash, String name) {
    return cache(new VarTB(rootHash, name));
  }

  // methods for creating Expr types

  private CallCB newCall(TypeB evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(CALL, evalT);
    return newCall(rootHash, evalT);
  }

  private CallCB newCall(Hash rootHash, TypeB evalT) {
    return cache(new CallCB(rootHash, evalT));
  }

  private CombineCB newCombine(TupleTB evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(COMBINE, evalT);
    return newCombine(rootHash, evalT);
  }

  private CombineCB newCombine(Hash rootHash, TupleTB evalT) {
    return cache(new CombineCB(rootHash, evalT));
  }

  private IfCB newIf(TypeB evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(IF, evalT);
    return newIf(rootHash, evalT);
  }

  private IfCB newIf(Hash rootHash, TypeB evalT) {
    return cache(new IfCB(rootHash, evalT));
  }

  private InvokeCB newInvoke(TypeB evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(INVOKE, evalT);
    return newInvoke(rootHash, evalT);
  }

  private InvokeCB newInvoke(Hash rootHash, TypeB evalT) {
    return cache(new InvokeCB(rootHash, evalT));
  }

  private MapCB newMap(ArrayTB evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(MAP, evalT);
    return newMap(rootHash, evalT);
  }

  private MapCB newMap(Hash rootHash, ArrayTB evalT) {
    return cache(new MapCB(rootHash, evalT));
  }

  private OrderCB newOrder(TypeB elemT) throws HashedDbExc {
    var evalT = array(elemT);
    var rootHash = writeExprRoot(ORDER, evalT);
    return newOrder(rootHash, evalT);
  }

  private OrderCB newOrder(Hash rootHash, ArrayTB evalT) {
    return cache(new OrderCB(rootHash, evalT));
  }

  private ParamRefCB newRef(TypeB evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(PARAM_REF, evalT);
    return newRef(rootHash, evalT);
  }

  private ParamRefCB newRef(Hash rootHash, TypeB evalT) {
    return cache(new ParamRefCB(rootHash, evalT));
  }

  private SelectCB newSelect(TypeB evalT) throws HashedDbExc {
    var rootHash = writeExprRoot(SELECT, evalT);
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

  private Hash writeArrayRoot(CatB elemT) throws HashedDbExc {
    return writeNonBaseRoot(ARRAY, elemT.hash());
  }

  private Hash writeFuncLikeRoot(TypeB res, TupleTB params, CatKindB kind) throws HashedDbExc {
    var hash = hashedDb.writeSeq(res.hash(), params.hash());
    return writeNonBaseRoot(kind, hash);
  }

  private Hash writeTupleRoot(ImmutableList<TypeB> itemTs) throws HashedDbExc {
    var itemsHash = hashedDb.writeSeq(Lists.map(itemTs, CatB::hash));
    return writeNonBaseRoot(TUPLE, itemsHash);
  }

  private Hash writeVarRoot(String name) throws HashedDbExc {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprRoot(CatKindB kind, CatB evalT) throws HashedDbExc {
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
