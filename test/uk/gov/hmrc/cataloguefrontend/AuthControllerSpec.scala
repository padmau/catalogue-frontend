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

import org.jsoup.Jsoup
import org.mockito.Matchers.{any, eq => is}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.mvc.{Cookie, Cookies, Session}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.cataloguefrontend.UserManagementConnector.DisplayName
import uk.gov.hmrc.cataloguefrontend.connector.UserManagementAuthConnector.{UmpToken, UmpUnauthorized}
import uk.gov.hmrc.cataloguefrontend.service.AuthService
import uk.gov.hmrc.cataloguefrontend.service.AuthService.TokenAndDisplayName

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthControllerSpec extends WordSpec with Matchers with OneAppPerSuite with MockitoSugar {

  "Authenticating" should {

    "redirect to landing page if successful and UMP auth in session" in new Setup {
      val username            = "n/a"
      val password            = "n/a"
      val request             = FakeRequest().withFormUrlEncodedBody("username" -> username, "password" -> password)
      val expectedToken       = UmpToken("ump-token")
      val expectedDisplayName = DisplayName("John Smith")

      when(authService.authenticate(is(username), is(password))(any()))
        .thenReturn(Future(Right(TokenAndDisplayName(expectedToken, expectedDisplayName))))

      val result = controller.submit(request)

      redirectLocation(result).get             shouldBe routes.CatalogueController.landingPage().url
      session(result).apply("ump.token")       shouldBe expectedToken.value
      session(result).apply("ump.displayName") shouldBe expectedDisplayName.value
    }

    "show 400 BAD_REQUEST and error message when auth service does not recognize user credentials" in new Setup {
      val username = "n/a"
      val password = "n/a"
      val request  = FakeRequest().withFormUrlEncodedBody("username" -> username, "password" -> password)

      when(authService.authenticate(is(username), is(password))(any())).thenReturn(Future(Left(UmpUnauthorized)))

      val result = controller.submit(request)

      status(result)          shouldBe 400
      contentAsString(result) should include(messagesApi("sign-in.wrong-credentials"))
    }

    "show 400 BAD_REQUEST and error message when no username or password are provided" in new Setup {
      val request = FakeRequest().withFormUrlEncodedBody("username" -> "", "password" -> "")

      val result = controller.submit(request)

      val foo = Some(Some(Some(1)))

      status(result)          shouldBe 400
      contentAsString(result) should include(messagesApi("sign-in.wrong-credentials"))
    }

  }

  "Showing sign-in page" should {
    "provide a link to help people who forgotten their password" in new Setup {
      val result                = controller.showSignInPage(FakeRequest())
      val signInPage            = Jsoup.parse(contentAsString(result))
      val forgottenPasswordLink = signInPage.select("#forgotten-password")

      forgottenPasswordLink.attr("href") shouldBe selfServiceUrl
      forgottenPasswordLink.text         shouldBe selfServiceUrl
    }
  }

  "Signing out" should {
    "redirect to landing page and clear session" in new Setup {
      val requestWithUmpData = FakeRequest()

      val result = controller.signOut(requestWithUmpData)

      val setCookie = Cookies.decodeSetCookieHeader(headers(result).get(SET_COOKIE).get).head

      setCookie.name               shouldBe Session.COOKIE_NAME
      setCookie.maxAge.get         should be < 0
      redirectLocation(result).get shouldBe routes.AuthController.showSignInPage().url
    }
  }

  private trait Setup {
    val messagesApi    = app.injector.instanceOf[MessagesApi]
    val authService    = mock[AuthService]
    val selfServiceUrl = "self-service-url"
    val config         = Configuration("self-service-url" -> selfServiceUrl)
    val controller     = new AuthController(messagesApi, authService, config)
  }

}
