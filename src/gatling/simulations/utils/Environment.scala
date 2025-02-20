package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURLETUIApp = "https://et-sya.#{env}.platform.hmcts.net"
  val baseURLETUIResp = "https://et-syr.#{env}.platform.hmcts.net"
  val baseURL = "https://manage-case.perftest.platform.hmcts.net"
  val idamURL = "https://idam-web-public.#{env}.platform.hmcts.net"


  val minThinkTime = 12
  val maxThinkTime = 14

  val HttpProtocol = http

  val commonHeader1 = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1"
  )
  
  val commonHeader = Map(
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "experimental" -> "true",
    "sec-ch-ua" -> """Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")
  
  val commonHeaderUpload = Map(
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val postHeader = Map(
    "accept" -> "*/*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin"
  )
  
  val postHeader1 = Map(
    "content-type" -> "application/x-www-form-urlencoded"
  )

}
