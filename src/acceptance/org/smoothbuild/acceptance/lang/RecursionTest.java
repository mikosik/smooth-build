package org.smoothbuild.acceptance.lang;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class RecursionTest extends AcceptanceTestCase {
  @Nested
  class one_element_cycle {
    @Test
    public void function() throws IOException {
      createUserModule("""
        function1() = function1();
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }

    @Test
    public void value() throws IOException {
      createUserModule("""
        myValue = myValue;
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }
  }

  @Nested
  class two_elements_cycle {
    @Test
    public void value_value() throws IOException {
      createUserModule("""
        myValue1 = myValue2;
        myValue2 = myValue1;
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }

    @Test
    public void function_function() throws IOException {
      createUserModule("""
        function1() = function2();
        function2() = function1();
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }

    @Test
    public void function_function_through_argument() throws IOException {
      createUserModule("""
        String function1() = myIdentity(function1());
        String myIdentity(String s) = s;
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }

    @Test
    public void value_value_through_argument() throws IOException {
      createUserModule("""
        String myIdentity(String s) = s;
        String myValue = myIdentity(myValue);
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }
  }

  @Nested
  class three_element_cycle {
    @Test
    public void function_function_function() throws IOException {
      createUserModule("""
        function1() = function2();
        function2() = function3();
        function3() = function1();
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }

    @Test
    public void value_value_value() throws IOException {
      createUserModule("""
        myValue1 = myValue2;
        myValue2 = myValue3;
        myValue3 = myValue1;
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }

    @Test
    public void value_function_value() throws IOException {
      createUserModule("""
        myValue1 = myFunction2();
        myFunction2() = myValue3;
        myValue3 = myValue1;
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }

    @Test
    public void function_value_function() throws IOException {
      createUserModule("""
        function1() = myValue2;
        myValue2 = function3();
        function3() = function1();
        """);
      runSmoothList();
      assertFinishedWithError();
      assertSysOutContains("Dependency graph contains cycle");
    }
  }
}
