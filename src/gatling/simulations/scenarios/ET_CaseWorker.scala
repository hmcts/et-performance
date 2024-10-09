package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, CsrfCheck, Environment, Headers}
import java.io.{BufferedWriter, FileWriter}

import scala.concurrent.duration._


object ET_CaseWorker {

  val xuiURL = Environment.baseURL
  val IdamURL = Environment.idamURL

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val postcodeFeeder = csv("postcodes.csv").circular

  val MakeAClaim =

  exec(session => {
  // Generate and set the random string and date
  val updatedSession = session.setAll(
      "ETCWRandomString" -> (Common.randomString(7)),
      "claimAcceptedDate" -> (Common.getDateClaimAccepted())
  )
  // Retrieve the date and print it to the terminal
  val claimAcceptedDate = updatedSession("claimAcceptedDate").as[String]
  println(s"Claim Accepted Date: $claimAcceptedDate")

  // Return the updated session
  updatedSession
})

  //exec(_.setAll(
    //  "ETCWRandomString" -> (Common.randomString(7)),
      //"claimAcceptedDate" -> (Common.getDateClaimAccepted())))

    /*======================================================================================
    * Open Case
    ======================================================================================*/

    .exec(http("XUI_PRL_XXX_500_SelectCase")
      .get(xuiURL + "/data/internal/cases/#{caseId}")
      .headers(Headers.xuiHeader)
      .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
      .check(jsonPath("$.tabs[12].fields[1].formatted_value.claimant_first_names").saveAs("ClaimantFirstName"))
      .check(jsonPath("$.tabs[12].fields[1].formatted_value.claimant_last_name").saveAs("ClaimantLastName"))
      .check(jsonPath("$.tabs[12].fields[3].value.claimant_addressUK.AddressLine1").saveAs("ClaimantAdressLine1")) //Strip left address1
      .check(jsonPath("$.tabs[6].fields[0].value[0].value.respondent_name").saveAs("RespondentName")) // Strip right Respondent
      .check(jsonPath("$.tabs[12].fields[2].value[0].value.uploadedDocument.document_url").saveAs("etDocHash"))
      .check(jsonPath("$.tabs[4].fields[0].value[*].value.juridictionCodesList").findAll.saveAs("jurisdictionCode")) // Save all codes
      .check(jsonPath("$.tabs[6].fields[0].value[0].value.respondent_ACAS").findAll.saveAs("acasCertID"))
      .check(jsonPath("$.case_id").is("#{caseId}"))
      .check(status.in(200,204)))

    .exec(Common.waJurisdictions)
    .exec(Common.activity)
    .exec(Common.userDetails)
    .exec(Common.caseActivityGet)
    .exec(Common.isAuthenticated)

    .exec(getCookieValue(CookieKey("XSRF-TOKEN").withDomain(xuiURL.replace("https://", "")).saveAs("XSRFToken")))

    .pause(MinThinkTime, MaxThinkTime)

  // ==================================================================================================
  // Code to strip the captured Claimant and Respondent random string for use in subsequent requests
  // ==================================================================================================
  .exec { session =>
    // Access the captured value and strip the right side for `ClaimantAddressLine1`
    val claimantAddress = session("ClaimantAdressLine1").as[String]
    val claimantRandString = claimantAddress.stripPrefix("address1")
    println(s"Claimant Rand String: $claimantRandString")
    
    // Return the session with the new value set
    session.set("claimantRandString", claimantRandString)
  }
  .exec { session =>
    // Access the captured value and strip the left side for `RespondentName`
    val respondentName = session("RespondentName").as[String]
    val respondentRandString = respondentName.stripSuffix("Respondent")
    println(s"Respondent Rand String: $respondentRandString")
    
    // Return the session with the new value set
    session.set("respondentRandString", respondentRandString)
  }

    /*======================================================================================
    * Find Case
    ======================================================================================*/

    /*.group("ET_CW_500_FindCase") {
      exec(http("ET_CW_500_005_Find_Case")
        .get(xuiURL + "/api/role-access/roles/manageLabellingRoleAssignment/#{caseId}")
        .headers(CommonHeader)
        .header("sec-fetch-site", "none")
        .check(substring("Claimant")))

      .exec(http("ET_CW_500_010_Find_Case")
        .get(xuiURL + "/api/wa-supported-jurisdiction/get")
        .headers(CommonHeader)
        .header("sec-fetch-site", "none")
        .check(substring("CIVIL")))

        .exec(Common.userDetails)
    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

*/
    /*======================================================================================
    * Click on 'ET1 Case Vetting'
    ======================================================================================*/

    .group("ET_CW_510_Case_Vetting") {
      exec(http("ET_CW_510_005_Case_Vetting")
        .get(xuiURL + "/workallocation/case/tasks/#{caseId}/event/et1Vetting/caseType/ET_EnglandWales/jurisdiction/EMPLOYMENT")
        .headers(CommonHeader)
        .check(substring("Before you start")))

      .exec(Common.profile)

      exec(http("ET_CW_510_010_Case_Vetting")
        .get(xuiURL + "/data/internal/cases/#{caseId}/event-triggers/et1Vetting?ignore-warning=false")
        .headers(CommonHeader)
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("Before you start")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*======================================================================================
    * Before you Start
    ======================================================================================*/

    .group("ET_CW_520_Before_You_Start") {
      exec(http("ET_CW_520_005_Before_You_Start")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting1")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/EtBeforeYouStart.json"))
        .check(substring("et1VettingBeforeYouStart")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*======================================================================================
    * Minimum required information
    ======================================================================================*/

    .group("ET_CW_530_Min_Required_Information") {
      exec(http("ET_CW_530_005_Min_Required_Information")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting2")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/MinRequiredInformation.json"))
        .check(substring("et1VettingCanServeClaimGeneralNote")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Minimum required information - Acas Certificate
  ======================================================================================*/

    .group("ET_CW_540_Acas_Certificate ") {
      exec(http("ET_CW_540_005_Acas_Certificate")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting3")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/AcasCertificate.json"))
        .check(substring("et1VettingAcasCertGeneralNote")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Possible substantive defects
  ======================================================================================*/

    .group("ET_CW_550_Possible_Substantive_Defects") {
      exec(http("ET_CW_550_005_Possible_Substantive_Defects")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting4")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/PossibleSubstantiveDefects.json"))
        .check(substring("et1SubstantiveDefectsGeneralNotes")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Jurisdiction Codes - yes
  ======================================================================================*/

    .group("ET_CW_560_Jurisdiction_Codes") {
      exec(http("ET_CW_560_005_Jurisdiction_Codes")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting5")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/JurisdictionCodes.json"))
        .check(substring("areTheseCodesCorrect")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Track allocation - yes
  ======================================================================================*/

    .group("ET_CW_570_Track_Allocation") {
      exec(http("ET_CW_570_005_Track_Allocation")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting6")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/TrackAllocation.json"))
        .check(substring("isTrackAllocationCorrect")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Tribunal location - yes
  ======================================================================================*/

    .group("ET_CW_580_Tribunal_Location") {
      exec(http("ET_CW_580_005_Tribunal_Location")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting7")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/TribunalLocation.json"))
        .check(substring("isTrackAllocationCorrect")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Listing Details - no
  ======================================================================================*/

    .group("ET_CW_590_Listing_Details") {
      exec(http("ET_CW_590_005_Listing_Details")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting8")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/ListingDetails.json"))
        .check(substring("et1HearingVenueGeneralNotes")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Further questions
======================================================================================*/

    .group("ET_CW_600_Further_Questions") {
      exec(http("ET_CW_600_005_Further_Questions")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting9")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/FurtherQuestions.json"))
        .check(substring("et1FurtherQuestionsGeneralNotes")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Possible referral to a judge or legal officer
======================================================================================*/

    .group("ET_CW_610_Possible_Referral") {
      exec(http("ET_CW_610_005_Possible_Referral")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting10")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/PossibleReferral.json"))
        .check(substring("et1JudgeReferralGeneralNotes")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Possible referral to Regional Employment Judge or Vice-President
  ======================================================================================*/

    .group("ET_CW_620_Possible_Referral_Employ_Judge") {
      exec(http("ET_CW_620_005_Possible_Referral_Employ_Judge")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting11")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/PossibleReferralEmployJudge.json"))
        .check(substring("et1REJOrVPReferralGeneralNotes")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Does the claim include any other factors
  ======================================================================================*/

      .group("ET_CW_630_Other_Factors") {
        exec(http("ET_CW_630_005_Other_Factors")
          .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting12")
          .headers(CommonHeader)
          .body(ElFileBody("bodies/CaseWorker/OtherFactors.json"))
          .check(substring("et1OtherReferralGeneralNotes")))

          .exec(Common.userDetails)
      }
      .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


  /*======================================================================================
  * Final Notes
  ======================================================================================*/

    .group("ET_CW_640_Final_Notes") {
      exec(http("ET_CW_640_005_Final_Notes")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting13")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/FinalNotes.json"))
        .check(substring("et1VettingAdditionalInformationTextArea")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Check Your Answers
======================================================================================*/

    .group("ET_CW_650_Vet_Check_Answers") {
      exec(http("ET_CW_650_005_Vet_Check_Answers")
        .post(xuiURL + "/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/VetCheckAnswers.json"))
        .check(jsonPath("$.state").is("Vetted")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


  val dateCaseAccepted = 

  /*======================================================================================
  * Accept or Reject Case Link
  ======================================================================================*/

    group("ET_CW_660_Accept_Or_Reject") {
      exec(http("ET_CW_660_005_Accept_Or_Reject")
        .get(xuiURL + "/cases/case-details/#{caseId}/trigger/preAcceptanceCase/preAcceptanceCase1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("HMCTS Manage cases")))

        .exec(Common.configurationui)
        .exec(Common.configJson)
        .exec(Common.userDetails)
        .exec(Common.TsAndCs)
        .exec(Common.configUI)
        .exec(Common.isAuthenticated)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Accept Case Event
  ======================================================================================*/

    .group("ET_CW_665_Accept_Case_Event") {
      exec(http("ET_CW_665_005_Accept_Case_Event")
        .get(xuiURL + "/data/internal/cases/#{caseId}/event-triggers/preAcceptanceCase?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("preAcceptanceCase")))
    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Accept Case
  ======================================================================================*/

    .group("ET_CW_670_Accept_Case") {
      exec(http("ET_CW_670_005_Accept_Case")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=preAcceptanceCase1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/CaseWorker/AcceptCase.json"))
        //Date needs to be today or later date after case was made
        .check(substring("caseSource")))

        .exec(Common.userDetails)

      .exec(http("ET_CW_670_010_Accept_Case")
        .post(xuiURL + "/data/cases/${caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/CaseWorker/AcceptCaseEvent.json"))
        //Date needs to be today or later date after case was made
        .check(jsonPath("$.state").is("Accepted")))

      .exec(http("XUI_PRL_670_015_Accept_Case")
        .get(xuiURL + "/data/internal/cases/#{caseId}")
        .headers(Headers.xuiHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.case_id").is("#{caseId}"))
        .check(status.in(200,204)))

      .exec(Common.waJurisdictions)
      .exec(Common.activity)
      .exec(Common.userDetails)
      .exec(Common.caseActivityGet)
      .exec(Common.isAuthenticated)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


  val generateLetters = 
  /*======================================================================================
  * Select Letters Event 
  ======================================================================================*/

   group("ET_CW_680_Letters_EventTrigger") {
      exec(http("ET_CW_680_005_Letters_EventTrigger")
        .get(xuiURL + "/data/internal/cases/#{caseId}/event-triggers/generateCorrespondence?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(substring("generateCorrespondence")))
    }

    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
  * Select Letter types --> Part 2 --> Letter 2.6 & Submit
  ======================================================================================*/

  .group("ET_CW_690_Select_Letter_Event") {
      exec(http("ET_CW_690_005_Select_Letter_Event")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=generateCorrespondence1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/CaseWorker/LetterGeneration.json"))
        .check(substring("et1GovOrMajorQuestion")))

      .exec(http("ET_CW_690_010_Select_Letter_Event")
        .post(xuiURL + "/data/cases/${caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/CaseWorker/LetterGenerationEvent.json"))
        .check(jsonPath("$.state").is("Accepted")))

      .exec(http("XUI_PRL_690_015_Select_Letter_Event")
        .get(xuiURL + "/data/internal/cases/#{caseId}")
        .headers(Headers.xuiHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(jsonPath("$.case_id").is("#{caseId}"))
        .check(status.in(200,204)))

      .exec(Common.activity)
  }
  .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  //===================================================================================
	//Write session info to case to the E3Cases data files for Claimant and Respondent
	//===================================================================================
    .exec { session =>
    val fw = new BufferedWriter(new FileWriter("E3CaseLinkData.csv", true))
    try {
      fw.write(session("caseId").as[String] + "," + session("respondentRandString").as[String] + "Respondent" + "," + "perftest" + "," + "Employment" + "\r\n")
    } finally fw.close()

        session
    } 

   // val fw = new BufferedWriter(new FileWriter("E3CaseLinkDataRespondent.csv", true))
   // try {
   //   fw.write(session("username").as[String] + "," + session("password").as[String] + "," + session("caseId").as[String] + "," + "(respondentRandString)".as[String] + "," + "Respondent" + "\r\n")
   // } finally fw.close()



}
