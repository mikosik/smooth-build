package org.smoothbuild.compile.sb;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.compile.lang.define.ExprS;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.load.FileLoader;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.func.bytecode.ReturnAbc;
import org.smoothbuild.testing.func.bytecode.ReturnIdFunc;
import org.smoothbuild.testing.func.bytecode.ReturnReturnAbcFunc;

import com.google.common.collect.ImmutableMap;

public class SbTranslatorTest extends TestContext {
  @Nested
  class _translate {
    @Nested
    class _val {
      @Test
      public void blob() {
        var blobS = blobS(37);
        assertConversion(blobS, blobB(37));
      }

      @Test
      public void int_() {
        var intS = intS(1);
        assertConversion(intS, intB(1));
      }

      @Test
      public void string() {
        var stringS = stringS("abc");
        assertConversion(stringS, stringB("abc"));
      }

      @Nested
      class _named_val {
        @Test
        public void def_val() {
          var valS = defValS("myValue", intS(7));
          assertConversion(valS, intB(7));
        }

        @Test
        public void def_val_referencing_other_def_val() {
          var valS = defValS("myValue", defValS("otherValue", intS(7)));
          assertConversion(valS, intB(7));
        }

        @Test
        public void native_val() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natValS = annValS(ann, stringTS(), modPath(filePath), "myValue", loc(filePath, 2));

          var jar = blobB(37);
          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
          var converter = newTranslator(fileLoader);

          assertCall(() -> converter.translateExpr(natValS))
              .throwsException(new TranslateSbExc("Illegal value annotation: `@Native`."));
        }

        @Test
        public void bytecode_val() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteValS = annValS(ann, stringTS(), modPath(filePath), "myValue", loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var converter = newTranslator(fileLoader);
          assertThat(converter.translateExpr(byteValS))
              .isEqualTo(stringB("abc"));
        }
      }

      @Nested
      class _func {
        @Test
        public void def_func() {
          var funcS = defFuncS("myFunc", nlist(), intS(7));
          assertConversion(funcS, defFuncB(intB(7)));
        }

        @Test
        public void native_func() {
          var funcTS = funcTS(intTS(), blobTS());
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natFuncS = natFuncS(funcTS, "myFunc", nlist(), ann);

          var funcTB = funcTB(intTB(), blobTB());
          var natFuncB = natFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var converter = newTranslator(fileLoader);
          assertThat(converter.translateExpr(natFuncS))
              .isEqualTo(natFuncB);
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var funcTS = funcTS(stringTS());
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, funcTS, modPath(filePath), "myFunc",
              nlist(itemS(blobTS(), "p")), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var converter = newTranslator(fileLoader);
          assertThat(converter.translateExpr(byteFuncS))
              .isEqualTo(returnAbcFuncB());
        }
      }
    }

    @Nested
    class _operator {
      @Test
      public void call() {
        var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
        var call = callS(stringTS(), defFunc);
        assertConversion(call, callB(defFuncB(stringB("abc"))));
      }

      @Test
      public void order() {
        var order = orderS(intTS(), intS(3), intS(7));
        assertConversion(order, orderB(intB(3), intB(7)));
      }

      @Test
      public void paramRef() {
        var func = defFuncS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
        assertConversion(func, idFuncB());
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var syntCtorS = syntCtorS(structTS);
        var callS = callS(structTS, syntCtorS, stringS("abc"));
        var selectS = selectS(stringTS(), callS, "field");

        var ctorB = defFuncB(list(stringTB()), combineB(paramRefB(stringTB(), 0)));
        var callB = callB(ctorB, stringB("abc"));
        assertConversion(selectS, selectB(callB, intB(0)));
      }

      @Nested
      class _monoize {
        @Nested
        class _val {
          @Test
          public void defined() {
            var emptyArrayVal = emptyArrayValS();
            var monoizeS = monoizeS(aToIntVarMapS(), emptyArrayVal);
            var arrayB = orderB(intTB());
            assertConversion(monoizeS, arrayB);
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_val_type_param() {
            var a = varA();
            var b = varB();

            var emptyArrayValS = emptyArrayValS();
            var bEmptyArrayMonoValS = monoizeS(ImmutableMap.of(a, b), emptyArrayValS);

            var referencingValS = polyDefValS("referencing", bEmptyArrayMonoValS);
            var referencingMonoValS = monoizeS(ImmutableMap.of(b, intTS()), referencingValS);

            var referencingValB = orderB(intTB());
            assertConversion(referencingMonoValS, referencingValB);
          }

          @Test
          public void bytecode() throws IOException {
            var clazz = ReturnIdFunc.class;
            var a = varA();
            var funcTS = funcTS(a, a);
            var filePath = smoothFilePath();
            var classBinaryName = clazz.getCanonicalName();
            var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
            var byteValS = polyByteValS(2, ann, funcTS, "myFunc");

            var fileLoader = createFileLoaderMock(
                filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
            var converter = newTranslator(fileLoader);
            var monoizeS = monoizeS(aToIntVarMapS(), byteValS);
            assertThat(converter.translateExpr(monoizeS))
                .isEqualTo(idFuncB());
          }
        }

        @Nested
        class _func {
          @Test
          public void defined() {
            var identity = idFuncS();
            var monoizeS = monoizeS(aToIntVarMapS(), identity);
            var funcB = defFuncB(funcTB(intTB(), intTB()), paramRefB(intTB(), 0));
            assertConversion(monoizeS, funcB);
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_func_type_param() {
            var a = varA();
            var b = varB();

            var idFuncS = idFuncS();
            var bIdMonoFuncS = monoizeS(ImmutableMap.of(a, b), idFuncS);

            var bodyS = callS(b, bIdMonoFuncS, paramRefS(b, "p"));
            var wrapFuncS = polyDefFuncS(b, "wrap", nlist(itemS(b, "p")), bodyS);
            var wrapMonoFuncS = monoizeS(ImmutableMap.of(b, intTS()), wrapFuncS);

            var idFuncB = defFuncB(funcTB(intTB(), intTB()), paramRefB(intTB(), 0));
            var wrapFuncB = defFuncB(funcTB(intTB(), intTB()),
                callB(idFuncB, paramRefB(intTB(), 0)));
            assertConversion(wrapMonoFuncS, wrapFuncB);
          }

          @Test
          public void native_() {
            var a = varA();
            var funcTS = funcTS(a, a);
            var filePath = filePath(PRJ, path("my/path"));
            var classBinaryName = "class.binary.name";
            var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
            var natFuncS = polyNatFuncS(funcTS, "myIdentity", nlist(itemS(a, "param")), ann);

            var funcTB = funcTB(intTB(), intTB());
            var natFuncB = natFuncB(funcTB, blobB(37), stringB(classBinaryName), boolB(true));

            var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
            var converter = newTranslator(fileLoader);
            var monoizeS = monoizeS(ImmutableMap.of(a, intTS()), natFuncS);
            assertThat(converter.translateExpr(monoizeS))
                .isEqualTo(natFuncB);
          }

          @Test
          public void bytecode() throws IOException {
            var clazz = ReturnIdFunc.class;
            var a = varA();
            var funcTS = funcTS(a, a);
            var filePath = smoothFilePath();
            var classBinaryName = clazz.getCanonicalName();
            var ann = bytecodeS(classBinaryName, loc(filePath, 1));
            var byteFuncS = polyByteFuncS(ann, 1, funcTS.res(), "myFunc", nlist(itemS(a, "p")));

            var fileLoader = createFileLoaderMock(
                filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
            var converter = newTranslator(fileLoader);
            var monoizeS = monoizeS(ImmutableMap.of(a, intTS()), byteFuncS);
            assertThat(converter.translateExpr(monoizeS))
                .isEqualTo(idFuncB());
          }
        }
      }
    }
  }

  @Nested
  class _caching {
    @Test
    public void def_val_conversion_result() {
      assertConversionIsCached(defValS("myVal", stringS("abcdefghi")));
    }

    @Test
    public void bytecode_val_conversion_result() throws IOException {
      var clazz = ReturnAbc.class;
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var bytecodeValS = annValS(ann, stringTS(), modPath(filePath), "myFunc", loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertConversionIsCached(bytecodeValS, newTranslator(fileLoader));
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
    public void bytecode_func_conversion_result() throws IOException {
      var clazz = ReturnReturnAbcFunc.class;
      var funcTS = funcTS(stringTS());
      var filePath = filePath(PRJ, path("my/path"));
      var classBinaryName = clazz.getCanonicalName();
      var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
      var bytecodeFuncS = annFuncS(ann, funcTS, modPath(filePath), "myFunc", nlist(), loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertConversionIsCached(bytecodeFuncS, newTranslator(fileLoader));
    }

    @Test
    public void synt_ctor_conversion_result() {
      assertConversionIsCached(syntCtorS(structTS("MyStruct", nlist(sigS(stringTS(), "name")))));
    }

    @Test
    public void monoized_poly_func_conversion_result() {
      var monoizeS = monoizeS(aToIntVarMapS(), idFuncS());
      assertConversionIsCached(monoizeS);
    }

    private void assertConversionIsCached(ExprS exprS) {
      var converter = newTranslator();
      assertConversionIsCached(exprS, converter);
    }

    private void assertConversionIsCached(ExprS exprS, SbTranslator sbTranslator) {
      assertThat(sbTranslator.translateExpr(exprS))
          .isSameInstanceAs(sbTranslator.translateExpr(exprS));
    }
  }

  private void assertConversion(ExprS exprS, ExprB expected) {
    assertThat(newTranslator().translateExpr(exprS))
        .isEqualTo(expected);
  }

  private SbTranslator newTranslator() {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobB(1));
      return newTranslator(mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private SbTranslator newTranslator(FileLoader fileLoader) {
    return sbTranslatorProv(fileLoader).get();
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
}
