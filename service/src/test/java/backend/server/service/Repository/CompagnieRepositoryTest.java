package backend.server.service.Repository;

import backend.server.service.domain.Compagnie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

//This annotation is used for testing JPA repositories. It sets up an in-memory database and configures Spring Data JPA repositories, EntityManager, and Hibernate for testing.
@DataJpaTest
class CompagnieRepositoryTest {

    @Autowired
    private CompagnieRepository compagnieRepository;

    @Autowired
    private TestEntityManager entityManager;
    @Test
    public void TestFindByNom() {
        //Create a Compagnie entity
        Compagnie compagnie = new Compagnie();
        compagnie.setNom("TestCompagnie");
        entityManager.persist(compagnie);
        entityManager.flush();

        //invoke the repository method
        Compagnie foundCompagnie = compagnieRepository.findByNom("TestCompagnie");

        //Assertion
        Assertions.assertNotNull(foundCompagnie);
        Assertions.assertEquals("TestCompagnie", foundCompagnie.getNom());

    }

    @Test
    public void deleteByNom() {
    }
//    @Autowired
//    private CompagnieRepository compagnieRepository;
//
//    @Autowired
//    private TestEntityManager entityManager;
//    @Test
//    public void TestFindByNom() {
////        //Create a Compagnie entity
////        Compagnie compagnie = new Compagnie();
////        compagnie.setNom("TestCompagnie");
////        entityManager.persist(compagnie);
////        entityManager.flush();
////
////        //invoke the repository method
////        Compagnie foundCompagnie = compagnieRepository.findByNom("TestCompagnie");
////
////        //Assertion
////        Assertions.assertNotNull(foundCompagnie);
////        Assertions.assertEquals("TestCompagnie", foundCompagnie.getNom());
//
//    }
//
//    @Test
//    public void deleteByNom() {
//    }
}