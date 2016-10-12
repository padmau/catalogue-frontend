/*
 * Copyright 2016 HM Revenue & Customs
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

/*
 * Copyright 2016 HM Revenue & Customs
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

import play.api.Logger
import play.api.libs.json.Json
import uk.gov.hmrc.cataloguefrontend.config.WSHttp
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext.fromLoggingDetails
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpPost, HttpResponse}

import scala.concurrent.Future

case class Release(
                    name: String,
                    productionDate: java.time.LocalDateTime,
                    creationDate: Option[java.time.LocalDateTime] = None,
                    interval: Option[Long] = None,
                    leadTime: Option[Long] = None,
                    version: String)

trait ServiceReleasesConnector extends ServicesConfig {
  val http: HttpGet with HttpPost
  def servicesReleasesBaseUrl: String

  import uk.gov.hmrc.play.http.HttpReads._
  import JavaDateTimeJsonFormatter._

  implicit val releasesFormat = Json.reads[Release]

  def getReleases(serviceNames: Iterable[String])(implicit hc: HeaderCarrier) = {
    val url =  s"$servicesReleasesBaseUrl"

    http.POST[Seq[String],HttpResponse](url, serviceNames.toSeq).map { r =>
      r.status match {
        case 200 => r.json.as[Seq[Release]]
        case 404 => Seq()
      }
    }.recover {
      case ex =>
        Logger.error(s"An error occurred when connecting to $servicesReleasesBaseUrl: ${ex.getMessage}", ex)
        Seq.empty
    }
  }

  def getReleases(serviceName: Option[String] = None)(implicit hc: HeaderCarrier): Future[Seq[Release]] = {
    val url = serviceName.fold(servicesReleasesBaseUrl)(name => s"$servicesReleasesBaseUrl/$name")

    http.GET[HttpResponse](url).map { r =>
      r.status match {
        case 200 => r.json.as[Seq[Release]]
        case 404 => Seq()
      }
    }.recover {
      case ex =>
        Logger.error(s"An error occurred when connecting to $servicesReleasesBaseUrl: ${ex.getMessage}", ex)
        Seq.empty
    }
  }

}

object ServiceReleasesConnector extends ServiceReleasesConnector {
  override val http = WSHttp
  override def servicesReleasesBaseUrl: String = baseUrl("service-releases") + "/api/releases"
}
