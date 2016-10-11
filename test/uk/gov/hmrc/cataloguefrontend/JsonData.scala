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

/**
  * Created by armin.
  */
object JsonData {

  val serviceDetailsData =
    """
      |    {
      |	     "name": "serv",
      |      "repoType": "Deployable",
      |      "teamNames": ["teamA", "teamB"],
      |	     "githubUrls": [{
      |		     "name": "github",
      |        "displayName": "github.com",
      |		     "url": "https://github.com/hmrc/serv"
      |	     }],
      |	     "ci": [
      |		     {
      |		       "name": "open1",
      |		       "displayName": "open 1",
      |		       "url": "http://open1/serv"
      |		     },
      |		     {
      |		       "name": "open2",
      |		       "displayName": "open 2",
      |		       "url": "http://open2/serv"
      |		     }
      |	     ],
      |      "environments" : [{
      |        "name" : "env1",
      |        "services" : [{
      |          "name": "ser1",
      |		       "displayName": "service1",
      |          "url": "http://ser1/serv"
      |        }, {
      |          "name": "ser2",
      |		       "displayName": "service2",
      |          "url": "http://ser2/serv"
      |        }]
      |      },{
      |        "name" : "env2",
      |        "services" : [{
      |          "name": "ser1",
      |		       "displayName": "service1",
      |          "url": "http://ser1/serv"
      |        }, {
      |          "name": "ser2",
      |		       "displayName": "service2",
      |          "url": "http://ser2/serv"
      |        }]
      |       }]
      |     }
    """.stripMargin

  val libraryDetailsData =
    """
      |    {
      |	     "name": "serv",
      |      "repoType": "Library",
      |      "teamNames": ["teamA", "teamB"],
      |	     "githubUrls": [{
      |		     "name": "github",
      |        "displayName": "github.com",
      |		     "url": "https://github.com/hmrc/serv"
      |	     }],
      |	     "ci": [
      |		     {
      |		       "name": "open1",
      |		       "displayName": "open 1",
      |		       "url": "http://open1/serv"
      |		     },
      |		     {
      |		       "name": "open2",
      |		       "displayName": "open 2",
      |		       "url": "http://open2/serv"
      |		     }
      |	     ]
      |     }
    """.stripMargin

  val deploymentThroughputData =
    """[
      |    {
      |        "period": "2015-11",
      |        "from": "2015-12-01",
      |        "to": "2016-02-29",
      |        "throughput": {
      |            "leadTime": {
      |                "median": 6
      |            },
      |            "interval": {
      |                "median": 1
      |            }
      |        },
      |        "stability": {
      |            "hotfixRate": 23,
      |            "hotfixLeadTime": {
      |                "median": 6
      |            }
      |        }
      |    },
      |    {
      |        "period": "2015-12",
      |        "from": "2015-12-01",
      |        "to": "2016-02-29",
      |        "throughput": {
      |            "leadTime": {
      |                "median": 6
      |            },
      |            "interval": {
      |                "median": 5
      |            }
      |        },
      |        "stability": {
      |            "hotfixRate": 23,
      |            "hotfixLeadTime": {
      |                "median": 6
      |            }
      |        }
      |    },
      |    {
      |        "period": "2016-01",
      |        "from": "2015-12-01",
      |        "to": "2016-02-29",
      |        "throughput": {
      |            "leadTime": {
      |                "median": 6
      |            },
      |            "interval": {
      |                "median": 6
      |            }
      |        }
      |    }
      |]
      |""".stripMargin

}