package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment, Headers}

import scala.concurrent.duration._


object ET_CaseFileView {

  
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Headers.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val CaseFileView =

  exec(_.setAll("currentDateTime" -> Common.getCurrentDateTime()))

    /*======================================================================================
    * ET -Case Link  - select Create Case Links from the Next Step on case detail page
    ======================================================================================*/
  
    .group("ET_CaseFileView_030_CaseFileView") {
      exec(http("ET_CaseLink_030_005_CaseFileView")
        .get("/categoriesAndDocuments/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(substring("categories")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
    .pause(20) // required delay as per original script
  
}
