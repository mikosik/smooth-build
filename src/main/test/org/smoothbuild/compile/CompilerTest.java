package org.smoothbuild.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.cnst.BlobB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.MonoObjS;
import org.smoothbuild.lang.define.MonoTopRefableS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.testing.func.bytecode.ReturnReturnAbcFunc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CompilerTest extends TestContext {
  @Nested
  class _compiling {
    @Test
    public void blob() {
      var blob = blobS(37);
      assertCompilation(blob, blobB(37));
    }

    @Test
    public void call() {
      var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
      var call = callS(stringTS(), refS(defFunc));
      assertCompilation(defFunc, call, callB(funcB(stringB("abc"))));
    }

    @Test
    public void int_() {
      var int_ = intS(1);
      assertCompilation(int_, intB(1));
    }

    @Test
    public void order() {
      var order = orderS(stringTS(), stringS("abc"), stringS("def"));
      assertCompilation(order, orderB(stringB("abc"), stringB("def")));
    }

    @Test
    public void monoize_def_func() {
      var identity = idFuncS();
      var intIdentityTS = funcTS(intTS(), list(intTS()));
      var monoizeS = monoizeS(intIdentityTS, refS(identity));
      var funcB = funcB(intTB(), list(intTB()), paramRefB(intTB(), 0));
      assertCompilation(identity, monoizeS, funcB);
    }

    @Test
    public void monoize_nat_func() {
      var a = varA();
      var funcTS = funcTS(a, list(a));
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = "class.binary.name";
      var ann = nativeS(loc(filePath, 1), stringS(classBinaryName));
      var natFuncS = poly(natFuncS(funcTS, "myIdentity", nlist(itemS(a, "param")), ann));

      var resT = intTB();
      ImmutableList<TypeB> paramTs = list(intTB());
      var funcTB = funcTB(resT, paramTs);
      var jar = blobB(37);
      var method = methodB(methodTB(resT, paramTs), jar, stringB(classBinaryName), boolB(true));
      var bodyB = invokeB(method, paramRefB(intTB(), 0));
      var funcB = funcB(funcTB, bodyB);

      var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
      var compiler = newCompiler(defs(natFuncS), fileLoader);
      var monoizeS = monoizeS(funcTS(intTS(), list(intTS())), refS(natFuncS));
      assertThat(compiler.compileObj(monoizeS))
          .isEqualTo(funcB);
    }

    @Test
    public void monoize_bytecode_func() throws IOException {
      Class<?> clazz = ReturnIdFunc.class;
      var varTS = varA();
      var funcTS = funcTS(varTS, list(varTS));
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var byteFuncS = poly(
          byteFuncS(ann, funcTS, modPath(filePath), "myFunc", nlist(itemS(varTS, "p")),
              loc(filePath, 2)));

      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
      var compiler = newCompiler(defs(byteFuncS), fileLoader);
      var monoizeS = monoizeS(funcTS(intTS(), list(intTS())), refS(byteFuncS));
      assertThat(compiler.compileObj(monoizeS))
          .isEqualTo(idFuncB());
    }

    @Test
    public void paramRef() {
      var func = defFuncS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
      assertCompilation(func, refS(func),
          idFuncB());
    }

    @Test
    public void select() {
      var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
      var syntCtorS = syntCtorS(structTS);
      var callS = callS(structTS, refS(syntCtorS), stringS("abc"));
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
      assertCompilation(defVal, refS(defVal), stringB("abc"));
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
      assertThat(compiler.compileObj(refS(annValS)))
          .isEqualTo(valB);
    }

    @Test
    public void topRef_to_def_func() {
      var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
      assertCompilation(defFunc, refS(defFunc), funcB(stringB("abc")));
    }

    @Test
    public void topRef_to_def_func_with_bodyT_being_subtype_of_resT() {
      var defFunc = defFuncS(arrayTS(stringTS()), "myFunc", nlist(), orderS(nothingTS()));
      assertCompilation(defFunc, refS(defFunc),
          funcB(arrayTB(stringTB()), list(), orderB(nothingTB())));
    }

    @Test
    public void topRef_to_synt_ctor() {
      var structTS = structTS("MyStruct", nlist(sigS(intTS(), "f")));
      var syntCtorS = syntCtorS(structTS);
      var expected = funcB(list(intTB()), combineB(paramRefB(intTB(), 0)));
      assertCompilation(syntCtorS, refS(syntCtorS), expected);
    }

    @Test
    public void topRef_to_native_func() {
      var funcTS = funcTS(intTS(), list(blobTS()));
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = "class.binary.name";
      var ann = nativeS(loc(filePath, 1), stringS(classBinaryName));
      var natFuncS = natFuncS(funcTS, "myFunc", nlist(itemS(intTS(), "param")), ann);

      var resT = intTB();
      ImmutableList<TypeB> paramTs = list(blobTB());
      var funcTB = funcTB(resT, paramTs);
      var jar = blobB(37);
      var method = methodB(methodTB(resT, paramTs), jar, stringB(classBinaryName), boolB(true));
      var bodyB = invokeB(method, paramRefB(blobTB(), 0));
      var funcB = funcB(funcTB, bodyB);

      var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
      var compiler = newCompiler(defs(natFuncS), fileLoader);
      assertThat(compiler.compileObj(refS(natFuncS)))
          .isEqualTo(funcB);
    }

    @Test
    public void topRef_to_bytecode_func() throws IOException {
      Class<?> clazz = ReturnReturnAbcFunc.class;
      var funcTS = funcTS(stringTS());
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var byteFuncS = byteFuncS(ann, funcTS, modPath(filePath), "myFunc", nlist(), loc(filePath, 2));

      var funcB = funcB(stringB("abc"));

      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
      var compiler = newCompiler(defs(byteFuncS), fileLoader);
      assertThat(compiler.compileObj(refS(byteFuncS)))
          .isEqualTo(funcB);
    }

    private void assertCompilation(MonoObjS objS, ObjB expected) {
      assertCompilation(defs(), objS, expected);
    }

    private void assertCompilation(TopRefableS topRefable, MonoObjS objS, ObjB expected) {
      assertCompilation(defs(topRefable), objS, expected);
    }

    private void assertCompilation(DefsS defs, MonoObjS objS, ObjB expected) {
      var compiler = newCompiler(defs);
      assertThat(compiler.compileObj(objS))
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
      assertCompilationIsCached(defFuncS("myFunc", nlist(), stringS("abcdefghi")));
    }

    @Test
    public void nat_func_compilation_result() {
      assertCompilationIsCached(natFuncS(funcTS(stringTS()), "myFunc", nlist()));
    }

    @Test
    public void monoized_poly_func_compilation_result() {
      var monoizeS = monoizeS(funcTS(intTS(), list(intTS())), refS(idFuncS()));
      assertCompilationIsCached(monoizeS, defs(idFuncS()));
    }

    private void assertCompilationIsCached(MonoTopRefableS valS) {
      assertCompilationIsCached(refS(valS), defs(valS));
    }

    private void assertCompilationIsCached(MonoObjS monoObjS, DefsS defs) {
      var compiler = newCompiler(defs);
      assertThat(compiler.compileObj(monoObjS))
          .isSameInstanceAs(compiler.compileObj(monoObjS));
    }
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
    return DefsS.empty();
  }

  private DefsS defs(TopRefableS topRefable) {
    return new DefsS(
        immutableBindings(), immutableBindings(ImmutableMap.of(topRefable.name(), topRefable)));
  }
}
