package org.smoothbuild.db.object.obj;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.map;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.db.ObjDbException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.exc.DecodeObjTypeException;
import org.smoothbuild.db.object.obj.exc.NoSuchObjException;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.obj.expr.Invoke;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.NativeMethod;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.type.ObjTypeDb;
import org.smoothbuild.db.object.type.base.TypeO;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.expr.SelectOType;
import org.smoothbuild.db.object.type.val.ArrayTypeO;
import org.smoothbuild.db.object.type.val.LambdaTypeO;
import org.smoothbuild.db.object.type.val.TupleTypeO;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjDb {
  private final HashedDb hashedDb;
  private final ObjTypeDb objTypeDb;

  public ObjDb(HashedDb hashedDb, ObjTypeDb objTypeDb) {
    this.hashedDb = hashedDb;
    this.objTypeDb = objTypeDb;
  }

  // methods for creating value or value builders

  public ArrayBuilder arrayBuilder(TypeV elementType) {
    return new ArrayBuilder(objTypeDb.array(elementType), this);
  }

  public BlobBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobBuilder(this, hashedDb.sink()));
  }

  public Bool bool(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBool(value));
  }

  public Lambda lambda(LambdaTypeO type, Expr body) {
    if (!Objects.equals(type.result(), body.evaluationType())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.result().name()
          + " but body.evaluationType() is " + body.evaluationType().name() + ".");
    }
    return wrapHashedDbExceptionAsObjectDbException(() -> newLambda(type, body));
  }

  public Int int_(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInt(value));
  }

  public NativeMethod nativeMethod(Blob jarFile, Str classBinaryName) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newNativeMethod(jarFile, classBinaryName));
  }

  public Str string(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newString(value));
  }

  public Tuple tuple(TupleTypeO tupleType, Iterable<? extends Obj> items) {
    List<Obj> itemList = ImmutableList.copyOf(items);
    var types = tupleType.items();

    allMatchOtherwise(types, itemList, (s, i) -> Objects.equals(s, i.type()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + types.get(i).name() + " but provided item has type "
              + itemList.get(i).type().name() + " at that index.");
        }
    );

    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(tupleType, itemList));
  }

  // methods for creating expr-s

  public Call call(Expr function, Construct arguments) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(function, arguments));
  }

  public Const const_(Val val) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConst(val));
  }

  public Construct construct(ImmutableList<? extends Expr> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newConstruct(items));
  }

  public Invoke invoke(
      TypeV evaluationType, NativeMethod nativeMethod, Bool isPure, Int argumentCount) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newInvoke(evaluationType, nativeMethod, isPure, argumentCount));
  }

  public Order order(List<? extends Expr> elements) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elements));
  }

  public Ref ref(BigInteger value, TypeV evaluationType) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newRef(evaluationType, value));
  }

  public Select select(Expr tuple, Int index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(tuple, index));
  }

  // generic getter

  public Obj get(Hash rootHash) {
    List<Hash> hashes = decodeRootSequence(rootHash);
    if (hashes.size() != 2) {
      throw wrongSizeOfRootSequenceException(rootHash, hashes.size());
    }
    TypeO type = getTypeOrChainException(rootHash, hashes.get(0));
    Hash dataHash = hashes.get(1);
    return type.newObj(new MerkleRoot(rootHash, type, dataHash), this);
  }

  private TypeO getTypeOrChainException(Hash rootHash, Hash typeHash) {
    try {
      return objTypeDb.get(typeHash);
    } catch (ObjDbException e) {
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

  public Array newArray(ArrayTypeO type, List<? extends Obj> elements) throws HashedDbException {
    var data = writeArrayData(elements);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  public Blob newBlob(Hash dataHash) throws HashedDbException {
    var root = newRoot(objTypeDb.blob(), dataHash);
    return objTypeDb.blob().newObj(root, this);
  }

  private Bool newBool(boolean value) throws HashedDbException {
    var data = writeBoolData(value);
    var root = newRoot(objTypeDb.bool(), data);
    return objTypeDb.bool().newObj(root, this);
  }

  private Lambda newLambda(LambdaTypeO type, Expr body) throws HashedDbException {
    var data = writeLambdaData(body);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private Int newInt(BigInteger value) throws HashedDbException {
    var data = writeIntData(value);
    var root = newRoot(objTypeDb.int_(), data);
    return objTypeDb.int_().newObj(root, this);
  }

  private NativeMethod newNativeMethod(Blob jarFile, Str classBinaryName) throws HashedDbException {
    var type = objTypeDb.nativeMethod();
    var data = writeNativeMethodData(jarFile, classBinaryName);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private Str newString(String string) throws HashedDbException {
    var data = writeStringData(string);
    var root = newRoot(objTypeDb.string(), data);
    return objTypeDb.string().newObj(root, this);
  }

  private Tuple newTuple(TupleTypeO type, List<? extends Obj> objects) throws HashedDbException {
    var data = writeTupleData(objects);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  // methods for creating Expr-s

  private Call newCall(Expr function, Construct arguments) throws HashedDbException {
    var lambdaType = functionevaluationType(function);
    verifyArguments(lambdaType, arguments);
    var type = objTypeDb.call(lambdaType.result());
    var data = writeCallData(function, arguments);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private static void verifyArguments(LambdaTypeO lambdaType, Construct arguments) {
    if (!Objects.equals(lambdaType.parametersTuple(), arguments.evaluationType())) {
      throw new IllegalArgumentException(("Arguments evaluation type %s should be equal to "
          + "function evaluation type parameters %s.")
          .formatted(arguments.evaluationType().name(), lambdaType.parametersTuple().name()));
    }
  }

  private LambdaTypeO functionevaluationType(Expr function) {
    if (function.evaluationType() instanceof LambdaTypeO lambdaType) {
      return lambdaType;
    } else {
      throw new IllegalArgumentException("`function` component doesn't evaluate to Function.");
    }
  }

  private Const newConst(Val val) throws HashedDbException {
    var type = objTypeDb.const_(val.type());
    var data = writeConstData(val);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private Invoke newInvoke(TypeV evaluationType, NativeMethod nativeMethod, Bool isPure,
      Int argumentCount) throws HashedDbException {
    var data = writeInvokeData(nativeMethod, isPure, argumentCount);
    var type = objTypeDb.invoke(evaluationType);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private Order newOrder(List<? extends Expr> elements) throws HashedDbException {
    TypeV elementType = elementType(elements);
    var type = objTypeDb.order(elementType);
    var data = writeOrderData(elements);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private TypeV elementType(List<? extends Expr> elements) {
    Optional<TypeV> elementType = elements.stream()
        .map(expr -> expr.type().evaluationType())
        .reduce((type1, type2) -> {
          if (type1.equals(type2)) {
            return type1;
          } else {
            throw new IllegalArgumentException("Element evaluation types are not equal "
                + type1.name() + " != " + type2.name() + ".");
          }
        });
    TypeO type = elementType.orElse(objTypeDb.nothing());
    if (type instanceof TypeV typeV) {
      return typeV;
    } else {
      throw new IllegalArgumentException(
          "Element type should be ValOType but was " + type.getClass().getCanonicalName());
    }
  }

  private Construct newConstruct(List<? extends Expr> items) throws HashedDbException {
    var itemTypes = map(items, Expr::evaluationType);
    var evaluationType = objTypeDb.tuple(itemTypes);
    var type = objTypeDb.construct(evaluationType);
    var data = writeConstructData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private Select newSelect(Expr tuple, Int index) throws HashedDbException {
    var type = selectType(tuple, index);
    var data = writeSelectData(tuple, index);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private SelectOType selectType(Expr expr, Int index) {
    if (expr.type().evaluationType() instanceof TupleTypeO tuple) {
      int intIndex = index.jValue().intValue();
      ImmutableList<TypeV> items = tuple.items();
      checkElementIndex(intIndex, items.size());
      var itemType = items.get(intIndex);
      return objTypeDb.select(itemType);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private Ref newRef(TypeV evaluationType, BigInteger index) throws HashedDbException {
    var data = writeRefData(index);
    var type = objTypeDb.ref(evaluationType);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MerkleRoot newRoot(TypeO type, Hash dataHash) throws HashedDbException {
    Hash rootHash = hashedDb.writeSequence(type.hash(), dataHash);
    return new MerkleRoot(rootHash, type, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(Expr function, Construct arguments) throws HashedDbException {
    return hashedDb.writeSequence(function.hash(), arguments.hash());
  }

  private Hash writeConstData(Val val) {
    return val.hash();
  }

  private Hash writeConstructData(List<? extends Expr> items) throws HashedDbException {
    return writeSequence(items);
  }

  private Hash writeInvokeData(NativeMethod nativeMethod,
      Bool isPure, Int argumentCount) throws HashedDbException {
    return hashedDb.writeSequence(nativeMethod.hash(), isPure.hash(), argumentCount.hash());
  }

  private Hash writeNativeMethodData(Blob jarFile, Str classBinaryName) throws HashedDbException {
    return hashedDb.writeSequence(jarFile.hash(), classBinaryName.hash());
  }

  private Hash writeOrderData(List<? extends Expr> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeRefData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(Expr tuple, Int index) throws HashedDbException {
    return hashedDb.writeSequence(tuple.hash(), index.hash());
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(List<? extends Obj> elements) throws HashedDbException {
    return writeSequence(elements);
  }

  private Hash writeBoolData(boolean value) throws HashedDbException {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbException {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeLambdaData(Expr body) {
    return body.hash();
  }

  private Hash writeStringData(String string) throws HashedDbException {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(List<? extends Obj> items) throws HashedDbException {
    return writeSequence(items);
  }

  // helpers

  private Hash writeSequence(List<? extends Obj> objs) throws HashedDbException {
    var hashes = map(objs, Obj::hash);
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
