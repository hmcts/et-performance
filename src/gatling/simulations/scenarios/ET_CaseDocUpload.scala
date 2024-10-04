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
                   * Create ET - Upload around 11 docs to make it 20-25 documents for case file view
        ==========================================================================================*/
    .group("ET_CW_040_UploadDocs") {
      exec(http("ET_CW_040_StartUploadDoc")
        .get("/data/internal/cases/#{caseId}/event-triggers/uploadDocument?ignore-warning=false")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-start-event-trigger.v2+json;charset=UTF-8")
        .check(substring("Upload Document"))
        .check(status.is(200))
        .check(jsonPath("$.event_token").optional.saveAs("event_token"))
      )
        .pause(MinThinkTime, MaxThinkTime)
    }
  
  /*======================================================================================
                   * Create ET - upload document
        ==========================================================================================*/
  
    /*======================================================================================
    * Upload ACAS
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadACAS") {
      exec(http("XUI_Divorce_170_005_UploadACAS")
        .post("/documentsv2")
        .headers(commonHeaderUpload)
        //.headers(postHeader1)
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_ACAS"))
        
      )
       // .exec(Common.userDetails)
       // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
    /*  .group("CUI_CreateClaim_155_Upload") {
           exec(http("CUI_CreateClaim_155_Upload")
             .post(BaseURL + "/documents")
             .headers(Headers.commonHeader)
             .headers(Headers.postHeader)
             .header("accept", "application/json")
             .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryGPNRb3mRRAhkFBRV")
             .header("sec-fetch-dest", "empty")
             .header("sec-fetch-mode", "cors")
             .bodyPart(RawFileBodyPart("files[]", "2MB.pdf")
               .fileName("2MB.pdf")
               .transferEncoding("binary"))
             .asMultipartForm
           //  .check(jsonPath("$[0].id").saveAs("Document_ID"))
             .check(substring("2MB.pdf")))
         }
         .pause(MinThinkTime, MaxThinkTime)
   
        */
  
    /*======================================================================================
    * Upload ACAS1
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadACAS1") {
      exec(http("XUI_Divorce_170_005_UploadACAS1")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_ACAS1"))
       
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Acknowledgement
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadACK") {
      exec(http("XUI_Divorce_170_005_UploadACK")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Ack"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Acknowledgement1
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadACK1") {
      exec(http("XUI_Divorce_170_005_UploadACK1")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Ack1"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Notice
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadNotice") {
      exec(http("XUI_Divorce_170_005_UploadNotice")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Notice"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Notice1
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadNotice1") {
      exec(http("XUI_Divorce_170_005_UploadNotice1")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Notice1"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalCase
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadTribunalCaseFile") {
      exec(http("XUI_Divorce_170_005_UploadTribunalCaseFile")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalCaseFile"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalCase1
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadTribunalCaseFile1") {
      exec(http("XUI_Divorce_170_005_UploadTribunalCaseFile1")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalCaseFile1"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalOrder
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadTribunalOrder") {
      exec(http("XUI_Divorce_170_005_UploadTribunalOrder")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalOrder"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload TribunalOrder1
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadTribunalOrder1") {
      exec(http("XUI_Divorce_170_005_UploadTribunalOrder1")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalOrder1"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload Other
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadOther") {
      exec(http("XUI_Divorce_170_005_UploadOther")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Other"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
    /*======================================================================================
    * Upload ACAS
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadACAS2") {
      exec(http("XUI_Divorce_170_005_UploadACAS2")
        .post("/documentsv2")
        .headers(commonHeaderUpload)
        //.headers(postHeader1)
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_ACAS2"))
    
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
    /*  .group("CUI_CreateClaim_155_Upload") {
           exec(http("CUI_CreateClaim_155_Upload")
             .post(BaseURL + "/documents")
             .headers(Headers.commonHeader)
             .headers(Headers.postHeader)
             .header("accept", "application/json")
             .header("content-type", "multipart/form-data; boundary=----WebKitFormBoundaryGPNRb3mRRAhkFBRV")
             .header("sec-fetch-dest", "empty")
             .header("sec-fetch-mode", "cors")
             .bodyPart(RawFileBodyPart("files[]", "2MB.pdf")
               .fileName("2MB.pdf")
               .transferEncoding("binary"))
             .asMultipartForm
           //  .check(jsonPath("$[0].id").saveAs("Document_ID"))
             .check(substring("2MB.pdf")))
         }
         .pause(MinThinkTime, MaxThinkTime)
   
        */
  
  
    /*======================================================================================
    * Upload Acknowledgement
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadACK2") {
      exec(http("XUI_Divorce_170_005_UploadACK2")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Ack2"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
    /*======================================================================================
    * Upload Notice
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadNotice2") {
      exec(http("XUI_Divorce_170_005_UploadNotice2")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Notice2"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
    /*======================================================================================
    * Upload TribunalCase
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadTribunalCaseFile2") {
      exec(http("XUI_Divorce_170_005_UploadTribunalCaseFile2")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalCaseFile2"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
    /*======================================================================================
    * Upload TribunalOrder
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadTribunalOrder2") {
      exec(http("XUI_Divorce_170_005_UploadTribunalOrder2")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_TribunalOrder2"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
  
    /*======================================================================================
    * Upload Other
    ======================================================================================*/
  
    .group("XUI_Divorce_170_UploadOther2") {
      exec(http("XUI_Divorce_170_005_UploadOther2")
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
        .check(jsonPath("$.documents[0].hashToken").saveAs("hashToken_Other2"))
      )
      // .exec(Common.userDetails)
      // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  
    /*======================================================================================
    * Click Continue
    ======================================================================================*/
  
    .group("XUI_Divorce_180_ConfirmDocumentUploads") {
      exec(http("XUI_Divorce_180_005_ConfirmDocumentUploads")
        .post("/data/case-types/ET_EnglandWales/validate?pageId=uploadDocument1")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.case-data-validate.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/docupload/docupload.json"))
        .check(substring("documentCollection")))
      
       // .exec(Common.userDetails)
       // .exec(Common.userDetails)
    }
  
    .pause(MinThinkTime, MaxThinkTime)
  
  /*======================================================================================
      * Click Submit
      ======================================================================================*/
    .group("CD_CreateClaim_400_NotifyDetailsEventSubmit") {
      exec(http("CD_CreateClaim_370_EventSubmit")
        .post("/data/cases/#{caseId}/events")
        .headers(CommonHeader)
        .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.create-event.v2+json;charset=UTF-8")
        .header("x-xsrf-token", "${XSRFToken}")
        .body(ElFileBody("bodies/docupload/uploadsubmit.json"))
        .check(substring("#{caseId}"))
        .check(status.in(200, 201))
      )
        .exec(http("CD_CreateClaim_400_010_case")
          .get("/data/internal/cases/#{caseId}")
          .headers(CommonHeader)
          .header("accept", "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json")
          .check(status.in(200, 201, 204, 304))
        )
    }
    .pause(MinThinkTime, MaxThinkTime)
  
}
