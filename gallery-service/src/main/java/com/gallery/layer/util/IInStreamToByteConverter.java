package com.gallery.layer.util;

public interface IInStreamToByteConverter<T, D>{
    D convert(T t);
}
