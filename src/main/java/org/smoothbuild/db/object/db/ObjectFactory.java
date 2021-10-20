package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.ArrayExpr;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.RecExpr;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Lambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.SpecDb;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.exec.base.FileStruct;
import org.smoothbuild.util.io.DataWriter;

import com.google.common.collect.ImmutableList;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjectFactory {
  private final ObjectDb objectDb;
  private final SpecDb specDb;
  private final StructSpec messageSpec;
  private final StructSpec fileSpec;

  @Inject
  public ObjectFactory(ObjectDb objectDb, SpecDb specDb) {
    this.objectDb = objectDb;
    this.specDb = specDb;
    this.messageSpec = createMessageSpec(specDb);
    this.fileSpec = createFileSpec(specDb);
  }

  private static StructSpec createMessageSpec(SpecDb specDb) {
    StrSpec strSpec = specDb.string();
    return specDb.struct("", list(strSpec, strSpec), list("", ""));
  }

  private static StructSpec createFileSpec(SpecDb specDb) {
    return specDb.struct(
        FileStruct.NAME,
        list(specDb.blob(), specDb.string()),
        FileStruct.FIELD_NAMES
    );
  }

  public ArrayBuilder arrayBuilder(ValSpec elementSpec) {
    return objectDb.arrayBuilder(elementSpec);
  }

  public Blob blob(DataWriter dataWriter) {
    try (BlobBuilder builder = blobBuilder()) {
      builder.write(dataWriter);
      return builder.build();
    } catch (IOException e) {
      throw new ObjectDbException(e);
    }
  }

  public BlobBuilder blobBuilder() {
    return objectDb.blobBuilder();
  }

  public Bool bool(boolean value) {
    return objectDb.boolVal(value);
  }

  public Lambda lambda(LambdaSpec spec, Expr body) {
    return objectDb.lambdaVal(spec, body);
  }

  public Int intValue(BigInteger value) {
    return objectDb.intVal(value);
  }

  public Struc_ file(Str path, Blob content) {
    return objectDb.structVal(fileSpec(), list(content, path));
  }

  public Str string(String string) {
    return objectDb.strVal(string);
  }

  public Struc_ struct(StructSpec structSpec, ImmutableList<Val> items) {
    return objectDb.structVal(structSpec, items);
  }

  public Rec rec(RecSpec spec, Iterable<? extends Obj> items) {
    return objectDb.recVal(spec, items);
  }

  public Const constExpr(Val val) {
    return objectDb.constExpr(val);
  }

  public Call callExpr(Expr function, RecExpr arguments) {
    return objectDb.callExpr(function, arguments);
  }

  public ArrayExpr arrayExpr(List<? extends Expr> elements) {
    return objectDb.arrayExpr(elements);
  }

  public Select selectExpr(Expr rec, Int index) {
    return objectDb.selectExpr(rec, index);
  }

  public Null nullExpr() {
    return objectDb.nullExpr();
  }

  public Ref refExpr(BigInteger value, ValSpec evaluationSpec) {
    return objectDb.refExpr(value, evaluationSpec);
  }

  public ArraySpec arraySpec(ValSpec elementSpec) {
    return specDb.array(elementSpec);
  }

  public BlobSpec blobSpec() {
    return specDb.blob();
  }

  public BoolSpec boolSpec() {
    return specDb.bool();
  }

  public IntSpec intSpec() {
    return specDb.int_();
  }

  public StructSpec fileSpec() {
    return fileSpec;
  }

  public StructSpec messageSpec() {
    return messageSpec;
  }

  public NothingSpec nothingSpec() {
    return specDb.nothing();
  }

  public StrSpec stringSpec() {
    return specDb.string();
  }

  public RecSpec recSpec(ImmutableList<ValSpec> itemSpecs) {
    return specDb.recSpec(itemSpecs);
  }

  public StructSpec structSpec(
      String name, ImmutableList<? extends ValSpec> itemSpecs, ImmutableList<String> names) {
    return specDb.struct(name, itemSpecs, names);
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
    Val textObject = objectDb.strVal(text);
    Val severityObject = objectDb.strVal(severity);
    return objectDb.structVal(messageSpec(), list(textObject, severityObject));
  }
}
