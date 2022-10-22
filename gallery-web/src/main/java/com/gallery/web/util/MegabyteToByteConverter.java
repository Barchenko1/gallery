package com.gallery.web.util;

import com.gallery.layer.util.IConverter;

public class MegabyteToByteConverter implements IConverter<Integer, Long> {
    @Override
    public Long convert(Integer megaByte) {
        return (long) megaByte * 1024 * 1024;
    }
}
