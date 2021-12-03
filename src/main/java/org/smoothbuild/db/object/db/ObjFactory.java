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
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefFuncH;
import org.smoothbuild.db.object.obj.val.IfFuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFuncH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.CatDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.BlobTH;
import org.smoothbuild.db.object.type.val.BoolTH;
import org.smoothbuild.db.object.type.val.DefFuncTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.IntTH;
import org.smoothbuild.db.object.type.val.NatFuncTH;
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
  private final TupleTH messageType;
  private final TupleTH fileType;
  private final TypingH typing;

  @Inject
  public ObjFactory(ObjDb objDb, CatDb catDb, TypingH typing) {
    this.objDb = objDb;
    this.catDb = catDb;
    this.messageType = createMessageT(catDb);
    this.fileType = createFileT(catDb);
    this.typing = typing;
  }

  public TypingH typing() {
    return typing;
  }

  // Objects

  public ArrayHBuilder arrayBuilderWithElems(TypeH elemType) {
    return objDb.arrayBuilder(catDb.array(elemType));
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

  public DefFuncH defFunc(DefFuncTH type, ObjH body) {
    return objDb.defFunc(type, body);
  }

  public IfFuncH ifFunc() {
    return objDb.ifFunc();
  }

  public IntH int_(BigInteger value) {
    return objDb.int_(value);
  }

  public MapFuncH mapFunc() {
    return objDb.mapFunc();
  }

  public NatFuncH natFunc(NatFuncTH type, BlobH jarFile, StringH classBinaryName, BoolH isPure) {
    return objDb.natFunc(type, jarFile, classBinaryName,isPure);
  }

  public ParamRefH paramRef(BigInteger value, TypeH evalType) {
    return objDb.newParamRef(value, evalType);
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

  public OrderH order(ImmutableList<ObjH> elems) {
    return objDb.order(elems);
  }

  // Types

  public ArrayTH arrayT(TypeH elemType) {
    return catDb.array(elemType);
  }

  public BlobTH blobT() {
    return catDb.blob();
  }

  public BoolTH boolT() {
    return catDb.bool();
  }

  public DefFuncTH defFuncT(TypeH result, ImmutableList<TypeH> params) {
    return catDb.defFunc(result, params);
  }

  public FuncTH ifFuncT() {
    return catDb.ifFunc();
  }

  public IntTH intT() {
    return catDb.int_();
  }

  public FuncTH mapFuncT() {
    return catDb.ifFunc();
  }

  public TupleTH messageT() {
    return messageType;
  }

  public NatFuncTH natFuncT(TypeH result, ImmutableList<TypeH> params) {
    return catDb.natFunc(result, params);
  }

  public NothingTH nothingT() {
    return catDb.nothing();
  }

  public StringTH stringT() {
    return catDb.string();
  }

  public TupleTH tupleT(ImmutableList<TypeH> itemTypes) {
    return catDb.tuple(itemTypes);
  }

  public VarH var(String name) {
    return catDb.var(name);
  }

  // other values and its types

  public TupleTH fileT() {
    return fileType;
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
    StringTH stringType = catDb.string();
    return catDb.tuple(list(stringType, stringType));
  }

  private static TupleTH createFileT(CatDb catDb) {
    return catDb.tuple(list(catDb.blob(), catDb.string()));
  }
}
