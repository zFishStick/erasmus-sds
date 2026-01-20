package com.sds2;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ServerTest {

    @Test
    void main_runsSpringApplicationAndLogsInfo_whenDesktopSupported() throws Exception {
        try (MockedStatic<SpringApplication> springMock = mockStatic(SpringApplication.class);
             MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {

            springMock.when(() -> SpringApplication.run(Server.class, new String[]{})).thenReturn(null);

            Desktop desktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(desktop);

            Server.main(new String[]{});

            springMock.verify(() -> SpringApplication.run(Server.class, new String[]{}));

            verify(desktop).browse(new URI(Server.LOCALHOST));
        }
    }

    @Test
    void main_logsError_whenDesktopNotSupported() throws Exception {
        try (MockedStatic<SpringApplication> springMock = mockStatic(SpringApplication.class);
             MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {

            springMock.when(() -> SpringApplication.run(Server.class, new String[]{})).thenReturn(null);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(false);

            Server.main(new String[]{});

            springMock.verify(() -> SpringApplication.run(Server.class, new String[]{}));
            desktopMock.verify(Desktop::isDesktopSupported);
        }
    }

    @Test
    void main_throwsIOException_whenDesktopBrowseFails() throws Exception {
        try (MockedStatic<SpringApplication> springMock = mockStatic(SpringApplication.class);
             MockedStatic<Desktop> desktopMock = mockStatic(Desktop.class)) {

            springMock.when(() -> SpringApplication.run(Server.class, new String[]{})).thenReturn(null);

            Desktop desktop = mock(Desktop.class);
            desktopMock.when(Desktop::isDesktopSupported).thenReturn(true);
            desktopMock.when(Desktop::getDesktop).thenReturn(desktop);
            doThrow(new IOException("fail")).when(desktop).browse(any(URI.class));

            IOException ex = assertThrows(IOException.class, () -> Server.main(new String[]{}));
            assertTrue(ex.getMessage().contains("fail"));
        }
    }
}
