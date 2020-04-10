package com.nadia.mqhub.common.utils;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author xiang.shi
 * @date 2020/4/7 10:55 上午
 */
@Component
public class ConcurrencyWrapper {
    private static AtomicReference<ConcurrencyWrapper> instance= new AtomicReference<>();

    @PostConstruct
    public void init(){
        instance.compareAndSet(null,this);
    }

    public static Runnable of(Runnable runnable){
        final List<Pair<ConcurrencyProcessor,Object>> mainThreadValueHolders= new LinkedList<>();
        Runnable delegate= () -> {
            try {
                for(Pair<ConcurrencyProcessor,Object> each: mainThreadValueHolders){
                    each.getKey().setThresholdValue(each.getValue());
                }
                runnable.run();
            }finally {
                for(Pair<ConcurrencyProcessor,Object> each: mainThreadValueHolders){
                    each.getKey().clearThresholdValue();
                }
            }
        };
        return delegate;
    }

    public static <T> Callable<T> of(Callable<T> task){
        final List<Pair<ConcurrencyProcessor,Object>> mainThreadValueHolders= new LinkedList<>();
        Callable<T> delegate= () -> {
            try {
                for(Pair<ConcurrencyProcessor,Object> each: mainThreadValueHolders){
                    each.getKey().setThresholdValue(each.getValue());
                }
                return task.call();
            }finally {
                for(Pair<ConcurrencyProcessor,Object> each: mainThreadValueHolders){
                    each.getKey().clearThresholdValue();
                }
            }
        };
        return delegate;
    }
}
