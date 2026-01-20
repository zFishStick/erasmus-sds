package com.sds2.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClientResponseException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private Model model;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleRestErrors_ShouldAddErrorAndDetailsToModel() {
        // Arrange
        String errorMsg = "400 Bad Request";
        String responseBody = "{\"code\":\"INVALID_ID\", \"message\":\"Id cannot be null\"}";

        RestClientResponseException ex = mock(RestClientResponseException.class);
        when(ex.getMessage()).thenReturn(errorMsg);
        when(ex.getResponseBodyAsString()).thenReturn(responseBody);

        // Act
        String viewName = globalExceptionHandler.handleRestErrors(ex, model);

        // Assert
        assertEquals("error_page", viewName);

        // Verifichiamo che il modello sia stato popolato correttamente
        verify(model).addAttribute("error", "Upstream API error: " + errorMsg);
        verify(model).addAttribute("details", responseBody);
    }

    @Test
    void testHandleGeneric_ShouldAddOnlyErrorMessageToModel() {
        // Arrange
        String errorMsg = "Null Pointer Exception imprevista";
        Exception ex = new RuntimeException(errorMsg);

        // Act
        String viewName = globalExceptionHandler.handleGeneric(ex, model);

        // Assert
        assertEquals("error_page", viewName);

        verify(model).addAttribute("error", errorMsg);
    }
}