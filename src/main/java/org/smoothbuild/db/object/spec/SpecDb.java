package org.smoothbuild.db.object.spec;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.spec.Helpers.wrapHashedDbExceptionAsDecodeSpecException;
import static org.smoothbuild.db.object.spec.Helpers.wrapHashedDbExceptionAsDecodeSpecNodeException;
import static org.smoothbuild.db.object.spec.Helpers.wrapObjectDbExceptionAsDecodeSpecNodeException;
import static org.smoothbuild.db.object.spec.base.SpecKind.ANY;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY_EXPR;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;
import static org.smoothbuild.db.object.spec.base.SpecKind.LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.NATIVE_METHOD;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD_EXPR;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.SELECT;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT_EXPR;
import static org.smoothbuild.db.object.spec.base.SpecKind.VARIABLE;
import static org.smoothbuild.db.object.spec.base.SpecKind.fromMarker;
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
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.exc.DecodeSpecIllegalKindException;
import org.smoothbuild.db.object.spec.exc.DecodeSpecRootException;
import org.smoothbuild.db.object.spec.exc.DecodeStructSpecWrongNamesSizeException;
import org.smoothbuild.db.object.spec.exc.DecodeVariableIllegalNameException;
import org.smoothbuild.db.object.spec.exc.UnexpectedSpecNodeException;
import org.smoothbuild.db.object.spec.exc.UnexpectedSpecSequenceException;
import org.smoothbuild.db.object.spec.expr.ArrayExprSpec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.InvokeSpec;
import org.smoothbuild.db.object.spec.expr.RecExprSpec;
import org.smoothbuild.db.object.spec.expr.RefSpec;
import org.smoothbuild.db.object.spec.expr.SelectSpec;
import org.smoothbuild.db.object.spec.expr.StructExprSpec;
import org.smoothbuild.db.object.spec.val.AnySpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.NativeMethodSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.db.object.spec.val.VariableSpec;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.util.collect.Named;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * This class is thread-safe.
 */
public class SpecDb implements TypeFactory {
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
  private final ConcurrentHashMap<Hash, Spec> cache;

  private final AnySpec any;
  private final BlobSpec blob;
  private final BoolSpec bool;
  private final IntSpec int_;
  private final NothingSpec nothing;
  private final StrSpec string;
  private final NativeMethodSpec nativeMethod;

  public SpecDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.cache = new ConcurrentHashMap<>();

    try {
      this.any = cache(new AnySpec(writeBaseSpecRoot(ANY)));
      this.blob = cache(new BlobSpec(writeBaseSpecRoot(BLOB)));
      this.bool = cache(new BoolSpec(writeBaseSpecRoot(BOOL)));
      this.int_ = cache(new IntSpec(writeBaseSpecRoot(INT)));
      this.nothing = cache(new NothingSpec(writeBaseSpecRoot(NOTHING)));
      this.string = cache(new StrSpec(writeBaseSpecRoot(STRING)));

      // expr
      this.nativeMethod = cache(new NativeMethodSpec(writeBaseSpecRoot(NATIVE_METHOD)));
    } catch (HashedDbException e) {
      throw new ObjectDbException(e);
    }
  }

  // methods for getting Val-s specs

  @Override
  public AnySpec any() {
    return any;
  }

  @Override
  public ArraySpec array(Type elementSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArraySpec((ValSpec) elementSpec));
  }

  @Override
  public BlobSpec blob() {
    return blob;
  }

  @Override
  public BoolSpec bool() {
    return bool;
  }

  @Override
  public LambdaSpec function(Type result, ImmutableList<? extends Type> parameters) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newLambdaSpec((ValSpec) result, rec((ImmutableList<ValSpec>) parameters)));
  }

  @Override
  public IntSpec int_() {
    return int_;
  }

  public NativeMethodSpec nativeMethod() {
    return nativeMethod;
  }

  @Override
  public NothingSpec nothing() {
    return nothing;
  }

  public RecSpec rec(ImmutableList<ValSpec> itemSpecs) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRecSpec(itemSpecs));
  }

  @Override
  public StrSpec string() {
    return string;
  }

  @Override
  public StructSpec struct(String name, NamedList<? extends Type> fields) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newStructSpec(name, (NamedList<ValSpec>) fields));
  }

  @Override
  public VariableSpec variable(String name) {
    checkArgument(isVariableName(name), "Illegal type variable name '%s'.", name);
    return wrapHashedDbExceptionAsObjectDbException(() -> newVariableSpec(name));
  }

  // methods for getting Expr-s specs

  public ArrayExprSpec arrayExpr(ValSpec elementSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newArrayExprSpec(elementSpec));
  }

  public CallSpec call(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCallSpec(evaluationSpec));
  }

  public ConstSpec const_(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstSpec(evaluationSpec));
  }

  public InvokeSpec invoke(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvokeSpec(evaluationSpec));
  }

  public RecExprSpec recExpr(RecSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRecExprSpec(evaluationSpec));
  }

  public RefSpec ref(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRefSpec(evaluationSpec));
  }

  public SelectSpec select(ValSpec evaluationSpec) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelectSpec(evaluationSpec));
  }

  public StructExprSpec structExpr(StructSpec struct) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newStructExprSpec(struct));
  }

  // methods for reading from db

  public Spec get(Hash hash) {
    return requireNonNullElseGet(cache.get(hash), () -> read(hash));
  }

  private Spec read(Hash hash) {
    List<Hash> rootSequence = readSpecRootSequence(hash);
    SpecKind specKind = decodeSpecMarker(hash, rootSequence.get(0));
    return switch (specKind) {
      case ANY, BLOB, BOOL, INT, NATIVE_METHOD, NOTHING, STRING -> {
        assertSpecRootSequenceSize(hash, specKind, rootSequence, 1);
        throw new RuntimeException(
            "Internal error: Spec with kind " + specKind + " should be found in cache.");
      }
      case ARRAY -> newArraySpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case ARRAY_EXPR -> newArrayExprSpec(hash, getDataAsArraySpec(hash, rootSequence, specKind));
      case CALL -> newCallSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case CONST -> newConstSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case INVOKE -> newInvokeSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case LAMBDA -> readLambdaSpec(hash, rootSequence, specKind);
      case REF -> newRefSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case RECORD_EXPR -> newRecExprSpec(hash, getDataAsRecSpec(hash, rootSequence, specKind));
      case RECORD -> readRecord(hash, rootSequence);
      case SELECT -> newSelectSpec(hash, getDataAsValSpec(hash, rootSequence, specKind));
      case STRUCT -> readStructSpec(hash, rootSequence);
      case STRUCT_EXPR -> newStructExprSpec(hash, getDataAsStructSpec(hash, rootSequence, specKind));
      case VARIABLE -> readVariable(hash, rootSequence);
    };
  }

  private List<Hash> readSpecRootSequence(Hash hash) {
    List<Hash> hashes = wrapHashedDbExceptionAsDecodeSpecException(
        hash, () -> hashedDb.readSequence(hash));
    int sequenceSize = hashes.size();
    if (sequenceSize != 1 && sequenceSize != 2) {
      throw new DecodeSpecRootException(hash, sequenceSize);
    }
    return hashes;
  }

  private SpecKind decodeSpecMarker(Hash hash, Hash markerHash) {
    byte marker = wrapHashedDbExceptionAsDecodeSpecException(
        hash, () -> hashedDb.readByte(markerHash));
    SpecKind specKind = fromMarker(marker);
    if (specKind == null) {
      throw new DecodeSpecIllegalKindException(hash, marker);
    }
    return specKind;
  }

  private static void assertSpecRootSequenceSize(
      Hash rootHash, SpecKind specKind, List<Hash> hashes, int expectedSize) {
    if (hashes.size() != expectedSize) {
      throw new DecodeSpecRootException(rootHash, specKind, hashes.size(), expectedSize);
    }
  }

  private ValSpec getDataAsValSpec(Hash rootHash, List<Hash> rootSequence, SpecKind specKind) {
    return getDataAsSpecCastedTo(rootHash, rootSequence, specKind, ValSpec.class);
  }

  private ArraySpec getDataAsArraySpec(Hash rootHash, List<Hash> rootSequence, SpecKind specKind) {
    return getDataAsSpecCastedTo(rootHash, rootSequence, specKind, ArraySpec.class);
  }

  private RecSpec getDataAsRecSpec(Hash rootHash, List<Hash> rootSequence, SpecKind specKind) {
    return getDataAsSpecCastedTo(rootHash, rootSequence, specKind, RecSpec.class);
  }

  private StructSpec getDataAsStructSpec(
      Hash rootHash, List<Hash> rootSequence, SpecKind specKind) {
    return getDataAsSpecCastedTo(rootHash, rootSequence, specKind, StructSpec.class);
  }

  private <T extends Spec> T getDataAsSpecCastedTo(Hash rootHash, List<Hash> rootSequence,
      SpecKind specKind, Class<T> expectedSpecClass) {
    assertSpecRootSequenceSize(rootHash, specKind, rootSequence, 2);
    Hash hash = rootSequence.get(DATA_INDEX);
    return readInnerSpec(specKind, rootHash, hash, DATA_PATH, expectedSpecClass);
  }

  private Spec readLambdaSpec(Hash rootHash, List<Hash> rootSequence, SpecKind specKind) {
    assertSpecRootSequenceSize(rootHash, specKind, rootSequence, 2);
    Hash dataHash = rootSequence.get(DATA_INDEX);
    List<Hash> data = readSequenceHashes(rootHash, dataHash, specKind, DATA_PATH);
    if (data.size() != 2) {
      throw new UnexpectedSpecSequenceException(rootHash, specKind, DATA_PATH, 2, data.size());
    }
    ValSpec result = readInnerSpec(specKind, rootHash, data.get(LAMBDA_RESULT_INDEX),
        LAMBDA_RESULT_PATH, ValSpec.class);
    RecSpec parameters = readInnerSpec(specKind, rootHash, data.get(LAMBDA_PARAMS_INDEX),
        LAMBDA_PARAMS_PATH, RecSpec.class);
    return newLambdaSpec(rootHash, result, parameters);
  }

  private RecSpec readRecord(Hash rootHash, List<Hash> rootSequence) {
    assertSpecRootSequenceSize(rootHash, RECORD, rootSequence, 2);
    ImmutableList<ValSpec> items = readRecSpecItemSpecs(rootHash, rootSequence.get(DATA_INDEX));
    return newRecSpec(rootHash, items);
  }

  private ImmutableList<ValSpec> readRecSpecItemSpecs(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<ValSpec>builder();
    var itemSpecHashes = readSequenceHashes(rootHash, hash, RECORD, DATA_PATH);
    for (int i = 0; i < itemSpecHashes.size(); i++) {
      builder.add(readInnerSpec(RECORD, rootHash, itemSpecHashes.get(i), DATA_PATH, i));
    }
    return builder.build();
  }

  private StructSpec readStructSpec(Hash rootHash, List<Hash> rootSequence) {
    assertSpecRootSequenceSize(rootHash, STRUCT, rootSequence, 2);
    Hash dataHash = rootSequence.get(DATA_INDEX);
    List<Hash> data = readSequenceHashes(rootHash, dataHash, STRUCT, DATA_PATH);
    if (data.size() != 3) {
      throw new UnexpectedSpecSequenceException(rootHash, STRUCT, DATA_PATH, 2, data.size());
    }
    String name = wrapHashedDbExceptionAsDecodeSpecNodeException(
        rootHash, STRUCT, STRUCT_NAME_PATH, () -> hashedDb.readString(data.get(STRUCT_NAME_INDEX)));
    ImmutableList<ValSpec> fields = readStructFields(rootHash, data.get(STRUCT_FIELDS_INDEX));
    var names = readFieldNames(rootHash, data);
    return newStructSpec(rootHash, name, mergeFieldWithNames(rootHash, names, fields));
  }

  private NamedList<ValSpec> mergeFieldWithNames(Hash rootHash,
      ImmutableList<String> names, ImmutableList<ValSpec> fields) {
    if (names.size() != fields.size()) {
      throw new DecodeStructSpecWrongNamesSizeException(rootHash, fields.size(), names.size());
    }
    return namedList(zip(names, fields, (n, f) -> named(stringToOptionalString(n), f)));
  }

  private ImmutableList<ValSpec> readStructFields(Hash rootHash, Hash hash) {
    var builder = ImmutableList.<ValSpec>builder();
    var itemSpecHashes = readSequenceHashes(rootHash, hash, STRUCT, STRUCT_FIELDS_PATH);
    for (int i = 0; i < itemSpecHashes.size(); i++) {
      builder.add(readInnerSpec(
          STRUCT, rootHash, itemSpecHashes.get(i), STRUCT_FIELDS_PATH, i));
    }
    return builder.build();
  }

  private ImmutableList<String> readFieldNames(Hash rootHash, List<Hash> data) {
    var nameHashes = readSequenceHashes(
        rootHash, data.get(STRUCT_NAMES_INDEX), STRUCT, STRUCT_NAMES_PATH);
    Builder<String> builder = ImmutableList.builder();
    for (int i = 0; i < nameHashes.size(); i++) {
      final int index = i;
      builder.add(wrapHashedDbExceptionAsDecodeSpecNodeException(rootHash, STRUCT,
          STRUCT_NAMES_PATH, index, () -> hashedDb.readString(nameHashes.get(index))));
    }
    return builder.build();
  }

  private VariableSpec readVariable(Hash rootHash, List<Hash> rootSequence) {
    assertSpecRootSequenceSize(rootHash, VARIABLE, rootSequence, 2);
    String name = wrapHashedDbExceptionAsDecodeSpecNodeException(
        rootHash, VARIABLE, DATA_PATH, () ->hashedDb.readString(rootSequence.get(1)));
    if (!isVariableName(name)) {
      throw new DecodeVariableIllegalNameException(rootHash, name);
    }
    return newVariableSpec(rootHash, name);
  }

  private <T> T readInnerSpec(SpecKind specKind, Hash outerHash, Hash hash, String path,
      Class<T> expectedClass) {
    Spec result = wrapObjectDbExceptionAsDecodeSpecNodeException(
        specKind, outerHash, path, () -> get(hash));
    if (expectedClass.isInstance(result)) {
      @SuppressWarnings("unchecked")
      T castResult = (T) result;
      return castResult;
    } else {
      throw new UnexpectedSpecNodeException(
          outerHash, specKind, path, expectedClass, result.getClass());
    }
  }

  private ValSpec readInnerSpec(SpecKind specKind, Hash outerHash, Hash hash, String path,
      int index) {
    Spec result = wrapObjectDbExceptionAsDecodeSpecNodeException(
        specKind, outerHash, path, index, () -> get(hash));
    if (result instanceof ValSpec valSpec) {
      return valSpec;
    } else {
      throw new UnexpectedSpecNodeException(
          outerHash, specKind, path, index, ValSpec.class, result.getClass());
    }
  }

  // methods for creating Val Spec-s

  private ArraySpec newArraySpec(ValSpec elementSpec) throws HashedDbException {
    var rootHash = writeArraySpecRoot(elementSpec);
    return newArraySpec(rootHash, elementSpec);
  }

  private ArraySpec newArraySpec(Hash rootHash, ValSpec elementSpec) {
    return cache(new ArraySpec(rootHash, elementSpec));
  }

  private LambdaSpec newLambdaSpec(ValSpec result, RecSpec parameters) throws HashedDbException {
    var rootHash = writeLambdaSpecRoot(result, parameters);
    return newLambdaSpec(rootHash, result, parameters);
  }

  private LambdaSpec newLambdaSpec(Hash rootHash, ValSpec result, RecSpec parameters) {
    return cache(new LambdaSpec(rootHash, result, parameters));
  }

  private RecSpec newRecSpec(ImmutableList<ValSpec> itemSpecs) throws HashedDbException {
    var hash = writeRecSpecRoot(itemSpecs);
    return newRecSpec(hash, itemSpecs);
  }

  private RecSpec newRecSpec(Hash rootHash, ImmutableList<? extends ValSpec> itemSpecs) {
    return cache(new RecSpec(rootHash, itemSpecs));
  }

  private StructSpec newStructSpec(String name, NamedList<ValSpec> fields)
      throws HashedDbException {
    var rootHash = writeStructSpecRoot(name, fields);
    return newStructSpec(rootHash, name, fields);
  }

  private StructSpec newStructSpec(Hash rootHash, String name, NamedList<ValSpec> fields) {
    return cache(new StructSpec(rootHash, name, fields));
  }

  private VariableSpec newVariableSpec(String name) throws HashedDbException {
    var rootHash = writeVariableSpecRoot(name);
    return newVariableSpec(rootHash, name);
  }

  private VariableSpec newVariableSpec(Hash rootHash, String name) {
    return cache(new VariableSpec(rootHash, name));
  }

  // methods for creating Expr Spec-s

  private ArrayExprSpec newArrayExprSpec(ValSpec elementSpec) throws HashedDbException {
    var evaluationSpec = array(elementSpec);
    var rootHash = writeExprSpecRoot(ARRAY_EXPR, evaluationSpec);
    return newArrayExprSpec(rootHash, evaluationSpec);
  }

  private ArrayExprSpec newArrayExprSpec(Hash rootHash, ArraySpec evaluationSpec) {
    return cache(new ArrayExprSpec(rootHash, evaluationSpec));
  }

  private CallSpec newCallSpec(ValSpec evaluationSpec) throws HashedDbException {
    var rootHash = writeExprSpecRoot(CALL, evaluationSpec);
    return newCallSpec(rootHash, evaluationSpec);
  }

  private CallSpec newCallSpec(Hash rootHash, ValSpec evaluationSpec) {
    return cache(new CallSpec(rootHash, evaluationSpec));
  }

  private ConstSpec newConstSpec(ValSpec evaluationSpec) throws HashedDbException {
    var rootHash = writeExprSpecRoot(CONST, evaluationSpec);
    return newConstSpec(rootHash, evaluationSpec);
  }

  private ConstSpec newConstSpec(Hash rootHash, ValSpec evaluationSpec) {
    return cache(new ConstSpec(rootHash, evaluationSpec));
  }

  private InvokeSpec newInvokeSpec(ValSpec evaluationSpec) throws HashedDbException {
    var rootHash = writeExprSpecRoot(CONST, evaluationSpec);
    return newInvokeSpec(rootHash, evaluationSpec);
  }

  private InvokeSpec newInvokeSpec(Hash rootHash, ValSpec evaluationSpec) {
    return cache(new InvokeSpec(rootHash, evaluationSpec));
  }

  private RecExprSpec newRecExprSpec(RecSpec evaluationSpec) throws HashedDbException {
    var rootHash = writeExprSpecRoot(RECORD_EXPR, evaluationSpec);
    return newRecExprSpec(rootHash, evaluationSpec);
  }

  private RecExprSpec newRecExprSpec(Hash rootHash, RecSpec evaluationSpec) {
    return cache(new RecExprSpec(rootHash, evaluationSpec));
  }

  private RefSpec newRefSpec(ValSpec evaluationSpec) throws HashedDbException {
    var rootHash = writeExprSpecRoot(REF, evaluationSpec);
    return newRefSpec(rootHash, evaluationSpec);
  }

  private RefSpec newRefSpec(Hash rootHash, ValSpec evaluationSpec) {
    return cache(new RefSpec(rootHash, evaluationSpec));
  }

  private SelectSpec newSelectSpec(ValSpec evaluationSpec) throws HashedDbException {
    var rootHash = writeExprSpecRoot(SELECT, evaluationSpec);
    return newSelectSpec(rootHash, evaluationSpec);
  }

  private SelectSpec newSelectSpec(Hash rootHash, ValSpec evaluationSpec) {
    return cache(new SelectSpec(rootHash, evaluationSpec));
  }

  private StructExprSpec newStructExprSpec(StructSpec evaluationSpec) throws HashedDbException {
    var rootHash = writeExprSpecRoot(STRUCT_EXPR, evaluationSpec);
    return newStructExprSpec(rootHash, evaluationSpec);
  }

  private StructExprSpec newStructExprSpec(Hash rootHash, StructSpec evaluationSpec) {
    return cache(new StructExprSpec(rootHash, evaluationSpec));
  }

  private <T extends Spec> T cache(T spec) {
    @SuppressWarnings("unchecked")
    T result = (T) requireNonNullElse(cache.putIfAbsent(spec.hash(), spec), spec);
    return result;
  }

  // Methods for writing Val spec root

  private Hash writeArraySpecRoot(Spec elementSpec) throws HashedDbException {
    return writeNonBaseSpecRoot(ARRAY, elementSpec.hash());
  }

  private Hash writeLambdaSpecRoot(ValSpec result, RecSpec parameters) throws HashedDbException {
    var hash = hashedDb.writeSequence(result.hash(), parameters.hash());
    return writeNonBaseSpecRoot(LAMBDA, hash);
  }

  private Hash writeRecSpecRoot(ImmutableList<? extends Spec> itemSpecs) throws HashedDbException {
    var itemsHash = hashedDb.writeSequence(map(itemSpecs, Spec::hash));
    return writeNonBaseSpecRoot(RECORD, itemsHash);
  }

  private Hash writeStructSpecRoot(String name, NamedList<ValSpec> fields)
      throws HashedDbException {
    var nameHash = hashedDb.writeString(name);
    var fieldsSequenceHash = writeFieldsSequence(fields);
    var namesSequenceHash = writeNamesSequence(fields);
    var specData = hashedDb.writeSequence(nameHash, fieldsSequenceHash, namesSequenceHash);
    return writeNonBaseSpecRoot(STRUCT, specData);
  }

  private Hash writeFieldsSequence(NamedList<ValSpec> fields) throws HashedDbException {
    return hashedDb.writeSequence(map(fields.list(), f -> f.object().hash()));
  }

  private Hash writeNamesSequence(NamedList<ValSpec> fields) throws HashedDbException {
    var nameHashes = new ArrayList<Hash>(fields.size());
    for (Named<ValSpec> field : fields.list()) {
      nameHashes.add(hashedDb.writeString(field.saneName()));
    }
    return hashedDb.writeSequence(nameHashes);
  }

  private Hash writeVariableSpecRoot(String name) throws HashedDbException {
    var nameHash = hashedDb.writeString(name);
    return writeNonBaseSpecRoot(VARIABLE, nameHash);
  }

  // Helper methods for writing roots

  private Hash writeExprSpecRoot(SpecKind specKind, Spec evaluationSpec) throws HashedDbException {
    return writeNonBaseSpecRoot(specKind, evaluationSpec.hash());
  }

  private Hash writeNonBaseSpecRoot(SpecKind specKind, Hash dataHash) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(specKind.marker()), dataHash);
  }

  private Hash writeBaseSpecRoot(SpecKind specKind) throws HashedDbException {
    return hashedDb.writeSequence(hashedDb.writeByte(specKind.marker()));
  }

  // Helper methods for reading

  private ImmutableList<Hash> readSequenceHashes(
      Hash rootHash, Hash sequenceHash, SpecKind specKind, String path) {
    return wrapHashedDbExceptionAsDecodeSpecNodeException(
        rootHash, specKind, path, () -> hashedDb.readSequence(sequenceHash));
  }
}
