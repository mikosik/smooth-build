package org.smoothbuild.db.object.obj;

import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.hashed.exc.NoSuchDataExc;
import org.smoothbuild.db.object.db.ObjDbExc;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeObjCatExc;
import org.smoothbuild.db.object.obj.exc.NoSuchObjExc;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
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
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.SelectCH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.DefFuncTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.NatFuncTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class ObjDb {
  private final HashedDb hashedDb;
  private final CatDb catDb;
  private final TypingH typing;

  private final IfFuncH ifFunc;
  private final MapFuncH mapFunc;

  public ObjDb(HashedDb hashedDb, CatDb catDb, TypingH typing) {
    this.hashedDb = hashedDb;
    this.catDb = catDb;
    this.typing = typing;

    try {
      this.ifFunc = newIfFunc();
      this.mapFunc = newMapFunc();
    } catch (HashedDbExc e) {
      throw new ObjDbExc(e);
    }
  }

  // methods for creating ValueH subclasses

  public ArrayHBuilder arrayBuilder(ArrayTH type) {
    return new ArrayHBuilder(type, this);
  }

  public BlobHBuilder blobBuilder() {
    return wrapHashedDbExceptionAsObjectDbException(() -> new BlobHBuilder(this, hashedDb.sink()));
  }

  public BoolH bool(boolean value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newBool(value));
  }

  public DefFuncH defFunc(DefFuncTH type, ObjH body) {
    if (!typing.isAssignable(type.res(), body.type())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.res().name()
          + " but body.type() is " + body.type().name() + ".");
    }
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunc(type, body));
  }

  public IntH int_(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInt(value));
  }

  public NatFuncH natFunc(
      NatFuncTH type, BlobH jarFile, StringH classBinaryName, BoolH isPure) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newNatFunc(type, jarFile, classBinaryName, isPure));
  }

  public StringH string(String value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newString(value));
  }

  public TupleH tuple(TupleTH tupleT, ImmutableList<ValH> items) {
    var types = tupleT.items();
    allMatchOtherwise(types, items, (s, i) -> Objects.equals(s, i.cat()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + types.get(i).name() + " but provided item has type "
              + items.get(i).cat().name() + " at that index.");
        }
    );

    return wrapHashedDbExceptionAsObjectDbException(() -> newTuple(tupleT, items));
  }

  // methods for creating ExprH subclasses

  public CallH call(ObjH callable, CombineH args) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCall(callable, args));
  }

  public CombineH combine(ImmutableList<ObjH> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCombine(items));
  }

  public IfFuncH ifFunc() {
    return ifFunc;
  }

  public MapFuncH mapFunc() {
    return mapFunc;
  }

  public OrderH order(ImmutableList<ObjH> elems) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(elems));
  }

  public ParamRefH newParamRef(BigInteger value, TypeH evalT) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newParamRef(evalT, value));
  }

  public SelectH select(ObjH selectable, IntH index) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newSelect(selectable, index));
  }

  // generic getter

  public ObjH get(Hash rootHash) {
    List<Hash> hashes = decodeRootSeq(rootHash);
    if (hashes.size() != 2) {
      throw wrongSizeOfRootSeqException(rootHash, hashes.size());
    }
    CatH cat = getCatOrChainException(rootHash, hashes.get(0));
    Hash dataHash = hashes.get(1);
    return cat.newObj(new MerkleRoot(rootHash, cat, dataHash), this);
  }

  private CatH getCatOrChainException(Hash rootHash, Hash typeHash) {
    try {
      return catDb.get(typeHash);
    } catch (ObjDbExc e) {
      throw new DecodeObjCatExc(rootHash, e);
    }
  }

  private List<Hash> decodeRootSeq(Hash rootHash) {
    try {
      return hashedDb.readSeq(rootHash);
    } catch (NoSuchDataExc e) {
      throw new NoSuchObjExc(rootHash, e);
    } catch (HashedDbExc e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  // methods for creating Val Obj-s

  public ArrayH newArray(ArrayTH type, List<ValH> elems) throws HashedDbExc {
    var data = writeArrayData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  public BlobH newBlob(Hash dataHash) throws HashedDbExc {
    var root = newRoot(catDb.blob(), dataHash);
    return catDb.blob().newObj(root, this);
  }

  private BoolH newBool(boolean value) throws HashedDbExc {
    var data = writeBoolData(value);
    var root = newRoot(catDb.bool(), data);
    return catDb.bool().newObj(root, this);
  }

  private DefFuncH newFunc(DefFuncTH type, ObjH body)
      throws HashedDbExc {
    var data = writeFuncData(body);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private IntH newInt(BigInteger value) throws HashedDbExc {
    var data = writeIntData(value);
    var root = newRoot(catDb.int_(), data);
    return catDb.int_().newObj(root, this);
  }

  private NatFuncH newNatFunc(NatFuncTH type, BlobH jarFile,
      StringH classBinaryName, BoolH isPure) throws HashedDbExc {
    var data = writeNatFuncData(jarFile, classBinaryName, isPure);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private StringH newString(String string) throws HashedDbExc {
    var data = writeStringData(string);
    var root = newRoot(catDb.string(), data);
    return catDb.string().newObj(root, this);
  }

  private TupleH newTuple(TupleTH type, ImmutableList<ValH> vals) throws HashedDbExc {
    var data = writeTupleData(vals);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  // methods for creating Expr-s

  private CallH newCall(ObjH callable, CombineH args) throws HashedDbExc {
    var resT = inferCallResType(callable, args);
    var type = catDb.call(resT);
    var data = writeCallData(callable, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private TypeH inferCallResType(ObjH callable, CombineH args) {
    var funcT = callableEvalT(callable);
    var argTs = args.type().items();
    var paramTs = funcT.params();
    allMatchOtherwise(
        paramTs,
        argTs,
        typing::isParamAssignable,
        (expectedSize, actualSize) -> illegalArgs(funcT, args),
        i -> illegalArgs(funcT, args)
    );
    var varBounds = typing.inferVarBoundsInCall(paramTs, argTs);
    return typing.mapVars(funcT.res(), varBounds, catDb.lower());
  }

  private void illegalArgs(FuncTH funcT, CombineH args) {
    throw new IllegalArgumentException(
        "Arguments evaluation type %s should be equal to function evaluation type parameters %s."
            .formatted(args.type().name(), funcT.paramsTuple().name()));
  }

  private FuncTH callableEvalT(ObjH callable) {
    if (callable.type() instanceof FuncTH funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to function.");
    }
  }

  private IfFuncH newIfFunc() throws HashedDbExc {
    var type = catDb.ifFunc();
    return (IfFuncH) newInternalFunc(type);
  }

  private FuncH newInternalFunc(FuncTH type) throws HashedDbExc {
    // Internal funcs don't have any data. We use empty sequence as its data so
    // code reading such func from hashedDb can be simpler and code that stores
    // h-objects as artifacts doesn't need handle this special case.
    Hash dataHash = hashedDb.writeSeq();
    var root = newRoot(type, dataHash);
    return type.newObj(root, this);
  }

  private OrderH newOrder(ImmutableList<ObjH> elems) throws HashedDbExc {
    var elemT = elemType(elems);
    var type = catDb.order(elemT);
    var data = writeOrderData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private TypeH elemType(ImmutableList<ObjH> elems) {
    Optional<TypeH> elemT = elems.stream()
        .map(ObjH::type)
        .reduce((type1, type2) -> {
          if (type1.equals(type2)) {
            return type1;
          } else {
            throw new IllegalArgumentException("Element evaluation types are not equal "
                + type1.name() + " != " + type2.name() + ".");
          }
        });
    CatH type = elemT.orElse(catDb.nothing());
    if (type instanceof TypeH typeH) {
      return typeH;
    } else {
      throw new IllegalArgumentException(
          "Element type should be ValOType but was " + type.getClass().getCanonicalName());
    }
  }

  private CombineH newCombine(ImmutableList<ObjH> items) throws HashedDbExc {
    var itemTs = Lists.map(items, ObjH::type);
    var evalT = catDb.tuple(itemTs);
    var type = catDb.combine(evalT);
    var data = writeCombineData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MapFuncH newMapFunc() throws HashedDbExc {
    var type = catDb.mapFunc();
    return (MapFuncH) newInternalFunc(type);
  }

  private SelectH newSelect(ObjH selectable, IntH index) throws HashedDbExc {
    var type = selectCat(selectable, index);
    var data = writeSelectData(selectable, index);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private SelectCH selectCat(ObjH selectable, IntH index) {
    if (selectable.type() instanceof TupleTH tuple) {
      int intIndex = index.toJ().intValue();
      var items = tuple.items();
      checkElementIndex(intIndex, items.size());
      var itemT = items.get(intIndex);
      return catDb.select(itemT);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private ParamRefH newParamRef(TypeH evalT, BigInteger index) throws HashedDbExc {
    var data = writeParamRefData(index);
    var type = catDb.ref(evalT);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MerkleRoot newRoot(CatH cat, Hash dataHash) throws HashedDbExc {
    Hash rootHash = hashedDb.writeSeq(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ObjH callable, CombineH args) throws HashedDbExc {
    return hashedDb.writeSeq(callable.hash(), args.hash());
  }

  private Hash writeCombineData(ImmutableList<ObjH> items) throws HashedDbExc {
    return writeSeq(items);
  }

  private Hash writeNatFuncData(BlobH jarFile, StringH classBinaryName, BoolH isPure)
      throws HashedDbExc {
    return hashedDb.writeSeq(jarFile.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeOrderData(ImmutableList<ObjH> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeParamRefData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(ObjH selectable, IntH index) throws HashedDbExc {
    return hashedDb.writeSeq(selectable.hash(), index.hash());
  }

  // methods for writing data of Val-s

  private Hash writeArrayData(List<ValH> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeBoolData(boolean value) throws HashedDbExc {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeIntData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeFuncData(ObjH body) {
    return body.hash();
  }

  private Hash writeStringData(String string) throws HashedDbExc {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(ImmutableList<ValH> items) throws HashedDbExc {
    return writeSeq(items);
  }

  // helpers

  private Hash writeSeq(List<? extends ObjH> objs) throws HashedDbExc {
    var hashes = Lists.map(objs, ObjH::hash);
    return hashedDb.writeSeq(hashes);
  }

  public ImmutableList<Hash> readSeq(Hash hash) throws HashedDbExc {
    return hashedDb().readSeq(hash);
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
