package org.smoothbuild.db.object.db;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.math.BigInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.val.ArrayBuilder;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.BoolSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
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
  private final RecSpec messageSpec;
  private final RecSpec fileSpec;

  @Inject
  public ObjectFactory(ObjectDb objectDb) {
    this.objectDb = objectDb;
    this.messageSpec = createMessageSpec(objectDb);
    this.fileSpec = createFileSpec(objectDb);
  }

  private static RecSpec createMessageSpec(ObjectDb objectDb) {
    StrSpec strSpec = objectDb.strS();
    return objectDb.recS(list(strSpec, strSpec));
  }

  private static RecSpec createFileSpec(ObjectDb objectDb) {
    return objectDb.recS(list(objectDb.blobS(), objectDb.strS()));
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

  public Int intValue(BigInteger value) {
    return objectDb.intVal(value);
  }

  public Rec file(Str path, Blob content) {
    return objectDb.recVal(fileSpec(), list(content, path));
  }

  public Str string(String string) {
    return objectDb.strVal(string);
  }

  public Rec rec(RecSpec spec, Iterable<? extends Obj> elements) {
    return objectDb.recVal(spec, elements);
  }

  public ArraySpec arraySpec(ValSpec elementSpec) {
    return objectDb.arrayS(elementSpec);
  }

  public BlobSpec blobSpec() {
    return objectDb.blobS();
  }

  public BoolSpec boolSpec() {
    return objectDb.boolS();
  }

  public IntSpec intSpec() {
    return objectDb.intS();
  }

  public RecSpec fileSpec() {
    return fileSpec;
  }

  public RecSpec messageSpec() {
    return messageSpec;
  }

  public NothingSpec nothingSpec() {
    return objectDb.nothingS();
  }

  public StrSpec stringSpec() {
    return objectDb.strS();
  }

  public RecSpec recSpec(Iterable<? extends ValSpec> elementSpecs) {
    return objectDb.recS(elementSpecs);
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
