package ru.yandex.practicum.mapper;

public class EnumMapper {
    public static <S extends Enum<S>, T extends Enum<T>> T mapEnum(S source, Class<T> targetClass) {
        try {
            String name = source.name();
            return Enum.valueOf(targetClass, name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Не удалось найти соответствующее значение для " + source, e);
        }
    }
}
