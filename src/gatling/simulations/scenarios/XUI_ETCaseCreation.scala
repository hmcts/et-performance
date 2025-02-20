package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment}

import java.io.{BufferedWriter, FileWriter}


object XUI_ETCaseCreation {

  
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val InitiateAClaim =

  exec(_.setAll(
      "ETCWRandomString" -> (Common.randomString(7))))
  
    /*======================================================================================
     Create Civil Claim - Create Case
    ==========================================================================================*/

    .group("XUI_ET_040_StartCreateCase") {
      exec(http("XUI_ET_040_StartCreateCase")
        .get("/aggregated/caseworkers/:uid/jurisdictions?access=create")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(substring("Employment"))
        .check(status.in(200,201)))
    }
     .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    * Create Civil Claim - Initiate Case
    ==========================================================================================*/

    .group("XUI_ET_050_InitiateCase") {
      exec(http("XUI_ET_050_InitiateCase")
        .get("/data/internal/case-types/ET_EnglandWales/event-triggers/et1ReppedCreateCase?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-case-trigger.v2+json;charset=UTF-8")
        .check(substring("Create draft claim"))
        .check(status.in(200,201))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - Initiate case 1 - Enter Work Location
    ==========================================================================================*/

    .group("XUI_ET_060_EnterLocation") {
      exec(http("XUI_ET_060_005_EnterLocation")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1ReppedCreateCase1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1.json"))
        .check(substring("=et1ReppedCreateCase1"))
        .check(status.in(200,201)))

      .exec(http("XUI_ET_060_010_EnterLocation")
        .post("/data/case-types/ET_EnglandWales/cases?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-case.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/enterlocation.json"))
        .check(substring("ET_EnglandWales"))
        .check(jsonPath("$.id").optional.saveAs("caseId"))
        .check(status.in(201)))
  
      .exec(http("XUI_ET_060_015_EnterLocation")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("ET_EnglandWales"))
        .check(status.in(200,201)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET Section 1- click on Claimant Details
    ==========================================================================================*/

    val ETFormClaimantDetailsSection1=

    group("XUI_ET_070_ClickOnSectionOne") {
      exec(http("XUI_ET_070_005_ClickOnSectionOne")
        .get("/cases/case-details/#{caseId}/trigger/et1SectionOne/et1SectionOne1")
        .headers(CommonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .check(status.is(200)))

      .exec(http("XUI_ET_070_010_ClickOnSectionOne")
        .get("/data/internal/cases/#{caseId}/event-triggers/et1SectionOne?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("et1SectionOne"))
        .check(jsonPath("$.event_token").optional.saveAs("event_token_section1"))
        .check(status.is(200)))
  
        .exec(http("XUI_ET_070_015_ClickOnSectionOne")
          .get("/data/internal/profile")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
          .check(substring("internal/profile"))
          .check(status.is(200)))
  
        .exec(Common.configurationui)
  
        .exec(Common.configJson)
  
        .exec(Common.userDetails)
  
        .exec(Common.TsAndCs)
  
        .exec(Common.configUI)
  
        .exec(Common.isAuthenticated)
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section 1 - Continue
    ==========================================================================================*/

    .group("XUI_ET_080_SectionOneContinue") {
      exec(http("XUI_ET_080_SectionOneContinue")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionOne1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1Section1Continue.json"))
        .check(substring("et1SectionOne1"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
    
    /*======================================================================================
    Create Civil Claim - ET Section one 2 -- claimant details
    ==========================================================================================*/

    .group("XUI_ET_090_ClaimantDetails2") {
      exec(http("XUI_ET_090_ClaimantDetails2")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionOne2")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1section2.json"))
        .check(substring("et1SectionOne2"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
  
  /*======================================================================================
   Create Civil Claim - ET Section one 3 -- claimant details
  ==========================================================================================*/

    .group("XUI_ET_100_ClaimantDetails3") {
      exec(http("XUI_ET_100_ClaimantDetails3")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionOne3")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1section3.json"))
        .check(substring("et1SectionOne3"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section one 4 -- claimant post code
    ==========================================================================================*/

    .group("XUI_ET_110_ClaimantDetails4") {
      exec(http("XUI_ET_110_ClaimantDetails4")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionOne4")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1section4.json"))
        .check(substring("et1SectionOne4"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section one 5 -- hearing type
    ==========================================================================================*/

    .group("XUI_ET_120_ClaimantDetails5") {
      exec(http("XUI_ET_120_ClaimantDetails5")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionOne5")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1section5.json"))
        .check(substring("et1SectionOne5"))
        .check(status.is(200))
      )
        .pause(MinThinkTime, MaxThinkTime)
    }
  
    /*======================================================================================
    Create Civil Claim - ET Section one 6 -- Any support required
    ==========================================================================================*/

    .group("XUI_ET_130_ClaimantDetails6") {
      exec(http("XUI_ET_130_ClaimantDetails6")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionOne6")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1section6.json"))
        .check(substring("et1SectionOne6"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    Create Civil Claim - ET Section one 7 -- mode of correspondance
    ==========================================================================================*/

    .group("XUI_ET_140_ClaimantDetails7") {
      exec(http("XUI_ET_140_ClaimantDetails7")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionOne7")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1section7.json"))
        .check(substring("et1SectionOne7"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
    Create Civil Claim - ET Section one -- save as draft
    ==========================================================================================*/

    .group("XUI_ET_150_SectionOneSaveAsDraft") {
      exec(http("XUI_ET_150_005_SectionOneSaveAsDraft")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/section1CreateCase.json"))
        .check(substring("CALLBACK_COMPLETED"))
        .check(status.is(201)))
  
      .exec(http("XUI_ET_150_010_SectionOneSaveAsDraft")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("#{caseId}"))
        .check(status.in(200,201)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section One -- back to details
    ==========================================================================================*/

    .group("XUI_ET_160_SectionOneBacktoCaseDetails") {
      exec(http("XUI_ET_160_005_SectionOneBacktoCaseDetails")
        .post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/createcase/section2CaseDetails.json"))
        .check(status.in(200, 201, 204)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
  /*======================================================================================
  Create Civil Claim - Create Draft - dowload initiation after section1 
  ==========================================================================================*/

  val downloadETformAfterSection1=

    group("XUI_ET_170_DownloadETAfterSection1") {
      exec(http("XUI_ET_170_005_DownloadETAfterSection1")
        .get("/workallocation/case/tasks/#{caseId}/event/createDraftEt1/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(status.in(200, 201, 204))
        .check(substring("task_required_for_event")))
  
      .exec(http("XUI_ET_170_010_DownloadETAfterSection1")
        .get("/data/internal/cases/#{caseId}/event-triggers/createDraftEt1?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(status.in(200, 201, 204))
        .check(substring("Download draft ET1 Form")))
  
      .exec(http("XUI_ET_170_015_DownloadETAfterSection1")
        .get("/case/tasks/1715167319753506/event/createDraftEt1/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .header("accept", "application/json")
        .check(status.in(200, 201, 204))
        .check(substring("task_required_for_event")))
    }
    .pause(MinThinkTime, MaxThinkTime)
      
    /*======================================================================================
     Create Civil Claim -  -- download ET form after section1
      ==========================================================================================*/

      .group("XUI_ET_180_downlaodETFormAfterSection1") {
        exec(http("XUI_ET_180_005_downlaodETFormAfterSection1")
          .post("/data/case-types/ET_EnglandWales/validate?pageId=createDraftEt11")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/createcase/downloadetform.json"))
          .check(status.in(200, 201, 204))
          .check(substring("createDraftEt11")))
          
          .exec(http("XUI_ET_180_010_downlaodETFormAfterSection1")
            .post("/data/cases/#{caseId}/events")
            .headers(CommonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
            .body(ElFileBody("bodies/createcase/downloadetformevent.json"))
            .check(substring("CALLBACK_COMPLETED"))
            .check(status.in(200,201)))
          
          .exec(http("XUI_ET_180_015_SectionThreeSaveAsDraft")
            .get("/data/internal/cases/#{caseId}")
            .headers(CommonHeader)
            .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
            .check(substring("#{caseId}"))
            .check(status.in(200, 201)))
      }
      .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section two 1-- Employment and respondent details
    ==========================================================================================*/
      
      val ETFormEmploymentDetailsSection2=

      group("XUI_ET_190_ClickOnSectionTwo1") {
        exec(http("XUI_ET_190_005_ClickOnSectionTwo1")
          .get("/cases/case-details/#{caseId}/trigger/et1SectionTwo/et1SectionTwo1")
          .headers(CommonHeader)
          .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
          .check(status.is(200)))

        .exec(http("XUI_ET_190_010_ClickOnSectionTwo1")
          .get("/data/internal/cases/#{caseId}/event-triggers/et1SectionTwo?ignore-warning=false")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
          .check(substring("et1SectionTwo"))
          .check(jsonPath("$.event_token").optional.saveAs("event_token_section2"))
          .check(status.is(200)))
      
        .exec(http("XUI_ET_190_015_ClickOnSectionTwo1")
          .get("/data/internal/profile")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
          .check(substring("internal/profile"))
          .check(status.is(200)))

        .exec(Common.configurationui)
      
        .exec(Common.configJson)
      
        .exec(Common.userDetails)
      
        .exec(Common.TsAndCs)
      
        .exec(Common.configUI)
      
        .exec(Common.isAuthenticated)
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section Two - Continue
    ==========================================================================================*/

    .group("XUI_ET_200_SectionTwoContinue") {
      exec(http("XUI_ET_200_SectionTwoContinue")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1Section2Continue.json"))
        .check(substring("et1SectionTwo1"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET Section Two 2 -- Respondant Yes or No --Yes
    ==========================================================================================*/

    .group("XUI_ET_210_SectionTwo2") {
      exec(http("XUI_ET_210_SectionTwo2")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo2")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step2.json"))
        .check(substring("et1SectionTwo2"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section Two 3 -- claimant details
    ==========================================================================================*/

    .group("XUI_ET_220_SectionTwo3") {
      exec(http("XUI_ET_0220_SectionTwo3")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo3")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step3.json"))
        .check(substring("et1SectionTwo3"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET Section two 4 -- claimant post code
    ==========================================================================================*/

    .group("XUI_ET_230_SectionOne4") {
      exec(http("XUI_ET_230_SectionOne4")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo4")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step4.json"))
        .check(substring("et1SectionTwo4"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      Create Civil Claim - ET Section two 7 -- hearing type
    ==========================================================================================*/

      .group("XUI_ET_240_SectionOne7") {
        exec(http("XUI_ET_240_SectionOne7")
          .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo7")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/createcase/initiateSection2Step7.json"))
          .check(substring("et1SectionTwo7"))
          .check(status.is(200)))
      }
      .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section two 7 -- hearing type
    ==========================================================================================*/

    .group("XUI_ET_250_SectionOne8") {
      exec(http("XUI_ET_250_SectionOne8")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo8")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step8.json"))
        .check(substring("et1SectionTwo8"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)

    /*======================================================================================
      Create Civil Claim - ET Section one 8 -- Any support required
    ==========================================================================================*/

    .group("XUI_ET_260_SectionOne9") {
      exec(http("XUI_ET_260_SectionOne9")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo9")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step9.json"))
        .check(substring("et1SectionTwo9"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET Section two 9 -- mode of correspondance
    ==========================================================================================*/

    .group("XUI_ET_120_SectionOne10") {
      exec(http("XUI_ET_120_SectionOne10")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo10")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step10.json"))
        .check(substring("et1SectionTwo10"))
        .check(status.is(200)))
    }  
    .pause(MinThinkTime, MaxThinkTime)
    
    /*======================================================================================
      Create Civil Claim - ET Section two 11 -- mode of correspondance
    ==========================================================================================*/

      .group("XUI_ET_270_SectionOne11") {
        exec(http("XUI_ET_270_SectionOne11")
          .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo11")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/createcase/initiateSection2Step11.json"))
          .check(substring("et1SectionTwo11"))
          .check(status.is(200)))
      }
      .pause(MinThinkTime, MaxThinkTime)
    
      /*======================================================================================
        Create Civil Claim - ET Section two 12 -- mode of correspondance
      ==========================================================================================*/
      .group("XUI_ET_280_SectionOne12") {
        exec(http("XUI_ET_280_SectionOne12")
          .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo12")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
          .body(ElFileBody("bodies/createcase/initiateSection2Step12.json"))
          .check(substring("et1SectionTwo12"))
          .check(status.is(200)))
      }
      .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      Create Civil Claim - ET Section two 10 -- mode of correspondance
    ==========================================================================================*/

    .group("XUI_ET_290_SectionOne13") {
      exec(http("XUI_ET_290_SectionOne13")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo13")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step13.json"))
        .check(substring("et1SectionTwo13"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      Create Civil Claim - ET Section two 11 -- mode of correspondance
    ==========================================================================================*/

    .group("XUI_ET_300_SectionOne14") {
      exec(http("XUI_ET_300_SectionOne14")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo14")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step14.json"))
        .check(substring("et1SectionTwo14"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      Create Civil Claim - ET Section two 12 -- mode of correspondance
    ==========================================================================================*/

    .group("XUI_ET_310_SectionOne15") {
      exec(http("XUI_ET_310_SectionOne15")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo15")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step15.json"))
        .check(substring("et1SectionTwo15"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
    
    /*======================================================================================
      Create Civil Claim - ET Section two 13 -- mode of correspondance
    ==========================================================================================*/

    .group("XUI_ET_320_SectionOne17") {
      exec(http("XUI_ET_320_SectionOne17")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo17")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step17.json"))
        .check(substring("et1SectionTwo17"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      Create Civil Claim - ET Section two 14 -- mode of correspondance
    ==========================================================================================*/

    .group("XUI_ET_330_SectionOne19") {
      exec(http("XUI_ET_330_SectionOne19")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionTwo19")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection2Step19.json"))
        .check(substring("et1SectionTwo19"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
    
    /*======================================================================================
      Create Civil Claim - ET Section two -- save as draft
    ==========================================================================================*/

    .group("XUI_ET_340_SectionTwoSaveAsDraft") {
      exec(http("XUI_ET_340_005_SectionTwoSaveAsDraft")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/section2CreateCase.json"))
        .check(substring("CALLBACK_COMPLETED"))
        .check(status.is(201)))
      
      .exec(http("XUI_ET_340_010_SectionOneSaveAsDraft")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("#{caseId}"))
        .check(status.in(200, 201)))
    }
    .pause(MinThinkTime, MaxThinkTime)
      
    /*======================================================================================
      Create Civil Claim - ET Section two -- back to details
    ==========================================================================================*/

    .group("XUI_ET_350_SectionTwoBacktoCaseDetails") {
      exec(http("XUI_ET_350_005_SectionTwoBacktoCaseDetails")
        .post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/createcase/section2CaseDetails.json"))
        .check(status.in(200,201,204)))
    }    
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      Create Civil Claim - ET Section 3- Details Of Claim
    ==========================================================================================*/
      
    val ETFormClaimDetailsSection3=
      
    group("XUI_ET_360_ClickOnSectionThree") {
      exec(http("XUI_ET_360_005_ClickOnSectionThree")
        .get("/cases/case-details/#{caseId}/trigger/et1SectionOne/et1SectionThree1")
        .headers(CommonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .check(status.is(200)))

      .exec(http("XUI_ET_360_010_ClickOnSectionThree")
        .get("/data/internal/cases/#{caseId}/event-triggers/et1SectionThree?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("et1SectionThree"))
        .check(jsonPath("$.event_token").optional.saveAs("event_token_section3"))
        .check(status.is(200)))
      
      .exec(http("XUI_ET_360_015_ClickOnSectionThree")
        .get("/data/internal/profile")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
        .check(substring("internal/profile"))
        .check(status.is(200)))
      
        .exec(Common.configurationui)
      
        .exec(Common.configJson)
      
        .exec(Common.userDetails)
      
        .exec(Common.TsAndCs)
      
        .exec(Common.configUI)
      
        .exec(Common.isAuthenticated)
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Create Civil Claim - ET Section 3 - Continue
    ==========================================================================================*/

    .group("XUI_ET_370_SectionOneContinue") {
      exec(http("XUI_ET_370_SectionOneContinue")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionThree1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateCase1Section3Continue.json"))
        .check(substring("et1SectionThree1"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
      Create Civil Claim - ET Section three 2 -- claimant details
    ==========================================================================================*/

    .group("XUI_ET_380_SectionThree2") {
      exec(http("XUI_ET_380_SectionThree2")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionThree2")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection3Step2.json"))
        .check(substring("et1SectionThree2"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
    
    /*======================================================================================
     Create Civil Claim - ET Section one 3 -- claimant details
    ==========================================================================================*/

    .group("XUI_ET_390_SectionThree3") {
      exec(http("XUI_ET_390_SectionThree3")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionThree3")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection3Step3.json"))
        .check(substring("et1SectionThree3"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET Section one 4 -- claimant post code
    ==========================================================================================*/

    .group("XUI_ET_400_SectionThree4") {
      exec(http("XUI_ET_400_SectionOne4")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionThree4")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection3Step4.json"))
        .check(substring("et1SectionThree4"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET Section one 5 -- hearing type
    ==========================================================================================*/

    .group("XUI_ET_410_SectionThree5") {
      exec(http("XUI_ET_410_SectionThree5")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=et1SectionThree5")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/initiateSection3Step5.json"))
        .check(substring("et1SectionThree5"))
        .check(status.is(200)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET Section three -- save as draft
    ==========================================================================================*/

    .group("XUI_ET_420_SectionThreeSaveAsDraft") {
      exec(http("XUI_ET_420_005_SectionThreeSaveAsDraft")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/section3CreateCase.json"))
        .check(substring("CALLBACK_COMPLETED"))
        .check(status.is(201)))
      
        .exec(http("XUI_ET_420_010_SectionThreeSaveAsDraft")
          .get("/data/internal/cases/#{caseId}")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .check(substring("#{caseId}"))
          .check(status.in(200, 201)))
    }
    .pause(MinThinkTime, MaxThinkTime)
    
    /*======================================================================================
      Create Civil Claim - ET Section Three -- back to details
    ==========================================================================================*/

    .group("XUI_ET_430_SectionThreeBacktoCaseDetails") {
      exec(http("XUI_ET_430_005_SectionThreeBacktoCaseDetails")
        .post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .body(ElFileBody("bodies/createcase/section3CaseDetails.json"))
        .check(status.in(200, 201, 204)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - Click On ET case Submit
    ==========================================================================================*/

    val submitETForm=

    group("XUI_ET_440_ClickOnETCaseSubmit") {
      exec(http("XUI_ET_440_005_ClickOnETCaseSubmi")
        .get("/cases/case-details/#{caseId}/trigger/submitEt1Draft/submitEt1Draft1")
        .headers(CommonHeader)
        .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
        .check(status.is(200)))

      .exec(http("XUI_ET_440_010_ClickOnETCaseSubmit")
        .get("/data/internal/cases/#{caseId}/event-triggers/submitEt1Draft?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("Submit ET1"))
        .check(jsonPath("$.event_token").optional.saveAs("event_token_submit"))
        .check(status.is(200)))
    
      .exec(http("XUI_ET_440_015_ClickOnETCaseSubmit")
        .get("/data/internal/profile")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-user-profile.v2+json;charset=UTF-8")
        .check(substring("internal/profile"))
        .check(status.is(200)))
      
        .exec(Common.configurationui)
      
        .exec(Common.configJson)
      
        .exec(Common.userDetails)
      
        .exec(Common.TsAndCs)
      
        .exec(Common.configUI)
      
        .exec(Common.isAuthenticated)
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
     Create Civil Claim - ET case Submit
    ==========================================================================================*/

    .group("XUI_ET_450_ETCaseSubmit") {
      exec(http("XUI_ET_450_005_ETCaseSubmit")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=submitEt1Draft1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/submitetform.json"))
        .check(status.is(200))
        .check(substring("submitEt1Draft1")))

      .exec(http("XUI_ET_450_010_ETCaseSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/createcase/submitevent.json"))
        .check(substring("CALLBACK_COMPLETED"))
        .check(status.in(200,201)))
      
      .exec(http("XUI_ET_450_015_ETSubmit")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(substring("Create draft claim"))
        .check(status.in(200,201)))
    }  
    .pause(MinThinkTime, MaxThinkTime)
  
      /*======================================================================================
                     * Create Civil Claim - ET after submit -- back to details
      ==========================================================================================*/

      .group("XUI_ET_460_AfterSubmitBacktoCaseDetails") {
        exec(http("XUI_ET_460_005_AfterSubmit")
          .post("/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
          .headers(CommonHeader)
          .header("accept", "application/json, text/plain, */*")
          .body(ElFileBody("bodies/createcase/aftersubmitCaseDetails.json"))
          .check(status.in(200, 201, 204)))
      }
      .pause(MinThinkTime, MaxThinkTime)
    
    .exec { session =>
      val fw = new BufferedWriter(new FileWriter("ETcases.csv", true))
      try {
        fw.write(session("caseId").as[String] + "\r\n")
      } finally fw.close()
      session
    }
}
