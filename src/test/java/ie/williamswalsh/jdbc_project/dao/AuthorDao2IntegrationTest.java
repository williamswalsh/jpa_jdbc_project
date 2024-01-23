package ie.williamswalsh.jdbc_project.dao;

import ie.williamswalsh.jdbc_project.domain.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ActiveProfiles("local")
@DataJpaTest
@Import(AuthorDaoImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // don't use H2 DB.
class AuthorDao2IntegrationTest {

    @Autowired
    AuthorDao authorDao;

    @Test
    void testGetMethod() {
        Author author = authorDao.getById(1L);

        assertThat(author).isNotNull();
    }
}