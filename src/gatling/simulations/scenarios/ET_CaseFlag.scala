package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment}

import java.io.{BufferedWriter, FileWriter}
import scala.concurrent.duration._


object ET_CaseFlag {

  
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val manageCaseFlag =

  exec(_.setAll(
      "ETRespondRandomString" -> (Common.randomString(7)),
    "currentDateTime" -> Common.getCurrentDateTime()
  ))

    /*======================================================================================
    * ET -Case Flag Management - select Create Case Flag from the Next Step on case detail page
    ======================================================================================*/
    .group("ET_CaseFlag_030_InitiateCaseFlag") {
      exec(http("ET_CaseFlag_030_005_InitiateCaseFlag")
        .get("/workallocation/case/tasks/#{caseId}/event/createFlag/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .check(substring("tasks")))
      
        .exec(http("ET_CaseFlag_030_010_InitiateCaseFlag")
          .get("/data/internal/profile")
          .headers(CommonHeader)
          .check(substring("EMPLOYMENT")))
      
        .exec(http("ET_CaseFlag_030_015_InitiateCaseFlag")
          .get("/data/internal/cases/#{caseId}/event-triggers/createFlag?ignore-warning=false")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(substring("Create a case flag"))
          .check(jsonPath("$.event_token").optional.saveAs("event_token"))
          .check(jsonPath("$.case_fields[1].value.partyName").saveAs("respondentName"))
          .check(jsonPath("$.case_fields[0].value.partyName").saveAs("claimantName"))
      
        )
        .exec(http("ET_CaseFlag_030_020_InitiateCaseFlag")
          .get("/workallocation/case/tasks/#{caseId}/event/createFlag/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
          .headers(CommonHeader)
          .check(substring("tasks")))
    
    }
  
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
  
    /*======================================================================================
    *Where should this flag be added? - Respondent Level
    ======================================================================================*/
  
    .group("ET_CaseFlag_040_WhereFlagAdded") {
      exec(http("ET_CaseFlag_040_005_WhereFlagAdded")
        .get("/refdata/commondata/caseflags/service-id=BHA1?flag-type=PARTY")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("Party"))
        .check(substring("FlagDetails")))
    
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Add FlagType-Main --- add comments
    ======================================================================================*/
  
    .group("ET_CaseFlag_050_AddComments") {
      exec(http("ET_CaseFlag_050_005_AddComments")
        .get("/api/user/details?refreshRoleAssignments=undefined")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .check(substring("canShareCases")))
    
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
    /*======================================================================================
    * Submit flag details
    ======================================================================================*/
  
    .group("ET_CaseFlag_060_SubmitFlagCreation") {
      exec(http("ET_CaseFlag_060_005_SubmitFlagCreation")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/caseflag/CreateFlagSubmit.json"))
        .check(substring("case_type")))
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * View Flag - by click on Flag
    ======================================================================================*/
  
    .exec(http("ET_CaseFlag_070_005_ReviewFlagDetails")
      .get("/api/user/details?refreshRoleAssignments=undefined")
      .headers(CommonHeader)
      .check(jsonPath("$..token").optional.saveAs("bearertoken"))
       .check(substring("canShareCases"))
    )
  
    .group("ET_CaseFlag_070_ViewFlagDetails ") {
      exec(http("ET_CaseFlag_070_010_ReviewFlagDetails")
        .options("https://gateway-ccd.perftest.platform.hmcts.net/activity/cases/#{caseId}/activity")
        .headers(CommonHeader)
      )
      
        .exec(http("ET_CaseFlag_070_015_ReviewFlagDetails")
          .post("https://gateway-ccd.perftest.platform.hmcts.net/activity/cases/#{caseId}/activity")
          .headers(CommonHeader)
          .header("Authorization", "#{bearertoken}")
          .body(ElFileBody("bodies/caseflag/Viewcaseflag.json"))
          .check(substring("view")))
      
       
    
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
  
    /*======================================================================================
    * ET -Case Flag Management - select Manage Case Flag from the Next Step on case detail page
    ======================================================================================*/
  
  
    .group("ET_CaseFlag_080_InitiateManageCaseFlag") {
      exec(http("ET_CaseFlag_080_005_InitiateManageCaseFlag")
        .get("/workallocation/case/tasks/#{caseId}/event/manageFlags/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .check(substring("tasks")))
        
      
        .exec(http("ET_CaseFlag_080_010_InitiateManageCaseFlag")
          .get("/data/internal/cases/#{caseId}/event-triggers/manageFlags?ignore-warning=false")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(substring("Manage case flags"))
          .check(jsonPath("$.event_token").optional.saveAs("event_token_manageflag"))
          //.check(jsonPath("$.case_fields[1].value.partyName").saveAs("respondentName"))
          .check(jsonPath("$.case_fields[0].value.partyName").saveAs("claimantName"))
          .check(jsonPath("$..formatted_value.details[0].value.dateTimeCreated").saveAs("dateTimeCreated"))
         // .check(jsonPath("$..formatted_value.details[0].value.dateTimeModified").saveAs("dateTimeModified"))
          
        )
      
        .exec(http("ET_CaseFlag_080_015_InitiateManageCaseFlag")
          .get("/workallocation/case/tasks/#{caseId}/event/manageFlags/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
          .headers(CommonHeader)
          .check(substring("tasks")))
    
    }
  
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)
  
    /*======================================================================================
    * Update Case Flag Comment - Submit flag Comments
    ======================================================================================*/
  
    .group("ET_CaseFlag_090_UpdateFlagComments") {
      exec(http("XUI_PRL_070_005_UpdateFlagComments")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/caseflag/UpdateFlagSubmit.json"))
        .check(substring("case_type")))
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
}
