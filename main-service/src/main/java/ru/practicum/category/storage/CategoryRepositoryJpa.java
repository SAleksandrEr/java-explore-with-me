package ru.practicum.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.category.model.Category;

@Repository
public interface CategoryRepositoryJpa extends JpaRepository<Category, Long>, QuerydslPredicateExecutor<Category> {

    @Modifying
    @Query("UPDATE Category " +
            "SET name = ?2 " +
            "where id = ?1")
    void updateCategory(Long id, String name);

}
