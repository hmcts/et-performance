package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment}

import scala.concurrent.duration._


object ET_CaseLink {

  val IdamURL = Environment.idamURL
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val manageCaseLink =

  exec(_.set("currentDateTime" -> Common.getCurrentDateTime()))

    /*======================================================================================
    * ET -Case Link  - Initiate Case Link
    ======================================================================================*/
  
    .group("ET_CaseLink_030_InitiateCaseLink") {
      exec(http("ET_CaseLink_030_005_InitiateLink")
        .get("/workallocation/case/tasks/#{caseId}/event/createCaseLink/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(substring("tasks")))
      
      .exec(http("ET_CaseLink_030_010_InitiateLink")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("#{caseId}")))
    
      .exec(http("ET_CaseLink_030_015_InitiateCaselink")
        .get("/data/internal/cases/#{caseId}/event-triggers/createCaseLink?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("createCaseLink"))
        .check(jsonPath("$.event_token").optional.saveAs("event_token_link")))

      .exec(http("ET_CaseLink_030_020_InitiateCaselink")
        .get("/workallocation/case/tasks/#{caseId}/event/createCaseLink/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(substring("tasks")))
    
      .exec(http("ET_Respond_030_025_ReasonCode")
        .get("/refdata/commondata/lov/categories/CaseLinkingReasonCode")
        .headers(CommonHeader)
        .check(substring("CaseLinkingReasonCode")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
  
    /*======================================================================================
    *Enter the case number for linking
    ======================================================================================*/

    .group("ET_CaseLink_040_caseLinking") {
      exec(http("ET_CaseLink_040_005_caseLinking")
        .get("/data/internal/cases/#{linkcaseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("accept", "application/json, text/plain, */*")
        .check(substring("CaseOverview")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    *submit case linking
    ======================================================================================*/
  
    .group("ET_CaseLink_050_SubmitLinking") {
      exec(http("ET_CaseLink_050_005_SubmitLinking")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=createCaseLinkcreateCaseLink")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/caselink/SubmitCaseLink.json"))
        .check(substring("#{linkcaseId}")))
      
      .exec(http("ET_CaseLink_050_010_SubmitLinking")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/caselink/CreateLinkEventSubmit.json"))
        .check(substring("#{caseId}")))
    
      .exec(http("ET_CaseLink_050_015_SumitLinking")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .header("accept", "application/json, text/plain, */*")
        .check(substring("CaseOverview")))
    }
    .pause(MinThinkTime, MaxThinkTime)
    .pause(25) // required delay as per original script
  
    /*======================================================================================
    * ET -Manage Case Link  -Below scenarios are for unlink using manage links option
    ======================================================================================*/
    /*======================================================================================
    * ET -Manage Case Link  - select manage Case Links from the Next Step on case detail page
    ======================================================================================*/
  
    .group("ET_CaseLink_060_InitiateManageCaseLink") {
      exec(http("ET_CaseLink_060_005_InitiateManageCaseLink")
        .get("/workallocation/case/tasks/#{caseId}/event/maintainCaseLink/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(substring("tasks")))
        
      .exec(http("ET_CaseLink_060_010_InitiateManageCaseLink")
        .get("/data/internal/cases/#{caseId}/event-triggers/maintainCaseLink?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("maintainCaseLink"))
        .check(jsonPath("$.event_token").optional.saveAs("event_token_link_manage")))

      .exec(http("ET_CaseLink_060_015_InitiateManageCaseLink")
        .get("/workallocation/case/tasks/#{caseId}/event/maintainCaseLink/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(substring("tasks")))

      .exec(http("ET_CaseLink_060_020_InitiateManageCaseLink")
        .get("/data/internal/profile")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
        .check(substring("EMPLOYMENT")))

      .exec(http("ET_CaseLink_060_025_InitiateManageCaseLink")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("#{caseId}")))
    
      .exec(http("ET_CaseLink_060_030_InitiateManageCaseLink")
        .get("/refdata/commondata/lov/categories/CaseLinkingReasonCode")
        .headers(CommonHeader)
        .check(substring("CaseLinkingReasonCode")))

      .exec(http("ET_CaseLink_060_035_InitiateManageCaseLink")
        .post("/data/internal/searchCases?ctid=ET_EnglandWales&use_case=WORKBASKET")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .body(ElFileBody("bodies/caselink/ManageLinkWorkBasket.json"))
        .check(substring("Case Number")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
  
    /*======================================================================================
    * ET -Case link Management - select next to list the cases for unlink
    ======================================================================================*/

    .group("ET_CaseLink_070_ManageCaselinkNext") {
      exec(http("ET_CaseLink_070_005_ManageCaselinkNext")
        .get("/data/internal/cases/#{linkcaseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("Case Details")))
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
  
    /*======================================================================================
    * Manage Case Link - Review your answers after selecting a case to unlink
    ======================================================================================*/
  
    .group("ET_CaseLink_080_ReviewlinkDetails") {
      exec(http("ET_CaseLink_080_005_ReviewlinkDetails")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=maintainCaseLinkmaintainCaseLink")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/caselink/ReviewManageCaseLink.json"))
        .check(substring("maintainCaseLinkmaintainCaseLink")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Manage Case Link - Submit  your answers after selecting a case to unlink
    ======================================================================================*/
  
    .group("ET_CaseLink_090_SubmitUnlinkCases") {
      exec(http("ET_CaseLink_090_005_SubmitUnlinkCases")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/caselink/CreateUnlinkSubmit.json"))
        .check(substring("case_type")))
        
      .exec(http("ET_CaseLink_090_010_SubmitUnlinkCases")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("#{caseId}")))
    }
    .pause(MinThinkTime, MaxThinkTime)
    .pause(50) // required delay as per original script
}
