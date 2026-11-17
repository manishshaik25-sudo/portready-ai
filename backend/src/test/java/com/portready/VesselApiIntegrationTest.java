package com.portready;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class VesselApiIntegrationTest {
 @Autowired MockMvc mvc;
 @Test void readsFleetAndUpdatesFinding() throws Exception {
  mvc.perform(get("/api/vessels")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Atlantic Pioneer"));
  mvc.perform(get("/api/vessels/1/readiness")).andExpect(status().isOk()).andExpect(jsonPath("$.openFindings").value(2));
 mvc.perform(patch("/api/findings/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"status\":\"RESOLVED\"}"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.status").value("RESOLVED"));
  mvc.perform(get("/api/vessels/1/readiness")).andExpect(status().isOk()).andExpect(jsonPath("$.openFindings").value(1));
 }
 @Test void missingVesselReturns404() throws Exception {
  mvc.perform(get("/api/vessels/999/readiness")).andExpect(status().isNotFound());
 }
}
