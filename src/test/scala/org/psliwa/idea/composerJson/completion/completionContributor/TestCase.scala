package org.psliwa.idea.composerJson.completion.completionContributor

import com.intellij._
import com.intellij.json.JsonLanguage
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.psliwa.idea.composerJson.completion.{BaseLookupElement, CompletionContributor}
import org.psliwa.idea.composerJson.ComposerJson
import org.junit.Assert._

abstract class TestCase extends LightPlatformCodeInsightFixtureTestCase {
  def getCompletionContributor = {
    getCompletionContributors.head
  }

  def getCompletionContributors = {
    import scala.collection.JavaConverters._

    codeInsight.completion.CompletionContributor.forLanguage(JsonLanguage.INSTANCE).asScala
      .filter(_.isInstanceOf[CompletionContributor])
      .map(_.asInstanceOf[CompletionContributor])
  }

  def setCompletionPackageLoader(f: () => Seq[BaseLookupElement]) = {
    getCompletionContributors.foreach(_.setPackagesLoader(f))
  }

  def setCompletionVersionsLoader(f: String => Seq[String]) = {
    getCompletionContributors.foreach(_.setVersionsLoader(f))
  }

  protected def suggestions(contents: String, expectedSuggestions: Array[String], unexpectedSuggestions: Array[String] = Array()) = {
    myFixture.configureByText(ComposerJson, contents)
    myFixture.completeBasic()

    val lookupElements = myFixture.getLookupElementStrings

    assertNotNull(lookupElements)
    assertContainsElements(lookupElements, expectedSuggestions:_*)
    assertDoesntContain(lookupElements, unexpectedSuggestions:_*)
  }

  protected def completion(contents: String, expected: String) = {
    myFixture.configureByText(ComposerJson, contents)
    myFixture.completeBasic()

    myFixture.checkResult(expected.replace("\r", ""))
  }
}
