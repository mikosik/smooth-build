package org.smoothbuild.bytecode.expr;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;
import static org.smoothbuild.bytecode.expr.exc.DecodeExprRootExc.cannotReadRootException;
import static org.smoothbuild.bytecode.expr.exc.DecodeExprRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.bytecode.type.ValidateArgs.validateArgs;
import static org.smoothbuild.bytecode.type.val.TNamesB.BOOL;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import org.smoothbuild.bytecode.expr.exc.DecodeExprCatExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNoSuchExprExc;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.IfB;
import org.smoothbuild.bytecode.expr.oper.InvokeB;
import org.smoothbuild.bytecode.expr.oper.MapB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.ArrayBBuilder;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BlobBBuilder;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.MethodB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashedDb;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.bytecode.hashed.exc.NoSuchDataExc;
import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.exc.CatDbExc;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.MethodTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.util.collect.Lists;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 */
public class BytecodeDb {
  private final HashedDb hashedDb;
  private final CatDb catDb;

  public BytecodeDb(HashedDb hashedDb, CatDb catDb) {
    this.hashedDb = hashedDb;
    this.catDb = catDb;
  }

  // methods for creating ValB subclasses

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return new ArrayBBuilder(type, this);
  }

  public BlobBBuilder blobBuilder() {
    return wrapHashedDbExcAsBytecodeDbExc(() -> new BlobBBuilder(this, hashedDb.sink()));
  }

  public BoolB bool(boolean value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newBool(value));
  }

  public MethodB method(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return wrapHashedDbExcAsBytecodeDbExc(
        () -> newMethod(type, jar, classBinaryName, isPure));
  }

  public FuncB func(FuncTB type, ExprB body) {
    checkBodyTypeAssignableToFuncResT(type, body);
    return wrapHashedDbExcAsBytecodeDbExc(() -> newFunc(type, body));
  }

  private void checkBodyTypeAssignableToFuncResT(FuncTB type, ExprB body) {
    if (!type.res().equals(body.type())) {
      throw new IllegalArgumentException("`type` specifies result as " + type.res().q()
          + " but body.type() is " + body.type().q() + ".");
    }
  }

  public IntB int_(BigInteger value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newInt(value));
  }

  public StringB string(String value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newString(value));
  }

  public TupleB tuple(TupleTB tupleT, ImmutableList<ValB> items) {
    var itemTs = tupleT.items();
    allMatchOtherwise(itemTs, items, (s, i) -> Objects.equals(s, i.cat()),
        (i, j) -> {
          throw new IllegalArgumentException(
              "tupleType specifies " + i + " items but provided " + j + ".");
        },
        (i) -> {
          throw new IllegalArgumentException("tupleType specifies item at index " + i
              + " with type " + itemTs.get(i).name() + " but provided item has type "
              + items.get(i).cat().name() + " at that index.");
        }
    );

    return wrapHashedDbExcAsBytecodeDbExc(() -> newTuple(tupleT, items));
  }

  // methods for creating OperB subclasses

  public CallB call(TypeB evalT, ExprB func, CombineB args) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCall(evalT, func, args));
  }

  public CombineB combine(TupleTB evalT, ImmutableList<ExprB> items) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newCombine(evalT, items));
  }

  public IfB if_(TypeB evalT, ExprB condition, ExprB then, ExprB else_) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newIf(evalT, condition, then, else_));
  }

  public InvokeB invoke(TypeB evalT, ExprB method, CombineB args) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newInvoke(evalT, method, args));
  }

  public MapB map(ExprB array, ExprB func) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newMap(array, func));
  }

  public OrderB order(ArrayTB evalT, ImmutableList<ExprB> elems) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newOrder(evalT, elems));
  }

  public ParamRefB paramRef(TypeB evalT, BigInteger value) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newParamRef(evalT, value));
  }

  public SelectB select(TypeB evalT, ExprB selectable, IntB index) {
    return wrapHashedDbExcAsBytecodeDbExc(() -> newSelect(evalT, selectable, index));
  }

  // generic getter

  public ExprB get(Hash rootHash) {
    List<Hash> hashes = decodeRootSeq(rootHash);
    if (hashes.size() != 2) {
      throw wrongSizeOfRootSeqException(rootHash, hashes.size());
    }
    CatB cat = getCatOrChainException(rootHash, hashes.get(0));
    Hash dataHash = hashes.get(1);
    return cat.newObj(new MerkleRoot(rootHash, cat, dataHash), this);
  }

  private CatB getCatOrChainException(Hash rootHash, Hash typeHash) {
    try {
      return catDb.get(typeHash);
    } catch (CatDbExc e) {
      throw new DecodeExprCatExc(rootHash, e);
    }
  }

  private List<Hash> decodeRootSeq(Hash rootHash) {
    try {
      return hashedDb.readSeq(rootHash);
    } catch (NoSuchDataExc e) {
      throw new DecodeExprNoSuchExprExc(rootHash, e);
    } catch (HashedDbExc e) {
      throw cannotReadRootException(rootHash, e);
    }
  }

  // methods for creating ValB-s

  public ArrayB newArray(ArrayTB type, List<ValB> elems) throws HashedDbExc {
    var data = writeArrayData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  public BlobB newBlob(Hash dataHash) throws HashedDbExc {
    var root = newRoot(catDb.blob(), dataHash);
    return catDb.blob().newObj(root, this);
  }

  private BoolB newBool(boolean value) throws HashedDbExc {
    var data = writeBoolData(value);
    var root = newRoot(catDb.bool(), data);
    return catDb.bool().newObj(root, this);
  }

  private FuncB newFunc(FuncTB type, ExprB body) throws HashedDbExc {
    var data = writeFuncData(body);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private IntB newInt(BigInteger value) throws HashedDbExc {
    var data = writeIntData(value);
    var root = newRoot(catDb.int_(), data);
    return catDb.int_().newObj(root, this);
  }

  private MethodB newMethod(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    var data = writeMethodData(jar, classBinaryName, isPure);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private StringB newString(String string) throws HashedDbExc {
    var data = writeStringData(string);
    var root = newRoot(catDb.string(), data);
    return catDb.string().newObj(root, this);
  }

  private TupleB newTuple(TupleTB type, ImmutableList<ValB> items) throws HashedDbExc {
    var data = writeTupleData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  // methods for creating Expr-s

  private CallB newCall(TypeB evalT, ExprB func, CombineB args) throws HashedDbExc {
    CallableTB callableTB = castTypeToFuncTB(func);
    validateArgsInCall(callableTB, args);
    var resT = callableTB.res();
    if (!evalT.equals(resT)) {
      throw new IllegalArgumentException(
          "Call's result type " + resT.q() + " cannot be assigned to evalT " + evalT.q() + ".");
    }

    var type = catDb.call(evalT);
    var data = writeCallData(func, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private FuncTB castTypeToFuncTB(ExprB callable) {
    if (callable.type() instanceof FuncTB funcT) {
      return funcT;
    } else {
      throw new IllegalArgumentException("`func` component doesn't evaluate to FuncB.");
    }
  }

  private void validateArgsInCall(CallableTB callableTB, CombineB args) {
    validateArgs(callableTB, args.type().items(),
        () -> {throw illegalArgs(callableTB, args.type());}
    );
  }

  private IllegalArgumentException illegalArgs(CallableTB callableTB, TupleTB argsT) {
    return new IllegalArgumentException(
        "Argument evaluation types %s should be equal to callable parameter types %s."
            .formatted(itemTsToString(argsT), itemTsToString(callableTB.paramsTuple())));
  }

  private static String itemTsToString(TupleTB argsT) {
    return "(" + toCommaSeparatedString(argsT.items()) + ")";
  }

  private OrderB newOrder(ArrayTB evalT, ImmutableList<ExprB> elems) throws HashedDbExc {
    validateOrderElems(evalT.elem(), elems);
    var type = catDb.order(evalT);
    var data = writeOrderData(elems);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private void validateOrderElems(TypeB elemT, ImmutableList<ExprB> elems) {
    for (int i = 0; i < elems.size(); i++) {
      var iElemT = elems.get(i).type();
      if (!elemT.equals(iElemT)) {
        throw new IllegalArgumentException("Illegal elem type. Expected " + elemT.q()
            + " but element at index " + i + " has type " + iElemT.q() + ".");
      }
    }
  }

  private CombineB newCombine(TupleTB evalT, ImmutableList<ExprB> items) throws HashedDbExc {
    validateCombineItems(evalT, items);
    var type = catDb.combine(evalT);
    var data = writeCombineData(items);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private void validateCombineItems(TupleTB evalT, ImmutableList<ExprB> items) {
    var expectedItemTs = evalT.items();
    if (expectedItemTs.size() != items.size()) {
      throw new IllegalArgumentException(
          "Expected " + expectedItemTs.size() + " items, got " + items.size() + ".");
    }
    for (int i = 0; i < items.size(); i++) {
      var expectedItemT = expectedItemTs.get(i);
      var actualItemT = items.get(i).type();
      if (!expectedItemT.equals(actualItemT)) {
        throw new IllegalArgumentException("Illegal item type. Expected " + expectedItemT.q()
            + " at index " + i + " has type " + actualItemT.q() + ".");
      }
    }
  }

  private IfB newIf(TypeB evalT, ExprB condition, ExprB then, ExprB else_) throws HashedDbExc {
    checkArgument(condition.type().equals(catDb.bool()),
        "`condition` component must evaluate to " + BOOL + " but is " + condition.type().q() + ".");
    checkArgument(evalT.equals(then.type()),
        "`then` component must evaluate to " + evalT.q() + " but is " + then.type().q() + ".");
    checkArgument(evalT.equals(else_.type()),
        "`else` component must evaluate to " + evalT.q() + " but is " + else_.type().q() + ".");
    var type = catDb.if_(evalT);
    var data = writeIfData(condition, then, else_);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private InvokeB newInvoke(TypeB evalT, ExprB method, CombineB args) throws HashedDbExc {
    CallableTB callableTB = castTypeToMethodTB(method);
    validateArgsInCall(callableTB, args);
    var resT = callableTB.res();
    if (!evalT.equals(resT)) {
      throw new IllegalArgumentException(
          "Method's result type " + resT.q() + " cannot be assigned to evalT " + evalT.q() + ".");
    }
    var type = catDb.invoke(evalT);
    var data = writeInvokeData(method, args);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MethodTB castTypeToMethodTB(ExprB method) {
    if (method.type() instanceof MethodTB methodTB) {
      return methodTB;
    } else {
      throw new IllegalArgumentException("`method` component doesn't evaluate to MethodB.");
    }
  }

  private MapB newMap(ExprB array, ExprB func) throws HashedDbExc {
    if (!(array.type() instanceof ArrayTB arrayT)) {
      throw new IllegalArgumentException("array.type() must be instance of "
          + ArrayTB.class.getSimpleName() + " but is "
          + array.type().getClass().getSimpleName() + ".");
    }
    if (!(func.type() instanceof FuncTB funcT)) {
      throw new IllegalArgumentException("func.type() must be instance of "
          + FuncTB.class.getSimpleName() + " but is "
          + func.type().getClass().getSimpleName() + ".");
    }
    if (funcT.params().size() != 1) {
      throw new IllegalArgumentException(
          "func parameter count must be 1 but is " + funcT.params().size() + ".");
    }
    var elemT = arrayT.elem();
    var funcParamT = funcT.params().get(0);
    if (!funcParamT.equals(elemT)) {
      throw new IllegalArgumentException("array element type " + elemT
          + " is not assignable to func parameter type " + funcParamT + ".");
    }
    var evalT = catDb.array(funcT.res());
    var type = catDb.map(evalT);

    var data = writeMapData(array, func);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private SelectB newSelect(TypeB evalT, ExprB selectable, IntB index) throws HashedDbExc {
    var inferredEvalT = selectEvalT(selectable, index);
    if (!evalT.equals(inferredEvalT)) {
      throw new IllegalArgumentException("Selected item type " + inferredEvalT.q()
          + " cannot be assigned to evalT " + evalT.q() + ".");
    }
    var data = writeSelectData(selectable, index);
    var cat = catDb.select(evalT);
    var root = newRoot(cat, data);
    return cat.newObj(root, this);
  }

  private TypeB selectEvalT(ExprB selectable, IntB index) {
    var evalT = selectable.type();
    if (evalT instanceof TupleTB tuple) {
      int intIndex = index.toJ().intValue();
      var items = tuple.items();
      checkElementIndex(intIndex, items.size());
      return items.get(intIndex);
    } else {
      throw new IllegalArgumentException(
          "Selectable.type() should be instance of TupleTB but is " + evalT.q());
    }
  }

  private ParamRefB newParamRef(TypeB evalT, BigInteger index) throws HashedDbExc {
    var data = writeParamRefData(index);
    var type = catDb.paramRef(evalT);
    var root = newRoot(type, data);
    return type.newObj(root, this);
  }

  private MerkleRoot newRoot(CatB cat, Hash dataHash) throws HashedDbExc {
    Hash rootHash = hashedDb.writeSeq(cat.hash(), dataHash);
    return new MerkleRoot(rootHash, cat, dataHash);
  }

  // methods for writing data of Expr-s

  private Hash writeCallData(ExprB func, CombineB args) throws HashedDbExc {
    return hashedDb.writeSeq(func.hash(), args.hash());
  }

  private Hash writeCombineData(ImmutableList<ExprB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  private Hash writeIfData(ExprB condition, ExprB then, ExprB else_) throws HashedDbExc {
    return hashedDb.writeSeq(condition.hash(), then.hash(), else_.hash());
  }

  private Hash writeInvokeData(ExprB method, CombineB args) throws HashedDbExc {
    return hashedDb.writeSeq(method.hash(), args.hash());
  }

  private Hash writeMapData(ExprB array, ExprB func) throws HashedDbExc {
    return hashedDb.writeSeq(array.hash(), func.hash());
  }

  private Hash writeOrderData(ImmutableList<ExprB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeParamRefData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeSelectData(ExprB selectable, IntB index) throws HashedDbExc {
    return hashedDb.writeSeq(selectable.hash(), index.hash());
  }

  // methods for writing data of ValB-s

  private Hash writeArrayData(List<ValB> elems) throws HashedDbExc {
    return writeSeq(elems);
  }

  private Hash writeBoolData(boolean value) throws HashedDbExc {
    return hashedDb.writeBoolean(value);
  }

  private Hash writeFuncData(ExprB body) {
    return body.hash();
  }

  private Hash writeIntData(BigInteger value) throws HashedDbExc {
    return hashedDb.writeBigInteger(value);
  }

  private Hash writeMethodData(BlobB jar, StringB classBinaryName, BoolB isPure)
      throws HashedDbExc {
    return hashedDb.writeSeq(jar.hash(), classBinaryName.hash(), isPure.hash());
  }

  private Hash writeStringData(String string) throws HashedDbExc {
    return hashedDb.writeString(string);
  }

  private Hash writeTupleData(ImmutableList<ValB> items) throws HashedDbExc {
    return writeSeq(items);
  }

  // helpers

  private Hash writeSeq(List<? extends ExprB> exprs) throws HashedDbExc {
    var hashes = Lists.map(exprs, ExprB::hash);
    return hashedDb.writeSeq(hashes);
  }

  // visible for classes from db.object package tree until creating Obj is cached and
  // moved completely to ObjectDb class
  public HashedDb hashedDb() {
    return hashedDb;
  }

  public CatDb catDb() {
    return catDb;
  }
}
