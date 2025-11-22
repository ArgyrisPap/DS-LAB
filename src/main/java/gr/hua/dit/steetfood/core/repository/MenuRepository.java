package gr.hua.dit.steetfood.core.repository;

import gr.hua.dit.steetfood.core.model.Menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu,Long> {

    Optional<Menu> findById(int id);
}
