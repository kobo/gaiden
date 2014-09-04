/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaiden

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.ast.expr.MethodCallExpression.*
import static org.codehaus.groovy.ast.expr.VariableExpression.*

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class PagesAstTransformation implements ASTTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        def newStatements = replaceFilePathExpression(source.AST.statementBlock.statements)
        source.AST.statementBlock.statements.clear()
        source.AST.statementBlock.statements.addAll(newStatements)
    }

    private static List<Statement> replaceFilePathExpression(List<Statement> statements) {
        statements.collect { statement ->
            if (!(statement instanceof ExpressionStatement)) {
                return statement
            }

            def expr = statement as ExpressionStatement
            if (expr.expression instanceof MethodCallExpression) {
                def call = expr.expression as MethodCallExpression
                if (call.arguments instanceof ArgumentListExpression) {
                    def arguments = call.arguments as ArgumentListExpression
                    def closureExpr = arguments.expressions.find {
                        it instanceof ClosureExpression
                    } as ClosureExpression
                    if (closureExpr) {
                        def blockStatement = closureExpr.code as BlockStatement
                        def newBlockStatements = replaceFilePathExpression(blockStatement.statements)
                        blockStatement.statements.clear()
                        blockStatement.statements.addAll(newBlockStatements)
                    }
                }
                return statement
            } else {
                return new ExpressionStatement(new MethodCallExpression(THIS_EXPRESSION, expr.expression.text, NO_ARGUMENTS))
            }
        }
    }
}
