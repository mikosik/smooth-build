package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CycleTest {
  @Nested
  class one_element_cycle {
    @Test
    public void value() {
      module("""
             myValue = myValue;
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue -> myValue""");
    }

    @Test
    public void function() {
      module("""
             myFunction1() = myFunction1();
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myFunction1""");
    }

    @Test
    public void struct() {
      module("""
             MyStruct {
               MyStruct myField
             }
             """)
          .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct -> MyStruct""");
    }
  }

  @Nested
  class two_elements_cycle {
    @Test
    public void value_value() {
      module("""
             myValue1 = myValue2;
             myValue2 = myValue1;
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue1 -> myValue2
              myBuild.smooth:2: myValue2 -> myValue1""");
    }

    @Test
    public void function_function() {
      module("""
             myFunction1() = myFunction2();
             myFunction2() = myFunction1();
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myFunction2
              myBuild.smooth:2: myFunction2 -> myFunction1""");
    }

    @Test
    public void function_function_through_argument() {
      module("""
             String myFunction() = myIdentity(myFunction());
             String myIdentity(String s) = s;
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction -> myFunction""");
    }

    @Test
    public void value_value_through_argument() {
      module("""
             String myIdentity(String s) = s;
             String myValue = myIdentity(myValue);
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:2: myValue -> myValue""");
    }

    @Test
    public void struct_struct() {
      module("""
             MyStruct1 {
               MyStruct2 myField
             }
             MyStruct2 {
               MyStruct1 myField
             }
             """)
          .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct1""");
    }
  }

  @Nested
  class three_element_cycle {
    @Test
    public void value_value_value() {
      module("""
             myValue1 = myValue2;
             myValue2 = myValue3;
             myValue3 = myValue1;
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue1 -> myValue2
              myBuild.smooth:2: myValue2 -> myValue3
              myBuild.smooth:3: myValue3 -> myValue1""");
    }

    @Test
    public void function_function_function() {
      module("""
             myFunction1() = myFunction2();
             myFunction2() = myFunction3();
             myFunction3() = myFunction1();
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myFunction2
              myBuild.smooth:2: myFunction2 -> myFunction3
              myBuild.smooth:3: myFunction3 -> myFunction1""");
    }

    @Test
    public void value_function_value() {
      module("""
             myValue1 = myFunction();
             myFunction() = myValue2;
             myValue2 = myValue1;
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myValue1 -> myFunction
              myBuild.smooth:2: myFunction -> myValue2
              myBuild.smooth:3: myValue2 -> myValue1""");
    }

    @Test
    public void function_value_function() {
      module("""
             myFunction1() = myValue;
             myValue = myFunction2();
             myFunction2() = myFunction1();
             """)
          .loadsWithError("""
              Dependency graph contains cycle:
              myBuild.smooth:1: myFunction1 -> myValue
              myBuild.smooth:2: myValue -> myFunction2
              myBuild.smooth:3: myFunction2 -> myFunction1""");
    }

    @Test
    public void struct_struct_struct() {
      module("""
             MyStruct1 {
               MyStruct2 myField
             }
             MyStruct2 {
               MyStruct3 myField
             }
             MyStruct3 {
               MyStruct1 myField
             }
             """)
          .loadsWithError("""
              Type hierarchy contains cycle:
              myBuild.smooth:2: MyStruct1 -> MyStruct2
              myBuild.smooth:5: MyStruct2 -> MyStruct3
              myBuild.smooth:8: MyStruct3 -> MyStruct1""");
    }
  }
}
