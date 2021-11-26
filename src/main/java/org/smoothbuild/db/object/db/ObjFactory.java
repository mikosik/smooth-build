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
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.val.ArrayHBuilder;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefinedFunctionH;
import org.smoothbuild.db.object.obj.val.IfFunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.MapFunctionH;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.TypeHDb;
import org.smoothbuild.db.object.type.TypingH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.BoolTypeH;
import org.smoothbuild.db.object.type.val.DefinedFunctionTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.NativeFunctionTypeH;
import org.smoothbuild.db.object.type.val.NothingTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.db.object.type.val.VariableH;
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
    this.fileType = createFileType(typeHDb);
    this.typing = typing;
  }

  public TypingH typing() {
    return typing;
  }

  // Objects

  public ArrayHBuilder arrayBuilder(TypeHV elementType) {
    return objectHDb.arrayBuilder(elementType);
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

  public CallH call(ObjectH function, ConstructH arguments) {
    return objectHDb.call(function, arguments);
  }

  public ConstructH construct(ImmutableList<ObjectH> items) {
    return objectHDb.construct(items);
  }

  public TupleH file(StringH path, BlobH content) {
    return objectHDb.tuple(fileType(), list(content, path));
  }

  public DefinedFunctionH definedFunction(DefinedFunctionTypeH type, ObjectH body) {
    return objectHDb.definedFunction(type, body);
  }

  public IfFunctionH ifFunction() {
    return objectHDb.ifFunction();
  }

  public IntH int_(BigInteger value) {
    return objectHDb.int_(value);
  }

  public MapFunctionH mapFunction() {
    return objectHDb.mapFunction();
  }

  public NativeFunctionH nativeFunction(
      NativeFunctionTypeH type, BlobH jarFile, StringH classBinaryName, BoolH isPure) {
    return objectHDb.nativeFunction(type, jarFile, classBinaryName,isPure);
  }

  public RefH ref(BigInteger value, TypeHV evaluationType) {
    return objectHDb.ref(value, evaluationType);
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

  public OrderH order(ImmutableList<ObjectH> elements) {
    return objectHDb.order(elements);
  }

  // Types

  public ArrayTypeH arrayType(TypeHV elementType) {
    return typeHDb.array(elementType);
  }

  public BlobTypeH blobType() {
    return typeHDb.blob();
  }

  public BoolTypeH boolType() {
    return typeHDb.bool();
  }

  public DefinedFunctionTypeH definedFunctionType(TypeHV result, ImmutableList<TypeHV> parameters) {
    return typeHDb.definedFunction(result, parameters);
  }

  public FunctionTypeH ifFunctionType() {
    return typeHDb.ifFunction();
  }

  public IntTypeH intType() {
    return typeHDb.int_();
  }

  public FunctionTypeH mapFunctionType() {
    return typeHDb.ifFunction();
  }

  public TupleTypeH messageType() {
    return messageType;
  }

  public NativeFunctionTypeH nativeFunctionType(TypeHV result, ImmutableList<TypeHV> parameters) {
    return typeHDb.nativeFunction(result, parameters);
  }

  public NothingTypeH nothingType() {
    return typeHDb.nothing();
  }

  public StringTypeH stringType() {
    return typeHDb.string();
  }

  public TupleTypeH tupleType(ImmutableList<TypeHV> itemTypes) {
    return typeHDb.tuple(itemTypes);
  }

  public VariableH variable(String name) {
    return typeHDb.variable(name);
  }

  // other values and its types

  public TupleTypeH fileType() {
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

  private static TupleTypeH createFileType(TypeHDb typeHDb) {
    return typeHDb.tuple(list(typeHDb.blob(), typeHDb.string()));
  }
}
