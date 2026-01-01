package gr.hua.dit.steetfood.core.repository;

import gr.hua.dit.steetfood.core.model.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Client} entity.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByName(final String name);
}
