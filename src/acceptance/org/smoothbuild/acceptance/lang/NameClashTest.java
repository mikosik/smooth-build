package org.smoothbuild.acceptance.lang;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class NameClashTest extends AcceptanceTestCase {

  @Nested
  class user_function_clashes_with {
    @Nested
    class slib {
      @Test
      public void struct_constructor() throws Exception {
        createUserModule(
            "  file = 'def';  ");
        runSmoothBuild("myStruct");
        assertFinishedWithError();
        assertSysOutContainsParseError(1, "'file' is already defined at {slib}/slib.smooth:");
      }

      @Test
      public void slib_function() throws Exception {
        createUserModule(
            "  aFile = 'abc';  ");
        runSmoothBuild("aFile");
        assertFinishedWithError();
        assertSysOutContainsParseError(1, "'aFile' is already defined at");
      }
    }

    @Nested
    class user {
      @Test
      public void struct_constructor() throws Exception {
        createUserModule(
            "  MyStruct {}        ",
            "  myStruct = 'def';  ");
        runSmoothBuild("myStruct");
        assertFinishedWithError();
        assertSysOutContainsParseError(2, "'myStruct' is already defined at build.smooth:1.");
      }

      @Test
      public void other_function() throws Exception {
        createUserModule(
            "  function1 = 'abc';  ",
            "  function1 = 'def';  ");
        runSmoothBuild("function1");
        assertFinishedWithError();
        assertSysOutContainsParseError(2, "'function1' is already defined at build.smooth:1.\n");
      }
    }
  }

  @Nested
  class user_struct {
    @Nested
    class clashes_with_slib {
      @Test
      public void basic_type() throws Exception {
        createUserModule(
            "  String {}        ",
            "  result = 'abc';  ");
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContainsParseError(1, "'String' is already defined.\n");
      }

      @Test
      public void struct()
          throws Exception {
        createUserModule(
            "  File {}          ",
            "  result = 'abc';  ");
        runSmoothBuild("result");
        assertFinishedWithError();
        assertSysOutContainsParseError(1, "'File' is already defined");
      }
    }

    @Test
    public void clashes_with_other_user_struct() throws Exception {
      createUserModule(
          "  MyStruct {}      ",
          "  MyStruct {}      ",
          "  result = 'abc';  ");
      runSmoothBuild("result");
      assertFinishedWithError();
      assertSysOutContainsParseError(2, "'MyStruct' is already defined at build.smooth:1.\n");
    }

    @Test
    public void constructor_clashes_with_slib_function() throws Exception {
      createUserModule(
          "  AFile {}   ");
      runSmoothBuild("myStruct");
      assertFinishedWithError();
      assertSysOutContainsParseError(1, "'aFile' is already defined at {slib}/slib.smooth:");
    }
  }
}
