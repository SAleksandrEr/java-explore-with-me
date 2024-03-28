package ru.practicum.ApiPublic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.GetCompilationRequest;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.exception.ValidationException;

@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationControllerPublic {

    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<Object> findCompilation(@RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                                 @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        // добавить pinned
        if (from < 0 || size < 1) {
            throw new ValidationException("Param - <from> or <size> is not correct", "Incorrectly made request.");
        }
        return new ResponseEntity<>(compilationService.findCompilation(GetCompilationRequest.pageRequest(from, size)), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<Object> findCompilationId(@PathVariable("compId") Long compId) {
        return new ResponseEntity<>(compilationService.findCompilationId(compId), HttpStatus.OK);
    }

}
