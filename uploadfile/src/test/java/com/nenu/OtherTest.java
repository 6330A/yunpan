package com.nenu;

import ch.qos.logback.core.model.INamedModel;
import io.lettuce.core.ScriptOutputType;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.constructor.Constructor;

import java.awt.*;
import java.lang.Thread;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
//import java.util.*;
//import java.util.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

//抽象类的应用，模板方法的设计模式


public class OtherTest {
    @Test
    public void test1() {
        int[] arr = new int[]{ 3, 5, 1, 2, 11, 21, 0 };
        quickSort(arr, 0, arr.length - 1);
        for (int i : arr) {
            System.out.println(i);
        }
    }
    public  void quickSort(int[] arr, int l, int r) {
        if (l >= r) {
            return;
        }
        int temp = arr[l];
        int left = l, right = r;

        while (left < right) {
            while (left < right && arr[right] >= temp)
                right--;
            arr[left] = arr[right];
            while (left < right && arr[left] <= temp)
                left++;
            arr[right] = arr[left];
        }
        arr[left] = temp;
        quickSort(arr, l, left - 1);
        quickSort(arr, left + 1, r);
    }
}
