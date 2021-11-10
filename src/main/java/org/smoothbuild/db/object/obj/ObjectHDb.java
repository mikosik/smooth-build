package org.smoothbuild.db.object.obj;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.db.ObjectHDbException;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.DecodeObjTypeException;
import org.smoothbuild.db.object.obj.exc.NoSuchObjException;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NativeMethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.TypeHDb;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjectHDb {
  private final HashedDb hashedDb;
  private final TypeHDb typeHDb;

  public ObjectHDb(HashedDb hashedDb, TypeHDb typeHDb) {
    this.hashedDb = hashedDb;
    this.typeHDb = typeHDb;
  }

  // methods for creating ValueH subclasses

  public ArrayHBuilder arrayBuilder(TypeHV elementType) {
    return new ArrayHBuilder(typeHDb.array(elementType), this);
  }

  public BlobHBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobHBuilder(this, hashedDb.sink()));
  }

  public BoolH bool(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBool(value));
  }

  public FunctionH function(FunctionTypeH type, ExprH body) {
    if (!Objects.equals(type.result(), body.evaluationType())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.result().name()
          + " but body.evaluationType() is " + body.evaluationType().name() + ".");
    }
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunction(type, body));
  }

  public IntH int_(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInt(value));
  }

  public NativeMethodH nativeMethod(BlobH jarFile, StringH classBinaryName) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newNativeMethod(jarFile, classBinaryName));
  }

  public StringH string(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newString(value));
  }

  public TupleH tuple(TupleTypeH tupleType, ImmutableList<ValueH> items) {
    var types = tupleType.items();
    allMatchOtherwise(types, items, (s, i) -> Objects.equals(s, i.type()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + types.get(i).name() + " but provided item has type "
              + items.get(i).type().name() + " at that index.");
        }
    );

    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(tupleType, items));
  }

  // methods for creating ExprH subclasses

  public CallH call(ExprH function, ConstructH arguments) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(function, arguments));
  }

  public ConstH const_(ValueH val) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConst(val));
  }

  public ConstructH construct(ImmutableList<ExprH> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstruct(items));
  }

  public IfH if_(ExprH condition, ExprH then, ExprH else_) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIf(condition, then, else_));
  }

  public InvokeH invoke(
      TypeHV evaluationType, NativeMethodH nativeMethod, BoolH isPure, IntH argumentCount) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newInvoke(evaluationType, nativeMethod, isPure, argumentCount));
  }

  public MapH map(ExprH array, ExprH function) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newMap(array, function));
  }

  public OrderH order(ImmutableList<ExprH> elements) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elements));
  }

  public RefH ref(BigInteger value, TypeHV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evaluationType, value));
  }

  public SelectH select(ExprH tuple, IntH index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(tuple, index));
  }

  // generic getter

  public ObjectH get(Hash rootHash) {
    List<Hash> hashes = decodeRootSequence(rootHash);
    if (hashes.size() != 2) {
      throw wrongSizeOfRootSequenceException(rootHash, hashes.size());
    }
    TypeH type = getTypeOrChainException(rootHash, hashes.get(0));
    Hash dataHash = hashes.get(1);
    return type.newObj(new MerkleRoot(rootHash, type, dataHash), this);
  }

  private TypeH getTypeOrChainException(Hash rootHash, Hash typeHash) {
    try {
      return typeHDb.get(typeHash);
    } catch (ObjectHDbException e) {
      throw new DecodeObjTypeException(rootHash, e);
    }
  }

  private List<Hash> decodeRootSequence(Hash rootHash) {
    try {
      return hashedDb.readSequence(rootHash);
    } catch (NoSuchDataException e) {
      throw new NoSuchObjException(rootHash, e);
    } catch (HashedDbException e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  // methods for creating Val Obj-s

  public ArrayH newArray(ArrayTypeH type, List<ValueH> elements) throws HashedDbException {
    var data = writeArrayData(elements);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  public BlobH newBlob(Hash dataHash) throws HashedDbException {
    var root = newRoot(typeHDb.blob(), dataHash);
    return typeHDb.blob().newObj(root, this);
  }

  private BoolH newBool(boolean value) throws HashedDbException {
    var data = writeBoolData(value);
    var root = newRoot(typeHDb.bool(), data);
    return typeHDb.bool().newObj(root, this);
  }

  private FunctionH newFunction(FunctionTypeH type, ExprH body) throws HashedDbException {
    var data = writeFunctionData(body);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private IntH newInt(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = newRoot(typeHDb.int_(), data);
    return typeHDb.int_().newObj(root, this);
  }

  private NativeMethodH newNativeMethod(BlobH jarFile, StringH classBinaryName) throws HashedDbException {
    var type = typeHDb.nativeMethod();
    var data = writeNativeMethodData(jarFile, classBinaryName);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private StringH newString(String string) throws HashedDbException {
    var data = writeStringData(string);
    var root = newRoot(typeHDb.string(), data);
    return typeHDb.string().newObj(root, this);
  }

  private TupleH newTuple(TupleTypeH type, ImmutableList<ValueH> objects) throws HashedDbException {
    var data = writeTupleData(objects);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  // methods for creating Expr-s

  private CallH newCall(ExprH function, ConstructH arguments) throws HashedDbException {
    var functionType = functionEvaluationType(function);
    verifyArguments(functionType, arguments);
    var type = typeHDb.call(functionType.result());
    var data = writeCallData(function, arguments);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private static void verifyArguments(FunctionTypeH functionType, ConstructH arguments) {
    if (!Objects.equals(functionType.parametersTuple(), arguments.evaluationType())) {
      throw new IllegalArgumentException(("Arguments evaluation type %s should be equal to "
          + "function evaluation type parameters %s.")
          .formatted(arguments.evaluationType().name(), functionType.parametersTuple().name()));
    }
  }

  private FunctionTypeH functionEvaluationType(ExprH function) {
    if (function.evaluationType() instanceof FunctionTypeH functionType) {
      return functionType;
    } else {
      throw new IllegalArgumentException("`function` component doesn't evaluate to Function.");
    }
  }

  private ConstH newConst(ValueH val) throws HashedDbException {
    var type = typeHDb.const_(val.type());
    var data = writeConstData(val);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private IfH newIf(ExprH condition, ExprH then, ExprH else_) throws HashedDbException {
    var evaluationType = then.evaluationType();
    var type = typeHDb.if_(evaluationType);
    var data = writeIfData(condition, then, else_);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private InvokeH newInvoke(TypeHV evaluationType, NativeMethodH nativeMethod, BoolH isPure,
      IntH argumentCount) throws HashedDbException {
    var data = writeInvokeData(nativeMethod, isPure, argumentCount);
    var type = typeHDb.invoke(evaluationType);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MapH newMap(ExprH array, ExprH function) throws HashedDbException {
    // TODO type-variables are not handled by this line
    var evaluationType = typeHDb.array(functionEvaluationType(function).result());
    var type = typeHDb.map(evaluationType);
    var data = writeMapData(array, function);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private OrderH newOrder(ImmutableList<ExprH> elements) throws HashedDbException {
    TypeHV elementType = elementType(elements);
    var type = typeHDb.order(elementType);
    var data = writeOrderData(elements);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private TypeHV elementType(ImmutableList<ExprH> elements) {
    Optional<TypeHV> elementType = elements.stream()
        .map(expr -> expr.type().evaluationType())
        .reduce((type1, type2) -> {
          if (type1.equals(type2)) {
            return type1;
          } else {
            throw new IllegalArgumentException("Element evaluation types are not equal "
                + type1.name() + " != " + type2.name() + ".");
          }
        });
    TypeH type = elementType.orElse(typeHDb.nothing());
    if (type instanceof TypeHV typeHV) {
      return typeHV;
    } else {
      throw new IllegalArgumentException(
          "Element type should be ValOType but was " + type.getClass().getCanonicalName());
    }
  }

  private ConstructH newConstruct(ImmutableList<ExprH> items) throws HashedDbException {
    var itemTypes = Lists.map(items, ExprH::evaluationType);
    var evaluationType = typeHDb.tuple(itemTypes);
    var type = typeHDb.construct(evaluationType);
    var data = writeConstructData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private SelectH newSelect(ExprH tuple, IntH index) throws HashedDbException {
    var type = selectType(tuple, index);
    var data = writeSelectData(tuple, index);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private SelectTypeH selectType(ExprH expr, IntH index) {
    if (expr.type().evaluationType() instanceof TupleTypeH tuple) {
      int intIndex = index.jValue().intValue();
      ImmutableList<TypeHV> items = tuple.items();
      checkElementIndex(intIndex, items.size());
      var itemType = items.get(intIndex);
      return typeHDb.select(itemType);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private RefH newRef(TypeHV evaluationType, BigInteger index) throws HashedDbException {
    var data = writeRefData(index);
    var type = typeHDb.ref(evaluationType);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MerkleRoot newRoot(TypeH type, Hash dataHash) throws HashedDbException {
    Hash rootHash = hashedDb.writeSequence(type.hash(), dataHash);
    return new MerkleRoot(rootHash, type, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ExprH function, ConstructH arguments) throws HashedDbException {
    return hashedDb.writeSequence(function.hash(), arguments.hash());
  }

  private Hash writeConstData(ValueH val) {
    return val.hash();
  }

  private Hash writeConstructData(ImmutableList<ExprH> items) throws HashedDbException {
    return writeSequence(items);
  }

  private Hash writeIfData(ExprH condition, ExprH then, ExprH else_) throws HashedDbException {
    return hashedDb.writeSequence(condition.hash(), then.hash(), else_.hash());
  }

  private Hash writeInvokeData(NativeMethodH nativeMethod,
      BoolH isPure, IntH argumentCount) throws HashedDbException {
    return hashedDb.writeSequence(nativeMethod.hash(), isPure.hash(), argumentCount.hash());
  }

  private Hash writeMapData(ExprH array, ExprH function) throws HashedDbException {
    return hashedDb.writeSequence(array.hash(), function.hash());
  }

  private Hash writeNativeMethodData(BlobH jarFile, StringH classBinaryName) throws HashedDbException {
    return hashedDb.writeSequence(jarFile.hash(), classBinaryName.hash());
  }

  private Hash writeOrderData(ImmutableList<ExprH> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeRefData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(ExprH tuple, IntH index) throws HashedDbException {
    return hashedDb.writeSequence(tuple.hash(), index.hash());
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(List<ValueH> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeFunctionData(ExprH body) {
    return body.hash();
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(ImmutableList<ValueH> items) throws HashedDbException {
    return writeSequence(items);
  }

  // helpers

  private Hash writeSequence(List<? extends ObjectH> objs) throws HashedDbException {
    var hashes = Lists.map(objs, ObjectH::hash);
    return hashedDb.writeSequence(hashes);
  }

  public ImmutableList<Hash> readSequence(Hash hash) throws HashedDbException {
    return hashedDb().readSequence(hash);
  }

  // TODO visible for classes from db.object package tree until creating Obj is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }
}
