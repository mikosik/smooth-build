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
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.DecodeObjTypeException;
import org.smoothbuild.db.object.obj.exc.NoSuchObjException;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefFuncH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IfFuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFuncH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.TypeHDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.SelectTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.DefFuncTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.NatFuncTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjectHDb {
  private final HashedDb hashedDb;
  private final TypeHDb typeHDb;
  private final TypingH typing;

  private final IfFuncH ifFunc;
  private final MapFuncH mapFunc;

  public ObjectHDb(HashedDb hashedDb, TypeHDb typeHDb,
      TypingH typing) {
    this.hashedDb = hashedDb;
    this.typeHDb = typeHDb;
    this.typing = typing;

    try {
      this.ifFunc = newIfFunc();
      this.mapFunc = newMapFunc();
    } catch (HashedDbException e) {
      throw new ObjectHDbException(e);
    }
  }

  // methods for creating ValueH subclasses

  public ArrayHBuilder arrayBuilder(TypeH elemType) {
    return new ArrayHBuilder(typeHDb.array(elemType), this);
  }

  public BlobHBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobHBuilder(this, hashedDb.sink()));
  }

  public BoolH bool(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBool(value));
  }

  public DefFuncH defFunc(DefFuncTypeH type, ObjectH body) {
    if (!typing.isAssignable(type.result(), body.type())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.result().name()
          + " but body.evaluationType() is " + body.type().name() + ".");
    }
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunc(type, body));
  }

  public IntH int_(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInt(value));
  }

  public NatFuncH natFunc(
      NatFuncTypeH type, BlobH jarFile, StringH classBinaryName, BoolH isPure) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newNatFunc(type, jarFile, classBinaryName, isPure));
  }

  public StringH string(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newString(value));
  }

  public TupleH tuple(TupleTypeH tupleType, ImmutableList<ValueH> items) {
    var types = tupleType.items();
    allMatchOtherwise(types, items, (s, i) -> Objects.equals(s, i.spec()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + types.get(i).name() + " but provided item has type "
              + items.get(i).spec().name() + " at that index.");
        }
    );

    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(tupleType, items));
  }

  // methods for creating ExprH subclasses

  public CallH call(ObjectH func, ConstructH arguments) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(func, arguments));
  }

  public ConstructH construct(ImmutableList<ObjectH> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstruct(items));
  }

  public IfFuncH ifFunc() {
    return ifFunc;
  }

  public MapFuncH mapFunc() {
    return mapFunc;
  }

  public OrderH order(ImmutableList<ObjectH> elems) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elems));
  }

  public RefH ref(BigInteger value, TypeH evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evaluationType, value));
  }

  public SelectH select(ObjectH tuple, IntH index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(tuple, index));
  }

  // generic getter

  public ObjectH get(Hash rootHash) {
    List<Hash> hashes = decodeRootSequence(rootHash);
    if (hashes.size() != 2) {
      throw wrongSizeOfRootSequenceException(rootHash, hashes.size());
    }
    SpecH type = getTypeOrChainException(rootHash, hashes.get(0));
    Hash dataHash = hashes.get(1);
    return type.newObj(new MerkleRoot(rootHash, type, dataHash), this);
  }

  private SpecH getTypeOrChainException(Hash rootHash, Hash typeHash) {
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

  public ArrayH newArray(ArrayTypeH type, List<ValueH> elems) throws HashedDbException {
    var data = writeArrayData(elems);
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

  private DefFuncH newFunc(DefFuncTypeH type, ObjectH body)
      throws HashedDbException {
    var data = writeFuncData(body);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private IntH newInt(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = newRoot(typeHDb.int_(), data);
    return typeHDb.int_().newObj(root, this);
  }

  private NatFuncH newNatFunc(NatFuncTypeH type, BlobH jarFile,
      StringH classBinaryName, BoolH isPure) throws HashedDbException {
    var data = writeNatFuncData(jarFile, classBinaryName, isPure);
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

  private CallH newCall(ObjectH func, ConstructH arguments) throws HashedDbException {
    var resultType = inferCallResultType(func, arguments);
    var type = typeHDb.call(resultType);
    var data = writeCallData(func, arguments);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private TypeH inferCallResultType(ObjectH func, ConstructH arguments) {
    var funcType = funcEvaluationType(func);
    var argTypes = arguments.type().items();
    var paramTypes = funcType.params();
    allMatchOtherwise(
        paramTypes,
        argTypes,
        typing::isParamAssignable,
        (expectedSize, actualSize) -> illegalArguments(funcType, arguments),
        i -> illegalArguments(funcType, arguments)
    );
    var varBounds = typing.inferVariableBoundsInCall(paramTypes, argTypes);
    return typing.mapVariables(funcType.result(), varBounds, typeHDb.lower());
  }

  private void illegalArguments(FuncTypeH funcType, ConstructH arguments) {
    throw new IllegalArgumentException(
        "Arguments evaluation type %s should be equal to function evaluation type parameters %s."
            .formatted(arguments.type().name(), funcType.paramsTuple().name()));
  }

  private FuncTypeH funcEvaluationType(ObjectH func) {
    if (func.type() instanceof FuncTypeH funcType) {
      return funcType;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to function.");
    }
  }

  private IfFuncH newIfFunc() throws HashedDbException {
    var type = typeHDb.ifFunc();
    return (IfFuncH) newInternalFunc(type);
  }

  private FuncH newInternalFunc(FuncTypeH type) throws HashedDbException {
    // Internal funcs don't have any data. We use empty sequence as its data so
    // code reading such func from hashedDb can be simpler and code that stores
    // h-objects as artifacts doesn't need handle this special case.
    Hash dataHash = hashedDb.writeSequence();
    var root = newRoot(type, dataHash);
    return type.newObj(root, this);
  }

  private OrderH newOrder(ImmutableList<ObjectH> elems) throws HashedDbException {
    TypeH elemType = elemType(elems);
    var type = typeHDb.order(elemType);
    var data = writeOrderData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private TypeH elemType(ImmutableList<ObjectH> elems) {
    Optional<TypeH> elemType = elems.stream()
        .map(ObjectH::type)
        .reduce((type1, type2) -> {
          if (type1.equals(type2)) {
            return type1;
          } else {
            throw new IllegalArgumentException("Element evaluation types are not equal "
                + type1.name() + " != " + type2.name() + ".");
          }
        });
    SpecH type = elemType.orElse(typeHDb.nothing());
    if (type instanceof TypeH typeH) {
      return typeH;
    } else {
      throw new IllegalArgumentException(
          "Element type should be ValOType but was " + type.getClass().getCanonicalName());
    }
  }

  private ConstructH newConstruct(ImmutableList<ObjectH> items) throws HashedDbException {
    var itemTypes = Lists.map(items, ObjectH::type);
    var evaluationType = typeHDb.tuple(itemTypes);
    var type = typeHDb.construct(evaluationType);
    var data = writeConstructData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MapFuncH newMapFunc() throws HashedDbException {
    var type = typeHDb.mapFunc();
    return (MapFuncH) newInternalFunc(type);
  }

  private SelectH newSelect(ObjectH tuple, IntH index) throws HashedDbException {
    var type = selectType(tuple, index);
    var data = writeSelectData(tuple, index);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private SelectTypeH selectType(ObjectH expr, IntH index) {
    if (expr.type() instanceof TupleTypeH tuple) {
      int intIndex = index.jValue().intValue();
      ImmutableList<TypeH> items = tuple.items();
      checkElementIndex(intIndex, items.size());
      var itemType = items.get(intIndex);
      return typeHDb.select(itemType);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private RefH newRef(TypeH evaluationType, BigInteger index) throws HashedDbException {
    var data = writeRefData(index);
    var type = typeHDb.ref(evaluationType);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MerkleRoot newRoot(SpecH type, Hash dataHash) throws HashedDbException {
    Hash rootHash = hashedDb.writeSequence(type.hash(), dataHash);
    return new MerkleRoot(rootHash, type, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ObjectH func, ConstructH arguments) throws HashedDbException {
    return hashedDb.writeSequence(func.hash(), arguments.hash());
  }

  private Hash writeConstructData(ImmutableList<ObjectH> items) throws HashedDbException {
    return writeSequence(items);
  }

  private Hash writeNatFuncData(BlobH jarFile, StringH classBinaryName, BoolH isPure)
      throws HashedDbException {
    return hashedDb.writeSequence(jarFile.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeOrderData(ImmutableList<ObjectH> elems) throws HashedDbException {
    return writeSequence(elems);
  }

  private Hash writeRefData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(ObjectH tuple, IntH index) throws HashedDbException {
    return hashedDb.writeSequence(tuple.hash(), index.hash());
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(List<ValueH> elems) throws HashedDbException {
    return writeSequence(elems);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeFuncData(ObjectH body) {
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

  public Typing<TypeH> typing() {
    return typing;
  }
}
