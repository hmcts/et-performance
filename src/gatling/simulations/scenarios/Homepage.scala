package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment.commonHeader
import utils.{Common, CsrfCheck, Environment}

import scala.concurrent.duration._

object Homepage {

  val BaseURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  
  /*====================================================================================
  *Manage Case Homepage
  *=====================================================================================*/
  
  val XUIHomePage =
    
    exec(flushHttpCache)
      .exec(flushCookieJar)
      
      .group("XUI_010_Homepage") {
        exec(http("XUI_010_005_Homepage")
          .get("/")
          .headers(commonHeader)
          .header("sec-fetch-site", "none"))
          
          .exec(Common.configurationui)
          
          .exec(Common.configJson)
          
          .exec(Common.TsAndCs)
          
          .exec(Common.configUI)
          
          .exec(Common.userDetails)
          
          .exec(Common.isAuthenticated)
          
          .exec(http("XUI_010_010_AuthLogin")
            .get("/auth/login")
            .headers(commonHeader)
            .check(CsrfCheck.save)
            .check(regex("/oauth2/callback&amp;state=(.*)&amp;nonce=").saveAs("state"))
            .check(regex("&nonce=(.*)&response_type").saveAs("nonce")))
      }
      
      .pause(MinThinkTime, MaxThinkTime)


}