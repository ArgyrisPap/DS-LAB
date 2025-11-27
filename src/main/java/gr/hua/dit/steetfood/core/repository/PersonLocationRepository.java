package gr.hua.dit.steetfood.core.repository;

import gr.hua.dit.steetfood.core.model.PersonLocation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonLocationRepository extends JpaRepository<PersonLocation, Integer> {


}
