package org.smoothbuild.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
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

public class SbConverterTest extends TestContext {
  @Nested
  class _converting {
    @Test
    public void blob() {
      var blob = blobS(37);
      assertConversion(blob, blobB(37));
    }

    @Test
    public void call() {
      var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
      var call = callS(stringTS(), refS(defFunc));
      assertConversion(defFunc, call, callB(funcB(stringB("abc"))));
    }

    @Test
    public void int_() {
      var int_ = intS(1);
      assertConversion(int_, intB(1));
    }

    @Test
    public void order() {
      var order = orderS(stringTS(), stringS("abc"), stringS("def"));
      assertConversion(order, orderB(stringB("abc"), stringB("def")));
    }

    @Test
    public void monoize_def_func() {
      var identity = idFuncS();
      var intIdentityTS = funcTS(intTS(), list(intTS()));
      var monoizeS = monoizeS(intIdentityTS, refS(identity));
      var funcB = funcB(intTB(), list(intTB()), paramRefB(intTB(), 0));
      assertConversion(identity, monoizeS, funcB);
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
      var converter = newConverter(defs(natFuncS), fileLoader);
      var monoizeS = monoizeS(funcTS(intTS(), list(intTS())), refS(natFuncS));
      assertThat(converter.convertObj(monoizeS))
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
      var converter = newConverter(defs(byteFuncS), fileLoader);
      var monoizeS = monoizeS(funcTS(intTS(), list(intTS())), refS(byteFuncS));
      assertThat(converter.convertObj(monoizeS))
          .isEqualTo(idFuncB());
    }

    @Test
    public void paramRef() {
      var func = defFuncS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
      assertConversion(func, refS(func),
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
      assertConversion(syntCtorS, selectS, selectB(callB, intB(0)));
    }

    @Test
    public void string() {
      var string = stringS("abc");
      assertConversion(string, stringB("abc"));
    }

    @Test
    public void topRef_to_val() {
      var defVal = defValS("myVal", stringS("abc"));
      assertConversion(defVal, refS(defVal), stringB("abc"));
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
      var converter = newConverter(defs(annValS), fileLoader);
      assertThat(converter.convertObj(refS(annValS)))
          .isEqualTo(valB);
    }

    @Test
    public void topRef_to_def_func() {
      var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
      assertConversion(defFunc, refS(defFunc), funcB(stringB("abc")));
    }

    @Test
    public void topRef_to_def_func_with_bodyT_being_subtype_of_resT() {
      var defFunc = defFuncS(arrayTS(stringTS()), "myFunc", nlist(), orderS(nothingTS()));
      assertConversion(defFunc, refS(defFunc),
          funcB(arrayTB(stringTB()), list(), orderB(nothingTB())));
    }

    @Test
    public void topRef_to_synt_ctor() {
      var structTS = structTS("MyStruct", nlist(sigS(intTS(), "f")));
      var syntCtorS = syntCtorS(structTS);
      var expected = funcB(list(intTB()), combineB(paramRefB(intTB(), 0)));
      assertConversion(syntCtorS, refS(syntCtorS), expected);
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
      var converter = newConverter(defs(natFuncS), fileLoader);
      assertThat(converter.convertObj(refS(natFuncS)))
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
      var converter = newConverter(defs(byteFuncS), fileLoader);
      assertThat(converter.convertObj(refS(byteFuncS)))
          .isEqualTo(funcB);
    }

    private void assertConversion(MonoObjS objS, ObjB expected) {
      assertConversion(defs(), objS, expected);
    }

    private void assertConversion(TopRefableS topRefable, MonoObjS objS, ObjB expected) {
      assertConversion(defs(topRefable), objS, expected);
    }

    private void assertConversion(DefsS defs, MonoObjS objS, ObjB expected) {
      var converter = newConverter(defs);
      assertThat(converter.convertObj(objS))
          .isEqualTo(expected);
    }
  }

  @Nested
  class _caching {
    @Test
    public void val_conversion_result() {
      assertConversionIsCached(defValS("myVal", stringS("abcdefghi")));
    }

    @Test
    public void def_func_conversion_result() {
      assertConversionIsCached(defFuncS("myFunc", nlist(), stringS("abcdefghi")));
    }

    @Test
    public void nat_func_conversion_result() {
      assertConversionIsCached(natFuncS(funcTS(stringTS()), "myFunc", nlist()));
    }

    @Test
    public void monoized_poly_func_conversion_result() {
      var monoizeS = monoizeS(funcTS(intTS(), list(intTS())), refS(idFuncS()));
      assertConversionIsCached(monoizeS, defs(idFuncS()));
    }

    private void assertConversionIsCached(MonoTopRefableS valS) {
      assertConversionIsCached(refS(valS), defs(valS));
    }

    private void assertConversionIsCached(MonoObjS monoObjS, DefsS defs) {
      var converter = newConverter(defs);
      assertThat(converter.convertObj(monoObjS))
          .isSameInstanceAs(converter.convertObj(monoObjS));
    }
  }

  private SbConverter newConverter(DefsS defs) {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobB(1));
      return newConverter(defs, mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private SbConverter newConverter(DefsS defs, FileLoader fileLoader) {
    return sbConverterProv(fileLoader).get(defs);
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
