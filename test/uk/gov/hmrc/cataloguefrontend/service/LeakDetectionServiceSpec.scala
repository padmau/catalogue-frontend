/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.cataloguefrontend.service

import org.scalatest.prop.PropertyChecks
import org.scalatest.{Matchers, WordSpec}
import play.api.Configuration
import uk.gov.hmrc.cataloguefrontend.Team
import uk.gov.hmrc.cataloguefrontend.connector.RepositoryWithLeaks

class LeakDetectionServiceSpec extends WordSpec with Matchers with PropertyChecks {

  "Service" should {

    "determine if at least one of team's repos has leaks" in {
      val team = Team(
        name                     = "team0",
        firstActiveDate          = None,
        lastActiveDate           = None,
        firstServiceCreationDate = None,
        repos                    = Some(Map("library" -> List("repo1", "repo2")))
      )

      val repoWithLeak = RepositoryWithLeaks("repo1")

      val service = new LeakDetectionService(null, configuration)
      service.teamHasLeaks(team, Seq(repoWithLeak)) shouldBe true
    }

    "determine if a team has no leaks" in {
      val team = Team(
        name                     = "team0",
        firstActiveDate          = None,
        lastActiveDate           = None,
        firstServiceCreationDate = None,
        repos                    = Some(Map("library" -> List("repo1", "repo2")))
      )

      val repoWithLeak = RepositoryWithLeaks("repo3")

      val service = new LeakDetectionService(null, configuration)
      service.teamHasLeaks(team, Seq(repoWithLeak)) shouldBe false
    }

    "filter repositories in the exclusion list" in {
      val team = Team(
        name                     = "team0",
        firstActiveDate          = None,
        lastActiveDate           = None,
        firstServiceCreationDate = None,
        repos                    = Some(Map("library" -> List("a-repo-to-ignore")))
      )

      val repoToIgnore = RepositoryWithLeaks("a-repo-to-ignore")

      val service = new LeakDetectionService(null, configuration)
      service.teamHasLeaks(team, Seq(repoToIgnore)) shouldBe false
    }
  }

  val configuration =
    Configuration(
      "lds.publicUrl"          -> "",
      "lds.integrationEnabled" -> "true",
      "lds.noWarningsOn.0"     -> "a-repo-to-ignore"
    )
}
