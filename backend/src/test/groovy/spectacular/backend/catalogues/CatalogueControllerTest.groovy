package spectacular.backend.catalogues


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

//Full integration tests
//@SpringBootTest(properties = ["github.api.root-url=http://api-url","github.api.app.id=898","github.api.app.private-key=xyz"])
//@AutoConfigureMockMvc
//class CatalogueControllerTest extends Specification {
//    @Autowired
//    protected MockMvc mvc
//
//    @Autowired
//    private MockRestServiceServer server
//
//    def "when getCatalogues is performed then the response has status 200 and content is a catalogues response object"() {
//        given: ""
//
//        expect: "Status is 200 and the response is a catalogues response object"
//        def mvcResult = mvc.perform(get("/api/12345/test-owner1/test-repo2/catalogues"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath('$.catalogues.length()').value(1))
//                .andReturn()
//    }
//}
