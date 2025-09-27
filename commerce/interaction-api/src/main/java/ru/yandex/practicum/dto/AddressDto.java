package ru.yandex.practicum.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {

    String country;

    String city;

    String street;

    String house;

    String flat;
}
