package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.expr.CallB;
import org.smoothbuild.db.object.obj.expr.CombineB;
import org.smoothbuild.db.object.obj.expr.IfB;
import org.smoothbuild.db.object.obj.expr.InvokeB;
import org.smoothbuild.db.object.obj.expr.MapB;
import org.smoothbuild.db.object.obj.expr.OrderB;
import org.smoothbuild.db.object.obj.expr.ParamRefB;
import org.smoothbuild.db.object.obj.expr.SelectB;
import org.smoothbuild.db.object.obj.val.ArrayBBuilder;
import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.obj.val.BlobBBuilder;
import org.smoothbuild.db.object.obj.val.BoolB;
import org.smoothbuild.db.object.obj.val.FuncB;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.obj.val.MethodB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.obj.val.TupleB;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypingB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.db.object.type.val.BlobTB;
import org.smoothbuild.db.object.type.val.BoolTB;
import org.smoothbuild.db.object.type.val.FuncTB;
import org.smoothbuild.db.object.type.val.IntTB;
import org.smoothbuild.db.object.type.val.MethodTB;
import org.smoothbuild.db.object.type.val.NothingTB;
import org.smoothbuild.db.object.type.val.StringTB;
import org.smoothbuild.db.object.type.val.TupleTB;
import org.smoothbuild.db.object.type.val.VarB;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjFactory {
  private final ByteDb byteDb;
  private final CatDb catDb;
  private final TupleTB messageT;
  private final TupleTB fileT;
  private final TypingB typing;

  @Inject
  public ObjFactory(ByteDb byteDb, CatDb catDb, TypingB typing) {
    this.byteDb = byteDb;
    this.catDb = catDb;
    this.messageT = createMessageT(catDb);
    this.fileT = createFileT(catDb);
    this.typing = typing;
  }

  public TypingB typing() {
    return typing;
  }

  // Objects

  public ArrayBBuilder arrayBuilderWithElems(TypeB elemT) {
    return byteDb.arrayBuilder(catDb.array(elemT));
  }

  public ArrayBBuilder arrayBuilder(ArrayTB type) {
    return byteDb.arrayBuilder(type);
  }

  public BlobB blob(DataWriter dataWriter) {
    try (BlobBBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new ByteDbExc(e);
    }
  }

  public BlobBBuilder blobBuilder() {
    return byteDb.blobBuilder();
  }

  public BoolB bool(boolean value) {
    return byteDb.bool(value);
  }

  public CallB call(ObjB func, ObjB args) {
    return byteDb.call(func, args);
  }

  public CombineB combine(TupleTB evalT, ImmutableList<ObjB> items) {
    return byteDb.combine(evalT, items);
  }

  public TupleB file(StringB path, BlobB content) {
    return byteDb.tuple(fileT(), list(content, path));
  }

  public IfB if_(ObjB condition, ObjB then, ObjB else_) {
    return byteDb.if_(condition, then, else_);
  }

  public FuncB func(FuncTB type, ObjB body) {
    return byteDb.func(type, body);
  }

  public MethodB method(MethodTB type, BlobB jar, StringB classBinaryName, BoolB isPure) {
    return byteDb.method(type, jar, classBinaryName, isPure);
  }

  public IntB int_(BigInteger value) {
    return byteDb.int_(value);
  }

  public MapB map(ObjB array, ObjB func) {
    return byteDb.map(array, func);
  }

  public InvokeB invoke(ObjB method, ObjB args) {
    return byteDb.invoke(method, args);
  }

  public ParamRefB paramRef(BigInteger value, TypeB evalT) {
    return byteDb.newParamRef(value, evalT);
  }

  public SelectB select(ObjB tuple, IntB index) {
    return byteDb.select(tuple, index);
  }

  public StringB string(String string) {
    return byteDb.string(string);
  }

  public TupleB tuple(TupleTB type, ImmutableList<ValB> items) {
    return byteDb.tuple(type, items);
  }

  public OrderB order(ArrayTB arrayTB, ImmutableList<ObjB> elems) {
    return byteDb.order(arrayTB, elems);
  }

  // Types

  public ArrayTB arrayT(TypeB elemT) {
    return catDb.array(elemT);
  }

  public BlobTB blobT() {
    return catDb.blob();
  }

  public BoolTB boolT() {
    return catDb.bool();
  }

  public FuncTB funcT(TypeB resT, ImmutableList<TypeB> paramTs) {
    return catDb.func(resT, paramTs);
  }

  public IntTB intT() {
    return catDb.int_();
  }

  public TupleTB messageT() {
    return messageT;
  }

  public MethodTB methodT(TypeB resT, ImmutableList<TypeB> paramTs) {
    return catDb.method(resT, paramTs);
  }

  public NothingTB nothingT() {
    return catDb.nothing();
  }

  public StringTB stringT() {
    return catDb.string();
  }

  public TupleTB tupleT(ImmutableList<TypeB> itemTs) {
    return catDb.tuple(itemTs);
  }

  public VarB var(String name) {
    return catDb.var(name);
  }

  // other values and its types

  public TupleTB fileT() {
    return fileT;
  }

  public TupleB errorMessage(String text) {
    return message(ERROR.name(), text);
  }

  public TupleB warningMessage(String text) {
    return message(WARNING.name(), text);
  }

  public TupleB infoMessage(String text) {
    return message(INFO.name(), text);
  }

  private TupleB message(String severity, String text) {
    ValB textObject = byteDb.string(text);
    ValB severityObject = byteDb.string(severity);
    return byteDb.tuple(messageT(), list(textObject, severityObject));
  }

  private static TupleTB createMessageT(CatDb catDb) {
    var stringT = catDb.string();
    return catDb.tuple(list(stringT, stringT));
  }

  private static TupleTB createFileT(CatDb catDb) {
    return catDb.tuple(list(catDb.blob(), catDb.string()));
  }
}
