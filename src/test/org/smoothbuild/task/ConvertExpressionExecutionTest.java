package org.smoothbuild.task;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.expr.ArrayExpr;
import org.smoothbuild.lang.expr.ConstantExpr;
import org.smoothbuild.lang.expr.Convert;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.task.exec.Task;
import org.smoothbuild.task.exec.TaskGraph;
import org.smoothbuild.testing.common.StreamTester;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ConvertExpressionExecutionTest {
  private static final ImmutableList<Expr<SNothing>> emptyNothingExprList = ImmutableList
      .<Expr<SNothing>> of();
  private ObjectsDb objectsDb;
  private TaskGraph taskGraph;
  private Expr<SArray<SNothing>> nilExpr;
  private Expr<SArray<SString>> stringArrayExpr;
  private Expr<SArray<SBlob>> blobArrayExpr;
  private Expr<SArray<SFile>> fileArrayExpr;
  private Expr<SFile> fileExpr;
  private Expr<SBlob> blobExpr;
  private Task<?> task;
  private SBlob blob;
  private SFile file;

  @Before
  public void before() {
    Injector injector = Guice.createInjector(new TestExecutorModule());
    objectsDb = injector.getInstance(ObjectsDb.class);
    taskGraph = injector.getInstance(TaskGraph.class);
  }

  @Test
  public void converts_file_to_blob() throws Exception {
    given(blob = createBlob());
    given(file = objectsDb.file(path("file.txt"), blob));
    given(fileExpr = new ConstantExpr<>(FILE, file, codeLocation(2)));
    given(blobExpr = Convert.convertExpr(BLOB, fileExpr));
    given(task = taskGraph.createTasks(blobExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(blob));
    thenEqual(task.resultType(), BLOB);
  }

  @Test
  public void converts_file_array_to_blob_array() throws Exception {
    given(blob = createBlob());
    given(file = objectsDb.file(path("file.txt"), blob));
    given(fileExpr = new ConstantExpr<>(FILE, file, codeLocation(2)));
    given(fileArrayExpr = new ArrayExpr<>(FILE_ARRAY, ImmutableList.of(fileExpr), codeLocation(2)));
    given(blobArrayExpr = Convert.convertExpr(BLOB_ARRAY, fileArrayExpr));
    given(task = taskGraph.createTasks(blobArrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(objectsDb.arrayBuilder(BLOB_ARRAY).add(blob).build()));
    thenEqual(task.resultType(), BLOB_ARRAY);
  }

  @Test
  public void converts_nil_to_string_array() throws Exception {
    given(nilExpr = new ArrayExpr<>(NIL, emptyNothingExprList, codeLocation(2)));
    given(stringArrayExpr = Convert.convertExpr(STRING_ARRAY, nilExpr));
    given(task = taskGraph.createTasks(stringArrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(objectsDb.arrayBuilder(STRING_ARRAY).build()));
    thenEqual(task.resultType(), STRING_ARRAY);
  }

  @Test
  public void converts_nil_to_blob_array() throws Exception {
    given(nilExpr = new ArrayExpr<>(NIL, emptyNothingExprList, codeLocation(2)));
    given(blobArrayExpr = Convert.convertExpr(BLOB_ARRAY, nilExpr));
    given(task = taskGraph.createTasks(blobArrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(objectsDb.arrayBuilder(BLOB_ARRAY).build()));
    thenEqual(task.resultType(), BLOB_ARRAY);
  }

  @Test
  public void converts_nil_to_file_array() throws Exception {
    given(nilExpr = new ArrayExpr<>(NIL, emptyNothingExprList, codeLocation(2)));
    given(fileArrayExpr = Convert.convertExpr(FILE_ARRAY, nilExpr));
    given(task = taskGraph.createTasks(fileArrayExpr));
    when(taskGraph).executeAll();
    thenEqual(task.output(), new TaskOutput<>(objectsDb.arrayBuilder(FILE_ARRAY).build()));
    thenEqual(task.resultType(), FILE_ARRAY);
  }

  @Test
  public void converting_nil_to_nil_is_forbidden() throws Exception {

  }

  private SBlob createBlob() throws IOException {
    BlobBuilder builder = objectsDb.blobBuilder();
    StreamTester.writeAndClose(builder.openOutputStream(), "content");
    return builder.build();
  }
}
