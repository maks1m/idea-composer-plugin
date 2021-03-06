package org.psliwa.idea.composerJson.reference

import com.intellij.json.psi.JsonProperty
import com.intellij.openapi.util.TextRange
import com.intellij.psi.impl.source.resolve.reference.impl.providers.{FileReference, FileReferenceSet}
import com.intellij.psi.{ElementManipulators, PsiReference, PsiElement, PsiReferenceProvider}
import com.intellij.util.ProcessingContext

private[reference] object PackageReferenceProvider extends PsiReferenceProvider {
  private val EmptyReferences: Array[PsiReference] = Array()

  override def getReferencesByElement(element: PsiElement, context: ProcessingContext): Array[PsiReference] = {
    val maybeReferences = for {
      property <- ensureJsonProperty(element)
      references <- propertyToReferences(property)
    } yield references

    maybeReferences.getOrElse(EmptyReferences)
  }

  private def propertyToReferences(property: JsonProperty): Option[Array[PsiReference]] = {
    val nameElement = property.getNameElement
    val range = ElementManipulators.getValueTextRange(nameElement)
    val text = range.substring(nameElement.getText)

    val set = new FileReferenceSet("vendor/"+text, nameElement, range.getStartOffset, this, true)

    packageName(text)
      .map{ case(vendor, pkg) => {
        Array[PsiReference](
          new FileReference(set, new TextRange(1, vendor.length + 1), 0, "vendor/"+vendor),
          new FileReference(set, new TextRange(1, vendor.length+pkg.length+2), 0, "vendor/"+vendor+"/"+pkg)
        )
      }}
  }

  private def packageName(s: String): Option[(String, String)] = {
    s.split("/") match {
      case Array(vendor, pkg) => Some((vendor, pkg))
      case _ => None
    }
  }

  private def ensureJsonProperty(element: PsiElement): Option[JsonProperty] = element match {
    case x: JsonProperty => Some(x)
    case _ => None
  }
}