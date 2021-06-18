package org.smoothbuild.lang.parse.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Lists.sane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.CallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldReadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FunctionTypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.antlr.lang.SmoothParser.NatContext;
import org.smoothbuild.antlr.lang.SmoothParser.NonPipeExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.RefContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeListContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.lang.base.define.FilePath;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.expr.NativeExpression;

import com.google.common.collect.ImmutableList;

public class AstCreator {
  public static Ast fromParseTree(FilePath filePath, ModuleContext module) {
    List<StructNode> structs = new ArrayList<>();
    List<ReferencableNode> referencables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        String name = struct.TNAME().getText();
        Location location = locationOf(filePath, struct.TNAME().getSymbol());
        List<ItemNode> fields = createFields(struct.fieldList());
        structs.add(new StructNode(name, fields, location));
        return null;
      }

      private List<ItemNode> createFields(FieldListContext fieldList) {
        if (fieldList != null) {
          return sane(fieldList.field())
              .stream()
              .map(this::createField)
              .collect(toImmutableList());
        }
        return new ArrayList<>();
      }

      private ItemNode createField(FieldContext field) {
        TypeNode type = createType(field.type());
        TerminalNode nameNode = field.NAME();
        String name = nameNode.getText();
        Location location = locationOf(filePath, nameNode);
        return new ItemNode(type, name, Optional.empty(), location);
      }

      @Override
      public Void visitRef(RefContext ref) {
        TerminalNode nameNode = ref.NAME();
        visitChildren(ref);
        Optional<TypeNode> type = createTypeSane(ref.type());
        String name = nameNode.getText();
        Optional<ExprNode> expr = createExprSane(ref.expr());
        Optional<NativeExpression> nativ = createNativeSane(ref.nat());
        Location location = locationOf(filePath, nameNode);
        if (ref.p == null) {
          referencables.add(new ValueNode(type, name, expr, nativ, location));
        } else {
          List<ItemNode> params = createParams(ref.paramList());
          referencables.add(new FuncNode(type, name, params, expr, nativ, location));
        }
        return null;
      }

      private Optional<NativeExpression> createNativeSane(NatContext nativ) {
        if (nativ == null) {
          return Optional.empty();
        } else {
          return Optional.of(new NativeExpression(
              unquote(nativ.STRING().getText()),
              isPure(nativ),
              locationOf(filePath, nativ),
              filePath.withExtension("jar")));
        }
      }

      private boolean isPure(NatContext atNative) {
        return atNative.pure != null || atNative.impure == null;
      }

      private List<ItemNode> createParams(ParamListContext paramList) {
        ArrayList<ItemNode> result = new ArrayList<>();
        if (paramList != null) {
          return sane(paramList.param())
              .stream().map(this::createParam)
              .collect(toImmutableList());
        }
        return result;
      }

      private ItemNode createParam(ParamContext param) {
        TypeNode type = createType(param.type());
        String name = param.NAME().getText();
        Location location = locationOf(filePath, param);
        Optional<ExprNode> defaultValue = Optional.ofNullable(param.expr()).map(this::createExpr);
        return new ItemNode(type, name, defaultValue, location);
      }

      private Optional<ExprNode> createExprSane(ExprContext expr) {
        return expr == null ? Optional.empty() : Optional.of(createExpr(expr));
      }

      private ExprNode createExpr(ExprContext expr) {
        NonPipeExprContext initialExpression = expr.nonPipeExpr();
        ExprNode result = createNonPipeExpr(initialExpression);
        List<CallContext> callsInPipe = expr.call();
        for (int i = 0; i < callsInPipe.size(); i++) {
          CallContext call = callsInPipe.get(i);
          result = createCallInPipe(result, expr, i, call.NAME(), createArgList(call.argList()));
        }
        return result;
      }

      private ExprNode createCallInPipe(ExprNode result, ExprContext expr, int i,
          TerminalNode calledName, List<ArgNode> argList) {
        // Location of nameless piped argument is set to the location of pipe character '|'.
        Location location = locationOf(filePath, expr.p.get(i));
        List<ArgNode> args = new ArrayList<>();
        args.add(new ArgNode(null, result, location));
        args.addAll(argList);
        return new CallNode(newRefNode(calledName), args, locationOf(filePath, calledName));
      }

      private ExprNode createNonPipeExpr(NonPipeExprContext expr) {
        if (expr.fieldRead() != null) {
          ExprNode structExpr = createNonPipeExpr(expr.nonPipeExpr());
          FieldReadContext accessor = expr.fieldRead();
          String name = accessor.NAME().getText();
          return new FieldReadNode(structExpr, name, locationOf(filePath, accessor));
        }
        if (expr.array() != null) {
          List<ExprNode> elements = map(expr.array().expr(), this::createExpr);
          return new ArrayNode(elements, locationOf(filePath, expr));
        }
        if (expr.call() != null) {
          CallContext call = expr.call();
          Location location = locationOf(filePath, call);
          List<ArgNode> args = createArgList(call.argList());
          return new CallNode(newRefNode(call.NAME()), args, location);
        }
        if (expr.NAME() != null) {
          return newRefNode(expr.NAME());
        }
        if (expr.STRING() != null) {
          String quotedString = expr.STRING().getText();
          return new StringNode(unquote(quotedString), locationOf(filePath, expr));
        }
        if (expr.BLOB() != null) {
          return new BlobNode(expr.BLOB().getText().substring(2), locationOf(filePath, expr));
        }
        throw new RuntimeException("Illegal parse tree: " + NonPipeExprContext.class.getSimpleName()
            + " without children.");
      }

      private RefNode newRefNode(TerminalNode name) {
        return new RefNode(name.getText(), locationOf(filePath, name));
      }

      private List<ArgNode> createArgList(ArgListContext argList) {
        List<ArgNode> result = new ArrayList<>();
        if (argList != null) {
          List<ArgContext> args = argList.arg();
          for (ArgContext arg : args) {
            ExprContext expr = arg.expr();
            TerminalNode nameNode = arg.NAME();
            String name = nameNode == null ? null : nameNode.getText();
            ExprNode exprNode = createExpr(expr);
            result.add(new ArgNode(name, exprNode, locationOf(filePath, arg)));
          }
        }
        return result;
      }

      private Optional<TypeNode> createTypeSane(TypeContext type) {
        return type == null ? Optional.empty() : Optional.of(createType(type));
      }

      private TypeNode createType(TypeContext type) {
        if (type instanceof TypeNameContext typeIdentifier) {
          return createType(typeIdentifier);
        }
        if (type instanceof ArrayTypeContext arrayType) {
          return createArrayType(arrayType);
        }
        if (type instanceof FunctionTypeContext functionType) {
          return createFunctionType(functionType);
        }
        throw new RuntimeException("Illegal parse tree: " + TypeContext.class.getSimpleName()
            + " without children.");
      }

      private TypeNode createType(TypeNameContext type) {
        return new TypeNode(type.getText(), locationOf(filePath, type.TNAME()));
      }

      private TypeNode createArrayType(ArrayTypeContext arrayType) {
        TypeNode elementType = createType(arrayType.type());
        return new ArrayTypeNode(elementType, locationOf(filePath, arrayType));
      }

      private TypeNode createFunctionType(FunctionTypeContext functionType) {
        TypeNode resultType = createType(functionType.type());
        return new FunctionTypeNode(resultType, createTypeList(functionType.typeList()),
            locationOf(filePath, functionType));
      }

      private ImmutableList<TypeNode> createTypeList(TypeListContext typeList) {
        if (typeList != null) {
          return map(typeList.type(), this::createType);
        } else {
          return ImmutableList.of();
        }
      }
    }.visit(module);
    return new Ast(structs, referencables);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }
}
