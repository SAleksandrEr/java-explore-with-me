package ru.practicum.ApiPublic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.GetCategoriesRequest;
import ru.practicum.category.service.CategoryService;
import ru.practicum.exception.ValidationException;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Object> findCategories(@RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                               @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct", "Incorrectly made request.");
        }
        return new ResponseEntity<>(categoryService.findCategories(GetCategoriesRequest.pageRequest(from, size)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findCategoryId(@PathVariable("id") Long id) {
        return new ResponseEntity<>(categoryService.findCategoryId(id), HttpStatus.OK);
    }
}
