package ru.practicum.ApiAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.dto.CategoryDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return new ResponseEntity<>(categoryService.createCategory(categoryDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> categoryDeleteById(@PathVariable("id") Long id) {
        categoryService.categoryDeleteById(id);
        return new ResponseEntity<>("Категория удалена", HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCategory(@Valid @RequestBody CategoryDto categoryDto, @PathVariable Long id) {
        categoryDto.setId(id);
        return new ResponseEntity<>(categoryService.updateCategory(categoryDto), HttpStatus.OK);
    }
}
