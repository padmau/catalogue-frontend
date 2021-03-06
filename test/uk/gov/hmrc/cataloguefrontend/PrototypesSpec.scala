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

package uk.gov.hmrc.cataloguefrontend

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import play.api.test.FakeRequest
import uk.gov.hmrc.cataloguefrontend.actions.{UmpAuthenticated, VerifySignInStatus}
import uk.gov.hmrc.cataloguefrontend.connector.{IndicatorsConnector, ServiceDependenciesConnector}
import uk.gov.hmrc.cataloguefrontend.events.{EventService, ReadModelService}
import uk.gov.hmrc.cataloguefrontend.service.{DeploymentsService, LeakDetectionService}
import uk.gov.hmrc.play.test.UnitSpec

class PrototypesSpec extends UnitSpec with ScalaFutures with MockitoSugar {
  "/prototypes" should {
    "redirect to the repositories page with a filter showing only prototypes" in {

      val result: Result =
        new CatalogueController(
          mock[UserManagementConnector],
          mock[TeamsAndRepositoriesConnector],
          mock[ServiceDependenciesConnector],
          mock[IndicatorsConnector],
          mock[LeakDetectionService],
          mock[DeploymentsService],
          mock[EventService],
          mock[ReadModelService],
          mock[play.api.Environment],
          mock[VerifySignInStatus],
          mock[UmpAuthenticated],
          Configuration(),
          mock[MessagesApi]
        ).allPrototypes(FakeRequest()).futureValue

      result.header.status              shouldBe 303
      result.header.headers("Location") shouldBe "/repositories?name=&type=Prototype"
    }
  }
}
