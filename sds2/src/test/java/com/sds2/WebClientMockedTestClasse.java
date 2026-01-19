package com.sds2;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.service.AmadeusAuthService;

@ExtendWith(MockitoExtension.class)
public class WebClientMockedTestClasse {
    @Mock
    public WebClient.Builder webClientBuilder;

    @Mock
    public WebClient webClient;

    @Mock
    public WebClient.RequestHeadersUriSpec<?> uriSpec;

    @Mock
    public WebClient.RequestHeadersSpec<?> headersSpec;

    @Mock
    public WebClient.ResponseSpec responseSpec;

    @Mock
    public AmadeusAuthService amadeusAuthService;

    @BeforeEach
    void mockage(){when(amadeusAuthService.getAccessToken()).thenReturn("fake-token");

    doReturn(webClient).when(webClientBuilder).build();
    doReturn(uriSpec).when(webClient).get();
    doReturn(headersSpec).when(uriSpec).uri(any(URI.class));
    when(headersSpec.header(anyString(), anyString())).thenAnswer(invocation -> headersSpec);

    when(headersSpec.retrieve()).thenReturn(responseSpec);
}    
}
