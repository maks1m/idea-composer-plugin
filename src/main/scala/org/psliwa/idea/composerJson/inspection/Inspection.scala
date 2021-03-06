package org.psliwa.idea.composerJson.inspection

import com.intellij.codeInspection.{ProblemsHolder, ProblemDescriptor, InspectionManager, LocalInspectionTool}
import com.intellij.json.psi.JsonFile
import com.intellij.psi.{PsiElement, PsiFile}
import org.psliwa.idea.composerJson._
import org.psliwa.idea.composerJson.json.Schema

private[inspection] abstract class Inspection extends LocalInspectionTool {
  val schema = ComposerSchema

  override final def checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array[ProblemDescriptor] = {
    if(file.getName != ComposerJson) Array()
    else doCheckFile(file, manager, isOnTheFly)
  }

  private def doCheckFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array[ProblemDescriptor] = {
    val problems = new ProblemsHolder(manager, file, isOnTheFly)

    for {
      file <- ensureJsonFile(file)
      schema <- schema
      topLevelValue <- Option(file.getTopLevelValue)
    } yield collectProblems(topLevelValue, schema, problems)

    problems.getResultsArray
  }

  private def ensureJsonFile(file: PsiFile) = file match {
    case x: JsonFile => Some(x)
    case _ => None
  }

  protected def collectProblems(element: PsiElement, schema: Schema, problems: ProblemsHolder): Unit
}
