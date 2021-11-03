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
import static org.smoothbuild.db.object.type.base.ObjKind.STRUCT;
import static org.smoothbuild.db.object.type.base.ObjKind.STRUCT_EXPR;
import static org.smoothbuild.db.object.type.base.ObjKind.TUPLE;
import static org.smoothbuild.db.object.type.base.ObjKind.VARIABLE;
import static org.smoothbuild.db.object.type.base.ObjKind.fromMarker;
import static org.smoothbuild.lang.base.type.api.TypeNames.isVariableName;
import static org.smoothbuild.util.Strings.stringToOptionalString;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.type.base.ObjKind;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.exc.DecodeStructTypeWrongNamesSizeException;
import org.smoothbuild.db.object.type.exc.DecodeTypeIllegalKindException;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootException;
import org.smoothbuild.db.object.type.exc.DecodeVariableIllegalNameException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeNodeException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeSequenceException;
import org.smoothbuild.db.object.type.expr.CallOType;
import org.smoothbuild.db.object.type.expr.ConstOType;
import org.smoothbuild.db.object.type.expr.ConstructOType;
import org.smoothbuild.db.object.type.expr.InvokeOType;
import org.smoothbuild.db.object.type.expr.OrderOType;
import org.smoothbuild.db.object.type.expr.RefOType;
import org.smoothbuild.db.object.type.expr.SelectOType;
import org.smoothbuild.db.object.type.expr.StructExprOType;
import org.smoothbuild.db.object.type.val.AnyOType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.db.object.type.val.BlobOType;
import org.smoothbuild.db.object.type.val.BoolOType;
import org.smoothbuild.db.object.type.val.IntOType;
import org.smoothbuild.db.object.type.val.LambdaOType;
import org.smoothbuild.db.object.type.val.NativeMethodOType;
import org.smoothbuild.db.object.type.val.NothingOType;
import org.smoothbuild.db.object.type.val.StringOType;
import org.smoothbuild.db.object.type.val.StructOType;
import org.smoothbuild.db.object.type.val.TupleOType;
import org.smoothbuild.db.object.type.val.VariableOType;
import org.smoothbuild.lang.base.type.api.AbstractTypeFactory;
import org.smoothbuild.lang.base.type.api.Sides;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * This class is thread-safe.
 */
public class ObjTypeDb extends AbstractTypeFactory implements OTypeFactory {
  public static final String DATA_PATH = "data";
  private static final int DATA_INDEX = 1;
  private static final int LAMBDA_RESULT_INDEX = 0;
  public static final String LAMBDA_RESULT_PATH = DATA_PATH + "[" + LAMBDA_RESULT_INDEX + "]";
  private static final int LAMBDA_PARAMS_INDEX = 1;
  public static final String LAMBDA_PARAMS_PATH = DATA_PATH + "[" + LAMBDA_PARAMS_INDEX + "]";
  private static final int STRUCT_NAME_INDEX = 0;
  public static final String STRUCT_NAME_PATH = DATA_PATH + "[" + STRUCT_NAME_INDEX + "]";
  private static final int STRUCT_FIELDS_INDEX = 1;
  public static final String STRUCT_FIELDS_PATH = DATA_PATH + "[" + STRUCT_FIELDS_INDEX + "]";
  private static final int STRUCT_NAMES_INDEX = 2;
  public static final String STRUCT_NAMES_PATH = DATA_PATH + "[" + STRUCT_NAMES_INDEX + "]";


  private final HashedDb hashedDb;
  private final ConcurrentHashMap<Hash, ObjType> cache;

  private final AnyOType any;
  private final BlobOType blob;
  private final BoolOType bool;
  private final IntOType int_;
  private final NothingOType nothing;
  private final StringOType string;
  private final NativeMethodOType nativeMethod;
  private final Sides sides;

  public ObjTypeDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.any = cache(new AnyOType(writeBaseRoot(ANY)));
      this.blob = cache(new BlobOType(writeBaseRoot(BLOB)));
      this.bool = cache(new BoolOType(writeBaseRoot(BOOL)));
      this.int_ = cache(new IntOType(writeBaseRoot(INT)));
      this.nothing = cache(new NothingOType(writeBaseRoot(NOTHING)));
      this.string = cache(new StringOType(writeBaseRoot(STRING)));

      // expr
      this.nativeMethod = cache(new NativeMethodOType(writeBaseRoot(NATIVE_METHOD)));
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
    this.sides = new Sides(this.any, this.nothing);
  }

  @Override
  public Side upper() {
    return sides.upper();
  }

  @Override
  public Side lower() {
    return sides.lower();
  }

  // methods for getting Val-s types

  @Override
  public AnyOType any() {
    return any;
  }

  @Override
  public ArrayOType array(Type elementType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArray((ValType) elementType));
  }

  @Override
  public BlobOType blob() {
    return blob;
  }

  @Override
  public BoolOType bool() {
    return bool;
  }

  @Override
  public LambdaOType function(Type result, ImmutableList<? extends Type> parameters) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newLambda((ValType) result, tuple((ImmutableList<ValType>) parameters)));
  }

  @Override
  public IntOType int_() {
    return int_;
  }

  public NativeMethodOType nativeMethod() {
    return nativeMethod;
  }

  @Override
  public NothingOType nothing() {
    return nothing;
  }

  public TupleOType tuple(ImmutableList<ValType> itemTypes) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(itemTypes));
  }

  @Override
  public StringOType string() {
    return string;
  }

  @Override
  public StructOType struct(String name, NamedList<? extends Type> fields) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newStruct(name, (NamedList<ValType>) fields));
  }

  @Override
  public VariableOType variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVariable(name));
  }

  // methods for getting Expr-s types

  public OrderOType order(ValType elementType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elementType));
  }

  public CallOType call(ValType evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(evaluationType));
  }

  public ConstOType const_(ValType evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConst(evaluationType));
  }

  public InvokeOType invoke(ValType evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvoke(evaluationType));
  }

  public ConstructOType construct(TupleOType evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstruct(evaluationType));
  }

  public RefOType ref(ValType evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evaluationType));
  }

  public SelectOType select(ValType evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(evaluationType));
  }

  public StructExprOType structExpr(StructOType struct) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newStructExpr(struct));
  }

  // methods for reading from db

  public ObjType get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private ObjType read(Hash hash) {
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
      case STRUCT -> readStruct(hash, rootSequence);
      case STRUCT_EXPR -> newStructExpr(hash, readDataAsStruct(hash, rootSequence, objKind));
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

  private ValType readDataAsValue(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    return readDataAsClass(rootHash, rootSequence, objKind, ValType.class);
  }

  private ArrayOType readDataAsArray(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    return readDataAsClass(rootHash, rootSequence, objKind, ArrayOType.class);
  }

  private TupleOType readDataAsTuple(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    return readDataAsClass(rootHash, rootSequence, objKind, TupleOType.class);
  }

  private StructOType readDataAsStruct(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    return readDataAsClass(rootHash, rootSequence, objKind, StructOType.class);
  }

  private <T extends ObjType> T readDataAsClass(Hash rootHash, List<Hash> rootSequence,
      ObjKind objKind, Class<T> expectedTypeClass) {
    assertTypeRootSequenceSize(rootHash, objKind, rootSequence, 2);
    Hash hash = rootSequence.get(DATA_INDEX);
    return readInnerType(objKind, rootHash, hash, DATA_PATH, expectedTypeClass);
  }

  private ObjType readLambda(Hash rootHash, List<Hash> rootSequence, ObjKind objKind) {
    assertTypeRootSequenceSize(rootHash, objKind, rootSequence, 2);
    Hash dataHash = rootSequence.get(DATA_INDEX);
    List<Hash> data = readSequenceHashes(rootHash, dataHash, objKind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedTypeSequenceException(rootHash, objKind, DATA_PATH, 2, data.size());
    }
    ValType result = readInnerType(objKind, rootHash, data.get(LAMBDA_RESULT_INDEX),
        LAMBDA_RESULT_PATH, ValType.class);
    TupleOType parameters = readInnerType(objKind, rootHash, data.get(LAMBDA_PARAMS_INDEX),
        LAMBDA_PARAMS_PATH, TupleOType.class);
    return newLambda(rootHash, result, parameters);
  }

  private TupleOType readTuple(Hash rootHash, List<Hash> rootSequence) {
    assertTypeRootSequenceSize(rootHash, TUPLE, rootSequence, 2);
    var items = readTupleItems(rootHash, rootSequence.get(DATA_INDEX));
    return newTuple(rootHash, items);
  }

  private ImmutableList<ValType> readTupleItems(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<ValType>builder();
    var itemTypeHashes = readSequenceHashes(rootHash, hash, TUPLE, DATA_PATH);
    for (int i = 0; i < itemTypeHashes.size(); i++) {
      builder.add(readInnerType(TUPLE, rootHash, itemTypeHashes.get(i), DATA_PATH, i));
    }
    return builder.build();
  }

  private StructOType readStruct(Hash rootHash, List<Hash> rootSequence) {
    assertTypeRootSequenceSize(rootHash, STRUCT, rootSequence, 2);
    Hash dataHash = rootSequence.get(DATA_INDEX);
    List<Hash> data = readSequenceHashes(rootHash, dataHash, STRUCT, DATA_PATH);
    if (data.size() != 3) {
      throw new UnexpectedTypeSequenceException(rootHash, STRUCT, DATA_PATH, 2, data.size());
    }
    String name = wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, STRUCT, STRUCT_NAME_PATH, () -> hashedDb.readString(data.get(STRUCT_NAME_INDEX)));
    ImmutableList<ValType> fields = readStructFields(rootHash, data.get(STRUCT_FIELDS_INDEX));
    var names = readFieldNames(rootHash, data);
    return newStruct(rootHash, name, mergeFieldWithNames(rootHash, names, fields));
  }

  private NamedList<ValType> mergeFieldWithNames(Hash rootHash,
      ImmutableList<String> names, ImmutableList<ValType> fields) {
    if (names.size() != fields.size()) {
      throw new DecodeStructTypeWrongNamesSizeException(rootHash, fields.size(), names.size());
    }
    return namedList(zip(names, fields, (n, f) -> named(stringToOptionalString(n), f)));
  }

  private ImmutableList<ValType> readStructFields(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<ValType>builder();
    var itemTypeHashes = readSequenceHashes(rootHash, hash, STRUCT, STRUCT_FIELDS_PATH);
    for (int i = 0; i < itemTypeHashes.size(); i++) {
      builder.add(readInnerType(
          STRUCT, rootHash, itemTypeHashes.get(i), STRUCT_FIELDS_PATH, i));
    }
    return builder.build();
  }

  private ImmutableList<String> readFieldNames(Hash rootHash, List<Hash> data) {
    var nameHashes = readSequenceHashes(
        rootHash, data.get(STRUCT_NAMES_INDEX), STRUCT, STRUCT_NAMES_PATH);
    Builder<String> builder = ImmutableList.builder();
    for (int i = 0; i < nameHashes.size(); i++) {
      final int index = i;
      builder.add(Helpers.wrapHashedDbExceptionAsDecodeTypeNodeException(rootHash, STRUCT,
          STRUCT_NAMES_PATH, index, () -> hashedDb.readString(nameHashes.get(index))));
    }
    return builder.build();
  }

  private VariableOType readVariable(Hash rootHash, List<Hash> rootSequence) {
    assertTypeRootSequenceSize(rootHash, VARIABLE, rootSequence, 2);
    String name = wrapHashedDbExceptionAsDecodeTypeNodeException(
        rootHash, VARIABLE, DATA_PATH, () ->hashedDb.readString(rootSequence.get(1)));
    if (!isVariableName(name)) {
      throw new DecodeVariableIllegalNameException(rootHash, name);
    }
    return newVariable(rootHash, name);
  }

  private <T> T readInnerType(ObjKind objKind, Hash outerHash, Hash hash, String path,
      Class<T> expectedClass) {
    ObjType result = wrapObjectDbExceptionAsDecodeTypeNodeException(
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

  private ValType readInnerType(ObjKind objKind, Hash outerHash, Hash hash, String path,
      int index) {
    ObjType result = Helpers.wrapObjectDbExceptionAsDecodeTypeNodeException(
        objKind, outerHash, path, index, () -> get(hash));
    if (result instanceof ValType valType) {
      return valType;
    } else {
      throw new UnexpectedTypeNodeException(
          outerHash, objKind, path, index, ValType.class, result.getClass());
    }
  }

  // methods for creating Val types

  private ArrayOType newArray(ValType elementType) throws HashedDbException {
    var rootHash = writeArrayRoot(elementType);
    return newArray(rootHash, elementType);
  }

  private ArrayOType newArray(Hash rootHash, ValType elementType) {
    return cache(new ArrayOType(rootHash, elementType));
  }

  private LambdaOType newLambda(ValType result, TupleOType parameters) throws HashedDbException {
    var rootHash = writeLambdaRoot(result, parameters);
    return newLambda(rootHash, result, parameters);
  }

  private LambdaOType newLambda(Hash rootHash, ValType result, TupleOType parameters) {
    return cache(new LambdaOType(rootHash, result, parameters));
  }

  private StructOType newStruct(String name, NamedList<ValType> fields)
      throws HashedDbException {
    var rootHash = writeStructRoot(name, fields);
    return newStruct(rootHash, name, fields);
  }

  private StructOType newStruct(Hash rootHash, String name, NamedList<ValType> fields) {
    return cache(new StructOType(rootHash, name, fields));
  }

  private TupleOType newTuple(ImmutableList<ValType> itemTypes) throws HashedDbException {
    var hash = writeTupleRoot(itemTypes);
    return newTuple(hash, itemTypes);
  }

  private TupleOType newTuple(Hash rootHash, ImmutableList<? extends ValType> itemTypes) {
    return cache(new TupleOType(rootHash, itemTypes));
  }

  private VariableOType newVariable(String name) throws HashedDbException {
    var rootHash = writeVariableRoot(name);
    return newVariable(rootHash, name);
  }

  private VariableOType newVariable(Hash rootHash, String name) {
    return cache(new VariableOType(rootHash, name));
  }

  // methods for creating Expr types

  private CallOType newCall(ValType evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CALL, evaluationType);
    return newCall(rootHash, evaluationType);
  }

  private CallOType newCall(Hash rootHash, ValType evaluationType) {
    return cache(new CallOType(rootHash, evaluationType));
  }

  private ConstOType newConst(ValType evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONST, evaluationType);
    return newConst(rootHash, evaluationType);
  }

  private ConstOType newConst(Hash rootHash, ValType evaluationType) {
    return cache(new ConstOType(rootHash, evaluationType));
  }

  private ConstructOType newConstruct(TupleOType evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONSTRUCT, evaluationType);
    return newConstruct(rootHash, evaluationType);
  }

  private ConstructOType newConstruct(Hash rootHash, TupleOType evaluationType) {
    return cache(new ConstructOType(rootHash, evaluationType));
  }

  private InvokeOType newInvoke(ValType evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(CONST, evaluationType);
    return newInvoke(rootHash, evaluationType);
  }

  private InvokeOType newInvoke(Hash rootHash, ValType evaluationType) {
    return cache(new InvokeOType(rootHash, evaluationType));
  }

  private OrderOType newOrder(ValType elementType) throws HashedDbException {
    var evaluationType = array(elementType);
    var rootHash = writeExprRoot(ORDER, evaluationType);
    return newOrder(rootHash, evaluationType);
  }

  private OrderOType newOrder(Hash rootHash, ArrayOType evaluationType) {
    return cache(new OrderOType(rootHash, evaluationType));
  }

  private RefOType newRef(ValType evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(REF, evaluationType);
    return newRef(rootHash, evaluationType);
  }

  private RefOType newRef(Hash rootHash, ValType evaluationType) {
    return cache(new RefOType(rootHash, evaluationType));
  }

  private SelectOType newSelect(ValType evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(SELECT, evaluationType);
    return newSelect(rootHash, evaluationType);
  }

  private SelectOType newSelect(Hash rootHash, ValType evaluationType) {
    return cache(new SelectOType(rootHash, evaluationType));
  }

  private StructExprOType newStructExpr(StructOType evaluationType) throws HashedDbException {
    var rootHash = writeExprRoot(STRUCT_EXPR, evaluationType);
    return newStructExpr(rootHash, evaluationType);
  }

  private StructExprOType newStructExpr(Hash rootHash, StructOType evaluationType) {
    return cache(new StructExprOType(rootHash, evaluationType));
  }

  private <T extends ObjType> T cache(T type) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(type.hash(), type), type);
    return result;
  }

  // Methods for writing Val type root

  private Hash writeArrayRoot(ObjType elementType) throws HashedDbException {
    return writeNonBaseRoot(ARRAY, elementType.hash());
  }

  private Hash writeLambdaRoot(ValType result, TupleOType parameters) throws HashedDbException {
    var hash = hashedDb.writeSequence(result.hash(), parameters.hash());
    return writeNonBaseRoot(LAMBDA, hash);
  }

  private Hash writeStructRoot(String name, NamedList<ValType> fields)
      throws HashedDbException {
    var nameHash = hashedDb.writeString(name);
    var fieldsSequenceHash = writeFieldsSequence(fields);
    var namesSequenceHash = writeNamesSequence(fields);
    var typeData = hashedDb.writeSequence(nameHash, fieldsSequenceHash, namesSequenceHash);
    return writeNonBaseRoot(STRUCT, typeData);
  }

  private Hash writeFieldsSequence(NamedList<ValType> fields) throws HashedDbException {
    return hashedDb.writeSequence(map(fields.list(), f -> f.object().hash()));
  }

  private Hash writeNamesSequence(NamedList<ValType> fields) throws HashedDbException {
    var nameHashes = new ArrayList<Hash>(fields.size());
    for (Named<ValType> field : fields.list()) {
      nameHashes.add(hashedDb.writeString(field.saneName()));
    }
    return hashedDb.writeSequence(nameHashes);
  }

  private Hash writeTupleRoot(ImmutableList<? extends ObjType> itemTypes) throws HashedDbException {
    var itemsHash = hashedDb.writeSequence(map(itemTypes, ObjType::hash));
    return writeNonBaseRoot(TUPLE, itemsHash);
  }

  private Hash writeVariableRoot(String name) throws HashedDbException {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprRoot(ObjKind objKind, ObjType evaluationType) throws HashedDbException {
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
