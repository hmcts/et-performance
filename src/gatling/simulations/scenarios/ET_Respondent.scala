package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment}

import scala.concurrent.duration._


object ET_Respondent {

  val xuiURL = Environment.baseURL
  val IdamURL = Environment.idamURL


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  val CommonHeader = Headers.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val MakeAClaim =

    /*======================================================================================
    * Notice Of Change
    ======================================================================================*/

    group("ET_Respond_680_NoC") {
      exec(Common.isAuthenticated)
      .exec(Common.userDetails)
    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*======================================================================================
    * Notice Of Change Case Select
    ======================================================================================*/

    .group("ET_Respond_685_NoC_Case") {
      exec(http("ET_Respond_685_005_NoC_Case")
        .get(xuiURL + "/api/noc/nocQuestions?caseId=1692881334351065")
        .headers(CommonHeader)
        .check(substring("questions")))

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

    /*======================================================================================
    * NoC - Enter details
    ======================================================================================*/

    .group("ET_Respond_690_NoC_Enter_Details") {
      exec(http("ET_Respond_690_005_NoC_Enter_Details")
        .post(xuiURL + "/api/noc/validateNoCQuestions")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtNocEnterDetails.json"))
        //names have to be the same as previous
        .check(substring("et1VettingBeforeYouStart")))

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

  /*======================================================================================
 * NoC - Submit
 ======================================================================================*/

    .group("ET_Respond_700_NoC_Submit") {
      exec(http("ET_Respond_700_005_NoC_Submit")
        .post(xuiURL + "/api/noc/submitNoCEvents")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtNocSubmit.json"))
        //names have to be the same as previous
        .check(substring("APPROVED")))

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* View This Case
======================================================================================*/

    .group("ET_Respond_710_View_This_Case") {
      exec(http("ET_Respond_710_005_View_This_Case")
        .get(xuiURL + "/cases/case-details/#{caseId}}")
        .headers(CommonHeader)
        .check(substring("Case Details")))

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Make an application
======================================================================================*/

    .group("ET_Respond_720_Make_An_Application") {
      exec(http("ET_Respond_720_005_Make_An_Application")
        .get(xuiURL + "/cases/case-details/1692974573081186/trigger/respondentTSE/respondentTSE1")
        .headers(CommonHeader)
        .check(substring("Select an application")))

        .exec(Common.configurationui)

        .exec(Common.configJson)

        .exec(Common.TsAndCs)

        .exec(Common.userDetails)

        .exec(Common.configUI)

        .exec(Common.isAuthenticated)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Select an application
======================================================================================*/

    .group("ET_Respond_730_Select_An_Application") {
      exec(http("ET_Respond_730_005_Select_An_Application")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=respondentTSE2")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtSelectApplication.json"))
        .check(substring("resTseSelectApplication")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)


/*======================================================================================
* Amend response Upload
======================================================================================*/

    .group("ET_Respond_740_Amend_Response_Upload") {
      exec(http("ET_Respond_740_005_Amend_Response_Upload")
        .post("/documents")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TestFile.pdf")
          .fileName("TestFile.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "PRLAPPS")
        .formParam("jurisdictionId", "PRIVATELAW")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("documentHashPD36Q"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLPD36Q")))

    }
    .pause(MinThinkTime, MaxThinkTime)

/*======================================================================================
* Amend response
======================================================================================*/

    .group("ET_Respond_745_Amend_Response") {
      exec(http("ET_Respond_745_005_Amend_Response")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=respondentTSE3")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtAmendResponse.json"))
        .check(substring("et1AddressDetails")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Copy this correspondence to the other party
======================================================================================*/

    .group("ET_Respond_750_Copy_Correspondence") {
      exec(http("ET_Respond_750_005_Copy_Correspondence")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=respondentTSE15")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtCopyCorrespondence.json"))
        .check(substring("resTseCopyToOtherPartyYesOrNo")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Make An Application Submit
======================================================================================*/

    .group("ET_Respond_760_Make_Application_Submit") {
      exec(http("ET_Respond_760_005_Make_Application_Submit")
        .post(xuiURL + "/data/cases/1692974573081186/events")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtMakeApplicationSubmit.json"))
        .check(substring("CALLBACK_COMPLETED")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* All Applications
======================================================================================*/

    .group("ET_Respond_770_All_Applications") {
      exec(http("ET_Respond_770_005_All_Applications")
        .get(xuiURL + "/cases/case-details/1692974573081186/trigger/respondentTseAllApplications/respondentTseAllApplications1")
        .headers(CommonHeader)
        .check(substring("All applications")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
*  Applications Submit
======================================================================================*/

    .group("ET_Respond_770_All_Applications_Submit") {
      exec(http("ET_Respond_770_005_All_Applications_Submit")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=respondentTseAllApplications1")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtAllApplicationsSubmit.json"))
        .check(substring("resTseTableMarkUp")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
*  Close and return to case details
======================================================================================*/

    .group("ET_Respond_780_Return_To_Case_Details") {
      exec(http("ET_Respond_780_005_Return_To_Case_Details")
        .post(xuiURL + "/data/cases/1692974573081186/events")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtReturnToCaseDetails.json"))
        .check(substring("Accepted")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Respond to an application
======================================================================================*/

    .group("ET_Respond_790_Respond_To_Application") {
      exec(http("ET_Respond_790_005_Respond_To_Application")
        .get(xuiURL + "/cases/case-details/1692974573081186/trigger/tseRespond/tseRespond1")
        .headers(CommonHeader)
        .check(substring("Select an application")))

        .exec(Common.configurationui)

        .exec(Common.configJson)

        .exec(Common.TsAndCs)

        .exec(Common.userDetails)

        .exec(Common.configUI)

        .exec(Common.isAuthenticated)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Select an application
======================================================================================*/

    .group("ET_Respond_800_Select_An_Application") {
      exec(http("ET_Respond_800_005_Select_An_Application")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=tseRespond2")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtSelectAnApplication.json"))
        .check(substring("et1AddressDetails")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Your response
======================================================================================*/

    .group("ET_Respond_810_Your_Response") {
      exec(http("ET_Respond_810_005_Your_Response")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=tseRespond3")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtYourResponse.json"))
        .check(substring("tseResponseText")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Provide supporting material Document
======================================================================================*/

    .group("ET_Respond_820_Supporting_Material_Document") {
      exec(http("ET_Respond_820_005_Supporting_Material_Document")
        .post("/documentsv2")
        .headers(Headers.commonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("x-xsrf-token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TestFile.pdf")
          .fileName("TestFile.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "PRLAPPS")
        .formParam("jurisdictionId", "PRIVATELAW")
        .check(substring("originalDocumentName"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("documentHashPD36Q"))
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("DocumentURLPD36Q")))

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Provide supporting material
======================================================================================*/

    .group("ET_Respond_825_Supporting_Material") {
      exec(http("ET_Respond_825_005_Supporting_Material")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=tseRespond4")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtSupportingMaterial.json"))
        .check(substring("tseResponseSupportingMaterial")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Copy this correspondence to the other party
======================================================================================*/

    .group("ET_Respond_830_Correspondence_Other_Party") {
      exec(http("ET_Respond_830_005_Correspondence_Other_Party")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=tseRespond5")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtCorrespondenceOtherParty.json"))
        .check(substring("tseResponseSupportingMaterial")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Respond to an application Submit
======================================================================================*/

    .group("ET_Respond_840_Respond_To_Application_Submit") {
      exec(http("ET_Respond_840_005_Respond_To_Application_Submit")
        .post(xuiURL + "/data/cases/1692974573081186/events")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtRespondToApplicationSubmit.json"))
        .check(substring("confirmation_body")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* View An Application
======================================================================================*/

    .group("ET_Respond_850_View_An_Application ") {
      exec(http("ET_Respond_850_005_View_An_Application")
        .get(xuiURL + "/cases/case-details/1692974573081186/trigger/viewRespondentTSEApplications/viewRespondentTSEApplications1")
        .headers(CommonHeader)
        .check(substring("What application do you wish to view? ")))

        .exec(Common.configurationui)

        .exec(Common.configJson)

        .exec(Common.TsAndCs)

        .exec(Common.userDetails)

        .exec(Common.configUI)

        .exec(Common.isAuthenticated)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* What application do you wish to view?
======================================================================================*/

    .group("ET_Respond_860_Application_To_View") {
      exec(http("ET_Respond_860_005_Application_To_View")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=viewRespondentTSEApplications1")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtApplicationToView.json"))
        .check(substring("tseViewApplicationOpenOrClosed")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* Select Application
======================================================================================*/

    .group("ET_Respond_870_Select_Application") {
      exec(http("ET_Respond_870_005_Select_Application")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=viewRespondentTSEApplications2")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtSelectSingleApplication.json"))
        .check(substring("tseViewApplicationOpenOrClosed")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

/*======================================================================================
* View Application Submit
======================================================================================*/

    .group("ET_Respond_880_View_Application_Submit") {
      exec(http("ET_Respond_880_005_View_Application_Submit")
        .post(xuiURL + "/data/case-types/ET_EnglandWales/validate?pageId=viewRespondentTSEApplications3")
        .headers(CommonHeader)
        .body(ElFileBody("bodies/Respondent/EtViewApplicationSubmit.json"))
        .check(substring("tseApplicationSummaryAndResponsesMarkup")))

        .exec(Common.userDetails)

    }
    .pause(MinThinkTime.seconds, MaxThinkTime.seconds)

}
