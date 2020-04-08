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

    @Autowired
    private List<ConcurrencyProcessor> processors= new LinkedList<>();

    @PostConstruct
    public void init(){
        instance.compareAndSet(null,this);
    }

    public static Runnable of(Runnable runnable){
        final List<Pair<ConcurrencyProcessor,Object>> mainThreadValueHolders= new LinkedList<>();
        for(ConcurrencyProcessor each: instance.get().processors){
            mainThreadValueHolders.add(new Pair<>(each,each.getThresholdValue()));
        }
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
        for(ConcurrencyProcessor each: instance.get().processors){
            mainThreadValueHolders.add(new Pair<>(each,each.getThresholdValue()));
        }
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
