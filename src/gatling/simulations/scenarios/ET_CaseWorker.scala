package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, CsrfCheck, Environment}

import scala.concurrent.duration._


object ET_CaseWorker {

  val xuiURL = Environment.xuiURL
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Environment.commonHeader
  val NavHeader = Environment.navHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val MakeAClaim =

  exec(_.setAll(
      "ETCWRandomString" -> (Common.randomString(7)),
    "caseId" -> ("1665482203245489")
  ))

    /*======================================================================================
    * Find Case
    ======================================================================================*/



  /*  .group("ET_CW_500_FindCase") {
      exec(http("ET_CW_500_005_Find_Case")
        .get(xuiURL + "/data/internal/cases/#{caseId}")
        .headers(NavHeader)
        .check(substring("Submitted")))

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
        .headers(NavHeader)
        .header("accept", "application/json")
        .check(substring("task_required_for_event")))

      .exec(http("ET_CW_510_010_Case_Vetting")
        .get(xuiURL + "/data/internal/cases/#{caseId}/event-triggers/et1Vetting?ignore-warning=false")
        .headers(NavHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(jsonPath("$.event_token").saveAs("event_token"))
        .check(jsonPath("$.case_fields[1].formatted_value").saveAs("et1VettingBeforeYouStart"))
        .check(jsonPath("$.case_fields[3].formatted_value").saveAs("et1VettingClaimantDetailsMarkUp"))
        .check(jsonPath("$.case_fields[5].formatted_value").saveAs("et1VettingRespondentDetailsMarkUp"))
        .check(jsonPath("$.case_fields[11].formatted_value").saveAs("et1VettingRespondentAcasDetails1"))
        .check(jsonPath("$.case_fields[48].formatted_value").saveAs("existingJurisdictionCodes"))
        .check(substring("et1Vetting")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


    /*======================================================================================
    * Before you Start
    ======================================================================================*/

    .group("ET_CW_520_Before_You_Start") {
      exec(http("ET_CW_520_005_Before_You_Start")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=et1Vetting1")
        .headers(NavHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .body(ElFileBody("bodies/CaseWorker/EtBeforeYouStart.json"))
        .check(substring("et1VettingBeforeYouStart")))

        .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


    /*======================================================================================
    * Minimum required information - yes
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
        .check(substring("et1VettingAcasCertGeneralNote")))

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
        .check(jsonPath("$.data.et1AddressDetails").saveAs("et1AddressDetails"))
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
        .check(substring("jurCodesCollection")))

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
        .check(substring("$.state").is("Vetted")))

        .exec(Common.userDetails)


    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


    /*======================================================================================
* Accept or Reject Case Link
======================================================================================*/

    .group("ET_CW_660_Accept_Or_Reject") {
      exec(http("ET_CW_660_005_Accept_Or_Reject")
        .get(xuiURL + "/cases/case-details/#{caseId}/trigger/preAcceptanceCase/preAcceptanceCase1")
        .headers(CommonHeader)
        .check(substring("Pre-Acceptance")))


        .exec(Common.configurationui)

        .exec(Common.configJson)

        .exec(Common.userDetails)

        .exec(Common.TsAndCs)

        .exec(Common.configUI)

        .exec(Common.isAuthenticated)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


    /*======================================================================================
* Accept Case
======================================================================================*/

    .group("ET_CW_670_Accept_Case") {
      exec(http("ET_CW_670_005_Accept_Case")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=preAcceptanceCase1")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/CaseWorker/AcceptCase.json"))
        //Date needs to be today or later date after case was made
        .check(substring("caseSource")))

        .exec(Common.userDetails)


    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)



}
