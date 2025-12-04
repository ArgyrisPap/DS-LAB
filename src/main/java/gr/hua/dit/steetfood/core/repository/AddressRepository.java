package gr.hua.dit.steetfood.core.repository;

import gr.hua.dit.steetfood.core.model.Address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findById(long id);
}
