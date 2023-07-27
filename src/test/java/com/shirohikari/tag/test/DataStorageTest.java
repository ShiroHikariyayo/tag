package com.shirohikari.tag.test;

import com.shirohikari.tag.main.DataStorage;
import org.junit.Test;

import java.io.IOException;

public class DataStorageTest {

    @Test
    public void create() throws IOException {
        DataStorage.create("E:\\test");
    }
}
