package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Environment, CsrfCheck}
import scala.concurrent.duration._

object Homepage {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val Homepage =

    exec(flushHttpCache)
    .exec(flushCookieJar)

      /*======================================================================================
    * Click on 'Notice of Change'
    ======================================================================================*/

      .group("ET_010_Home") {
        exec(http("ET_010_005_Home")
          .get(BaseURL)
          .headers(CommonHeader)
          .header("sec-fetch-site", "none")
          .check(substring("Make a claim to an employment tribunal")))
      }
      .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


}