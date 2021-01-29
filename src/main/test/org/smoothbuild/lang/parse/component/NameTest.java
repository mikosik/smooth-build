package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NameTest {
  @Nested
  class value {
    @Test
    public void with_normal_name() {
      module("""
             myValue = "abc";
             """)
          .loadsSuccessfully();
    }

    @Test
    public void with_illegal_name_causes_error() {
      module("""
             myValue^ = "abc";
             """)
          .loadsWithError(1, """
            token recognition error at: '^'
            myValue^ = "abc";
                   ^""");
    }

    @Test
    public void with_name_starting_with_large_letter_causes_error() {
      module("""
             MyValue = "abc";
             """)
          .loadsWithError(1, """
            no viable alternative at input 'MyValue='
            MyValue = "abc";
                    ^""");
    }

    @Test
    public void with_one_large_letter_name_causes_error() {
      module("""
             A = "abc";
             """)
          .loadsWithError(1, """
            no viable alternative at input 'A='
            A = "abc";
              ^""");
    }
  }

  @Nested
  class function {
    @Test
    public void with_normal_name() {
      module("""
             myFunction() = "abc";
             """)
          .loadsSuccessfully();
    }

    @Test
    public void with_illegal_name_causes_error() {
      module("""
             myFunction^() = "abc";
             """)
          .loadsWithError(1, """
            token recognition error at: '^'
            myFunction^() = "abc";
                      ^""");
    }

    @Test
    public void with_name_starting_with_large_letter_causes_error() {
      module("""
             MyFunction() = "abc";
             """)
          .loadsWithError(1, """
                missing NAME at '='
                MyFunction() = "abc";
                             ^""");
    }

    @Test
    public void with_one_large_letter_name_causes_error() {
      module("""
             A() = "abc";
             """)
          .loadsWithError(1, """
              missing NAME at '='
              A() = "abc";
                  ^""");
    }
  }

  @Nested
  class parameter {
    @Test
    public void with_normal_name() {
      module("""
             @Native("impl")
             String myFunction(String name);
             """)
          .loadsSuccessfully();
    }

    @Test
    public void with_illegal_name_causes_error() {
      module("""
             String myFunction(String name^);
             """)
          .loadsWithError(1, """
              token recognition error at: '^'
              String myFunction(String name^);
                                           ^""");
    }

    @Test
    public void with_name_starting_with_large_letter_causes_error() {
      module("""
             String myFunction(String Name);
             """)
          .loadsWithError(1, """
              mismatched input 'Name' expecting {'(', NAME}
              String myFunction(String Name);
                                       ^^^^""");
    }

    @Test
    public void with_one_large_letter_name_causes_error() {
      module("""
             String myFunction(String A);
             """)
          .loadsWithError(1, """
              mismatched input 'A' expecting {'(', NAME}
              String myFunction(String A);
                                       ^""");
    }
  }

  @Nested
  class struct {
    @Test
    public void with_normal_name() {
      module("""
             MyStruct{}
             """)
          .loadsSuccessfully();
    }

    @Test
    public void with_illegal_name_causes_error() {
      module("""
             MyStruct^{}
             """)
          .loadsWithError(1, """
            token recognition error at: '^'
            MyStruct^{}
                    ^""");
    }

    @Test
    public void with_name_starting_with_small_letter_causes_error() {
      module("""
             myStruct{}
             """)
          .loadsWithError(1, """
              mismatched input '{' expecting {'(', '=', ';'}
              myStruct{}
                      ^""");
    }

    @Test
    public void with_one_large_letter_name_causes_error() {
      module("""
             A{}
             """)
          .loadsWithError(1, "`A` is illegal struct name. It must have at least two characters.");
    }
  }

  @Nested
  class field {
    @Test
    public void with_normal_name() {
      module("""
             MyStruct {
               String field
             }
             """)
          .loadsSuccessfully();
    }

    @Test
    public void with_illegal_name_causes_error() {
      module("""
             MyStruct {
               String field^
             }
             """)
          .loadsWithError(2, """
              token recognition error at: '^'
                String field^
                            ^""");
    }

    @Test
    public void with_name_starting_with_large_letter_causes_error() {
      module("""
             MyStruct {
               String Field
             }
             """)
          .loadsWithError(2, """
              mismatched input 'Field' expecting {'(', NAME}
                String Field
                       ^^^^^""");
    }

    @Test
    public void with_one_large_letter_name_causes_error() {
      module("""
             MyStruct {
               String A
             }
             """)
          .loadsWithError(2, """
              mismatched input 'A' expecting {'(', NAME}
                String A
                       ^""");
    }
  }
}
