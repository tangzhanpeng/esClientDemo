import com.google.gson.Gson;
import com.tzp.es.service.ESService;
import com.tzp.es.service.entity.User;
import com.tzp.es.service.impl.ESServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsTest {

    private static final ESService esService = new ESServiceImpl();

    private static final Gson gson = new Gson();

    @Test
    public void indexCreate() {
        assertTrue(esService.esIndexCreate("user"));
    }

    @Test
    public void indexSearch() {
        GetIndexResponse response = esService.esIndexSearch("user");
        assertNotNull(response);
        log.info(gson.toJson(response.getMappings()));
        log.info(gson.toJson(response.getSettings()));
        log.info(gson.toJson(response.getAliases()));
    }

    @Test
    public void indexDelete() {
        assertTrue(esService.esIndexDeleted("user"));
    }

    @Test
    public void docInsert() {
        User user = new User();
        user.setAge(18);
        user.setName("张三");
        user.setSex("男");
        IndexResponse response = esService.esDocInsert("user", "1001", gson.toJson(user));
        log.info(gson.toJson(response.getResult()));
    }

    @Test
    public void docUpdate() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", "12");
        map.put("sex", "女");
        UpdateResponse response = esService.esDocUpdate("user", "1001", map);
        log.info(gson.toJson(response.getResult()));
    }

    @Test
    public void docGet() {
        GetResponse response = esService.esDocGet("user", "1001");
        log.info(response.getSourceAsString());
    }

    @Test
    public void docDelete() {
        DeleteResponse response = esService.esDocDelete("user", "1001");
        log.info(gson.toJson(response.getResult()));
    }

    @Test
    public void docInsertBatch() {
        List<User> users = new ArrayList<>();
        users.add(new User(1200L, "小7", "男", 20));
        users.add(new User(1201L, "小8", "男", 30));
        users.add(new User(1202L, "小10", "女", 22));
        BulkResponse response = esService.esDocInsertBatch("user", users);
        log.info(response.toString());
    }

    @Test
    public void docDeleteBatch() {
        List<String> docIds = new ArrayList<>();
        docIds.add("1002");
        docIds.add("1003");
        docIds.add("1004");
        BulkResponse response = esService.esDocDeleteBatch("user", docIds);
        log.info(Arrays.toString(Arrays.stream(response.getItems()).toArray()));
    }

    @Test
    public void docQuery() {
        SearchResponse response = esService.esDocQuery("user", 0, 5,
                "age", SortOrder.DESC, null, new String[]{"age"});
        SearchHits hits = response.getHits();
        log.info(gson.toJson(hits.getTotalHits()));
        log.info(gson.toJson(response.getTook()));
        for (SearchHit hit : hits) {
            log.info(hit.getSourceAsString());
        }
    }

    @Test
    public void docQuery2() {
        SearchResponse response = esService.esDocQuery("user", "sex", 23);
        SearchHits hits = response.getHits();
        log.info(gson.toJson(hits.getTotalHits()));
        log.info(gson.toJson(response.getTook()));
        for (SearchHit hit : hits) {
            log.info(hit.getSourceAsString());
        }
    }

    @Test
    public void hutoJsonTest() {
        Map<String, String> map = new HashMap<>();
        map.put("tommy", "mary");
        map.put("tommy1", "mary");
    }
}
