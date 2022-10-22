package com.gallery.layer.util;

public interface IConverter<T, D> {
    D convert(T t);
}
