package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment}

import java.io.{BufferedWriter, FileWriter}
import scala.concurrent.duration._


object ET_CaseCreation {

  
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val MakeAClaim =

  exec(_.setAll(
      "ETCWRandomString" -> (Common.randomString(7))))
  
    /*======================================================================================
    Create Civil Claim - Create Case
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase") {
      exec(http("ET_CW_040_StartCreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(substring("Employment"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
     * Create Civil Claim - Initiate Case
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase") {
      exec(http("ET_CW_040_StartCreateCase1")
        .get("/data/internal/case-types/ET_EnglandWales/event-triggers/initiateCase?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(substring("initiateCase"))
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      * Create Civil Claim - Initiate case 1
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase1") {
      exec(http("ET_CW_040__StartCreateCase1")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1.json"))
        .check(substring("initiateCase1"))
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     * Create Civil Claim - Initiate case 2 - claimant details
    ========================================================================================*/

    .group("ET_CW_040__StartCreateCase2") {
      exec(http("ET_CW_040_StartCreateCase2")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase2")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase2.json"))
        .check(substring("initiateCase2"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     * Create Civil Claim - Initiate case 3 - Respondent
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase3") {
      exec(http("ET_CW_040_StartCreateCase3")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase3")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase3.json"))
        .check(substring("Respondent"))
        .check(status.is(200))
        .check(jsonPath("$..id").optional.saveAs("event_id")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     * Create Civil Claim - Initiate case 4 - Is address same for both
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase4") {
      exec(http("ET_CW_040_StartCreateCase4")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase4")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase4.json"))
        .check(substring("claimantWorkAddressQuestion"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      * Create Civil Claim - Initiate case 7 -claimant occupation details
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase4") {
      exec(http("ET_CW_040_StartCreateCase7")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase7")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase7.json"))
        .check(substring("claimant_occupation"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      * Create Civil Claim - Initiate case 8 -Is claimant represented by any one
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase8") {
      exec(http("ET_CW_040_StartCreateCase8")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase8")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase8.json"))
        .check(substring("claimantRepresentedQuestion"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     * Create Civil Claim - Initiate case 9 -Is claimant represented by any one
    ==========================================================================================*/

    .group("ET_CW_040_StartCreateCase9") {
      exec(http("ET_CW_040_StartCreateCase9")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=initiateCase9")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/InitiateCase9.json"))
        .check(substring("hearing_assistance"))
        .check(status.is(200)))

      .exec(http("Civil_CreateClaim_005_Submit")
        .post("/data/case-types/ET_EnglandWales/cases?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/caseSubmit.json"))
        .check(substring("CALLBACK_COMPLETED"))
        .check(status.is(201))
        .check(jsonPath("$.id").optional.saveAs("caseId")))
  
      .exec(http("Civil_CreateClaim_010_Submit")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .check(substring("ET_EnglandWales"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    .exec { session =>
      val fw = new BufferedWriter(new FileWriter("ETcasesold.csv", true))
      try {
        fw.write(session("caseId").as[String] + "\r\n")
      } finally fw.close()
      session
    }
}
