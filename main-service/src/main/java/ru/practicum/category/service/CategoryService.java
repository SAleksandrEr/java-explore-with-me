package ru.practicum.category.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.storage.CategoryRepositoryJpa;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.QCategory;
import ru.practicum.event.storage.EventRepositoryJpa;
import ru.practicum.exception.ConditionsDataException;
import ru.practicum.exception.DataNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
public class CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepositoryJpa categoryRepositoryJpa;

    private final EventRepositoryJpa eventRepositoryJpa;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepositoryJpa.save(category));
    }

    @Transactional
    public void categoryDeleteById(Long id) {
        categoryRepositoryJpa.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category with id=" + id + " was not found", "The required object was not found."));
        if (eventRepositoryJpa.findByCategoryId(id).isPresent()) {
            throw new ConditionsDataException("For the requested operation the conditions are not met.", "The category is not empty", HttpStatus.CONFLICT);
        }
        categoryRepositoryJpa.deleteById(id);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        categoryRepositoryJpa.findById(categoryDto.getId())
                .orElseThrow(() -> new DataNotFoundException("Category with id=" + categoryDto.getId() + " was not found", "The required object was not found."));
        categoryRepositoryJpa.updateCategory(categoryDto.getId(), categoryDto.getName());
        return categoryDto;
    }

    public List<CategoryDto> findCategories(Pageable page) {
        QCategory category = QCategory.category;
        BooleanExpression condition = category.isNotNull();
        Sort sort = Sort.by("id");
        PageRequest pageRequest = PageRequest.of(page.getPageNumber(), page.getPageSize(), sort);
        List<Category> categoryList = categoryRepositoryJpa.findAll(condition, pageRequest)
                .stream().collect(Collectors.toList());
        return categoryList.stream().map(categoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    public CategoryDto findCategoryId(Long id) {
        Category category = categoryRepositoryJpa.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Category with id=" + id + " was not found", "The required object was not found."));
        return categoryMapper.toCategoryDto(category);
    }
}
