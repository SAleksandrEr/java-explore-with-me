package ru.practicum.ApiAdmin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.service.UserService;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.dto.GetUserRequest;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userRequest) {
        checkEmail(userRequest.getEmail());
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> userDeleteById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("Пользователь удален", HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<Object> findUsersIds(@RequestParam(value = "ids", required = false) List<Long> users,
                                              @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                                              @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
            if (from < 0 || size < 1) {
                throw new ValidationException("Param - <from> or <size> is not correct", "Incorrectly made request.");
            }
        return new ResponseEntity<>(userService.findUsersIds(GetUserRequest.of(users, from, size)),HttpStatus.OK);
    }

    private void checkEmail(String email) {
        String[] lineEmail = email.split("@");
        String emailName = lineEmail[0];
        String[] localPartDomen = lineEmail[1].split("\\.");
        String localPart = localPartDomen[0];
        if (emailName.length() > 64) {
            throw new ValidationException("Line email > 64", "Incorrectly made request.");
        }
        if (localPart.length() > 63) {
            throw new ValidationException("Line localPart > 63", "Incorrectly made request.");
        }
    }

}
