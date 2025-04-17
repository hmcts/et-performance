package utils

object Headers {

  val navigationHeader = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en;q=0.9",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  
  val citUILandingHeader = Map(   //headers_0 (replace)
  		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
  		"priority" -> "u=0, i",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-fetch-dest" -> "document",
  		"sec-fetch-mode" -> "navigate",
  		"sec-fetch-site" -> "none",
  		"sec-fetch-user" -> "?1",
  		"upgrade-insecure-requests" -> "1"
  )

    val citUICommonHeader = Map(
  		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
  		"priority" -> "u=0, i",
  		"sec-ch-ua-mobile" -> "?0",
  		"sec-fetch-dest" -> "document",
  		"sec-fetch-mode" -> "navigate",
  		"sec-fetch-site" -> "same-origin",
  		"sec-fetch-user" -> "?1",
  		"upgrade-insecure-requests" -> "1"
    )

  val commonHeader = Map(
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en;q=0.9",
    "content-type" -> "application/json",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val postHeader = Map(
    "content-type" -> "application/x-www-form-urlencoded"
  )

  val commonHeaderPRL = Map(
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val uploadHeader = Map(
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
    "content-type" -> "multipart/form-data",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "same-origin",
    "upgrade-insecure-requests" -> "1")

  val taskHeader = Map(
    "cache-control" -> "no-cache",
    "dnt" -> "1",
    "pragma" -> "no-cache",
    "experimental" -> "true",
    "sec-ch-ua" -> """ Not A;Brand";v="99", "Chromium";v="96", "Google Chrome";v="96""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "macOS",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "request-id" -> "|/qDn7.xWuGp")

  val xuiHeader = Map(
		"cache-control" -> "no-cache",
		"dnt" -> "1",
		"pragma" -> "no-cache",
    "origin" -> "https://manage-case.#{env}.platform.hmcts.net",
    "experimental" -> "true",
		"sec-ch-ua" -> """ Not A;Brand";v="99", "Chromium";v="96", "Google Chrome";v="96""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-origin",
    "request-id" -> "|/qDn7.xWuGp"
  )

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
  
  // val commonHeader = Map(
  //   "accept-encoding" -> "gzip, deflate, br",
  //   "accept-language" -> "en-US,en;q=0.9",
  //   "content-type" -> "application/json",
  //   "experimental" -> "true",
  //   "sec-ch-ua" -> """Google Chrome";v="89", "Chromium";v="89", ";Not A Brand";v="99""",
  //   "sec-ch-ua-mobile" -> "?0",
  //   "sec-fetch-dest" -> "empty",
  //   "sec-fetch-mode" -> "cors",
  //   "sec-fetch-site" -> "same-origin")
  
  val commonHeaderUpload = Map(
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-GB,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val postHeaderEnv = Map(
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