package ru.itis.services;

import ru.itis.dto.UserDto;

import java.util.Optional;

/**
 * 16.10.2020
 * 4. Simple Web Application
 *
 * @author Sidikov Marsel (First Software Engineering Platform)
 * @version v1.0
 */
public interface UsersService {
    Optional<UserDto> getUserByCookie(String value);
}
