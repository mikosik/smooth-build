package org.smoothbuild.acceptance.lang;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class NameClashTest extends AcceptanceTestCase {
  @Nested
  class user_value_clashes_with {
    @Nested
    class slib {
      @Test
      public void value() throws Exception {
        createUserModule("""
                true = "abc";
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("true"));
      }

      @Test
      public void function() throws Exception {
        createUserModule("""
                and = "abc";
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("and"));
      }

      @Test
      public void constructor() throws Exception {
        createUserModule("""
                file = "abc";
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("file"));
      }
    }

    @Nested
    class user {
      @Test
      public void value() throws Exception {
        createUserModule("""
                myValue = "abc";
                myValue = "def";
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myValue"));
      }

      @Test
      public void function() throws Exception {
        createUserModule("""
                myName() = "abc";
                myName = "def";
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myName"));
      }

      @Test
      public void constructor() throws Exception {
        createUserModule("""
                MyStruct {}
                myStruct = 'abc';
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myStruct"));
      }
    }
  }

  @Nested
  class user_function_clashes_with {
    @Nested
    class slib {
      @Test
      public void value() throws Exception {
        createUserModule("""
                true() = 'abc';
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("true"));
      }

      @Test
      public void constructor() throws Exception {
        createUserModule("""
                file() = 'def';
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("file"));
      }

      @Test
      public void function() throws Exception {
        createUserModule("""
                and() = 'abc';
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("and"));
      }
    }

    @Nested
    class user {
      @Test
      public void value() throws Exception {
        createUserModule("""
                myName = 'abc';
                myName() = 'def';
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myName"));
      }

      @Test
      public void constructor() throws Exception {
        createUserModule("""
                MyStruct {}
                myStruct() = 'def';
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myStruct"));
      }

      @Test
      public void function() throws Exception {
        createUserModule("""
                myFunction() = 'abc';
                myFunction() = 'def';
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myFunction"));
      }
    }
  }

  @Nested
  class user_struct_clashes_with {
    @Nested
    class slib {
      @Test
      public void basic_type() throws Exception {
        createUserModule("""
              String {}
              """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, "'String' is already defined.");
      }

      @Test
      public void struct()
          throws Exception {
        createUserModule("""
              File {}
              """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("File"));
      }
    }

    @Nested
    class user {
      @Test
      public void struct() throws Exception {
        createUserModule("""
              MyStruct {}
              MyStruct {}
              """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("MyStruct"));
      }
    }
  }

  @Nested
  class user_constructor_clashes_with {
    @Nested
    class slib {
      @Test
      public void value() throws Exception {
        createUserModule("""
                True {}
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("true"));
      }

      @Test
      public void function() throws Exception {
        createUserModule("""
                And {}
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(1, alreadyDefinedInSlibModule("and"));
      }
    }

    @Nested
    class user {
      @Test
      public void value() throws Exception {
        createUserModule("""
                myName = "abc";
                MyName {}
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myName"));
      }

      @Test
      public void function() throws Exception {
        createUserModule("""
                myName() = "abc";
                MyName {}
                """);
        runSmoothList();
        assertFinishedWithError();
        assertSysOutContainsParseError(2, alreadyDefinedInUserModule("myName"));
      }
    }
  }

  private static String alreadyDefinedInUserModule(String name) {
    return "'" + name + "' is already defined at build.smooth:1.\n";
  }

  private static String alreadyDefinedInSlibModule(String name) {
    return "'" + name + "' is already defined at {slib}/slib.smooth:";
  }
}
