package com.gallery.web.util;

import com.gallery.layer.util.IConverter;

public class MegabyteToByteConverter implements IConverter<Long, Long> {
    @Override
    public Long convert(Long megaByte) {
        return (long) megaByte * 1024 * 1024;
    }
}
