package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.Environment.{commonHeaderUpload, postHeader1}
import utils.{Common, Environment}

import java.io.{BufferedWriter, FileWriter}


object ET_CaseDocUpload {

  val IdamURL = Environment.idamURL
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime
  val CommonHeader = Environment.commonHeader

  val postcodeFeeder = csv("postcodes.csv").circular

  val DocUpload =

  exec(_.setAll(
      "ETCWRandomString" -> (Common.randomString(7))))
  
    /*======================================================================================
    Create ET - Upload around 11 docs to make it 20-25 documents for case file view
    ==========================================================================================*/

    .group("ET_CW_040_UploadDocs") {
      exec(http("ET_CW_040_005_StartUploadDoc")
        .get("/data/internal/cases/#{caseId}/event-triggers/uploadDocument?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("Upload Document"))
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
  /*======================================================================================
   * Create ET - upload document
   ==========================================================================================*/
    /*======================================================================================
    * Upload ACAS
    ======================================================================================*/
  
    .group("ET_CW_050_UploadACAS") {
      exec(http("ET_CW_050_005_UploadACAS")
        .post("/documentsv2")
        .headers(commonHeaderUpload)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryKKB3qAYvMA5Pn40B")
        .header("X-Xsrf-Token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files","ACAS-1MB.pdf")
          .fileName("ACAS-1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_ACAS"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_ACAS")))
    }  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload ACAS1
    ======================================================================================*/
  
    .group("ET_CW_060_UploadACAS1") {
      exec(http("ET_CW_060_005_UploadACAS1")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "ACAS-1MB - Copy.pdf")
          .fileName("ACAS-1MB - Copy.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_ACAS1"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_ACAS1")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Acknowledgement
    ======================================================================================*/
  
    .group("ET_CW_070_UploadACK") {
      exec(http("ET_CW_070_005_UploadACK")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "Acknowledgement-1MB.pdf")
          .fileName("Acknowledgement-1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Ack"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Ack")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Acknowledgement1
    ======================================================================================*/
  
    .group("ET_CW_080_UploadACK1") {
      exec(http("ET_CW_080_005_UploadACK1")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "Acknowledgement-1MB - Copy.pdf")
          .fileName("Acknowledgement-1MB - Copy.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Ack1"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Ack1")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Notice
    ======================================================================================*/
  
    .group("ET_CW_090_UploadNotice") {
      exec(http("ET_CW_090_005_UploadNotice")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "NoticeOfClaim-1MB.pdf")
          .fileName("NoticeOfClaim-1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Notice"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Notice")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Notice1
    ======================================================================================*/
  
    .group("ET_CW_100_UploadNotice1") {
      exec(http("ET_CW_100_005_UploadNotice1")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "NoticeOfClaim-1MB - Copy.pdf")
          .fileName("NoticeOfClaim-1MB - Copy.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Notice1"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Notice1")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalCase
    ======================================================================================*/
  
    .group("ET_CW_110_UploadTribunalCaseFile") {
      exec(http("ET_CW_110_005_UploadTribunalCaseFile")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TribunalCaseFile-1MB.pdf")
          .fileName("TribunalCaseFile-1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_TribunalCaseFile"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalCaseFile")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalCase1
    ======================================================================================*/
  
    .group("ET_CW_120_UploadTribunalCaseFile1") {
      exec(http("ET_CW_120_005_UploadTribunalCaseFile1")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TribunalCaseFile-1MB - Copy.pdf")
          .fileName("TribunalCaseFile-1MB - Copy.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_TribunalCaseFile1"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalCaseFile1")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalOrder
    ======================================================================================*/
  
    .group("ET_CW_130_UploadTribunalOrder") {
      exec(http("ET_CW_130_005_UploadTribunalOrder")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TribunalOrder-1MB.pdf")
          .fileName("TribunalOrder-1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_TribunalOrder"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalOrder")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalOrder1
    ======================================================================================*/
  
    .group("ET_CW_140_UploadTribunalOrder1") {
      exec(http("ET_CW_140_005_UploadTribunalOrder1")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TribunalOrder-1MB - Copy.pdf")
          .fileName("TribunalOrder-1MB - Copy.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_TribunalOrder1"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalOrder1")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Other
    ======================================================================================*/
  
    .group("ET_CW_150_UploadOther") {
      exec(http("ET_CW_150_005_UploadOther")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "Other-1MB.pdf")
          .fileName("Other-1MB.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Other"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Other")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload ACAS
    ======================================================================================*/
  
    .group("ET_CW_160_UploadACAS2") {
      exec(http("ET_CW_160_005_UploadACAS2")
        .post("/documentsv2")
        .headers(commonHeaderUpload)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryKKB3qAYvMA5Pn40B")
        .header("X-Xsrf-Token", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "ACAS-1MB - 1.pdf")
          .fileName("ACAS-1MB - 1.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_ACAS2"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_ACAS2")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Acknowledgement
    ======================================================================================*/
  
    .group("ET_CW_170_UploadACK2") {
      exec(http("ET_CW_170_005_UploadACK2")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "Acknowledgement-1MB - 1.pdf")
          .fileName("Acknowledgement-1MB - 1.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Ack2"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Ack2")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Notice
    ======================================================================================*/
  
    .group("ET_CW_180_UploadNotice2") {
      exec(http("ET_CW_180_005_UploadNotice2")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "NoticeOfClaim-1MB - 1.pdf")
          .fileName("NoticeOfClaim-1MB - 1.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Notice2"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Notice2")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalCase
    ======================================================================================*/
  
    .group("ET_CW_190_UploadTribunalCaseFile2") {
      exec(http("ET_CW_190_005_UploadTribunalCaseFile2")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TribunalCaseFile-1MB - 1.pdf")
          .fileName("TribunalCaseFile-1MB - 1.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_TribunalCaseFile2"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalCaseFile2")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalOrder
    ======================================================================================*/
  
    .group("ET_CW_200_UploadTribunalOrder2") {
      exec(http("ET_CW_200_005_UploadTribunalOrder2")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "TribunalOrder-1MB - 1.pdf")
          .fileName("TribunalOrder-1MB - 1.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_TribunalOrder2"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalOrder2")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Other
    ======================================================================================*/
  
    .group("ET_CW_210_UploadOther2") {
      exec(http("ET_CW_210_005_UploadOther2")
        .post("/documentsv2")
        .headers(CommonHeader)
        .header("accept", "application/json, text/plain, */*")
        .header("content-type", "multipart/form-data")
        .header("X-XSRF-TOKEN", "${XSRFToken}")
        .bodyPart(RawFileBodyPart("files", "Other-1MB - 1.pdf")
          .fileName("Other-1MB - 1.pdf")
          .transferEncoding("binary"))
        .asMultipartForm
        .formParam("classification", "PUBLIC")
        .formParam("caseTypeId", "#{caseId}")
        .formParam("jurisdictionId", "EMPLOYMENT")
        .check(jsonPath("$.documents[0]._links.self.href").saveAs("documentURL_Other2"))
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Other2")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Click Continue
    ======================================================================================*/
  
    .group("ET_CW_220_ConfirmDocumentUploads") {
      exec(http("ET_CW_220_005_ConfirmDocumentUploads")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=uploadDocument1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/docupload/docupload.json"))
        .check(substring("documentCollection")))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    Click Submit
    ======================================================================================*/

    .group("ET_CW_CreateClaim_230_NotifyDetailsEventSubmit") {
      exec(http("ET_CW_CreateClaim_230_005_NotifyDetailsEventSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/docupload/uploadsubmit.json"))
        .check(substring("#{caseId}"))
        .check(status.in(200, 201)))
        
      .exec(http("ET_CW_CreateClaim_230_010_NotifyDetailsEventSubmit")
        .get("/data/internal/cases/#{caseId}")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
        .check(status.in(200, 201, 204, 304)))
    }
    .pause(MinThinkTime, MaxThinkTime)
  
}
