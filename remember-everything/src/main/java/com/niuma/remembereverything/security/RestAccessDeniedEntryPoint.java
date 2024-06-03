package com.niuma.remembereverything.security;

import static com.niuma.remembereverything.web.ReturnCode.RC_UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niuma.remembereverything.web.ResponseData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestAccessDeniedEntryPoint implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;


  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException e) throws IOException, ServletException {
    log.error(e.getMessage(), e);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    var result = ResponseData.fail(e.getMessage(), RC_UNAUTHORIZED.getCode());
    objectMapper.writeValue(response.getOutputStream(), result);
  }
}
