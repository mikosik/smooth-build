package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
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
import org.smoothbuild.db.object.type.TypeHDb;
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
  private final ObjectHDb objectHDb;
  private final TypeHDb typeHDb;
  private final TupleTypeH messageType;
  private final TupleTypeH fileType;
  private final TypingH typing;

  @Inject
  public ObjFactory(ObjectHDb objectHDb, TypeHDb typeHDb, TypingH typing) {
    this.objectHDb = objectHDb;
    this.typeHDb = typeHDb;
    this.messageType = createMessageType(typeHDb);
    this.fileType = createFileT(typeHDb);
    this.typing = typing;
  }

  public TypingH typing() {
    return typing;
  }

  // Objects

  public ArrayHBuilder arrayBuilder(TypeH elemType) {
    return objectHDb.arrayBuilder(elemType);
  }

  public BlobH blob(DataWriter dataWriter) {
    try (BlobHBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new ObjectHDbException(e);
    }
  }

  public BlobHBuilder blobBuilder() {
    return objectHDb.blobBuilder();
  }

  public BoolH bool(boolean value) {
    return objectHDb.bool(value);
  }

  public CallH call(ObjectH func, CombineH args) {
    return objectHDb.call(func, args);
  }

  public CombineH combine(ImmutableList<ObjectH> items) {
    return objectHDb.combine(items);
  }

  public TupleH file(StringH path, BlobH content) {
    return objectHDb.tuple(fileT(), list(content, path));
  }

  public DefFuncH defFunc(DefFuncTypeH type, ObjectH body) {
    return objectHDb.defFunc(type, body);
  }

  public IfFuncH ifFunc() {
    return objectHDb.ifFunc();
  }

  public IntH int_(BigInteger value) {
    return objectHDb.int_(value);
  }

  public MapFuncH mapFunc() {
    return objectHDb.mapFunc();
  }

  public NatFuncH natFunc(
      NatFuncTypeH type, BlobH jarFile, StringH classBinaryName, BoolH isPure) {
    return objectHDb.natFunc(type, jarFile, classBinaryName,isPure);
  }

  public ParamRefH paramRef(BigInteger value, TypeH evaluationType) {
    return objectHDb.newParamRef(value, evaluationType);
  }

  public SelectH select(ObjectH tuple, IntH index) {
    return objectHDb.select(tuple, index);
  }

  public StringH string(String string) {
    return objectHDb.string(string);
  }

  public TupleH tuple(TupleTypeH type, ImmutableList<ValueH> items) {
    return objectHDb.tuple(type, items);
  }

  public OrderH order(ImmutableList<ObjectH> elems) {
    return objectHDb.order(elems);
  }

  // Types

  public ArrayTypeH arrayT(TypeH elemType) {
    return typeHDb.array(elemType);
  }

  public BlobTypeH blobT() {
    return typeHDb.blob();
  }

  public BoolTypeH boolT() {
    return typeHDb.bool();
  }

  public DefFuncTypeH defFuncT(TypeH result, ImmutableList<TypeH> params) {
    return typeHDb.defFunc(result, params);
  }

  public FuncTypeH ifFuncT() {
    return typeHDb.ifFunc();
  }

  public IntTypeH intT() {
    return typeHDb.int_();
  }

  public FuncTypeH mapFuncT() {
    return typeHDb.ifFunc();
  }

  public TupleTypeH messageType() {
    return messageType;
  }

  public NatFuncTypeH natFuncT(TypeH result, ImmutableList<TypeH> params) {
    return typeHDb.natFunc(result, params);
  }

  public NothingTypeH nothingT() {
    return typeHDb.nothing();
  }

  public StringTypeH stringT() {
    return typeHDb.string();
  }

  public TupleTypeH tupleType(ImmutableList<TypeH> itemTypes) {
    return typeHDb.tuple(itemTypes);
  }

  public VarH var(String name) {
    return typeHDb.var(name);
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
    ValueH textObject = objectHDb.string(text);
    ValueH severityObject = objectHDb.string(severity);
    return objectHDb.tuple(messageType(), list(textObject, severityObject));
  }

  private static TupleTypeH createMessageType(TypeHDb typeHDb) {
    StringTypeH stringType = typeHDb.string();
    return typeHDb.tuple(list(stringType, stringType));
  }

  private static TupleTypeH createFileT(TypeHDb typeHDb) {
    return typeHDb.tuple(list(typeHDb.blob(), typeHDb.string()));
  }
}
