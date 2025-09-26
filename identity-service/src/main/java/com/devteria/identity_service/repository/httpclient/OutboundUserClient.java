package com.devteria.identity_service.repository.httpclient;

import com.devteria.identity_service.dto.ExchangeTokenRequest;
import com.devteria.identity_service.response.ExchangeTokenResponse;
import com.devteria.identity_service.response.OutBoundUserResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name ="outbound-user-client", url = "https://www.googleapis.com")
public interface OutboundUserClient {
    @GetMapping(value = "/oauth2/v1/userinfo")
    OutBoundUserResponse getUserInfo(@RequestParam("alt") String alt
            , @RequestParam("access_token") String accessToken);
}
