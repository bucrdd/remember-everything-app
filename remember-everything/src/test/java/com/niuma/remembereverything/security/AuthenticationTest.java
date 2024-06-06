package com.niuma.remembereverything.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthenticationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper mapper;

  @Test
  void loginOnSecondLoginThenFirstSessionTerminated() throws Exception {
    MockHttpServletRequestBuilder loginRequest = MockMvcRequestBuilders.post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(new HashMap<String, String>() {{
          put("username", "admin");
          put("password", "password");
        }}));

    MvcResult mvcResult = this.mvc.perform(loginRequest)
        .andExpect(authenticated())
        .andReturn();

    MockHttpSession firstLoginSession = (MockHttpSession) mvcResult.getRequest().getSession();

    this.mvc.perform(get("/management/user/online-list").session(firstLoginSession))
        .andExpect(authenticated());

    this.mvc.perform(loginRequest).andExpect(authenticated());

    // first session is terminated by second login
    this.mvc.perform(get("/management/user/online-list").session(firstLoginSession))
        .andExpect(unauthenticated());
  }

  @Test
  void testLogout() throws Exception {
    MvcResult mvcResult = this.mvc.perform(logout("/logout")).andExpect(unauthenticated()).andReturn();
    System.out.println(mvcResult.getResponse().getContentAsString());
  }
}
