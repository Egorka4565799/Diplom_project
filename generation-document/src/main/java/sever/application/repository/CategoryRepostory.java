package sever.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sever.application.model.Category;

@Repository
public interface CategoryRepostory extends JpaRepository<Category, Long> {
}
