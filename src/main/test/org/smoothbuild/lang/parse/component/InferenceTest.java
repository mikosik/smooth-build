package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.TestModuleLoader.module;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;
import static org.smoothbuild.lang.base.type.TestingTypes.item;

import org.junit.jupiter.api.Test;

public class InferenceTest {
  @Test
  public void string_literal() {
    module("""
        myValue = "abc";
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myValue", STRING);
  }

  @Test
  public void blob_literal() {
    module("""
        myValue = 0x07;
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myValue", BLOB);
  }

  @Test
  public void function_reference() {
    module("""
        @Native("Impl.met")
        String myFunction(Blob param);
        myValue = myFunction;
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myValue", f(STRING, BLOB));
  }

  @Test
  public void argless_function_call() {
    module("""
        @Native("Impl.met")
        String myFunction();
        myValue = myFunction();
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myValue", STRING);
  }

  @Test
  public void generic_function_with_monotype_argument() {
    module("""
        A myIdentity(A a) = a;
        myValue = myIdentity("abc");
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myValue", STRING);
  }

  @Test
  public void generic_function_with_monotype_array_argument() {
    module("""
        A myIdentity(A a) = a;
        myValue = myIdentity(["abc"]);
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myValue", a(STRING));
  }

  @Test
  public void generic_function_with_monotype_function_argument() {
    module("""
        A myIdentity(A a) = a;
        @Native("Impl.met")
        Blob myOtherFunction(String s);
        myValue = myIdentity(myOtherFunction);
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myValue", f(BLOB, STRING));
  }

  @Test
  public void generic_function_with_polytype_argument() {
    module("""
        A myIdentity(A a) = a;
        myFunction(B b) = myIdentity(b);
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myFunction", f(B, item(B, "b")));
  }

  @Test
  public void generic_function_with_polytype_array_argument() {
    module("""
        A myIdentity(A a) = a;
        myFunction(B b) = myIdentity([b]);
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myFunction", f(a(B), item(B, "b")));
  }

  @Test
  public void generic_function_with_polytype_function_argument() {
    module("""
        A myIdentity(A a) = a;
        @Native("Impl.met")
        B myOtherFunction(B b);
        myFunction() = myIdentity(myOtherFunction);
        """)
        .loadsSuccessfully()
        .containsReferencableWithType("myFunction", f(f(B, B)));
  }
}
