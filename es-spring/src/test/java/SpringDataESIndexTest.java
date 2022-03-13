import com.tzp.esSpring.EsSpringMain;
import com.tzp.esSpring.Product;
import com.tzp.esSpring.ProductDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@Slf4j
@SpringBootTest(classes = EsSpringMain.class)
public class SpringDataESIndexTest {

    @Autowired
    private ProductDao productDao;

    //创建索引并增加映射配置
    @Test
    public void createIndex(){
        Product product = new Product();
        product.setId(3L);
        product.setTitle("华为手机");
        product.setCategory("手机");
        product.setPrice(299.0);
        product.setImages("http://www.zp/hw.jpg");
        productDao.save(product);
    }

    @Test
    public void docQuery() {
        Optional<Product> byId = productDao.findById(3L);
        log.info(byId.get().getTitle());
    }
}
