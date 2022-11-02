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
import org.smoothbuild.bytecode.expr.inst.BlobB;
import org.smoothbuild.bytecode.expr.inst.DefFuncB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.EvaluableS;
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
    class _inst {
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
          assertConversion(valS, callB(defFuncB(intB(7))));
        }

        @Test
        public void def_val_referencing_other_def_val() {
          var valS = defValS("myValue", monoizeS(defValS("otherValue", intS(7))));
          assertConversion(valS, callB(defFuncB(callB(defFuncB(intB(7))))));
        }

        @Test
        public void native_val() {
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natValS = annValS(ann, stringTS(), "myValue", loc(filePath, 2));

          var jar = blobB(37);
          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), jar);
          var translator = sbTranslator(fileLoader);

          assertCall(() -> translator.translateEvaluable(natValS))
              .throwsException(new SbTranslatorExc("Illegal value annotation: `@Native`."));
        }

        @Test
        public void bytecode_val() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteValS = annValS(ann, stringTS(), "myValue", loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var translator = sbTranslator(fileLoader);
          assertThat(translator.translateEvaluable(byteValS))
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
          var translator = sbTranslator(fileLoader);
          assertThat(translator.translateEvaluable(natFuncS))
              .isEqualTo(natFuncB);
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var funcTS = funcTS(stringTS());
          var filePath = filePath(PRJ, path("my/path"));
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, funcTS, "myFunc",
              nlist(itemS(blobTS(), "p")), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var translator = sbTranslator(fileLoader);
          assertThat(translator.translateEvaluable(byteFuncS))
              .isEqualTo(returnAbcFuncB());
        }
      }
    }

    @Nested
    class _operator {
      @Test
      public void call() {
        var defFunc = defFuncS("myFunc", nlist(), stringS("abc"));
        var call = callS(monoizeS(defFunc));
        assertConversion(call, callB(defFuncB(stringB("abc"))));
      }

      @Test
      public void order() {
        var order = orderS(intTS(), intS(3), intS(7));
        assertConversion(order, orderB(intB(3), intB(7)));
      }

      @Test
      public void ref() {
        var func = defFuncS("f", nlist(itemS(intTS(), "p")), paramRefS(intTS(), "p"));
        assertConversion(func, idFuncB());
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var syntCtorS = syntCtorS(structTS);
        var callS = callS(monoizeS(syntCtorS), stringS("abc"));
        var selectS = selectS(callS, "field");

        var ctorB = defFuncB(list(stringTB()), combineB(refB(stringTB(), 0)));
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
            var orderB = orderB(intTB());
            assertConversion(monoizeS, callB(defFuncB(orderB)));
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_val_type_param() {
            var a = varA();
            var b = varB();

            var emptyArrayValS = emptyArrayValS(a);
            var bEmptyArrayMonoValS = monoizeS(ImmutableMap.of(a, b), emptyArrayValS);

            var referencingValS = polyDefValS("referencing", bEmptyArrayMonoValS);
            var referencingMonoValS = monoizeS(ImmutableMap.of(b, intTS()), referencingValS);

            var orderB = orderB(intTB());
            assertConversion(referencingMonoValS, callB(defFuncB(callB(defFuncB(orderB)))));
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
            var translator = sbTranslator(fileLoader);
            var monoizeS = monoizeS(aToIntVarMapS(), byteValS);
            assertThat(translator.translateExpr(monoizeS))
                .isEqualTo(idFuncB());
          }
        }

        @Nested
        class _func {
          @Test
          public void defined() {
            var identity = idFuncS();
            var monoizeS = monoizeS(aToIntVarMapS(), identity);
            var funcB = defFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
            assertConversion(monoizeS, funcB);
          }

          @Test
          public void defined_monoized_with_type_parameter_of_enclosing_func_type_param() {
            var a = varA();
            var b = varB();

            var idFuncS = idFuncS();
            var bIdMonoFuncS = monoizeS(ImmutableMap.of(a, b), idFuncS);

            var bodyS = callS(bIdMonoFuncS, paramRefS(b, "p"));
            var wrapFuncS = polyDefFuncS(b, "wrap", nlist(itemS(b, "p")), bodyS);
            var wrapMonoFuncS = monoizeS(ImmutableMap.of(b, intTS()), wrapFuncS);

            var idFuncB = defFuncB(funcTB(intTB(), intTB()), refB(intTB(), 0));
            var wrapFuncB = defFuncB(funcTB(intTB(), intTB()),
                callB(idFuncB, refB(intTB(), 0)));
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
            var translator = sbTranslator(fileLoader);
            var monoizeS = monoizeS(ImmutableMap.of(a, intTS()), natFuncS);
            assertThat(translator.translateExpr(monoizeS))
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
            var translator = sbTranslator(fileLoader);
            var monoizeS = monoizeS(ImmutableMap.of(a, intTS()), byteFuncS);
            assertThat(translator.translateExpr(monoizeS))
                .isEqualTo(idFuncB());
          }
        }
      }
    }
  }

  @Nested
  class _bs_mapping {
    @Nested
    class _inst {
      @Test
      public void blob() {
        var blobS = blobS(7, 0x37);
        assertNameAndLocMapping(blobS, null, loc(7));
      }

      @Test
      public void int_() {
        var intS = intS(7, 37);
        assertNameAndLocMapping(intS, null, loc(7));
      }

      @Test
      public void string() {
        var stringS = stringS(7, "abc");
        assertNameAndLocMapping(stringS, null, loc(7));
      }

      @Nested
      class _named_val {
        @Test
        public void def_val() {
          var valS = defValS(3, "myValue", intS(7, 37));
          assertNameAndLocMapping(valS,  "myValue", loc(3));
        }

        @Test
        public void def_val_referencing_other_def_val() {
          var valS = defValS(5, "myValue", monoizeS(defValS(6, "otherValue", intS(7, 37))));
          assertNameAndLocMapping(valS, "myValue", loc(5));
        }

        @Test
        public void bytecode_val() throws IOException {
          var clazz = ReturnAbc.class;
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 7));
          var byteValS = annValS(ann, stringTS(), "myValue", loc(filePath, 8));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileLoader);
          var exprB = sbTranslator.translateEvaluable(byteValS);
          assertNameAndLocMapping(sbTranslator, exprB, "myValue", loc(8));
        }
      }

      @Nested
      class _func {
        @Test
        public void def_func() {
          var funcS = defFuncS(7, "myFunc", nlist(), intS(37));
          assertNameAndLocMapping(funcS, "myFunc", loc(7));
        }

        @Test
        public void expr_inside_def_func_body() {
          var funcS = defFuncS(7, "myFunc", nlist(), intS(8, 37));
          var sbTranslator = newTranslator();
          var funcB = (DefFuncB) sbTranslator.translateEvaluable(funcS);
          var body = funcB.body();
          assertNameAndLocMapping(sbTranslator, body, null, loc(8));
        }

        @Test
        public void native_func() {
          var funcTS = funcTS(intTS(), blobTS());
          var filePath = filePath();
          var classBinaryName = "class.binary.name";
          var ann = natAnnS(loc(filePath, 1), stringS(classBinaryName));
          var natFuncS = natFuncS(2, funcTS, "myFunc", nlist(), ann);

          var fileLoader = createFileLoaderMock(filePath.withExtension("jar"), blobB(37));
          var sbTranslator = sbTranslator(fileLoader);
          assertNameAndLocMapping(sbTranslator, natFuncS, "myFunc", loc(2));
        }

        @Test
        public void bytecode_func() throws IOException {
          var clazz = ReturnReturnAbcFunc.class;
          var funcTS = funcTS(stringTS());
          var filePath = filePath();
          var classBinaryName = clazz.getCanonicalName();
          var ann = bytecodeS(stringS(classBinaryName), loc(filePath, 1));
          var byteFuncS = annFuncS(ann, funcTS, "myFunc",
              nlist(itemS(blobTS(), "p")), loc(filePath, 2));

          var fileLoader = createFileLoaderMock(
              filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));
          var sbTranslator = sbTranslator(fileLoader);
          assertNameAndLocMapping(sbTranslator, byteFuncS, "myFunc", loc(2));
        }
      }
    }

    @Nested
    class _operator {
      @Test
      public void call() {
        var defFunc = defFuncS(7, "myFunc", nlist(), stringS("abc"));
        var call = callS(8, monoizeS(defFunc));
        assertNameAndLocMapping(call, null, loc(8));
      }

      @Test
      public void order() {
        var order = orderS(3, intTS(), intS(6), intS(7));
        assertNameAndLocMapping(order, null, loc(3));
      }

      @Test
      public void ref() {
        var func = defFuncS(4, "myFunc", nlist(itemS(intTS(), "p")), paramRefS(5, intTS(), "p"));
        var sbTranslator = newTranslator();
        var funcB = (DefFuncB) sbTranslator.translateExpr(monoizeS(func));
        var refB = funcB.body();
        assertNameAndLocMapping(sbTranslator, refB, null, loc(5));
      }

      @Test
      public void select() {
        var structTS = structTS("MyStruct", nlist(sigS(stringTS(), "field")));
        var syntCtorS = syntCtorS(structTS);
        var callS = callS(monoizeS(syntCtorS), stringS("abc"));
        var selectS = selectS(4, callS, "field");
        assertNameAndLocMapping(selectS, null, loc(4));
      }

      @Nested
      class _monoize {
        @Test
        public void def_val() {
          var emptyArrayVal = polyDefValS(7, "emptyArray", orderS(varA()));
          var monoizeS = monoizeS(4, aToIntVarMapS(), emptyArrayVal);
          assertNameAndLocMapping(monoizeS, "emptyArray", loc(7));
        }

        @Test
        public void def_func() {
          var identity = idFuncS();
          var monoizeS = monoizeS(aToIntVarMapS(), identity);
          assertNameAndLocMapping(monoizeS, "myId", loc(1));
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
      var bytecodeValS = annValS(ann, stringTS(), "myFunc", loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertConversionIsCached(bytecodeValS, sbTranslator(fileLoader));
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
      var bytecodeFuncS = annFuncS(ann, funcTS, "myFunc", nlist(), loc(filePath, 2));
      var fileLoader = createFileLoaderMock(
          filePath.withExtension("jar"), blobBJarWithJavaByteCode(clazz));

      assertConversionIsCached(bytecodeFuncS, sbTranslator(fileLoader));
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
      var translator = newTranslator();
      assertConversionIsCached(exprS, translator);
    }

    private void assertConversionIsCached(ExprS exprS, SbTranslator sbTranslator) {
      assertThat(sbTranslator.translateExpr(exprS))
          .isSameInstanceAs(sbTranslator.translateExpr(exprS));
    }

    private void assertConversionIsCached(EvaluableS evaluableS) {
      var translator = newTranslator();
      assertConversionIsCached(evaluableS, translator);
    }

    private void assertConversionIsCached(EvaluableS evaluableS, SbTranslator sbTranslator) {
      assertThat(sbTranslator.translateEvaluable(evaluableS))
          .isSameInstanceAs(sbTranslator.translateEvaluable(evaluableS));
    }
  }

  private void assertConversion(ExprS exprS, ExprB expected) {
    assertThat(newTranslator().translateExpr(exprS))
        .isEqualTo(expected);
  }

  private void assertConversion(EvaluableS evaluableS, ExprB expected) {
    assertThat(newTranslator().translateEvaluable(evaluableS))
        .isEqualTo(expected);
  }

  private void assertNameAndLocMapping(
      EvaluableS evaluableS, String expectedName, Loc expectedLoc) {
    assertNameAndLocMapping(newTranslator(), evaluableS, expectedName, expectedLoc);
  }

  private static void assertNameAndLocMapping(SbTranslator sbTranslator, EvaluableS evaluableS,
      String expectedName, Loc expectedLoc) {
    var exprB = sbTranslator.translateEvaluable(evaluableS);
    assertNameAndLocMapping(sbTranslator, exprB, expectedName, expectedLoc);
  }

  private void assertNameAndLocMapping(ExprS exprS, String expectedName, Loc expectedLoc) {
    assertNameAndLocMapping(newTranslator(), exprS, expectedName, expectedLoc);
  }

  private static void assertNameAndLocMapping(SbTranslator sbTranslator, ExprS exprS,
      String expectedName, Loc expectedLoc) {
    var exprB = sbTranslator.translateExpr(exprS);
    assertNameAndLocMapping(sbTranslator, exprB, expectedName, expectedLoc);
  }

  private static void assertNameAndLocMapping(SbTranslator sbTranslator, ExprB exprB,
      String expectedName, Loc expectedLoc) {
    var bsMapping = sbTranslator.bsMapping();
    assertThat(bsMapping.nameMapping().get(exprB.hash()))
        .isEqualTo(expectedName);
    assertThat(bsMapping.locMapping().get(exprB.hash()))
        .isEqualTo(expectedLoc);

  }

  private SbTranslator newTranslator() {
    try {
      FileLoader mock = mock(FileLoader.class);
      when(mock.load(any())).thenReturn(blobB(1));
      return sbTranslator(mock);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
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
