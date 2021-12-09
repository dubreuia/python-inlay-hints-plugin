package com.dubreuia.pih.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.*
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.python.psi.PyTargetExpression
import com.jetbrains.python.psi.PyTypedElement
import com.jetbrains.python.psi.impl.stubs.PyAnnotationElementType
import com.jetbrains.python.psi.types.TypeEvalContext

/**
 * Folder builder for python variable which folds the variable name and its
 * type annotation together. A variable with type unknown, with type annotation,
 * or which is qualified won't fold.
 *
 * See the following fold region between the plus (+) sign:
 *
 * - +my_int: int+ = 123
 * - +my_str: str+ = "string"
 *
 * For the whole syntax tree: `PsiTreeUtil.findChildrenOfType(root, PyExpression::class.java)`
 */
class PythonVariableTypeHintFolderBuilder : FoldingBuilderEx() {

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        return PsiTreeUtil.findChildrenOfType(root, PyTargetExpression::class.java).flatMap { expression ->
            ProjectManager.getInstance().openProjects.mapNotNull { project ->
                val deepCodeInsight = TypeEvalContext.deepCodeInsight(project)
                val expressionType = deepCodeInsight.getType(expression as PyTypedElement)
                // TODO how to instanceOf in Kotlin?
                val hasTypeAnnotation =
                    expression.node?.treeNext?.elementType?.javaClass == PyAnnotationElementType::class.java
                // Do not show type information in fold if no analysed type or already has type annotation,
                // or if the expression is qualified (e.g. `contract.name` won't show types)
                if (expressionType == null || hasTypeAnnotation || expression.isQualified)
                    null
                else
                    FoldingDescriptor(
                        expression.node,
                        expression.textRange,
                        null,
                        expression.name + ": " + expressionType.name,
                    )
            }
        }.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode) = "..."

    override fun isCollapsedByDefault(node: ASTNode) = true

}
