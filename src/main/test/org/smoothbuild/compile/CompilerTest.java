package org.smoothbuild.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nList;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.TopEvalS;
import org.smoothbuild.lang.expr.ExprS;
import org.smoothbuild.lang.expr.IntS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;

import com.google.common.collect.ImmutableList;

public class CompilerTest extends TestingContext {
  @Nested
  class _compiling {
    @Test
    public void blob() {
      var blob = blobS(37);
      assertCompilation(blob, blobB(37));
    }

    @Test
    public void call() {
      var defFunc = defFuncS("myFunc", nList(), stringS("abc"));
      var call = callS(stringTS(), topRefS(defFunc));
      assertCompilation(defFunc, call, callB(funcB(stringB("abc"))));
    }

    @Test
    public void call_polymorphic() {
      var oa = varS("A");
      var ca = varS("A");
      var identity = defFuncS(oa, "myIdentity", nList(itemS(oa, "p")), paramRefS(ca, "p"));
      var call = callS(stringTS(), topRefS(identity), stringS("abc"));
      var v = varB("A");
      assertCompilation(identity, call,
          callB(stringTB(), funcB(v, list(v), paramRefB(v, 0)), stringB("abc")));
    }

    @Test
    public void int_() {
      IntS int_ = intS(1);
      assertCompilation(int_, intB(1));
    }

    @Test
    public void order() {
      var order = orderS(stringTS(), stringS("abc"), stringS("def"));
      assertCompilation(order, orderB(stringB("abc"), stringB("def")));
    }

    @Test
    public void paramRef() {
      var func = defFuncS("f", nList(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
      assertCompilation(func, topRefS(func), funcB(list(intTB()), paramRefB(intTB(), 0)));
    }

    @Test
    public void select() {
      var structTS = structTS("MyStruct", nList(sigS(stringTS(), "field")));
      var syntCtorS = syntCtorS(structTS);
      var callS = callS(structTS, topRefS(syntCtorS), stringS("abc"));
      var selectS = selectS(stringTS(), callS, "field");

      var ctorB = funcB(list(stringTB()), combineB(paramRefB(stringTB(), 0)));
      var callB = callB(ctorB, stringB("abc"));
      assertCompilation(syntCtorS, selectS, selectB(callB, intB(0)));
    }

    @Test
    public void string() {
      var string = stringS("abc");
      assertCompilation(string, stringB("abc"));
    }

    @Test
    public void topRef_to_val() {
      var defVal = defValS("myVal", stringS("abc"));
      assertCompilation(defVal, topRefS(defVal), stringB("abc"));
    }

    @Test
    public void topRef_to_bytecode_val() throws IOException {
      Class<?> clazz = ReturnAbc.class;
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var annValS = annValS(ann, stringTS(), modPath(filePath), "myVal", loc(filePath, 2));

      var valB = stringB("abc");

      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
      var compiler = newCompiler(defs(annValS), fileLoader);
      assertThat(compiler.compileExpr(topRefS(annValS)))
          .isEqualTo(valB);
    }

    @Test
    public void topRef_to_func() {
      var defFunc = defFuncS("myFunc", nList(), stringS("abc"));
      assertCompilation(defFunc, topRefS(defFunc), funcB(stringB("abc")));
    }

    @Test
    public void topRef_to_func_with_bodyT_being_subtype_of_resT() {
      var defFunc = defFuncS(arrayTS(stringTS()), "myFunc", nList(), orderS(nothingTS()));
      assertCompilation(defFunc, topRefS(defFunc),
          funcB(arrayTB(stringTB()), list(), orderB(nothingTB())));
    }

    @Test
    public void topRef_to_synt_ctor() {
      var structTS = structTS("MyStruct", nList(sigS(intTS(), "f")));
      var syntCtorS = syntCtorS(structTS);
      var expected = funcB(list(intTB()), combineB(paramRefB(intTB(), 0)));
      assertCompilation(syntCtorS, topRefS(syntCtorS), expected);
    }

    @Test
    public void topRef_to_native_func() {
      var funcTS = funcTS(intTS(), list(blobTS()));
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = "class.binary.name";
      var ann = nativeS(loc(filePath, 1), stringS(classBinaryName));
      var natFuncS = natFuncS(funcTS, "myFunc", nList(itemS(intTS(), "param")), ann);

      var resT = intTB();
      ImmutableList<TypeB> paramTs = list(blobTB());
      var funcTB = funcTB(resT, paramTs);
      var jar = blobB(37);
      var method = methodB(methodTB(resT, paramTs), jar, stringB(classBinaryName), boolB(true));
      var bodyB = invokeB(method, paramRefB(blobTB(), 0));
      var funcB = funcB(funcTB, bodyB);

      var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
      var compiler = newCompiler(defs(natFuncS), fileLoader);
      assertThat(compiler.compileExpr(topRefS(natFuncS)))
          .isEqualTo(funcB);
    }

    @Test
    public void topRef_to_bytecode_func() throws IOException {
      Class<?> clazz = ReturnIdFunc.class;
      var varTS = varS("A");
      var funcTS = funcTS(varTS, list(varTS));
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var byteFuncS = byteFuncS(ann, funcTS, modPath(filePath), "myFunc", nList(itemS(varTS, "p")),
          loc(filePath, 2));

      var a = varB("A");
      var funcTB = funcTB(a, list(a));
      var funcB = funcB(funcTB, paramRefB(varB("A"), 0));

      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
      var compiler = newCompiler(defs(byteFuncS), fileLoader);
      assertThat(compiler.compileExpr(topRefS(byteFuncS)))
          .isEqualTo(funcB);
    }

    private void assertCompilation(ExprS exprS, ObjB expected) {
      assertCompilation(defs(), exprS, expected);
    }

    private void assertCompilation(TopEvalS topEval, ExprS exprS, ObjB expected) {
      assertCompilation(defs(topEval), exprS, expected);
    }

    private void assertCompilation(DefsS defs, ExprS exprS, ObjB expected) {
      var compiler = newCompiler(defs);
      assertThat(compiler.compileExpr(exprS))
          .isEqualTo(expected);
    }
  }

  @Nested
  class _caching {
    @Test
    public void val_compilation_result() {
      assertCompilationIsCached(defValS("myVal", stringS("abcdefghi")));
    }

    @Test
    public void def_func_compilation_result() {
      assertCompilationIsCached(defFuncS("myFunc", nList(), stringS("abcdefghi")));
    }

    @Test
    public void nat_func_compilation_result() {
      assertCompilationIsCached(natFuncS(funcTS(stringTS()), "myFunc", nList()));
    }

    private void assertCompilationIsCached(TopEvalS topEval) {
      var compiler = newCompiler(topEval);
      assertThat(compiler.compileExpr(topRefS(topEval)))
          .isSameInstanceAs(compiler.compileExpr(topRefS(topEval)));
    }
  }

  private Compiler newCompiler(TopEvalS topEval) {
    return newCompiler(defs(topEval));
  }

  private Compiler newCompiler(DefsS defs) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobB(1));
      return newCompiler(defs, mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private Compiler newCompiler(DefsS defs, FileLoader fileLoader) {
    return compilerProv(fileLoader).get(defs);
  }

  private FileLoader createFileLoaderMock(FilePath filePath, BlobB value) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(filePath)).thenReturn(value);
      return mock;
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private DefsS defs() {
    return new DefsS(nList(), nList());
  }

  private DefsS defs(TopEvalS topEval) {
    return new DefsS(nList(), nList(topEval));
  }
}
