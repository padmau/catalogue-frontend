/*
 * Copyright 2017 HM Revenue & Customs
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

package view

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.OneAppPerTest
import play.twirl.api.Html
import uk.gov.hmrc.cataloguefrontend.connector.model._

class DependenciesSpec extends WordSpec with Matchers with OneAppPerTest {


  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  "library and sbt plugin dependencies list" should {

    val dependencies = Dependencies("service", Seq(
      LibraryDependencyState("lib1-up-to-date", Version(1, 0, 0), Some(Version(1, 0, 0))),
      LibraryDependencyState("lib2-minor-behind", Version(2, 0, 0), Some(Version(2, 1, 0))),
      LibraryDependencyState("lib3-major-behind", Version(3, 0, 0), Some(Version(4, 0, 0))),
      LibraryDependencyState("lib4-patch-behind", Version(3, 0, 0), Some(Version(3, 0, 1))),
      LibraryDependencyState("lib5-no-latest-version", Version(3, 0, 0), None),
      LibraryDependencyState("lib6-invalid-ahead-current", Version(4, 0, 0), Some(Version(3, 0, 1)))

    ), Seq(
      SbtPluginsDependenciesState("plugin1-up-to-date", Version(1, 0, 0), Some(Version(1, 0, 0))),
      SbtPluginsDependenciesState("plugin2-minor-behind", Version(2, 0, 0), Some(Version(2, 1, 0))),
      SbtPluginsDependenciesState("plugin3-major-behind", Version(3, 0, 0), Some(Version(4, 0, 0))),
      SbtPluginsDependenciesState("plugin4-patch-behind", Version(3, 0, 0), Some(Version(3, 0, 1))),
      SbtPluginsDependenciesState("plugin5-no-latest-version", Version(3, 0, 0), None),
      SbtPluginsDependenciesState("plugin6-invalid-ahead-current", Version(4, 0, 0), Some(Version(3, 0, 1)))
    ),
      Seq(OtherDependenciesState("sbt", Version(0,13,11), Some(Version(0,13,15))))
    )


    "show green if versions are the same" in {
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#lib1-up-to-date").get(0).text() shouldBe "lib1-up-to-date 1.0.0 1.0.0"
      verifyColour(document, "#lib1-up-to-date", "green")

      document.select("#plugin1-up-to-date").get(0).text() shouldBe "plugin1-up-to-date 1.0.0 1.0.0"
      verifyColour(document, "#plugin1-up-to-date", "green")
    }

    "show amber if there is a minor version discrepancy" in {
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#lib2-minor-behind").get(0).text() shouldBe "lib2-minor-behind 2.0.0 2.1.0"
      verifyColour(document, "#lib2-minor-behind", "amber")

      document.select("#plugin2-minor-behind").get(0).text() shouldBe "plugin2-minor-behind 2.0.0 2.1.0"
      verifyColour(document, "#plugin2-minor-behind", "amber")
    }

    "show amber if there is a patch version discrepancy" in {
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#lib4-patch-behind").get(0).text() shouldBe "lib4-patch-behind 3.0.0 3.0.1"
      verifyColour(document, "#lib4-patch-behind", "amber")

      document.select("#plugin4-patch-behind").get(0).text() shouldBe "plugin4-patch-behind 3.0.0 3.0.1"
      verifyColour(document, "#plugin4-patch-behind", "amber")
    }

    "show red if there is a major version discrepancy" in {
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#lib3-major-behind").get(0).text() shouldBe "lib3-major-behind 3.0.0 4.0.0"
      verifyColour(document, "#lib3-major-behind", "red")

      document.select("#plugin3-major-behind").get(0).text() shouldBe "plugin3-major-behind 3.0.0 4.0.0"
      verifyColour(document, "#plugin3-major-behind", "red")
    }

    "show grey and (not found) if there is no latest version available" in {
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#lib5-no-latest-version").get(0).text() shouldBe "lib5-no-latest-version 3.0.0 (not found)"
      verifyColour(document, "#lib5-no-latest-version", "grey")

      document.select("#plugin5-no-latest-version").get(0).text() shouldBe "plugin5-no-latest-version 3.0.0 (not found)"
      verifyColour(document, "#plugin5-no-latest-version", "grey")
    }

    "show black if versions are invalid (eg: current version > latest version) - (this scenario should not happen unless the reloading of the libraries' latest versions has been failing)" in {
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#lib6-invalid-ahead-current").get(0).text() shouldBe "lib6-invalid-ahead-current 4.0.0 3.0.1"
      verifyColour(document, "#lib6-invalid-ahead-current", "black")

      document.select("#plugin6-invalid-ahead-current").get(0).text() shouldBe "plugin6-invalid-ahead-current 4.0.0 3.0.1"
      verifyColour(document, "#plugin6-invalid-ahead-current", "black")
    }

  }

  private def verifyColour(document: Document, elementsCssSelector: String, colour: String) = {
    import collection.JavaConverters._
    val elements = document.select(elementsCssSelector)
    assert(elements.hasClass(colour) == true, s"colour($colour) is not found in element's classes: [${elements.get(0).classNames().asScala.mkString(", ")}]")
  }

  "sbt dependency" should {

    "show green if versions are the same" in {
      val dependencies = Dependencies("service", Nil, Nil, Seq(OtherDependenciesState("sbt", Version(1,0,0), Some(Version(1,0,0)))))
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#sbt").get(0).text() shouldBe "sbt 1.0.0 1.0.0"
      verifyColour(document, "#sbt", "green")

    }

    "show amber if there is a minor version discrepancy" in {
      val dependencies = Dependencies("service", Nil, Nil, Seq(OtherDependenciesState("sbt", Version(1,1,0), Some(Version(1,0,0)))))
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#sbt").get(0).text() shouldBe "sbt 1.1.0 1.0.0"
      verifyColour(document, "#sbt", "amber")

    }

    "show amber if there is a patch version discrepancy" in {
      val dependencies = Dependencies("service", Nil, Nil, Seq(OtherDependenciesState("sbt", Version(1,0,0), Some(Version(1,0,1)))))
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#sbt").get(0).text() shouldBe "sbt 1.0.0 1.0.1"
      verifyColour(document, "#sbt", "amber")

    }

    "show red if there is a major version discrepancy" in {
      val dependencies = Dependencies("service", Nil, Nil, Seq(OtherDependenciesState("sbt", Version(1,0,0), Some(Version(2,0,0)))))
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#sbt").get(0).text() shouldBe "sbt 1.0.0 2.0.0"
      verifyColour(document, "#sbt", "red")
    }

    "show grey and (not found) if there is no latest version available" in {
      val dependencies = Dependencies("service", Nil, Nil, Seq(OtherDependenciesState("sbt", Version(1,0,0), None)))
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#sbt").get(0).text() shouldBe "sbt 1.0.0 (not found)"
      verifyColour(document, "#sbt", "grey")
    }

    "show black if versions are invalid (eg: current version > latest version)" in {
      val dependencies = Dependencies("service", Nil, Nil, Seq(OtherDependenciesState("sbt", Version(5,0,0), Some(Version(1,0,0)))))
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#sbt").get(0).text() shouldBe "sbt 5.0.0 1.0.0"
      verifyColour(document, "#sbt", "black")
    }
  }

  "legend section" should {
    "be shown if least one library dependency entry exists" in {
      val dependencies = Dependencies("service", Seq(LibraryDependencyState("lib1-up-to-date", Version(1, 0, 0), Some(Version(1, 0, 0)))),Nil, Nil)
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      verifyLegendSectionIsShowing(document)
    }

    "be shown if at least one sbt dependency entry exists" in {
      val dependencies = Dependencies("service", Nil ,Seq(SbtPluginsDependenciesState("plugin1-up-to-date", Version(1, 0, 0), Some(Version(1, 0, 0)))), Nil)
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      verifyLegendSectionIsShowing(document)
    }

    "be shown if at least one other dependency entry exists" in {
      val dependencies = Dependencies("service", Nil ,Nil , Seq(OtherDependenciesState("sbt", Version(1,0,0), None)))
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      verifyLegendSectionIsShowing(document)
    }
    
    "not be shown if no dependency entry exists" in {
      val dependencies = Dependencies("service", Nil ,Nil , Nil)
      val document = asDocument(views.html.partials.dependencies(Some(dependencies)))

      document.select("#legend") shouldBe empty
    }
  }

  private def verifyLegendSectionIsShowing(document: Document) = {
    document.select("#legend").get(0).text() shouldBe "Legend: Up to date Minor version behind Major version behind"
  }
}
