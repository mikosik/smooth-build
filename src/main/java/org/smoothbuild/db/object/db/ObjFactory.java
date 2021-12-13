package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.IfH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.MapH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
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
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.BlobTH;
import org.smoothbuild.db.object.type.val.BoolTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.IntTH;
import org.smoothbuild.db.object.type.val.MethodTH;
import org.smoothbuild.db.object.type.val.NothingTH;
import org.smoothbuild.db.object.type.val.StringTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.db.object.type.val.VarH;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjFactory {
  private final ObjDb objDb;
  private final CatDb catDb;
  private final TupleTH messageT;
  private final TupleTH fileT;
  private final TypingH typing;

  @Inject
  public ObjFactory(ObjDb objDb, CatDb catDb, TypingH typing) {
    this.objDb = objDb;
    this.catDb = catDb;
    this.messageT = createMessageT(catDb);
    this.fileT = createFileT(catDb);
    this.typing = typing;
  }

  public TypingH typing() {
    return typing;
  }

  // Objects

  public ArrayHBuilder arrayBuilderWithElems(TypeH elemT) {
    return objDb.arrayBuilder(catDb.array(elemT));
  }

  public ArrayHBuilder arrayBuilder(ArrayTH type) {
    return objDb.arrayBuilder(type);
  }

  public BlobH blob(DataWriter dataWriter) {
    try (BlobHBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new ObjDbExc(e);
    }
  }

  public BlobHBuilder blobBuilder() {
    return objDb.blobBuilder();
  }

  public BoolH bool(boolean value) {
    return objDb.bool(value);
  }

  public CallH call(ObjH func, CombineH args) {
    return objDb.call(func, args);
  }

  public CombineH combine(ImmutableList<ObjH> items) {
    return objDb.combine(items);
  }

  public TupleH file(StringH path, BlobH content) {
    return objDb.tuple(fileT(), list(content, path));
  }

  public IfH if_(ObjH condition, ObjH then, ObjH else_) {
    return objDb.if_(condition, then, else_);
  }

  public FuncH func(FuncTH type, ObjH body) {
    return objDb.func(type, body);
  }

  public MethodH method(MethodTH type, BlobH jar, StringH classBinaryName, BoolH isPure) {
    return objDb.method(type, jar, classBinaryName, isPure);
  }

  public IntH int_(BigInteger value) {
    return objDb.int_(value);
  }

  public MapH map(ObjH array, ObjH func) {
    return objDb.map(array, func);
  }

  public InvokeH invoke(ObjH method, CombineH args) {
    return objDb.invoke(method, args);
  }

  public ParamRefH paramRef(BigInteger value, TypeH evalT) {
    return objDb.newParamRef(value, evalT);
  }

  public SelectH select(ObjH tuple, IntH index) {
    return objDb.select(tuple, index);
  }

  public StringH string(String string) {
    return objDb.string(string);
  }

  public TupleH tuple(TupleTH type, ImmutableList<ValH> items) {
    return objDb.tuple(type, items);
  }

  public OrderH order(ArrayTH arrayTH, ImmutableList<ObjH> elems) {
    return objDb.order(arrayTH, elems);
  }

  // Types

  public ArrayTH arrayT(TypeH elemT) {
    return catDb.array(elemT);
  }

  public BlobTH blobT() {
    return catDb.blob();
  }

  public BoolTH boolT() {
    return catDb.bool();
  }

  public FuncTH funcT(TypeH resT, ImmutableList<TypeH> paramTs) {
    return catDb.func(resT, paramTs);
  }

  public IntTH intT() {
    return catDb.int_();
  }

  public TupleTH messageT() {
    return messageT;
  }

  public MethodTH methodT(TypeH resT, ImmutableList<TypeH> paramTs) {
    return catDb.method(resT, paramTs);
  }

  public NothingTH nothingT() {
    return catDb.nothing();
  }

  public StringTH stringT() {
    return catDb.string();
  }

  public TupleTH tupleT(ImmutableList<TypeH> itemTs) {
    return catDb.tuple(itemTs);
  }

  public VarH var(String name) {
    return catDb.var(name);
  }

  // other values and its types

  public TupleTH fileT() {
    return fileT;
  }

  public TupleH errorMessage(String text) {
    return message(ERROR.name(), text);
  }

  public TupleH warningMessage(String text) {
    return message(WARNING.name(), text);
  }

  public TupleH infoMessage(String text) {
    return message(INFO.name(), text);
  }

  private TupleH message(String severity, String text) {
    ValH textObject = objDb.string(text);
    ValH severityObject = objDb.string(severity);
    return objDb.tuple(messageT(), list(textObject, severityObject));
  }

  private static TupleTH createMessageT(CatDb catDb) {
    var stringT = catDb.string();
    return catDb.tuple(list(stringT, stringT));
  }

  private static TupleTH createFileT(CatDb catDb) {
    return catDb.tuple(list(catDb.blob(), catDb.string()));
  }
}
