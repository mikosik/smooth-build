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
import org.smoothbuild.db.object.type.TypeDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.DefFuncTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.NatFuncTypeH;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
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
  private final TypeDb typeDb;
  private final TupleTypeH messageType;
  private final TupleTypeH fileType;
  private final TypingH typing;

  @Inject
  public ObjFactory(ObjDb objDb, TypeDb typeDb, TypingH typing) {
    this.objDb = objDb;
    this.typeDb = typeDb;
    this.messageType = createMessageType(typeDb);
    this.fileType = createFileT(typeDb);
    this.typing = typing;
  }

  public TypingH typing() {
    return typing;
  }

  // Objects

  public ArrayHBuilder arrayBuilderWithElems(TypeH elemType) {
    return objDb.arrayBuilder(typeDb.array(elemType));
  }

  public ArrayHBuilder arrayBuilder(ArrayTypeH type) {
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

  public DefFuncH defFunc(DefFuncTypeH type, ObjH body) {
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

  public NatFuncH natFunc(
      NatFuncTypeH type, BlobH jarFile, StringH classBinaryName, BoolH isPure) {
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

  public TupleH tuple(TupleTypeH type, ImmutableList<ValH> items) {
    return objDb.tuple(type, items);
  }

  public OrderH order(ImmutableList<ObjH> elems) {
    return objDb.order(elems);
  }

  // Types

  public ArrayTypeH arrayT(TypeH elemType) {
    return typeDb.array(elemType);
  }

  public BlobTypeH blobT() {
    return typeDb.blob();
  }

  public BoolTypeH boolT() {
    return typeDb.bool();
  }

  public DefFuncTypeH defFuncT(TypeH result, ImmutableList<TypeH> params) {
    return typeDb.defFunc(result, params);
  }

  public FuncTypeH ifFuncT() {
    return typeDb.ifFunc();
  }

  public IntTypeH intT() {
    return typeDb.int_();
  }

  public FuncTypeH mapFuncT() {
    return typeDb.ifFunc();
  }

  public TupleTypeH messageType() {
    return messageType;
  }

  public NatFuncTypeH natFuncT(TypeH result, ImmutableList<TypeH> params) {
    return typeDb.natFunc(result, params);
  }

  public NothingTypeH nothingT() {
    return typeDb.nothing();
  }

  public StringTypeH stringT() {
    return typeDb.string();
  }

  public TupleTypeH tupleType(ImmutableList<TypeH> itemTypes) {
    return typeDb.tuple(itemTypes);
  }

  public VarH var(String name) {
    return typeDb.var(name);
  }

  // other values and its types

  public TupleTypeH fileT() {
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
    return objDb.tuple(messageType(), list(textObject, severityObject));
  }

  private static TupleTypeH createMessageType(TypeDb typeDb) {
    StringTypeH stringType = typeDb.string();
    return typeDb.tuple(list(stringType, stringType));
  }

  private static TupleTypeH createFileT(TypeDb typeDb) {
    return typeDb.tuple(list(typeDb.blob(), typeDb.string()));
  }
}
