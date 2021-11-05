package org.smoothbuild.db.object.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExceptionAsDecodeTypeException;
import static org.smoothbuild.db.object.type.Helpers.wrapHashedDbExceptionAsDecodeTypeNodeException;
import static org.smoothbuild.db.object.type.Helpers.wrapObjectDbExceptionAsDecodeTypeNodeException;
import static org.smoothbuild.db.object.type.base.ObjKind.ANY;
import static org.smoothbuild.db.object.type.base.ObjKind.ARRAY;
import static org.smoothbuild.db.object.type.base.ObjKind.BLOB;
import static org.smoothbuild.db.object.type.base.ObjKind.BOOL;
import static org.smoothbuild.db.object.type.base.ObjKind.CALL;
import static org.smoothbuild.db.object.type.base.ObjKind.CONST;
import static org.smoothbuild.db.object.type.base.ObjKind.CONSTRUCT;
import static org.smoothbuild.db.object.type.base.ObjKind.INT;
import static org.smoothbuild.db.object.type.base.ObjKind.LAMBDA;
import static org.smoothbuild.db.object.type.base.ObjKind.NATIVE_METHOD;
import static org.smoothbuild.db.object.type.base.ObjKind.NOTHING;
import static org.smoothbuild.db.object.type.base.ObjKind.ORDER;
import static org.smoothbuild.db.object.type.base.ObjKind.REF;
import static org.smoothbuild.db.object.type.base.ObjKind.SELECT;
import static org.smoothbuild.db.object.type.base.ObjKind.STRING;
import static org.smoothbuild.db.object.type.base.ObjKind.TUPLE;
import static org.smoothbuild.db.object.type.base.ObjKind.VARIABLE;
import static org.smoothbuild.db.object.type.base.ObjKind.fromMarker;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.db.ObjDbException;
import org.smoothbuild.db.object.type.base.ObjKind;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.exc.DecodeTypeIllegalKindException;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootException;
import org.smoothbuild.db.object.type.exc.DecodeVariableIllegalNameException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeNodeException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeSequenceException;
import org.smoothbuild.db.object.type.expr.CallTypeO;
import org.smoothbuild.db.object.type.expr.ConstTypeO;
import org.smoothbuild.db.object.type.expr.ConstructTypeO;
import org.smoothbuild.db.object.type.expr.InvokeTypeO;
import org.smoothbuild.db.object.type.expr.OrderTypeO;
import org.smoothbuild.db.object.type.expr.RefTypeO;
import org.smoothbuild.db.object.type.expr.SelectTypeO;
import org.smoothbuild.db.object.type.val.AnyTypeO;
import org.smoothbuild.db.object.type.val.ArrayTypeO;
import org.smoothbuild.db.object.type.val.BlobTypeO;
import org.smoothbuild.db.object.type.val.BoolTypeO;
import org.smoothbuild.db.object.type.val.IntTypeO;
import org.smoothbuild.db.object.type.val.LambdaTypeO;
import org.smoothbuild.db.object.type.val.NativeMethodTypeO;
import org.smoothbuild.db.object.type.val.NothingTypeO;
import org.smoothbuild.db.object.type.val.StringTypeO;
import org.smoothbuild.db.object.type.val.TupleTypeO;
import org.smoothbuild.db.object.type.val.VariableO;
import org.smoothbuild.lang.base.type.api.AbstractTypeFactory;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjTypeDb extends AbstractTypeFactory<TypeV> implements TypeFactoryO {
  public static final String DATA_PATH = "data";
  private static final int DATA_INDEX = 1;
  private static final int LAMBDA_RESULT_INDEX = 0;
  public static final String LAMBDA_RESULT_PATH = DATA_PATH + "[" + LAMBDA_RESULT_INDEX + "]";
  private static final int LAMBDA_PARAMS_INDEX = 1;
  public static final String LAMBDA_PARAMS_PATH = DATA_PATH + "[" + LAMBDA_PARAMS_INDEX + "]";

  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, TypeO> cache;

  private final AnyTypeO any;
  private final BlobTypeO blob;
  private final BoolTypeO bool;
  private final IntTypeO int_;
  private final NothingTypeO nothing;
  private final StringTypeO string;
  private final NativeMethodTypeO nativeMethod;
  private final Sides<TypeV> sides;

  public ObjTypeDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.any = cache(new AnyTypeO(writeBaseRoot(ANY)));
      this.blob = cache(new BlobTypeO(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolTypeO(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntTypeO(writeBaseRoot(INT)));
      this.nothing = cache(new NothingTypeO(writeBaseRoot(NOTHING)));
      this.string = cache(new StringTypeO(writeBaseRoot(STRING)));

      // expr
      this.nativeMethod = cache(new NativeMethodTypeO(writeBaseRoot(NATIVE_METHOD)));
    } catch (HashedDbException e) {
      throw new ObjDbException(e);
    }
    this.sides = new Sides<>(this.any, this.nothing);
  }

  @Override
  public Side<TypeV> upper() {
    return sides.upper();
  }

  @Override
  public Side<TypeV> lower() {
    return sides.lower();
  }

  // methods for getting Val-s types

  @Override
  public AnyTypeO any() {
    return any;
  }

  @Override
  public ArrayTypeO array(TypeV elementType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArray(elementType));
  }

  @Override
  public BlobTypeO blob() {
    return blob;
  }

  @Override
  public BoolTypeO bool() {
    return bool;
  }

  @Override
  public LambdaTypeO function(TypeV result, ImmutableList<TypeV> parameters) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newLambda(result, tuple(parameters)));
  }

  @Override
  public IntTypeO int_() {
    return int_;
  }

  public NativeMethodTypeO nativeMethod() {
    return nativeMethod;
  }

  @Override
  public NothingTypeO nothing() {
    return nothing;
  }

  @Override
  public TupleTypeO tuple(ImmutableList<TypeV> itemTypes) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(itemTypes));
  }

  @Override
  public StringTypeO string() {
    return string;
  }

  @Override
  public VariableO variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVariable(name));
  }

  // methods for getting Expr-s types

  public OrderTypeO order(TypeV elementType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elementType));
  }

  public CallTypeO call(TypeV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evaluationType));
  }

  public ConstTypeO const_(TypeV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConst(evaluationType));
  }

  public InvokeTypeO invoke(TypeV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvoke(evaluationType));
  }

  public ConstructTypeO construct(TupleTypeO evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstruct(evaluationType));
  }

  public RefTypeO ref(TypeV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evaluationType));
  }

  public SelectTypeO select(TypeV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evaluationType));
  }

  // methods for reading from db

  public TypeO get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private TypeO read(Hash hash) {
    List<Hash> rootSequence = readTypeRootSequence(hash);
    ObjKind objKind = decodeTypeMarker(hash, rootSequence.get(0));
    return switch (objKind) {
      case ANY, BLOB, BOOL, INT, NATIVE_METHOD, NOTHING, STRING -> {
        assertTypeRootSequenceSize(hash, objKind, rootSequence, 1);
        throw new RuntimeException(
            "Internal error: Type with kind " + objKind + " should be found in cache.");
      }
      case ARRAY -> newArray(hash, readDataAsValue(hash, rootSequence, objKind));
      case ORDER -> newOrder(hash, readDataAsArray(hash, rootSequence, objKind));
      case CALL -> newCall(hash, readDataAsValue(hash, rootSequence, objKind));
      case CONST -> newConst(hash, readDataAsValue(hash, rootSequence, objKind));
      case INVOKE -> newInvoke(hash, readDataAsValue(hash, rootSequence, objKind));
      case LAMBDA -> readLambda(hash, rootSequence, objKind);
      case REF -> newRef(hash, readDataAsValue(hash, rootSequence, objKind));
      case CONSTRUCT -> newConstruct(hash, readDataAsTuple(hash, rootSequence, objKind));
      case TUPLE -> readTuple(hash, rootSequence);
      case SELECT -> newSelect(hash, readDataAsValue(hash, rootSequence, objKind));
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

  private ObjKind decodeTypeMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExceptionAsDecodeTypeException(
        hash, () -> hashedDb.readByte(markerHash));
    ObjKind objKind = fromMarker(marker);
    if (objKind == null) {
      throw new DecodeTypeIllegalKindException(hash, marker);
    }
    return objKind;
  }

  private static void assertTypeRootSequenceSize(
      Hash rootHash, ObjKind objKind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeTypeRootException(rootHash, objKind, hashes.size(), expectedSize);
    }
  }

  private TypeV readDataAsValue(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    return readDataAsClass(rootHash, rootSequence, objKind, TypeV.class);
  }

  private ArrayTypeO readDataAsArray(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    return readDataAsClass(rootHash, rootSequence, objKind, ArrayTypeO.class);
  }

  private TupleTypeO readDataAsTuple(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    return readDataAsClass(rootHash, rootSequence, objKind, TupleTypeO.class);
  }

  private <T extends TypeO> T readDataAsClass(Hash rootHash, List<Hash> rootSequence,
      ObjKind objKind, Class<T> expectedTypeClass) {
    assertTypeRootSequenceSize(rootHash, objKind, rootSequence, 2);
    Hash hash = rootSequence.get(DATA_INDEX);
    return readTupleItemType(objKind, rootHash, hash, DATA_PATH, expectedTypeClass);
  }

  private TypeO readLambda(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    assertTypeRootSequenceSize(rootHash, objKind, rootSequence, 2);
    Hash dataHash = rootSequence.get(DATA_INDEX);
    List<Hash> data = readSequenceHashes(rootHash, dataHash, objKind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedTypeSequenceException(rootHash, objKind, DATA_PATH, 2, data.size());
    }
    TypeV result = readTupleItemType(objKind, rootHash, data.get(LAMBDA_RESULT_INDEX),
        LAMBDA_RESULT_PATH, TypeV.class);
    TupleTypeO parameters = readTupleItemType(objKind, rootHash, data.get(LAMBDA_PARAMS_INDEX),
        LAMBDA_PARAMS_PATH, TupleTypeO.class);
    return newLambda(rootHash, result, parameters);
  }

  private TupleTypeO readTuple(Hash rootHash, List<Hash> rootSequence) {
    assertTypeRootSequenceSize(rootHash, TUPLE, rootSequence, 2);
    var items = readTupleItems(rootHash, rootSequence.get(DATA_INDEX));
    return newTuple(rootHash, items);
  }

  private ImmutableList<TypeV> readTupleItems(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<TypeV>builder();
    var itemTypeHashes = readSequenceHashes(rootHash, hash, TUPLE, DATA_PATH);
    for (int i = 0; i < itemTypeHashes.size(); i++) {
      builder.add(readTupleItemType(rootHash, itemTypeHashes.get(i), DATA_PATH, i));
    }
    return builder.build();
  }

  private VariableO readVariable(Hash rootHash, List<Hash> rootSequence) {
    assertTypeRootSequenceSize(rootHash, VARIABLE, rootSequence, 2);
    String name = wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, VARIABLE, DATA_PATH, () ->hashedDb.readString(rootSequence.get(1)));
    if (!isVariableName(name)) {
      throw new DecodeVariableIllegalNameException(rootHash, name);
    }
    return newVariable(rootHash, name);
  }

  private <T> T readTupleItemType(ObjKind objKind, Hash outerHash, Hash hash, String path,
      Class<T> expectedClass) {
    TypeO result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        objKind, outerHash, path, () -> get(hash));
    if (expectedClass.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new UnexpectedTypeNodeException(
          outerHash, objKind, path, expectedClass, result.getClass());
    }
  }

  private TypeV readTupleItemType(Hash outerHash, Hash hash, String path, int index) {
    TypeO result = wrapObjectDbExceptionAsDecodeTypeNodeException(
        TUPLE, outerHash, path, index, () -> get(hash));
    if (result instanceof TypeV typeV) {
      return typeV;
    } else {
      throw new UnexpectedTypeNodeException(
          outerHash, TUPLE, path, index, TypeV.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayTypeO newArray(TypeV elementType) throws HashedDbException {
    var rootHash = writeArrayRoot(elementType);
    return newArray(rootHash, elementType);
  }

  private ArrayTypeO newArray(Hash rootHash, TypeV elementType) {
    return cache(new ArrayTypeO(rootHash, elementType));
  }

  private LambdaTypeO newLambda(TypeV result, TupleTypeO parameters) throws HashedDbException {
    var rootHash = writeLambdaRoot(result, parameters);
    return newLambda(rootHash, result, parameters);
  }

  private LambdaTypeO newLambda(Hash rootHash, TypeV result, TupleTypeO parameters) {
    return cache(new LambdaTypeO(rootHash, result, parameters));
  }

  private TupleTypeO newTuple(ImmutableList<TypeV> itemTypes) throws HashedDbException {
    var hash = writeTupleRoot(itemTypes);
    return newTuple(hash, itemTypes);
  }

  private TupleTypeO newTuple(Hash rootHash, ImmutableList<TypeV> itemTypes) {
    return cache(new TupleTypeO(rootHash, itemTypes));
  }

  private VariableO newVariable(String name) throws HashedDbException {
    var rootHash = writeVariableRoot(name);
    return newVariable(rootHash, name);
  }

  private VariableO newVariable(Hash rootHash, String name) {
    return cache(new VariableO(rootHash, name));
  }

  // methods for creating Expr types

  private CallTypeO newCall(TypeV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CALL, evaluationType);
    return newCall(rootHash, evaluationType);
  }

  private CallTypeO newCall(Hash rootHash, TypeV evaluationType) {
    return cache(new CallTypeO(rootHash, evaluationType));
  }

  private ConstTypeO newConst(TypeV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONST, evaluationType);
    return newConst(rootHash, evaluationType);
  }

  private ConstTypeO newConst(Hash rootHash, TypeV evaluationType) {
    return cache(new ConstTypeO(rootHash, evaluationType));
  }

  private ConstructTypeO newConstruct(TupleTypeO evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONSTRUCT, evaluationType);
    return newConstruct(rootHash, evaluationType);
  }

  private ConstructTypeO newConstruct(Hash rootHash, TupleTypeO evaluationType) {
    return cache(new ConstructTypeO(rootHash, evaluationType));
  }

  private InvokeTypeO newInvoke(TypeV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONST, evaluationType);
    return newInvoke(rootHash, evaluationType);
  }

  private InvokeTypeO newInvoke(Hash rootHash, TypeV evaluationType) {
    return cache(new InvokeTypeO(rootHash, evaluationType));
  }

  private OrderTypeO newOrder(TypeV elementType) throws HashedDbException {
    var evaluationType = array(elementType);
    var rootHash = writeExprRoot(ORDER, evaluationType);
    return newOrder(rootHash, evaluationType);
  }

  private OrderTypeO newOrder(Hash rootHash, ArrayTypeO evaluationType) {
    return cache(new OrderTypeO(rootHash, evaluationType));
  }

  private RefTypeO newRef(TypeV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(REF, evaluationType);
    return newRef(rootHash, evaluationType);
  }

  private RefTypeO newRef(Hash rootHash, TypeV evaluationType) {
    return cache(new RefTypeO(rootHash, evaluationType));
  }

  private SelectTypeO newSelect(TypeV evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(SELECT, evaluationType);
    return newSelect(rootHash, evaluationType);
  }

  private SelectTypeO newSelect(Hash rootHash, TypeV evaluationType) {
    return cache(new SelectTypeO(rootHash, evaluationType));
  }

  private <T extends TypeO> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing Val type root

  private Hash writeArrayRoot(TypeO elementType) throws HashedDbException {
    return writeNonBaseRoot(ARRAY, elementType.hash());
  }

  private Hash writeLambdaRoot(TypeV result, TupleTypeO parameters) throws HashedDbException {
    var hash = hashedDb.writeSequence(result.hash(), parameters.hash());
    return writeNonBaseRoot(LAMBDA, hash);
  }

  private Hash writeTupleRoot(ImmutableList<TypeV> itemTypes) throws HashedDbException {
    var itemsHash = hashedDb.writeSequence(map(itemTypes, TypeO::hash));
    return writeNonBaseRoot(TUPLE, itemsHash);
  }

  private Hash writeVariableRoot(String name) throws HashedDbException {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprRoot(ObjKind objKind, TypeO evaluationType) throws HashedDbException {
    return writeNonBaseRoot(objKind, evaluationType.hash());
  }

  private Hash writeNonBaseRoot(ObjKind objKind, Hash dataHash) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(objKind.marker()), dataHash);
  }

  private Hash writeBaseRoot(ObjKind objKind) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(objKind.marker()));
  }

  // Helper methods for reading

  private ImmutableList<Hash> readSequenceHashes(
      Hash rootHash, Hash sequenceHash, ObjKind objKind, String path) {
    return wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, objKind, path, () -> hashedDb.readSequence(sequenceHash));
  }
}
