import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import yasmine.Application;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class MyTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void countExamples() throws Exception {
        this.mockMvc.perform(get("/1/queries/count/2015"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"count\":573697}"));
        this.mockMvc.perform(get("/1/queries/count/2015-08"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"count\":573697}"));
        this.mockMvc.perform(get("/1/queries/count/2015-08-03"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"count\":198117}"));
        this.mockMvc.perform(get("/1/queries/count/2015-08-01 00:04"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"count\":617}"));
    }

    @Test
    public void wrongCountDateFormat() throws Exception {
        this.mockMvc.perform(get("/1/queries/count/123"))
                .andExpect(status().is4xxClientError());
        this.mockMvc.perform(get("/1/queries/count/123-01"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void popularExamples() throws Exception {
        this.mockMvc.perform(get("/1/queries/popular/2015?size=3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{ \"queries\": [{ \"query\": \"http%3A%2F%2Fwww.getsidekick.com%2Fblog%2Fbody-language-advice\", \"count\": 6675 }, { \"query\": \"http%3A%2F%2Fwebboard.yenta4.com%2Ftopic%2F568045\", \"count\": 4652 }, { \"query\": \"http%3A%2F%2Fwebboard.yenta4.com%2Ftopic%2F379035%3Fsort%3D1\", \"count\": 3100 }]}"));
        this.mockMvc.perform(get("/1/queries/popular/2015-08-02?size=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{ \"queries\": [{ \"query\": \"http%3A%2F%2Fwww.getsidekick.com%2Fblog%2Fbody-language-advice\", \"count\": 2283 }, { \"query\": \"http%3A%2F%2Fwebboard.yenta4.com%2Ftopic%2F568045\", \"count\": 1943 }, { \"query\": \"http%3A%2F%2Fwebboard.yenta4.com%2Ftopic%2F379035%3Fsort%3D1\", \"count\": 1358 }, { \"query\": \"http%3A%2F%2Fjamonkey.com%2F50-organizing-ideas-for-every-room-in-your-house%2F\", \"count\": 890 }, { \"query\": \"http%3A%2F%2Fsharingis.cool%2F1000-musicians-played-foo-fighters-learn-to-fly-and-it-was-epic\", \"count\": 701 }]}"));
    }

    @Test
    public void wrongPopularDateFormat() throws Exception {
        this.mockMvc.perform(get("/1/queries/popular/11-08-02?size=5"))
                .andExpect(status().is4xxClientError());
        this.mockMvc.perform(get("/1/queries/popular/123-01"))
                .andExpect(status().is4xxClientError());
    }
}
