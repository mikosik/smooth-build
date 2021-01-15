package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNullElseGet;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.isVariableName;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.variable;
import static org.smoothbuild.lang.parse.InferCallType.inferCallType;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.MemoryLogger;
import org.smoothbuild.lang.base.Defined;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Struct;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.ArrayNode;
import org.smoothbuild.lang.parse.ast.ArrayTypeNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.BlobNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;
import org.smoothbuild.lang.parse.ast.FieldReadNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StringNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.TypeNode;
import org.smoothbuild.lang.parse.ast.ValueNode;

public class InferTypes {
  public static List<Log> inferTypes(Ast ast, Definitions imported) {
    var logger = new MemoryLogger();
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        super.visitStruct(struct);
        if (struct.fields().stream().anyMatch(f -> f.type().isEmpty())) {
          struct.setStruct(Optional.empty());
          return;
        }

        var fields = struct.fields()
            .stream()
            .map(f -> new Item(f.type().get(), f.name(), empty()))
            .collect(toImmutableList());
        struct.setStruct(Optional.of(new Struct(struct.name(), fields, struct.location())));
      }

      @Override
      public void visitField(ItemNode fieldNode) {
        super.visitField(fieldNode);
        fieldNode.setType(fieldNode.typeNode().type());
      }

      @Override
      public void visitFunc(FuncNode func) {
        visitParams(func.params());
        func.visitExpr(this);
        func.setType(codeType(func));
      }

      @Override
      public void visitValue(ValueNode value) {
        value.visitExpr(this);
        value.setType(codeType(value));
      }

      @Override
      public void visitCallable(CallableNode callable) {
        super.visitCallable(callable);
      }

      private Optional<Type> codeType(ReferencableNode referencable) {
        if (referencable.isNative()) {
          return typeOfNativeCode(referencable);
        } else {
          return typeOfDeclaredCode(referencable);
        }
      }

      private Optional<Type> typeOfNativeCode(ReferencableNode referencable) {
        if (referencable.declaresType()) {
          return createType(referencable.typeNode());
        } else {
          logger.log(parseError(
              referencable, referencable.q() + " is native so it should have type declaration."));
          return empty();
        }
      }

      private Optional<Type> typeOfDeclaredCode(ReferencableNode referencable) {
        Optional<Type> exprType = referencable.expr().type();
        if (referencable.declaresType()) {
          Optional<Type> type = createType(referencable.typeNode());
          type.ifPresent(t -> exprType.ifPresent(et -> {
            if (!t.isAssignableFrom(et)) {
              logger.log(parseError(referencable, "`" + referencable.name()
                  + "` has body which type is " + et.q()
                  + " and it is not convertible to its declared type " + t.q()
                  + "."));
            }
          }));
          return type;
        } else {
          return exprType;
        }
      }

      @Override
      public void visitParam(int index, ItemNode param) {
        super.visitParam(index, param);
        Optional<Type> optType = param.typeNode().type();
        param.setType(optType);
        if (optType.isPresent()) {
          Type type = optType.get();
          var optDefaultValue = param.defaultValue();
          if (optDefaultValue.isPresent()) {
            var optDefaultValueType = optDefaultValue.get().type();
            if (optDefaultValueType.isPresent()) {
              Type dt = optDefaultValueType.get();
              if (!type.isParamAssignableFrom(dt)) {
                logger.log(parseError(param, "Parameter " + param.q() + " is of type " + type.q()
                    + " so it cannot have default value of type " + dt.q() + "."));
              }
            }
          }
        }
      }

      @Override
      public void visitType(TypeNode type) {
        super.visitType(type);
        type.setType(createType(type));
      }

      private Optional<Type> createType(TypeNode type) {
        if (isVariableName(type.name())) {
          return Optional.of(variable(type.name()));
        } else if (type.isArray()) {
          TypeNode elementType = ((ArrayTypeNode) type).elementType();
          return createType(elementType).map(Types::array);
        } else {
          return Optional.of(findType(type.name()).type());
        }
      }

      private Defined findType(String name) {
        Defined type = imported.types().get(name);
        return requireNonNullElseGet(type, () -> findStruct(name));
      }

      private Defined findStruct(String name) {
        StructNode structNode = ast.structsMap().get(name);
        if (structNode == null) {
          throw new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structsMap());
        } else {
          return structNode.struct().orElseThrow(() -> new RuntimeException(
              "Cannot find type `" + name + "`. Available types = " + ast.structsMap()));
        }
      }

      @Override
      public void visitFieldRead(FieldReadNode expr) {
        super.visitFieldRead(expr);
        expr.expr().type().ifPresentOrElse(
            t -> {
              if (t instanceof StructType st && st.containsFieldWithName(expr.fieldName())) {
                expr.setType(((StructType) t).fieldWithName(expr.fieldName()).type());
              } else {
                expr.setType(empty());
                logger.log(parseError(expr.location(), "Type " + t.q()
                    + " doesn't have field `" + expr.fieldName() + "`."));
              }
            },
            () -> expr.setType(empty())
        );
      }

      @Override
      public void visitArray(ArrayNode array) {
        super.visitArray(array);
        array.setType(findArrayType(array));
      }

      private Optional<Type> findArrayType(ArrayNode array) {
        List<ExprNode> expressions = array.elements();
        if (expressions.isEmpty()) {
          return Optional.of(array(nothing()));
        }
        Optional<Type> firstType = expressions.get(0).type();
        if (firstType.isEmpty()) {
          return empty();
        }

        Type elemType = firstType.get();
        for (int i = 1; i < expressions.size(); i++) {
          Optional<Type> type = expressions.get(i).type();
          if (type.isEmpty()) {
            return empty();
          }
          elemType = elemType.mergeWith(type.get(), UPPER);
        }
        return Optional.of(array(elemType));
      }

      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        final Context context = new Context(imported, ast.callablesMap());
        Maybe<Type> type = inferCallType(
            call, context.resultTypeOf(call.calledName()), context.parametersOf(call.calledName()));
        call.setType(ofNullable(type.value()));
        logger.logAllFrom(type);
      }

      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        ref.setType(ref.target().inferredType());
      }

      @Override
      public void visitArg(ArgNode arg) {
        super.visitArg(arg);
        arg.setType(arg.expr().type());
      }

      @Override
      public void visitStringLiteral(StringNode string) {
        super.visitStringLiteral(string);
        string.setType(string());
      }

      @Override
      public void visitBlobLiteral(BlobNode blob) {
        super.visitBlobLiteral(blob);
        blob.setType(blob());
      }
    }.visitAst(ast);
    return logger.logs();
  }
}
