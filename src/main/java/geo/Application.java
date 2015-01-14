package geo;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.view.DesignDocument;
import com.couchbase.client.java.view.SpatialView;
import com.couchbase.client.java.view.SpatialViewQuery;
import com.couchbase.client.java.view.SpatialViewResult;
import com.couchbase.client.java.view.SpatialViewRow;
import com.couchbase.client.java.view.Stale;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Arrays;
import java.util.List;

@Controller
@SpringBootApplication
public class Application implements InitializingBean {

    @Value("${host}")
    private String host;
    @Value("${bucket}")
    private String bucket;
    @Value("${password}")
    private String password;

    @Bean
    public Bucket bucket() {
        return CouchbaseCluster.create(host).openBucket(bucket, password);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
       DesignDocument ddoc = DesignDocument.create(
            "shops",
            Arrays.asList(SpatialView.create("by_location", "function (doc) { if (doc.type == \"shop\") { " +
                "emit([doc.lon, doc.lat, [doc.from, doc.to]], null); } }"))
        );
        bucket().bucketManager().upsertDesignDocument(ddoc);

        JsonDocument london = JsonDocument.create("shop::1", JsonObject.create()
                .put("type", "shop")
                .put("name", "London")
                .put("desc", "Flagship Store")
                .put("from", 0)
                .put("to", 2400)
                .put("lat", 51.49848454717058)
                .put("lon", -0.1043701171875)
        );

        JsonDocument vienna = JsonDocument.create("shop::2", JsonObject.create()
                .put("type", "shop")
                .put("name", "Vienna")
                .put("desc", "Mozarts Finest")
                .put("from", 900)
                .put("to", 1800)
                .put("lat", 48.28319289548349)
                .put("lon", 16.3916015625)
        );

        JsonDocument berlin = JsonDocument.create("shop::3", JsonObject.create()
                .put("type", "shop")
                .put("name", "Berlin")
                .put("desc", "Between East and West")
                .put("from", 800)
                .put("to", 2000)
                .put("lat", 52.576349937498875)
                .put("lon", 13.38134765625)
        );

        JsonDocument munich = JsonDocument.create("shop::4", JsonObject.create()
                .put("type", "shop")
                .put("name", "Munich")
                .put("desc", "Beeery")
                .put("from", 900)
                .put("to", 1800)
                .put("lat", 48.16608541901253)
                .put("lon", 11.57958984375)
        );

        bucket().upsert(london);
        bucket().upsert(vienna);
        bucket().upsert(berlin);
        bucket().upsert(munich);
    }

    @RequestMapping("/shops/{lon1}/{lat1}/{lon2}/{lat2}/{from}/{to}")
    @ResponseBody
    String home(
        @PathVariable("lon1") double lon1,
        @PathVariable("lat1") double lat1,
        @PathVariable("lon2") double lon2,
        @PathVariable("lat2") double lat2,
        @PathVariable("from") int from,
        @PathVariable("to") int to
    ) {
        SpatialViewQuery query = SpatialViewQuery
            .from("shops", "by_location")
            .startRange(JsonArray.from(lon1, lat1, from))
            .endRange(JsonArray.from(lon2, lat2, to))
            .stale(Stale.FALSE);

        SpatialViewResult result = bucket().query(query);
        List<SpatialViewRow> spatialViewRows = result.allRows();

        JsonArray array = JsonArray.create();
        for (SpatialViewRow row : spatialViewRows) {
            JsonDocument doc = row.document();
            array.add(doc.content());
        }
        return array.toString();
    }

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

}
