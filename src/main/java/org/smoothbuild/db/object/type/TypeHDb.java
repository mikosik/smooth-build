package org.smoothbuild.db.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExceptionAsDecodeTypeException;
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExceptionAsDecodeTypeNodeException;
import static org.smoothbuild.db.object.type.Helpers.wrapObjectDbExceptionAsDecodeTypeNodeException;
import static org.smoothbuild.db.object.type.base.TypeKindH.ANY;
import static org.smoothbuild.db.object.type.base.TypeKindH.ARRAY;
import static org.smoothbuild.db.object.type.base.TypeKindH.BLOB;
import static org.smoothbuild.db.object.type.base.TypeKindH.BOOL;
import static org.smoothbuild.db.object.type.base.TypeKindH.CALL;
import static org.smoothbuild.db.object.type.base.TypeKindH.CONST;
import static org.smoothbuild.db.object.type.base.TypeKindH.CONSTRUCT;
import static org.smoothbuild.db.object.type.base.TypeKindH.FUNCTION;
import static org.smoothbuild.db.object.type.base.TypeKindH.IF;
import static org.smoothbuild.db.object.type.base.TypeKindH.INT;
import static org.smoothbuild.db.object.type.base.TypeKindH.INVOKE;
import static org.smoothbuild.db.object.type.base.TypeKindH.MAP;
import static org.smoothbuild.db.object.type.base.TypeKindH.NATIVE_METHOD;
import static org.smoothbuild.db.object.type.base.TypeKindH.NOTHING;
import static org.smoothbuild.db.object.type.base.TypeKindH.ORDER;
import static org.smoothbuild.db.object.type.base.TypeKindH.REF;
import static org.smoothbuild.db.object.type.base.TypeKindH.SELECT;
import static org.smoothbuild.db.object.type.base.TypeKindH.STRING;
import static org.smoothbuild.db.object.type.base.TypeKindH.TUPLE;
import static org.smoothbuild.db.object.type.base.TypeKindH.VARIABLE;
import static org.smoothbuild.db.object.type.base.TypeKindH.fromMarker;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.db.ObjectHDbException;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.base.TypeKindH;
import org.smoothbuild.db.object.type.exc.DecodeTypeIllegalKindException;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootException;
import org.smoothbuild.db.object.type.exc.DecodeVariableIllegalNameException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeNodeException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeSequenceException;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.ConstTypeH;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.expr.IfTypeH;
import org.smoothbuild.db.object.type.expr.InvokeTypeH;
import org.smoothbuild.db.object.type.expr.MapTypeH;
import org.smoothbuild.db.object.type.expr.OrderTypeH;
import org.smoothbuild.db.object.type.expr.RefTypeH;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.AnyTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.NativeMethodTypeH;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.db.object.type.val.VariableH;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class TypeHDb implements TypeFactory<TypeHV> {
  public static final String DATA_PATH = "data";
  private static final int DATA_INDEX = 1;
  private static final int FUNCTION_RESULT_INDEX = 0;
  public static final String FUNCTION_RESULT_PATH = DATA_PATH + "[" + FUNCTION_RESULT_INDEX + "]";
  private static final int FUNCTION_PARAMS_INDEX = 1;
  public static final String FUNCTION_PARAMS_PATH = DATA_PATH + "[" + FUNCTION_PARAMS_INDEX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, TypeH> cache;

  private final AnyTypeH any;
  private final BlobTypeH blob;
  private final BoolTypeH bool;
  private final IntTypeH int_;
  private final NothingTypeH nothing;
  private final StringTypeH string;
  private final NativeMethodTypeH nativeMethod;
  private final Sides<TypeHV> sides;

  public TypeHDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.any = cache(new AnyTypeH(writeBaseRoot(ANY)));
      this.blob = cache(new BlobTypeH(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolTypeH(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntTypeH(writeBaseRoot(INT)));
      this.nothing = cache(new NothingTypeH(writeBaseRoot(NOTHING)));
      this.string = cache(new StringTypeH(writeBaseRoot(STRING)));

      // expr
      this.nativeMethod = cache(new NativeMethodTypeH(writeBaseRoot(NATIVE_METHOD)));
    } catch (HashedDbException e) {
      throw new ObjectHDbException(e);
    }
    this.sides = new Sides<>(this.any, this.nothing);
  }

  @Override
  public Bounds<TypeHV> unbounded() {
    return new Bounds<>(nothing(), any());
  }

  @Override
  public Bounds<TypeHV> oneSideBound(Side<TypeHV> side, TypeHV type) {
    return switch (side) {
      case Sides.Lower l -> new Bounds<>(type, any());
      case Sides.Upper u -> new Bounds<>(nothing(), type);
    };
  }

  @Override
  public Side<TypeHV> upper() {
    return sides.upper();
  }

  @Override
  public Side<TypeHV> lower() {
    return sides.lower();
  }

  // methods for getting Val-s types

  public AnyTypeH any() {
    return any;
  }

  @Override
  public ArrayTypeH array(TypeHV elementType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArray(elementType));
  }

  public BlobTypeH blob() {
    return blob;
  }

  public BoolTypeH bool() {
    return bool;
  }

  @Override
  public FunctionTypeH function(TypeHV result, ImmutableList<TypeHV> parameters) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunction(result, tuple(parameters)));
  }

  public IntTypeH int_() {
    return int_;
  }

  public NativeMethodTypeH nativeMethod() {
    return nativeMethod;
  }

  public NothingTypeH nothing() {
    return nothing;
  }

  public TupleTypeH tuple(ImmutableList<TypeHV> itemTypes) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(itemTypes));
  }

  public StringTypeH string() {
    return string;
  }

  public VariableH variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVariable(name));
  }

  // methods for getting Expr-s types

  public CallTypeH call(TypeHV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evaluationType));
  }

  public ConstTypeH const_(TypeHV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConst(evaluationType));
  }

  public ConstructTypeH construct(TupleTypeH evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstruct(evaluationType));
  }

  public IfTypeH if_(TypeHV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIf(evaluationType));
  }

  public InvokeTypeH invoke(TypeHV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvoke(evaluationType));
  }

  public MapTypeH map(ArrayTypeH evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newMap(evaluationType));
  }

  public OrderTypeH order(TypeHV elementType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elementType));
  }

  public RefTypeH ref(TypeHV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evaluationType));
  }

  public SelectTypeH select(TypeHV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evaluationType));
  }

  // methods for reading from db

  public TypeH get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private TypeH read(Hash hash) {
    List<Hash> rootSequence = readTypeRootSequence(hash);
    TypeKindH kind = decodeTypeMarker(hash, rootSequence.get(0));
    return switch (kind) {
      case ANY, BLOB, BOOL, INT, NATIVE_METHOD, NOTHING, STRING -> {
        assertTypeRootSequenceSize(hash, kind, rootSequence, 1);
        throw new RuntimeException(
            "Internal error: Type with kind " + kind + " should be found in cache.");
      }
      case ARRAY -> newArray(hash, readDataAsValue(hash, rootSequence, kind));
      case CALL -> newCall(hash, readDataAsValue(hash, rootSequence, kind));
      case CONST -> newConst(hash, readDataAsValue(hash, rootSequence, kind));
      case CONSTRUCT -> newConstruct(hash, readDataAsTuple(hash, rootSequence, kind));
      case FUNCTION -> readFunction(hash, rootSequence, kind);
      case IF -> newIf(hash, readDataAsValue(hash, rootSequence, kind));
      case INVOKE -> newInvoke(hash, readDataAsValue(hash, rootSequence, kind));
      case MAP -> newMap(hash, readDataAsArray(hash, rootSequence, kind));
      case ORDER -> newOrder(hash, readDataAsArray(hash, rootSequence, kind));
      case REF -> newRef(hash, readDataAsValue(hash, rootSequence, kind));
      case SELECT -> newSelect(hash, readDataAsValue(hash, rootSequence, kind));
      case TUPLE -> readTuple(hash, rootSequence);
      case VARIABLE -> readVariable(hash, rootSequence);
    };
  }

  private List<Hash> readTypeRootSequence(Hash hash) {
    List<Hash> hashes = wrapHashedDbExceptionAsDecodeTypeException(
        hash, () -> hashedDb.readSequence(hash));
    int sequenceSize = hashes.size();
    if (sequenceSize != 1 && sequenceSize != 2) {
      throw new DecodeTypeRootException(hash, sequenceSize);
    }
    return hashes;
  }

  private TypeKindH decodeTypeMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExceptionAsDecodeTypeException(
        hash, () -> hashedDb.readByte(markerHash));
    TypeKindH kind = fromMarker(marker);
    if (kind == null) {
      throw new DecodeTypeIllegalKindException(hash, marker);
    }
    return kind;
  }

  private static void assertTypeRootSequenceSize(
      Hash rootHash, TypeKindH kind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeTypeRootException(rootHash, kind, hashes.size(), expectedSize);
    }
  }

  private TypeHV readDataAsValue(Hash rootHash, List<Hash> rootSequence, TypeKindH kind) {
    return readDataAsClass(rootHash, rootSequence, kind, TypeHV.class);
  }

  private ArrayTypeH readDataAsArray(Hash rootHash, List<Hash> rootSequence, TypeKindH kind) {
    return readDataAsClass(rootHash, rootSequence, kind, ArrayTypeH.class);
  }

  private TupleTypeH readDataAsTuple(Hash rootHash, List<Hash> rootSequence, TypeKindH kind) {
    return readDataAsClass(rootHash, rootSequence, kind, TupleTypeH.class);
  }

  private <T extends TypeH> T readDataAsClass(Hash rootHash, List<Hash> rootSequence,
      TypeKindH kind, Class<T> expectedTypeClass) {
    assertTypeRootSequenceSize(rootHash, kind, rootSequence, 2);
    Hash hash = rootSequence.get(DATA_INDEX);
    return readTupleItemType(kind, rootHash, hash, DATA_PATH, expectedTypeClass);
  }

  private TypeH readFunction(Hash rootHash, List<Hash> rootSequence, TypeKindH kind) {
    assertTypeRootSequenceSize(rootHash, kind, rootSequence, 2);
    Hash dataHash = rootSequence.get(DATA_INDEX);
    List<Hash> data = readSequenceHashes(rootHash, dataHash, kind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedTypeSequenceException(rootHash, kind, DATA_PATH, 2, data.size());
    }
    TypeHV result = readTupleItemType(kind, rootHash, data.get(FUNCTION_RESULT_INDEX),
        FUNCTION_RESULT_PATH, TypeHV.class);
    TupleTypeH parameters = readTupleItemType(kind, rootHash, data.get(FUNCTION_PARAMS_INDEX),
        FUNCTION_PARAMS_PATH, TupleTypeH.class);
    return newFunction(rootHash, result, parameters);
  }

  private TupleTypeH readTuple(Hash rootHash, List<Hash> rootSequence) {
    assertTypeRootSequenceSize(rootHash, TUPLE, rootSequence, 2);
    var items = readTupleItems(rootHash, rootSequence.get(DATA_INDEX));
    return newTuple(rootHash, items);
  }

  private ImmutableList<TypeHV> readTupleItems(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<TypeHV>builder();
    var itemTypeHashes = readSequenceHashes(rootHash, hash, TUPLE, DATA_PATH);
    for (int i = 0; i < itemTypeHashes.size(); i++) {
      builder.add(readTupleItemType(rootHash, itemTypeHashes.get(i), DATA_PATH, i));
    }
    return builder.build();
  }

  private VariableH readVariable(Hash rootHash, List<Hash> rootSequence) {
    assertTypeRootSequenceSize(rootHash, VARIABLE, rootSequence, 2);
    String name = wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, VARIABLE, DATA_PATH, () ->hashedDb.readString(rootSequence.get(1)));
    if (!isVariableName(name)) {
      throw new DecodeVariableIllegalNameException(rootHash, name);
    }
    return newVariable(rootHash, name);
  }

  private <T> T readTupleItemType(TypeKindH kind, Hash outerHash, Hash hash, String path,
      Class<T> expectedClass) {
    TypeH result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        kind, outerHash, path, () -> get(hash));
    if (expectedClass.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new UnexpectedTypeNodeException(
          outerHash, kind, path, expectedClass, result.getClass());
    }
  }

  private TypeHV readTupleItemType(Hash outerHash, Hash hash, String path, int index) {
    TypeH result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        TUPLE, outerHash, path, index, () -> get(hash));
    if (result instanceof TypeHV typeHV) {
      return typeHV;
    } else {
      throw new UnexpectedTypeNodeException(
          outerHash, TUPLE, path, index, TypeHV.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayTypeH newArray(TypeHV elementType) throws HashedDbException {
    var rootHash = writeArrayRoot(elementType);
    return newArray(rootHash, elementType);
  }

  private ArrayTypeH newArray(Hash rootHash, TypeHV elementType) {
    return cache(new ArrayTypeH(rootHash, elementType));
  }

  private FunctionTypeH newFunction(TypeHV result, TupleTypeH parameters) throws HashedDbException {
    var rootHash = writeFunctionRoot(result, parameters);
    return newFunction(rootHash, result, parameters);
  }

  private FunctionTypeH newFunction(Hash rootHash, TypeHV result, TupleTypeH parameters) {
    return cache(new FunctionTypeH(rootHash, result, parameters));
  }

  private TupleTypeH newTuple(ImmutableList<TypeHV> itemTypes) throws HashedDbException {
    var hash = writeTupleRoot(itemTypes);
    return newTuple(hash, itemTypes);
  }

  private TupleTypeH newTuple(Hash rootHash, ImmutableList<TypeHV> itemTypes) {
    return cache(new TupleTypeH(rootHash, itemTypes));
  }

  private VariableH newVariable(String name) throws HashedDbException {
    var rootHash = writeVariableRoot(name);
    return newVariable(rootHash, name);
  }

  private VariableH newVariable(Hash rootHash, String name) {
    return cache(new VariableH(rootHash, name));
  }

  // methods for creating Expr types

  private CallTypeH newCall(TypeHV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CALL, evaluationType);
    return newCall(rootHash, evaluationType);
  }

  private CallTypeH newCall(Hash rootHash, TypeHV evaluationType) {
    return cache(new CallTypeH(rootHash, evaluationType));
  }

  private ConstTypeH newConst(TypeHV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONST, evaluationType);
    return newConst(rootHash, evaluationType);
  }

  private ConstTypeH newConst(Hash rootHash, TypeHV evaluationType) {
    ConstTypeH type = new ConstTypeH(rootHash, evaluationType);
    return cache(type);
  }

  private ConstructTypeH newConstruct(TupleTypeH evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONSTRUCT, evaluationType);
    return newConstruct(rootHash, evaluationType);
  }

  private ConstructTypeH newConstruct(Hash rootHash, TupleTypeH evaluationType) {
    return cache(new ConstructTypeH(rootHash, evaluationType));
  }

  private IfTypeH newIf(TypeHV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(IF, evaluationType);
    return newIf(rootHash, evaluationType);
  }

  private IfTypeH newIf(Hash rootHash, TypeHV evaluationType) {
    return cache(new IfTypeH(rootHash, evaluationType));
  }

  private InvokeTypeH newInvoke(TypeHV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(INVOKE, evaluationType);
    return newInvoke(rootHash, evaluationType);
  }

  private InvokeTypeH newInvoke(Hash rootHash, TypeHV evaluationType) {
    return cache(new InvokeTypeH(rootHash, evaluationType));
  }

  private MapTypeH newMap(ArrayTypeH evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(MAP, evaluationType);
    return newMap(rootHash, evaluationType);
  }

  private MapTypeH newMap(Hash rootHash, ArrayTypeH evaluationType) {
    return cache(new MapTypeH(rootHash, evaluationType));
  }

  private OrderTypeH newOrder(TypeHV elementType) throws HashedDbException {
    var evaluationType = array(elementType);
    var rootHash = writeExprRoot(ORDER, evaluationType);
    return newOrder(rootHash, evaluationType);
  }

  private OrderTypeH newOrder(Hash rootHash, ArrayTypeH evaluationType) {
    return cache(new OrderTypeH(rootHash, evaluationType));
  }

  private RefTypeH newRef(TypeHV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(REF, evaluationType);
    return newRef(rootHash, evaluationType);
  }

  private RefTypeH newRef(Hash rootHash, TypeHV evaluationType) {
    return cache(new RefTypeH(rootHash, evaluationType));
  }

  private SelectTypeH newSelect(TypeHV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(SELECT, evaluationType);
    return newSelect(rootHash, evaluationType);
  }

  private SelectTypeH newSelect(Hash rootHash, TypeHV evaluationType) {
    return cache(new SelectTypeH(rootHash, evaluationType));
  }

  private <T extends TypeH> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing Val type root

  private Hash writeArrayRoot(TypeH elementType) throws HashedDbException {
    return writeNonBaseRoot(ARRAY, elementType.hash());
  }

  private Hash writeFunctionRoot(TypeHV result, TupleTypeH parameters) throws HashedDbException {
    var hash = hashedDb.writeSequence(result.hash(), parameters.hash());
    return writeNonBaseRoot(FUNCTION, hash);
  }

  private Hash writeTupleRoot(ImmutableList<TypeHV> itemTypes) throws HashedDbException {
    var itemsHash = hashedDb.writeSequence(Lists.map(itemTypes, TypeH::hash));
    return writeNonBaseRoot(TUPLE, itemsHash);
  }

  private Hash writeVariableRoot(String name) throws HashedDbException {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprRoot(TypeKindH kind, TypeH evaluationType) throws HashedDbException {
    return writeNonBaseRoot(kind, evaluationType.hash());
  }

  private Hash writeNonBaseRoot(TypeKindH kind, Hash dataHash) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(kind.marker()), dataHash);
  }

  private Hash writeBaseRoot(TypeKindH kind) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(kind.marker()));
  }

  // Helper methods for reading

  private ImmutableList<Hash> readSequenceHashes(
      Hash rootHash, Hash sequenceHash, TypeKindH kind, String path) {
    return wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, kind, path, () -> hashedDb.readSequence(sequenceHash));
  }
}
