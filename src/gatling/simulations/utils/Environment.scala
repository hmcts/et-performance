package utils

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

  val baseURL = "https://et-sya.perftest.platform.hmcts.net"
 // val baseURL = "https://et-sya.{env}.platform.hmcts.net"
 val employmentTribunalsURL = "https://employmenttribunals.service.gov.uk/en/apply"

  val idamURL = "https://idam-web-public.${env}.platform.hmcts.net"
  val idamAPIURL = "https://idam-api.${env}.platform.hmcts.net"
  val rpeAPIURL = "http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal"
  val ccdAPIURL = "http://ccd-data-store-api-${env}.service.core-compute-${env}.internal"
  val paymentURL = "https://www.payments.service.gov.uk"

  val minThinkTime = 4
  val maxThinkTime = 7

  val HttpProtocol = http

  val commonHeader = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1"
  )

  val postHeader = Map(
    "accept" -> "*/*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin"
  )

}
