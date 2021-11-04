package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.exec.base.FileStruct.CONTENT_FIELD_NAME;
import static org.smoothbuild.exec.base.FileStruct.PATH_FIELD_NAME;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Construct;
import org.smoothbuild.db.object.obj.expr.Order;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.expr.StructExpr;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.type.ObjTypeDb;
import org.smoothbuild.db.object.type.base.TypeV;
import org.smoothbuild.db.object.type.expr.StructExprOType;
import org.smoothbuild.db.object.type.val.ArrayTypeO;
import org.smoothbuild.db.object.type.val.BlobTypeO;
import org.smoothbuild.db.object.type.val.BoolTypeO;
import org.smoothbuild.db.object.type.val.IntTypeO;
import org.smoothbuild.db.object.type.val.LambdaTypeO;
import org.smoothbuild.db.object.type.val.NothingTypeO;
import org.smoothbuild.db.object.type.val.StringTypeO;
import org.smoothbuild.db.object.type.val.StructTypeO;
import org.smoothbuild.db.object.type.val.TupleTypeO;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.util.collect.NamedList;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjFactory {
  private final ObjDb objDb;
  private final ObjTypeDb objTypeDb;
  private final StructTypeO messageType;
  private final StructTypeO fileType;

  @Inject
  public ObjFactory(ObjDb objDb, ObjTypeDb objTypeDb) {
    this.objDb = objDb;
    this.objTypeDb = objTypeDb;
    this.messageType = createMessageType(objTypeDb);
    this.fileType = createFileType(objTypeDb);
  }

  // Values

  public ArrayBuilder arrayBuilder(TypeV elementType) {
    return objDb.arrayBuilder(elementType);
  }

  public Blob blob(DataWriter dataWriter) {
    try (BlobBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new ObjDbException(e);
    }
  }

  public BlobBuilder blobBuilder() {
    return objDb.blobBuilder();
  }

  public Bool bool(boolean value) {
    return objDb.bool(value);
  }

  public Call call(Expr function, Construct arguments) {
    return objDb.call(function, arguments);
  }

  public Const const_(Val val) {
    return objDb.const_(val);
  }

  public Struc_ file(Str path, Blob content) {
    return objDb.struct(fileType(), list(content, path));
  }

  public Int int_(BigInteger value) {
    return objDb.int_(value);
  }

  public Lambda lambda(LambdaTypeO type, Expr body) {
    return objDb.lambda(type, body);
  }

  public Ref ref(BigInteger value, TypeV evaluationType) {
    return objDb.ref(value, evaluationType);
  }

  public Select select(Expr struct, Int index) {
    return objDb.select(struct, index);
  }

  public Str string(String string) {
    return objDb.string(string);
  }

  public Struc_ struct(StructTypeO structType, ImmutableList<Val> items) {
    return objDb.struct(structType, items);
  }

  public StructExpr structExpr(StructTypeO evaluationType, ImmutableList<? extends Expr> items) {
    return objDb.structExpr(evaluationType, items);
  }

  public Tuple tuple(TupleTypeO type, Iterable<? extends Obj> items) {
    return objDb.tuple(type, items);
  }

  public Order order(List<? extends Expr> elements) {
    return objDb.order(elements);
  }

  // Types

  public ArrayTypeO arrayType(TypeV elementType) {
    return objTypeDb.array(elementType);
  }

  public BlobTypeO blobType() {
    return objTypeDb.blob();
  }

  public BoolTypeO boolType() {
    return objTypeDb.bool();
  }

  public IntTypeO intType() {
    return objTypeDb.int_();
  }

  public LambdaTypeO lambdaType(TypeV result, ImmutableList<TypeV> parameters) {
    return objTypeDb.function(result, parameters);
  }
  public StructTypeO messageType() {
    return messageType;
  }

  public NothingTypeO nothingType() {
    return objTypeDb.nothing();
  }

  public StringTypeO stringType() {
    return objTypeDb.string();
  }

  public StructTypeO structType(String name, NamedList<TypeV> fields) {
    return objTypeDb.struct(name, fields);
  }

  public StructExprOType structExprType(StructTypeO struct) {
    return objTypeDb.structExpr(struct);
  }

  public TupleTypeO tupleType(ImmutableList<TypeV> itemTypes) {
    return objTypeDb.tuple(itemTypes);
  }

  // other values and its types

  public StructTypeO fileType() {
    return fileType;
  }

  public Struc_ errorMessage(String text) {
    return message(ERROR.name(), text);
  }

  public Struc_ warningMessage(String text) {
    return message(WARNING.name(), text);
  }

  public Struc_ infoMessage(String text) {
    return message(INFO.name(), text);
  }

  private Struc_ message(String severity, String text) {
    Val textObject = objDb.string(text);
    Val severityObject = objDb.string(severity);
    return objDb.struct(messageType(), list(textObject, severityObject));
  }

  private static StructTypeO createMessageType(ObjTypeDb objTypeDb) {
    StringTypeO stringType = objTypeDb.string();
    return objTypeDb.struct("", namedList(list(named(stringType), named(stringType))));
  }

  private static StructTypeO createFileType(ObjTypeDb objTypeDb) {
    return objTypeDb.struct(FileStruct.NAME, namedList(list(
        named(CONTENT_FIELD_NAME, objTypeDb.blob()), named(PATH_FIELD_NAME, objTypeDb.string())))
    );
  }
}
