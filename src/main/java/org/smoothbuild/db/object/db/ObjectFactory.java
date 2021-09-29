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

import org.smoothbuild.db.object.exc.ObjectDbException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.ERec;
import org.smoothbuild.db.object.obj.expr.Null;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.NothingSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.util.io.DataWriter;

/**
 * This class is thread-safe.
 * Builders returned by xxxBuilder() methods are not thread-safe.
 */
@Singleton
public class ObjectFactory {
  private final ObjectDb objectDb;
  private final SpecDb specDb;
  private final RecSpec messageSpec;
  private final RecSpec fileSpec;

  @Inject
  public ObjectFactory(ObjectDb objectDb, SpecDb specDb) {
    this.objectDb = objectDb;
    this.specDb = specDb;
    this.messageSpec = createMessageSpec(specDb);
    this.fileSpec = createFileSpec(specDb);
  }

  private static RecSpec createMessageSpec(SpecDb specDb) {
    StrSpec strSpec = specDb.strSpec();
    return specDb.recSpec(list(strSpec, strSpec));
  }

  private static RecSpec createFileSpec(SpecDb specDb) {
    return specDb.recSpec(list(
        specDb.blobSpec(),
        specDb.strSpec())
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

  public DefinedLambda definedLambda(
      DefinedLambdaSpec spec, Expr body, List<Expr> defaultArguments) {
    return objectDb.definedLambdaVal(spec, body, defaultArguments);
  }

  public Int intValue(BigInteger value) {
    return objectDb.intVal(value);
  }

  public NativeLambda nativeLambda(
      NativeLambdaSpec spec, Str classBinaryName, Blob nativeJar, List<Expr> defaultArguments) {
    return objectDb.nativeLambdaVal(spec, classBinaryName, nativeJar, defaultArguments);
  }

  public Rec file(Str path, Blob content) {
    return objectDb.recVal(fileSpec(), list(content, path));
  }

  public Str string(String string) {
    return objectDb.strVal(string);
  }

  public Rec rec(RecSpec spec, Iterable<? extends Obj> items) {
    return objectDb.recVal(spec, items);
  }

  public Const constExpr(Val val) {
    return objectDb.constExpr(val);
  }

  public Call callExpr(Expr function, ERec arguments) {
    return objectDb.callExpr(function, arguments);
  }

  public EArray eArrayExpr(Iterable<? extends Expr> elements) {
    return objectDb.eArrayExpr(elements);
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
    return specDb.arraySpec(elementSpec);
  }

  public BlobSpec blobSpec() {
    return specDb.blobSpec();
  }

  public BoolSpec boolSpec() {
    return specDb.boolSpec();
  }

  public IntSpec intSpec() {
    return specDb.intSpec();
  }

  public RecSpec fileSpec() {
    return fileSpec;
  }

  public RecSpec messageSpec() {
    return messageSpec;
  }

  public NothingSpec nothingSpec() {
    return specDb.nothingSpec();
  }

  public StrSpec stringSpec() {
    return specDb.strSpec();
  }

  public RecSpec recSpec(Iterable<? extends ValSpec> itemSpecs) {
    return specDb.recSpec(itemSpecs);
  }

  public Rec errorMessage(String text) {
    return message(ERROR.name(), text);
  }

  public Rec warningMessage(String text) {
    return message(WARNING.name(), text);
  }

  public Rec infoMessage(String text) {
    return message(INFO.name(), text);
  }

  private Rec message(String severity, String text) {
    Obj textObject = objectDb.strVal(text);
    Obj severityObject = objectDb.strVal(severity);
    return objectDb.recVal(messageSpec(), list(textObject, severityObject));
  }
}
