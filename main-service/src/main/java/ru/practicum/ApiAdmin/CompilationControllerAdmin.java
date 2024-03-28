package ru.practicum.ApiAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationControllerAdmin {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<Object> createCompilation(@Valid @RequestBody NewCompilationDto compilation) {
        return new ResponseEntity<>(compilationService.createCompilation(compilation), HttpStatus.CREATED);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<Object> updateCompilation(@Valid @RequestBody UpdateCompilationRequest compilation, @PathVariable("compId") Long compId) {
        return new ResponseEntity<>(compilationService.updateCompilation(compilation, compId), HttpStatus.OK);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Object> compilationDeleteById(@PathVariable("compId") Long compId) {
        compilationService.compilationDeleteById(compId);
        return new ResponseEntity<>("Подборка удалена", HttpStatus.NO_CONTENT);
    }

}
