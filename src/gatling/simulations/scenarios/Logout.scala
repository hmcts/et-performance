package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment
import utils.Headers.commonHeader

object Logout {

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  /*====================================================================================
  *Manage Case Logout
  *=====================================================================================*/

  val XUILogout =

    group("XUI_999_Logout") {
      exec(http("XUI_999_005_Logout")
        .get("/auth/logout")
        .headers(commonHeader)
        )
    }

    .pause(MinThinkTime , MaxThinkTime)

}