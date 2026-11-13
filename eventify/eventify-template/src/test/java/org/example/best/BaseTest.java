package org.example.best;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.best.config.SecurityConfig;
import org.example.best.config.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
public abstract class BaseTest {
    @Autowired
    protected MockMvc mockMvc;


    protected MockedStatic<SecurityUtils> mockedSecurity;

    @BeforeEach
    protected void setUpBase() {

    }

    @AfterEach
    protected void tearDownBase() {
        if (mockedSecurity != null) {
            mockedSecurity.close();
        }
    }

    protected static ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected static String toJson(Object request) {
        try{
            return OBJECT_MAPPER.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка сериализации DTO в JSON", e);
        }
    }

    protected void assertGetRequestThrows(String url, Object uriVars, int expectedStatus) throws Exception {
        mockMvc.perform(get(url, uriVars).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));
    }

    protected void assertPutRequestThrows(String url, Object uriVars, Object body, int expectedStatus) throws Exception {
        mockMvc.perform(put(url, uriVars).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(body)))
                .andExpect(status().is(expectedStatus));
    }

    protected void assertPostRequestThrows(String url, Object body, int expectedStatus) throws Exception {
        mockMvc.perform(post(url).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andExpect(status().is(expectedStatus));
    }

    protected void assertDeleteRequestThrows(String url, Object uriVars, int expectedStatus) throws Exception {
        mockMvc.perform(delete(url, uriVars).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus));
    }
}
