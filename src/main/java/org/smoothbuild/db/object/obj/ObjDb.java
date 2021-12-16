package org.smoothbuild.db.object.obj;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.list;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.hashed.exc.NoSuchDataExc;
import org.smoothbuild.db.object.db.ObjDbExc;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeObjCatExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjNoSuchObjExc;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.expr.SelectCH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.CallableTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.MethodTH;
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

  public ObjDb(HashedDb hashedDb, CatDb catDb, TypingH typing) {
    this.hashedDb = hashedDb;
    this.catDb = catDb;
    this.typing = typing;
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

  public MethodH method(MethodTH type, BlobH jar, StringH classBinaryName, BoolH isPure) {
    return wrapHashedDbExceptionAsObjectDbException(
        () -> newMethod(type, jar, classBinaryName, isPure));
  }

  public FuncH func(FuncTH type, ObjH body) {
    checkBodyTypeAssignableToFuncResT(type, body);
    return wrapHashedDbExceptionAsObjectDbException(() -> newFunc(type, body));
  }

  private void checkBodyTypeAssignableToFuncResT(FuncTH type, ObjH body) {
    if (!typing.isAssignable(type.res(), body.type())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.res().q()
          + " but body.type() is " + body.type().q() + ".");
    }
  }

  public IntH int_(BigInteger value) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInt(value));
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

  public CombineH combine(TupleTH evalT, ImmutableList<ObjH> items) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newCombine(evalT, items));
  }

  public IfH if_(ObjH condition, ObjH then, ObjH else_) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newIf(condition, then, else_));
  }

  public InvokeH invoke(ObjH method, CombineH args) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newInvoke(method, args));
  }

  public MapH map(ObjH array, ObjH func) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newMap(array, func));
  }

  public OrderH order(ArrayTH arrayTH, ImmutableList<ObjH> elems) {
    return wrapHashedDbExceptionAsObjectDbException(() -> newOrder(arrayTH, elems));
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
      throw new DecodeObjNoSuchObjExc(rootHash, e);
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

  private FuncH newFunc(FuncTH type, ObjH body) throws HashedDbExc {
    var data = writeFuncData(body);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private IntH newInt(BigInteger value) throws HashedDbExc {
    var data = writeIntData(value);
    var root = newRoot(catDb.int_(), data);
    return catDb.int_().newObj(root, this);
  }

  private MethodH newMethod(MethodTH type, BlobH jar, StringH classBinaryName, BoolH isPure)
      throws HashedDbExc {
    var data = writeMethodData(jar, classBinaryName, isPure);
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
    var resT = inferCallResT(castTypeToFuncTH(callable), args);
    var type = catDb.call(resT);
    var data = writeCallData(callable, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private FuncTH castTypeToFuncTH(ObjH callable) {
    if (callable.type() instanceof FuncTH funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncH.");
    }
  }

  private TypeH inferCallResT(CallableTH callableTH, CombineH args) {
    var argTs = args.type().items();
    var paramTs = callableTH.params();
    allMatchOtherwise(
        paramTs,
        argTs,
        typing::isParamAssignable,
        (expectedSize, actualSize) -> illegalArgs(callableTH, args),
        i -> illegalArgs(callableTH, args)
    );
    var varBounds = typing.inferVarBoundsLower(paramTs, argTs);
    return typing.mapVarsLower(callableTH.res(), varBounds);
  }

  private void illegalArgs(CallableTH callableTH, CombineH args) {
    throw new IllegalArgumentException(
        "Arguments evaluation type %s should be equal to callable type parameters %s."
            .formatted(args.type().name(), callableTH.paramsTuple().name()));
  }

  private OrderH newOrder(ArrayTH arrayTH, ImmutableList<ObjH> elems) throws HashedDbExc {
    var elemT = arrayTH.elem();
    validateOrderElems(elemT, elems);
    var type = catDb.order(elemT);
    var data = writeOrderData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private void validateOrderElems(TypeH elemT, ImmutableList<ObjH> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElemT = elems.get(i).type();
      if (!typing.isAssignable(elemT, iElemT)) {
        throw new IllegalArgumentException("Illegal elem type. Expected " + elemT.q()
            + " but element at index " + i + " has type " + iElemT.q() + ".");
      }
    }
  }

  private CombineH newCombine(TupleTH evalT, ImmutableList<ObjH> items) throws HashedDbExc {
    validateCombineItems(evalT, items);
    var type = catDb.combine(evalT);
    var data = writeCombineData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private void validateCombineItems(TupleTH evalT, ImmutableList<ObjH> items) {
    var expectedItemTs = evalT.items();
    for (int i = 0; i < items.size(); i++) {
      var expectedItemT = expectedItemTs.get(i);
      var actualItemT = items.get(i).type();
      if (!typing.isAssignable(expectedItemT, actualItemT)) {
        throw new IllegalArgumentException("Illegal item type. Expected " + expectedItemT.q()
            + " at index " + i + " has type " + actualItemT.q() + ".");
      }
    }
  }

  private IfH newIf(ObjH condition, ObjH then, ObjH else_) throws HashedDbExc {
    checkArgument(condition.type().equals(catDb.bool()),
        "`condition` component must evaluate to BoolH but is " + condition.type().q() + ".");
    var evalT = typing.mergeUp(then.type(), else_.type());
    var type = catDb.if_(evalT);
    var data = writeIfData(condition, then, else_);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private InvokeH newInvoke(ObjH method, CombineH args) throws HashedDbExc {
    var resT = inferCallResT(castTypeToMethodTH(method), args);
    var type = catDb.invoke(resT);
    var data = writeInvokeData(method, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MethodTH castTypeToMethodTH(ObjH callable) {
    if (callable.type() instanceof MethodTH methodTH) {
      return methodTH;
    } else {
      throw new IllegalArgumentException("`method` component doesn't evaluate to MethodH.");
    }
  }

  private MapH newMap(ObjH array, ObjH func) throws HashedDbExc {
    if (array.type() instanceof ArrayTH arrayT
        && func.type() instanceof FuncTH funcT
        && funcT.params().size() == 1
        && typing.isAssignable(funcT.params().get(0), arrayT.elem())) {
      // TODO func can be generic so we need to infer proper result type
      var vars = typing.inferVarBoundsLower(funcT.params(), list(arrayT.elem()));
      var elemEvalT = typing.mapVarsLower(funcT.res(), vars);
      var evalT = catDb.array(elemEvalT);
      var type = catDb.map(evalT);

      var data = writeMapData(array, func);
      var root = newRoot(type, data);
      return type.newObj(root, this);
    } else {
      // TODO add more info
      throw new IllegalArgumentException();
    }
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

  private Hash writeIfData(ObjH condition, ObjH then, ObjH else_) throws HashedDbExc {
    return hashedDb.writeSeq(condition.hash(), then.hash(), else_.hash());
  }

  private Hash writeInvokeData(ObjH method, CombineH args) throws HashedDbExc {
    return hashedDb.writeSeq(method.hash(), args.hash());
  }

  private Hash writeMapData(ObjH array, ObjH func) throws HashedDbExc {
    return hashedDb.writeSeq(array.hash(), func.hash());
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

  private Hash writeFuncData(ObjH body) {
    return body.hash();
  }

  private Hash writeIntData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeMethodData(BlobH jar, StringH classBinaryName, BoolH isPure)
      throws HashedDbExc {
    return hashedDb.writeSeq(jar.hash(), classBinaryName.hash(), isPure.hash());
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
    return hashedDb.readSeq(hash);
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
