package bg.softuni.HappyCats.web;


import bg.softuni.HappyCats.model.DTOS.AddPlanDTO;
import bg.softuni.HappyCats.model.entity.Plan;

import bg.softuni.HappyCats.model.entity.User;
import bg.softuni.HappyCats.model.enums.PlanEnum;
import bg.softuni.HappyCats.repository.PlanRepository;

import bg.softuni.HappyCats.util.TestDataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PlanControllerIT {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRep;
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TestDataUtils testDataUtils;

    private User testUser, testAdmin;


    @BeforeEach
    void setUp() throws Exception {
        testUser = testDataUtils.createTestUser("user@example.com");
        testAdmin = testDataUtils.createTestAdmin("admin@example.com");
    }

    @AfterEach
    void tearDown() {
        testDataUtils.cleanUpDatabase();
    }

    @Test
    public void getPlanPage() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/price",
                String.class)).contains("Pricing Plan");
    }

    @Test
    void testAddWithoutUserPlan() throws Exception {
        mockMvc.perform(post("/addPlanStandard").
                        with(csrf())
                ).
                andExpect(status().is3xxRedirection()).
                andExpect(redirectedUrl("http://localhost/login"));
    }


    @Test
    @WithMockUser(
            username = "zdravko",
            password = "4b148b365433c559fdc07a0742712e88b61d5e23a52bb10206c308908e2e67836ecb3ff5714006ea"
    )
    void testAddWithUserPlan() throws Exception {
        mockMvc.perform(post("/addPlanStandard").
                        with(csrf())
                ).
                andExpect(status().is4xxClientError());
    }

    @Test
    void planTester() throws Exception {
        Plan plan = planRep.findById(1L).get();
        System.out.println(plan.getPlanEnum());
        System.out.println(plan.getPrice());
        System.out.println(plan.getUser());
        System.out.println(plan.getId());

        AddPlanDTO dto = new AddPlanDTO();
        dto.setPlanEnum(PlanEnum.STANDARD);
        dto.setUser(testUser);
        dto.setPrice(20);

        System.out.println(dto.getPlanEnum());
        System.out.println(dto.getPrice());
        System.out.println(dto.getUser());
    }
}